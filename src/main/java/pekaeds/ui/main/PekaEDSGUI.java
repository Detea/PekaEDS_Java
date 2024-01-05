package pekaeds.ui.main;

import pk2.PekkaKana2;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;

import org.tinylog.Logger;

import pekaeds.data.EditorConstants;
import pekaeds.data.Layer;
import pekaeds.data.PekaEDSVersion;
import pekaeds.pk2.map.*;
import pekaeds.settings.Settings;
import pekaeds.settings.Shortcuts;
import pekaeds.settings.StartupBehavior;
import pekaeds.tool.*;
import pekaeds.tool.tools.*;
import pekaeds.ui.actions.*;
import pekaeds.ui.listeners.MainUIWindowListener;
import pekaeds.ui.listeners.RepaintListener;
import pekaeds.ui.mapmetadatapanel.MapMetadataPanel;
import pekaeds.ui.mappanel.MapPanel;
import pekaeds.ui.mappanel.MapPanelView;
import pekaeds.ui.minimappanel.MiniMapPanel;
import pekaeds.ui.spritelistpanel.SpritesPanel;
import pekaeds.ui.tilesetpanel.TilesetPanel;
import pekaeds.ui.toolpropertiespanel.ToolPropertiesPanel;
import pekaeds.util.*;
import pekaeds.util.episodemanager.EpisodeManager;
import pekaeds.util.file.AutoSaveManager;
import pekaeds.util.file.LastSessionManager;
import pekaeds.util.file.PathUtils;

public class PekaEDSGUI implements ChangeListener {
    //private ChangeEvent changeEvent = new ChangeEvent(this);

    private PekaEDSGUIView view;
    private PekaEDSGUIModel model;
    
    private TilesetPanel tilesetPanel;
    private MapPanel mapPanel;
    private MapPanelView mapPanelView;
    
    private MainToolBar mainToolBar;
    
    private SpritesPanel spritesPanel;
    private MapMetadataPanel mapMetadataPanel;
    
    private MiniMapPanel miniMapPanel;
    
    private Statusbar statusbar;
    
    private ToolPropertiesPanel toolPropertiesPanel;
    
    private boolean unsavedChanges = false;
    
    private final AutoSaveManager autosaveManager;
    private final EpisodeManager episodeManager;
    
    private final LastSessionManager sessionManager = new LastSessionManager();
    
    public PekaEDSGUI() {
        // This has to be done before PekaEDSGUIView gets initialized, because it relies on the toolsList in the Tools class.
        registerTools();
    
        episodeManager = new EpisodeManager();
        
        view = new PekaEDSGUIView(this);
        model = new PekaEDSGUIModel();
    
        setupComponents();
        registerMapConsumers();
        registerRepaintListeners();
        registerPropertyListeners();
        
        Tool.setToolModeListener(mainToolBar);
        
        // This has to be done after setupComponents(), because View uses the components initialized in this method
        view.setupMainUI();
        
        mapPanel.getViewport().addChangeListener(miniMapPanel);
        
        registerChangeListeners();
        installKeyboardShortcuts();
        
        mapPanel.setLeftMouseTool(new BrushTool());
        mapPanel.setRightMouseTool(new SelectionTool());
        
        autosaveManager = new AutoSaveManager(this, model.getCurrentMapFile());
        autosaveManager.start();
    
        setSelectedTool(Tools.getTool(BrushTool.class));
        
        mapPanel.requestFocus();
        
        // TODO Optimization: Make this faster. Map loading might also need to be sped up/put in SwingUtils.invokeLater()
        handleStartup();
    }
    
