package pk.pekaeds.ui.main;

import pk.pekaeds.data.EditorConstants;
import pk.pekaeds.data.Layer;
import pk.pekaeds.data.PekaEDSVersion;
import pk.pekaeds.pk2.map.*;
import pk.pekaeds.settings.Shortcuts;
import pk.pekaeds.settings.StartupBehavior;
import pk.pekaeds.tool.*;
import pk.pekaeds.tool.tools.*;
import pk.pekaeds.ui.actions.*;
import pk.pekaeds.ui.listeners.MainUIWindowListener;
import pk.pekaeds.ui.mappanel.MapPanelView;
import pk.pekaeds.ui.toolpropertiespanel.ToolPropertiesPanel;
import pk.pekaeds.util.*;
import pk.pekaeds.settings.Settings;
import pk.pekaeds.ui.mapmetadatapanel.MapMetadataPanel;
import pk.pekaeds.ui.mappanel.MapPanel;
import pk.pekaeds.ui.minimappanel.MiniMapPanel;
import pk.pekaeds.ui.listeners.RepaintListener;
import pk.pekaeds.ui.spritelistpanel.SpritesPanel;
import pk.pekaeds.ui.tilesetpanel.TilesetPanel;
import pk.pekaeds.util.episodemanager.EpisodeManager;
import pk.pekaeds.util.file.AutoSaveManager;
import pk.pekaeds.util.file.LastSessionManager;
import pk.pekaeds.util.file.PathUtils;
import pk.pekaeds.util.undoredo.UndoManager;


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

public class PekaEDSGUI implements ChangeListener {
    private ChangeEvent changeEvent = new ChangeEvent(this);

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
    }
    
    /*
        * Map related methods
     */
    public void loadMap(PK2Map map) {
        BufferedImage tilesetImage = null;

        // TODO Handle filename case sensitivity on linux
        try {
            tilesetImage = ImageIO.read(new File(Settings.getTilesetPath() + map.getTileset()));
        } catch (IOException e) {
            Logger.error(e, "Unable to load tileset image.");
            
            JOptionPane.showMessageDialog(null, "Unable to load tileset image file. File: '" + map.getTileset() + "'", "Unable to find tileset", JOptionPane.ERROR_MESSAGE);
            
            return;
        }
    
        BufferedImage backgroundImage = null;
        
        try {
            backgroundImage = ImageIO.read(new File(Settings.getBackgroundsPath() + map.getBackground()));
        } catch (IOException e) {
            Logger.error(e, "Unable to load background image.");
        
            JOptionPane.showMessageDialog(null, "Unable to load background image file. File: '" + map.getBackground() + "'", "Unable to find background", JOptionPane.ERROR_MESSAGE);
            
            return;
        }
  
        if (tilesetImage != null && backgroundImage != null) {
            tilesetImage = GFXUtils.setPaletteToBackgrounds(tilesetImage, backgroundImage);
        }
    
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
    
        map.setBackgroundImage(backgroundImage);
        map.setTilesetImage(tilesetImage);
    
        map.setChangeListener(this);
    
        model.setCurrentMap(map);
    
        miniMapPanel.setMap(map);

        Tool.setMap(map);
    
        UndoManager.setMap(map);
    
        updateMapHolders();
    }
    
    public void loadMap(File file) {
        var r = (PK2MapReader13) MapIO.getReader(file);
        
        PK2Map13 map = null;
        try {
            Logger.info("Trying to load map file: {}", file.getAbsolutePath());
            
            map = r.load(file);
            
            if (map != null) {
                loadMap(map);

                Logger.info("Map loaded successfully.");
                
                if (map.getBackgroundImage() != null) {
                    map.setSpriteList(r.loadSpriteList(map.getSpriteFilenames(), map.getBackgroundImage(), map.getPlayerSpriteId()));
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
        var map = new PK2Map13();
        map.reset();
    
        loadMap(map);
        
        model.setCurrentMapFile(null);
        autosaveManager.setFile(null);
        unsavedChanges = false;
        
        tilesetPanel.resetSelection();
    
        Tool.setMode(Tool.MODE_TILE);
        Tool.setSelectionSize(1, 1);
        Tool.setSelection(new int[][]{{0}});
        
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
                UndoManager.undoLastAction();
            
                mapPanel.repaint(); // TODO Repaint only affected areas?
            }
        });
    
        ShortcutUtils.install(mapPanel, Shortcuts.REDO_ACTION, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UndoManager.redoLastAction();
            
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
                sb.append(model.getCurrentMapFile().getAbsolutePath());
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
    
    public void setSelectedTool(Tool selectedTool) {
        mapPanel.setLeftMouseTool(selectedTool);
        
        toolPropertiesPanel.setSelectedTool(selectedTool);
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
