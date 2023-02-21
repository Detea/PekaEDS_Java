package pk.pekaeds.ui.main;

import pk.pekaeds.data.Layer;
import pk.pekaeds.data.PekaEDSVersion;
import pk.pekaeds.pk2.map.*;
import pk.pekaeds.settings.Shortcuts;
import pk.pekaeds.tools.*;
import pk.pekaeds.ui.actions.*;
import pk.pekaeds.ui.listeners.MainUIWindowListener;
import pk.pekaeds.ui.mappanel.MapPanelBackground;
import pk.pekaeds.ui.toolpropertiespanel.ToolPropertiesPanel;
import pk.pekaeds.util.*;
import pk.pekaeds.settings.Settings;
import pk.pekaeds.ui.mapmetadatapanel.MapMetadataPanel;
import pk.pekaeds.ui.mappanel.MapPanel;
import pk.pekaeds.ui.minimappanel.MiniMapPanel;
import pk.pekaeds.ui.listeners.RepaintListener;
import pk.pekaeds.ui.spritelistpanel.SpritesPanel;
import pk.pekaeds.ui.tilesetpanel.TilesetPanel;
import pk.pekaeds.util.undoredo.UndoManager;


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
    private MapPanelBackground mapPanelBackground;
    private MapPanel mapPanel;
    
    private MainToolBar mainToolBar;
    
    private SpritesPanel spritesPanel;
    private MapMetadataPanel mapMetadataPanel;
    
    private MiniMapPanel miniMapPanel;
    
    private Statusbar statusbar;
    
    private ToolPropertiesPanel toolPropertiesPanel;
    
    private boolean unsavedChanges = false;
    
    private final AutoSaveManager autosaveManager;
    
    public PekaEDSGUI() {
        // This has to be done before PekaEDSGUIView gets initialized, because it relies on the toolsList in the Tools class.
        registerTools();
        
        view = new PekaEDSGUIView(this);
        model = new PekaEDSGUIModel();
    
        setupComponents();
        registerMapConsumers();
        registerRepaintListeners();
        registerPropertyListeners();
        
        Tool.setToolModeListener(mainToolBar);
        
        // This has to be done after setupComponents(), because View uses the components initialized in this method
        view.setupMainUI();
        
        registerChangeListeners();
        installKeyboardShortcuts();
        
        mapPanel.setLeftMouseTool(new BrushTool());
        mapPanel.setRightMouseTool(new SelectionTool());
        
        autosaveManager = new AutoSaveManager(this, model.getCurrentMapFile());
        autosaveManager.start();
        
        // TODO Do startup behavior saved in Settings
        newMap();
    }
    
    private void setupComponents() {
        tilesetPanel = new TilesetPanel(this);
        mapPanelBackground = new MapPanelBackground();
        mapPanel = new MapPanel();
        
        mainToolBar = new MainToolBar(this);
        
        spritesPanel = new SpritesPanel(this);
        mapMetadataPanel = new MapMetadataPanel(this);
        
        miniMapPanel = new MiniMapPanel();
        mapPanel.getModel().addPropertyChangeListener(miniMapPanel);
        
        statusbar = new Statusbar();
    
        toolPropertiesPanel = new ToolPropertiesPanel();
    }
    
    private void registerMapConsumers() {
        model.addMapConsumer(spritesPanel);
        model.addMapConsumer(miniMapPanel);
        model.addMapConsumer(mapPanelBackground);
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
    }
    
    /*
        * Map related methods
     */
    public void loadMap(PK2Map map) {
        BufferedImage tilesetImage;
        
        try {
            tilesetImage = ImageIO.read(new File(Settings.getTilesetPath() + map.getTileset()));
        } catch (IOException e) {
            Logger.error(e, "Unable to load tileset image.");
            
            JOptionPane.showMessageDialog(null, "Unable to load tileset image file. File: '" + map.getTileset() + "'", "Unable to find tileset", JOptionPane.ERROR_MESSAGE);
            
            return;
        }
    
        BufferedImage backgroundImage;
        
        try {
            backgroundImage = ImageIO.read(new File(Settings.getBackgroundsPath() + map.getBackground()));
        } catch (IOException e) {
            Logger.error(e, "Unable to load background image.");
        
            JOptionPane.showMessageDialog(null, "Unable to load background image file. File: '" + map.getBackground() + "'", "Unable to find background", JOptionPane.ERROR_MESSAGE);
            
            return;
        }
  
        tilesetImage = GFXUtils.setPaletteToBackgrounds(tilesetImage, backgroundImage);
    
        if (Settings.useBGTileset()) {
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
    
        updateMapHolders();
    }
    
    public void loadMap(File file) {
        var r = (PK2MapReader13) MapIO.getReader(file);
        
        PK2Map13 map = null;
        try {
            map = r.load(file);
            
            if (map != null) {
                loadMap(map);
                model.setCurrentMapFile(file);
                autosaveManager.setFile(model.getCurrentMapFile());
    
                UndoManager.setMap(map);
    
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
                var writer = MapIO.getWriter();
        
                writer.write(model.getCurrentMap(), file);
        
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
    
    void setShowSprites(boolean show) {
        mapPanel.getModel().setShowSprites(show);
    }
    
    boolean shouldShowSprites() {
        return mapPanel.getModel().shouldShowSprites();
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
    
    public void close() {
        System.exit(0);
    }
    
    public File getCurrentFile() {
        return model.getCurrentMapFile();
    }
    
    public void setCurrentFile(File file) {
        model.setCurrentMapFile(file);
    }
    
    private void updateFrameTitle() {
        String titleString = "PekaEDS " + PekaEDSVersion.VERSION_STRING + " - ";
        
        if (model.getCurrentMapFile() != null) {
            titleString += model.getCurrentMapFile().getAbsolutePath();
        } else {
            titleString += "Unnamed";
        }
    
        if (unsavedChanges) {
            titleString += "*";
        }
        
        view.setFrameTitle(titleString);
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
    
    public void switchMode(int mode) {
        switch (mode) {
            case Tool.MODE_TILE -> {
                Tool.setMode(Tool.MODE_TILE);

                mainToolBar.setToolMode(Tool.MODE_TILE);
            }
            
            case Tool.MODE_SPRITE -> {
                Tool.setMode(Tool.MODE_SPRITE);
                
                mainToolBar.setToolMode(Tool.MODE_SPRITE);
            }
        }
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
    
    MapPanelBackground getMapPanelBackground() {
        return mapPanelBackground;
    }
    
    public void updateAutosaveManager() {
        autosaveManager.setInterval(Settings.getAutosaveInterval());
        autosaveManager.setFileCount(Settings.getAutosaveFileCount());
    }
}