    private void handleStartup() {
        var fLastSession = new File(EditorConstants.LAST_SESSION_FILE);
        if (fLastSession.exists()) {
            try {
                Logger.info("Trying to load last.session...");
                var lastSession = sessionManager.loadLastSession(fLastSession);
                
                switch (Settings.getDefaultStartupBehavior()) {
                    case StartupBehavior.NEW_MAP -> {
                        newMap();
    
                        Logger.info("Creating new map.");
                    }
    
                    case StartupBehavior.LOAD_LAST_EPISODE -> {
                        if (lastSession.getLastEpisodeFile().exists()) {
                            episodeManager.loadEpisode(lastSession.getLastEpisodeFile());
            
                            loadMap(lastSession.getLastLevelFile());
                            mapPanelView.getViewport().setViewPosition(new Point(lastSession.getLastViewportX(), lastSession.getLastViewportY()));
                            
                            Logger.info("Loaded last episode: {} file: {}", episodeManager.getEpisode().getEpisodeName(), lastSession.getLastLevelFile().getAbsolutePath());
                        } else {
                            newMap();
                            
                            Logger.info("Unable to load last episode: {}. Creating new map instead.", lastSession.getLastEpisodeFile().getAbsolutePath());
                        }
                    }
                    
                    case StartupBehavior.LOAD_LAST_MAP -> {
                        if (lastSession.getLastLevelFile().exists()) {
                            loadMap(lastSession.getLastLevelFile());
                            
                            mapPanelView.getViewport().setViewPosition(new Point(lastSession.getLastViewportX(), lastSession.getLastViewportY()));
                            
                            Logger.info("Loaded last level: {}", lastSession.getLastLevelFile().getAbsolutePath());
                        } else {
                            newMap();
                            
                            Logger.info("Unable to load last level: {}. Creating new map instead.", lastSession.getLastLevelFile().getAbsolutePath());
                        }
                    }
                }
            } catch (IOException e) {
                Logger.info(e, "Unable to load last session file. Creating new map.");
                
                newMap();
            }
        } else {
            Logger.info("No last session found. Creating new map.");
            
            newMap();
        }
    }
    
    private void setupComponents() {
        tilesetPanel = new TilesetPanel(this);
        mapPanel = new MapPanel();
        mapPanelView = new MapPanelView(mapPanel);
        
        mainToolBar = new MainToolBar(this);
        
        spritesPanel = new SpritesPanel(this);
        mapMetadataPanel = new MapMetadataPanel(this);
        
        miniMapPanel = new MiniMapPanel();
        
        statusbar = new Statusbar(this);
    
        toolPropertiesPanel = new ToolPropertiesPanel();
    }
    
    private void registerMapConsumers() {
        model.addMapConsumer(spritesPanel);
        model.addMapConsumer(miniMapPanel);
        model.addMapConsumer(mapPanel);
        model.addMapConsumer(tilesetPanel);
        model.addMapConsumer(mapMetadataPanel);
    }
    
    private void registerRepaintListeners() {
        model.addRepaintListener(mapPanel);
        model.addRepaintListener(tilesetPanel);
    }
    
    private void registerChangeListeners() {
        // TODO Move this into the correct method
        view.setWindowListener(new MainUIWindowListener(this));
        
        spritesPanel.setChangeListener(this);
        mapMetadataPanel.setChangeListener(this);
        
        Tool.setToolInformationListener(statusbar);
    }
    
    private void registerPropertyListeners() {
        model.addPropertyChangeListener(mainToolBar);
    }
    
    public void updateRepaintListeners() {
        model.getRepaintListeners().forEach(RepaintListener::doRepaint);
    }
    
    private void registerTools() {
        Tools.addTool(BrushTool.class);
        Tools.addTool(LineTool.class);
        Tools.addTool(RectangleTool.class);
        Tools.addTool(EraserTool.class);
        Tools.addTool(FloodFillTool.class);
        Tools.addTool(CutTool.class);
    }
    
    /*
        * Map related methods
     */
    public void loadMap(PK2Map map, File mapFile) {
        

        

        String tilesetName = null;
        String backgroundName = null;

        if(mapFile!=null&&Settings.getDllPath()!=null){

            File episode = mapFile.getParentFile();
            String episodeName = episode.getAbsolutePath();
            
            tilesetName = PekkaKana2.findAsset(episodeName + File.separatorChar + map.getTileset(), "gfx"+File.separatorChar+"tiles");
            backgroundName = PekkaKana2.findAsset(episodeName + File.separatorChar + map.getBackground(), "gfx"+File.separatorChar+"scenery");

            if(tilesetName!=null && !new File(tilesetName).isAbsolute()){
                tilesetName = Settings.getBasePath() +File.separatorChar + tilesetName;
            }

            if(backgroundName!=null && !new File(backgroundName).isAbsolute()){
                backgroundName = Settings.getBasePath() +File.separatorChar + backgroundName;
            }

        }
        else{
            tilesetName = Settings.getTilesetPath() + map.getTileset();
            backgroundName = Settings.getBackgroundsPath() + map.getBackground();
        }

        BufferedImage tilesetImage = null;

        if(tilesetName!=null){
            try {
                tilesetImage = ImageIO.read(new File(tilesetName));
            } catch (IOException e) {
                System.out.println(tilesetName);
                Logger.error(e, "Unable to load tileset image.");
            }
        }

        if(tilesetImage==null){             
            JOptionPane.showMessageDialog(null, "Unable to load tileset image file. File: '" + map.getTileset() + "'", "Unable to find tileset", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        BufferedImage backgroundImage = null;
        if(backgroundName!=null){
            try {
                backgroundImage = ImageIO.read(new File(backgroundName));
            } catch (IOException e) {
                System.out.println(backgroundName);
                Logger.error(e, "Unable to load background image.");
            }
        }

        if(backgroundImage==null){
            JOptionPane.showMessageDialog(null, "Unable to load background image file. File: '" + map.getBackground() + "'", "Unable to find background", JOptionPane.ERROR_MESSAGE);
            return;
        }
  
        tilesetImage = GFXUtils.setPaletteToBackgrounds(tilesetImage, backgroundImage);    
        if (Settings.useBGTileset() && backgroundImage != null) {
            // Check if the tileset has a _bg version, if it does load and set it to the background tileset.
            BufferedImage bgTilesetImage = null;
            var bgTilesetFileStr = PathUtils.getTilesetAsBackgroundTileset(Settings.getTilesetPath() + map.getTileset());
        
            if (Files.exists(Path.of(bgTilesetFileStr))) {
                try {
                    bgTilesetImage = ImageIO.read(new File(bgTilesetFileStr));
                    
                    bgTilesetImage = GFXUtils.setPaletteToBackgrounds(bgTilesetImage, backgroundImage); // TODO Does the game do this?
    
                    map.setBackgroundTilesetImage(bgTilesetImage);
                } catch (IOException e) {
                    Logger.warn("Unable to load background tileset image.");
                    
                    JOptionPane.showMessageDialog(null, "Unable to load background tileset image.");
                }
            }
        }
    
        Tool.reset();
        setSelectedTool(Tools.getTool(BrushTool.class));
        
        map.setBackgroundImage(backgroundImage);
        map.setTilesetImage(tilesetImage);
    
        map.setChangeListener(this);
    
        model.setCurrentMap(map);

        miniMapPanel.setMap(map);

        spritesPanel.setMap(map);

        Tool.setMap(map);
    
        updateMapHolders();
    }
    
    public void loadMap(File file) {
        var r = (PK2MapReader13) MapIO.getReader(file);
        
        PK2Map13 map = null;
        try {
            Logger.info("Trying to load map file: {}", file.getAbsolutePath());
            
            map = r.load(file);
            
            if (map != null) {
                loadMap(map, file);

                Logger.info("Map loaded successfully.");

                if (map.getBackgroundImage() != null) {
                    map.setSpriteList(r.loadSpriteList(map.getSpriteFilenames(), map.getBackgroundImage(), map.getPlayerSpriteId(), file));
                    SpriteUtils.calculatePlacementAmountForSprites(map.getSpritesLayer(), map.getSpriteList());

                    spritesPanel.setMap(map);
                }
                
                model.setCurrentMapFile(file);
                autosaveManager.setFile(model.getCurrentMapFile());
                
                unsavedChanges = false;
    
                updateFrameTitle();
            } else {
                JOptionPane.showMessageDialog(null, "'" + file.getName() + "' doesn't seem to be a 1.3 Pekka Kana 2 map file.", "Unable to recognize file", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            Logger.warn(e, "Unable to load map file {}.", file.getAbsolutePath());
        }
    }
    
    public void saveMap() {
        // If the file has not been saved yet, ask the user to give it a name and location
        if (model.getCurrentMapFile() == null) {
            var fc = new JFileChooser(Settings.getEpisodesPath());
            fc.setDialogTitle("Save map...");
        
            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                var file = fc.getSelectedFile();
            
                if (!file.getName().endsWith(".map")) file = new File(file.getPath() + ".map");
                
                model.setCurrentMapFile(file);
                autosaveManager.setFile(model.getCurrentMapFile());
            }
        }
        
        saveMap(model.getCurrentMapFile());
    }
    
    public void saveMap(File file) {
        if (file != null) {
            mapMetadataPanel.commitSpinnerValues();
            
            try {
                if (!file.getName().endsWith(".map")) file = new File(file.getPath() + ".map");
                
                var writer = MapIO.getWriter();
                writer.write(model.getCurrentMap(), file);
    
                model.setCurrentMapFile(file);
                
                unsavedChanges = false;
            } catch (IOException e) {
                Logger.warn(e, "Unable to save map file {}.", model.getCurrentMapFile().getAbsolutePath());
            }
    
            statusbar.setLastChangedTime(LocalTime.now());
            updateFrameTitle();
        }
    }
    
    public void newMap() {
        Tool.reset();
        setSelectedTool(Tools.getTool(BrushTool.class));
        
        var map = new PK2Map13();
        map.reset();
    
        loadMap(map, null);
        
        model.setCurrentMapFile(null);
        autosaveManager.setFile(null);
        unsavedChanges = false;
        
        tilesetPanel.resetSelection();
        
        mapPanelView.getViewport().setViewPosition(new Point(0, 0));
        
        if (episodeManager.hasEpisodeLoaded()) {
            var jopAddToEpisode = JOptionPane.showConfirmDialog(null, "Add file to episode \"" + episodeManager.getEpisode().getEpisodeName() + "\"?", "Add to episode?", JOptionPane.YES_NO_OPTION);
            
            if (jopAddToEpisode == JOptionPane.YES_OPTION) {
                var fc = new JFileChooser(episodeManager.getEpisode().getEpisodeFolder());
                fc.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.getName().endsWith(".map");
                    }
    
                    @Override
                    public String getDescription() {
                        return "Pekka Kana 2 map file (*.map)";
                    }
                }); // TODO Create PK2MapFilterFilter?
                
                // TODO Create a PK2MapFileChooser?
                fc.setDialogTitle("Save map file as...");
                
                if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    var selectedFile = fc.getSelectedFile();
                    
                    if (!selectedFile.getName().endsWith(".map")) {
                        selectedFile = new File(selectedFile.getAbsolutePath() + ".map");
                    }
                    
                    episodeManager.addFileToEpisode(selectedFile);
                    
                    model.setCurrentMapFile(selectedFile);
                    
                    saveMap(selectedFile);
                } else {
                    JOptionPane.showMessageDialog(null, "File has not been added to episode.", "Not added to episode", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        
        updateFrameTitle();
    }
    
    public void setLayer(int layer) {
        Tool.setSelectedLayer(layer);
        
        mainToolBar.setSelectedLayer(layer);

        if (Settings.useBGTileset()) {
            switch (layer) {
                case Layer.BACKGROUND -> tilesetPanel.useBackgroundTileset(true);
                case Layer.FOREGROUND, Layer.BOTH -> tilesetPanel.useBackgroundTileset(false);
            }
        }
        
        tilesetPanel.repaint();
        
        mapPanel.getModel().setSelectedLayer(layer);
        mapPanel.repaint(); // TODO Optimize: Only repaint viewport
    }
    
    /*
        * Installing keyboard shortcuts
     */
    
    // Should probably add these to view.getFrame().getRootPane() or whatever
    public void installKeyboardShortcuts() {
        //Settings.resetKeyboardShortcuts();
        mapPanel.resetKeyboardActions();

        ShortcutUtils.install(mapPanel, Shortcuts.UNDO_ACTION, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Tool.getUndoManager().undoLastAction();
            
                mapPanel.repaint(); // TODO Repaint only affected areas?
            }
        });
    
        ShortcutUtils.install(mapPanel, Shortcuts.REDO_ACTION, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Tool.getUndoManager().redoLastAction();
            
                mapPanel.repaint(); // TODO Repaint only affected areas?
            }
        });
        
        ShortcutUtils.install(mapPanel, Shortcuts.SAVE_FILE_ACTION, new SaveMapAction(this));
        ShortcutUtils.install(mapPanel, Shortcuts.OPEN_FILE_ACTION, new OpenMapAction(this));
        ShortcutUtils.install(mapPanel, Shortcuts.TEST_MAP_ACTION, new PlayMapAction(this));
        
        ShortcutUtils.install(mapPanel, Shortcuts.SELECT_BOTH_LAYER_ACTION, new SwitchLayerAction(this, Layer.BOTH));
        ShortcutUtils.install(mapPanel, Shortcuts.SELECT_FOREGROUND_LAYER_ACTION, new SwitchLayerAction(this, Layer.FOREGROUND));
        ShortcutUtils.install(mapPanel, Shortcuts.SELECT_BACKGROUND_LAYER_ACTION, new SwitchLayerAction(this, Layer.BACKGROUND));
        
        ShortcutUtils.install(mapPanel, Shortcuts.SELECT_TILE_MODE, new SwitchModeAction(this, Tool.MODE_TILE));
        ShortcutUtils.install(mapPanel, Shortcuts.SELECT_SPRITE_MODE, new SwitchModeAction(this, Tool.MODE_SPRITE));
        
        ShortcutUtils.install(mapPanel, Shortcuts.TOOL_BRUSH, new SetSelectedToolAction(this, Tools.getTool(BrushTool.class)));
        ShortcutUtils.install(mapPanel, Shortcuts.TOOL_ERASER, new SetSelectedToolAction(this, Tools.getTool(EraserTool.class)));
        ShortcutUtils.install(mapPanel, Shortcuts.TOOL_LINE, new SetSelectedToolAction(this, Tools.getTool(LineTool.class)));
        ShortcutUtils.install(mapPanel, Shortcuts.TOOL_RECT, new SetSelectedToolAction(this, Tools.getTool(RectangleTool.class)));
        ShortcutUtils.install(mapPanel, Shortcuts.TOOL_CUT, new SetSelectedToolAction(this, Tools.getTool(CutTool.class)));
        ShortcutUtils.install(mapPanel, Shortcuts.TOOL_FLOOD_FILL, new SetSelectedToolAction(this, Tools.getTool(FloodFillTool.class)));
    }
    
    private void updateMapHolders() {
        for (var m : model.getMapConsumers()) {
            m.setMap(model.getCurrentMap());
        }
    }
    
    public void repaintView() {
        mapPanel.repaint();
    }
    
    public boolean unsavedChangesPresent() {
        return unsavedChanges;
    }
    
    /**
     * This method gets called when the whole application shuts down.
     */
    public void close() {
        sessionManager.saveSession(new File(EditorConstants.LAST_SESSION_FILE), episodeManager.getEpisodeFile(), model.getCurrentMapFile(), mapPanelView.getViewport().getViewPosition().x, mapPanelView.getViewport().getViewPosition().y);
        
        System.exit(0);
    }
    
    public File getCurrentFile() {
        return model.getCurrentMapFile();
    }
    
    public void setCurrentFile(File file) {
        model.setCurrentMapFile(file);
    }
    
    private void updateFrameTitle() {
        var sb = new StringBuilder();
        
        if (episodeManager.hasEpisodeLoaded()) {
            sb.append(episodeManager.getEpisode().getEpisodeName());
            sb.append(" - ");
        }
        
        if (model.getCurrentMapFile() != null) {
            if (episodeManager.hasEpisodeLoaded()) {
                sb.append(model.getCurrentMapFile().getName());
            } else {
                sb.append(model.getCurrentMapFile().getParentFile().getName()).append(File.separator).append(model.getCurrentMapFile().getName());
            }
        } else {
            sb.append("Unnamed");
        }

        if (unsavedChanges) {
            sb.append("*");
        }
        
        sb.append(" - PekaEDS ");
        sb.append(PekaEDSVersion.VERSION_STRING);
        
        view.setFrameTitle(sb.toString());
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        if (!unsavedChanges) {
            unsavedChanges = true;
            
            updateFrameTitle();
        }
    }
    
    private Tool currentTool = null;
    public void setSelectedTool(Tool selectedTool, boolean ignorePrompts) {
        if (currentTool != selectedTool) {
            if (currentTool != null) currentTool.onDeselect(ignorePrompts);
            Tool.setMode(Tool.MODE_TILE);
            
            mapPanel.setLeftMouseTool(selectedTool);
    
            toolPropertiesPanel.setSelectedTool(selectedTool);
    
            ((ToolsToolBar) view.getToolsToolBar()).setSelectedTool(selectedTool);
            
            currentTool = selectedTool;
            currentTool.onSelect();
        }
    }
    
    public void setSelectedTool(Tool selectedTool) {
        setSelectedTool(selectedTool, true);
    }
    
    /*
        Getters for components that get added to the frame in PekaEDSGUIView
     */
    
    TilesetPanel getTilesetPanel() {
        return tilesetPanel;
    }
    
    MapPanel getMapPanel() {
        return mapPanel;
    }
    
    MainToolBar getMainToolBar() {
        return mainToolBar;
    }
    
    SpritesPanel getSpritesPanel() {
        return spritesPanel;
    }
    
    MiniMapPanel getMiniMapPanel() {
        return miniMapPanel;
    }
    
    MapMetadataPanel getMapMetadataPanel() {
        return mapMetadataPanel;
    }
    
    Statusbar getStatusbar() { return statusbar; }
    
    ToolPropertiesPanel getToolPropertiesPanel() {
        return toolPropertiesPanel;
    }
    
    public EpisodeManager getEpisodeManager() {
        return episodeManager;
    }
    
    public void updateAutosaveManager() {
        autosaveManager.setInterval(Settings.getAutosaveInterval());
        autosaveManager.setFileCount(Settings.getAutosaveFileCount());
    }
    
    public void updateMapProfileData() {
        mapMetadataPanel.updateMapProfileData();
    }
    
    public MapPanelView getMapPanelView() {
        return mapPanelView;
    }
}
