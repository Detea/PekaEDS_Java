package pekaeds.ui.main;

import java.util.List;

import net.miginfocom.swing.MigLayout;
import pekaeds.data.PekaEDSVersion;
import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.ui.actions.NewLevelAction;
import pekaeds.ui.actions.OpenFolderAction;
import pekaeds.ui.actions.OpenLevelAction;
import pekaeds.ui.actions.SaveLevelAction;
import pekaeds.ui.episodepanel.EpisodePanel;
import pekaeds.ui.listeners.MainUIWindowListener;
import pekaeds.ui.settings.SettingsDialog;
import pekaeds.ui.toolpropertiespanel.ToolPropertiesPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PekaEDSGUIView {
    private JFrame frame;
    private JMenuBar menuBar;
    
    private JMenu mFile;
    private JMenuItem mFileNew;
    private JMenuItem mFileOpen;

    private JMenuItem mFileSave;
    private JMenuItem mFileSaveAs;
    private JMenuItem mFileExit;
    
    private JMenu mEpisode;
    private JMenuItem mEpisodeNew;
    private JMenuItem mEpisodeOpen;
    private JMenuItem mEpisodeExport;

    //private JMenu mFolders;
    private JMenuItem mfBase;
    private JMenuItem mfEpisodes;
    private JMenuItem mfTilesets;
    private JMenuItem mfBackgrounds;
    private JMenuItem mfSprites;
    private JMenuItem mfMusic;
    //private JMenuItem mfSounds;

    private JMenu mOther;
    private JMenuItem mOtherSettings;
    private JMenuItem mOtherAbout;


    private JMenu mOpenRecent;
    private JMenuItem mClearRecentlyOpened;

    private JTabbedPane tabbedPane;
    
    private PekaEDSGUI edsUI;
   
    private JToolBar toolsToolBar;
    
    private SettingsDialog settingsDialog = null;
   
    private ToolPropertiesPanel toolPropertiesPanel;
    
    public PekaEDSGUIView(PekaEDSGUI gui) {
        this.edsUI = gui;
    }
    
    void setupMainUI() {
        frame = new JFrame("PekaEDS");
        
        var mainToolBar = edsUI.getMainToolBar();
        
        var tilesetPanel = edsUI.getTilesetPanel();

        var spritesPanel = edsUI.getSpritesPanel();
        var mapMetadataPanel = edsUI.getLevelMetadataPanel();
        
        var miniMapPanel = edsUI.getMiniMapPanel();

        var mapView = edsUI.getMapPanelView();
        miniMapPanel.setViewport(mapView.getViewport());
        
        JScrollPane spLevelMetaDataPanel = new JScrollPane(mapMetadataPanel);
        JScrollPane spSectorMetaDataPanel = new JScrollPane(edsUI.getSectorMetadataPanel());
        
        EpisodePanel episodesPanel = new EpisodePanel(edsUI, edsUI.getEpisodeManager());
        
        tabbedPane = new JTabbedPane();
        tabbedPane.add("Sector", spSectorMetaDataPanel);
        tabbedPane.add("Level", spLevelMetaDataPanel);
        tabbedPane.add("Sprites", spritesPanel);
        tabbedPane.add("Episode", episodesPanel);

        
        frame.add(mainToolBar, BorderLayout.PAGE_START);
        
        var panelMiniMap = new JPanel();
        panelMiniMap.add(miniMapPanel, BorderLayout.CENTER);
        
        var sp = new JPanel();
        sp.setLayout(new MigLayout());
        sp.add(tabbedPane, "dock center");
        sp.add(panelMiniMap, "dock south");
    
        toolPropertiesPanel = edsUI.getToolPropertiesPanel();
        
        var tilesetAndToolPropPanel = new JPanel();
        var tsetScrollPane = new JScrollPane(tilesetPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        tilesetAndToolPropPanel.setLayout(new MigLayout("", "fill"));
        tilesetAndToolPropPanel.add(tsetScrollPane, "cell 0 0");
        tilesetAndToolPropPanel.add(toolPropertiesPanel, "cell 0 1");
        
        menuBar = new JMenuBar();
        mFile = new JMenu("File");
        mFileNew = new JMenuItem("New");
        mFileOpen = new JMenuItem("Open");


        mOpenRecent = new JMenu("Open Recent");
        mClearRecentlyOpened = new JMenuItem("Clear Recently Opened...");

        mClearRecentlyOpened.addActionListener(l->{
            mOpenRecent.removeAll();
            mOpenRecent.add(mClearRecentlyOpened);
            mClearRecentlyOpened.setEnabled(false);
        });
        
        mOpenRecent.add(mClearRecentlyOpened);
        mClearRecentlyOpened.setEnabled(false);


        mFileSave = new JMenuItem("Save");
        mFileSaveAs = new JMenuItem("Save as...");
        mFileExit = new JMenuItem("Exit");
        
        mFile.add(mFileNew);
        mFile.add(mFileOpen);
        mFile.add(mOpenRecent);
        mFile.addSeparator();
        mFile.add(mFileSave);
        mFile.add(mFileSaveAs);
        mFile.addSeparator();
        mFile.add(mFileExit);
        
        mEpisode = new JMenu("Episode");
        mEpisodeNew = new JMenuItem("New");
        mEpisodeOpen = new JMenuItem("Open");
        mEpisodeExport = new JMenuItem("Export");
        
        mEpisode.add(mEpisodeNew);
        mEpisode.add(mEpisodeOpen);
        mEpisode.addSeparator();
        mEpisode.add(mEpisodeExport);
        
        mOther = new JMenu("Other");
        mOtherSettings = new JMenuItem("Settings");
        mOtherAbout = new JMenuItem("About");
        mOtherAbout.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, PekaEDSVersion.VERSION_STRING, "PekaEDS", JOptionPane.INFORMATION_MESSAGE);
        });
        
        mOther.add(mOtherSettings);
        mOther.add(mOtherAbout);

        JMenu mFolders = new JMenu("Folders");
        mfBase = new JMenuItem("Base");
        mfEpisodes = new JMenuItem("Episodes");
        mfTilesets = new JMenuItem("Tilesets");
        mfBackgrounds = new JMenuItem("Backgrounds");
        mfSprites = new JMenuItem("Sprites");
        mfMusic = new JMenuItem("Music");

        mFolders.add(mfBase);
        mFolders.add(mfEpisodes);
        mFolders.add(mfTilesets);
        mFolders.add(mfBackgrounds);
        mFolders.add(mfSprites);
        mFolders.add(mfMusic);

        menuBar.add(mFile);
        menuBar.add(mEpisode);
        menuBar.add(mFolders);
        menuBar.add(mOther);

        toolsToolBar = new ToolsToolBar(edsUI);
        
        setActionListeners();
        
        var tilesetAndToolsPanel = new JPanel();
        tilesetAndToolsPanel.setLayout(new MigLayout());
        tilesetAndToolsPanel.add(toolsToolBar, "dock west");
        tilesetAndToolsPanel.add(tilesetAndToolPropPanel, "dock center");
        
        frame.setJMenuBar(menuBar);
        
        frame.add(tilesetAndToolsPanel, BorderLayout.WEST);
        frame.add(mapView, BorderLayout.CENTER);
        frame.add(sp, BorderLayout.EAST);
        frame.add(edsUI.getStatusbar(), BorderLayout.SOUTH);
        
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(new Dimension(1280, 720));
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        
        setFrameIcon();
    }

    public void setupOpenRecentMenu(List<File> files){
        this.mOpenRecent.removeAll();

        boolean flag = false;
        for(File f:files){
            if(f!=null && f.exists() && !f.isDirectory()){

                flag = true;
                String name = f.getPath();

                String assetsPath = PK2FileSystem.getAssetsPath(PK2FileSystem.EPISODES_DIR).getPath();
                
                if(name.startsWith(assetsPath)){
                    
                    String episodeName = f.getParentFile().getName();
                    name = episodeName + "/"+f.getName();
                }

                JMenuItem item = new JMenuItem(name);
                item.addActionListener(e->{
                    edsUI.loadLevelSafe(f);
                });

                this.mOpenRecent.add(item);
            }
        }

        this.mOpenRecent.addSeparator();
        this.mOpenRecent.add(mClearRecentlyOpened);
        this.mClearRecentlyOpened.setEnabled(flag);
    }
    
    private void setFrameIcon() {
        // Not sure if this should be logged, it shouldn't fail. But if it does the program will not function correctly so just check and be safe.
        BufferedImage iconImg = null;
        try {
            var iconResource = getClass().getClassLoader().getResourceAsStream("levelEditorIcon.png");
        
            if (iconResource != null) iconImg = ImageIO.read(iconResource);
        } catch (IOException e) {
            System.out.println("unable to load icon");
        }
    
        if (iconImg != null) frame.setIconImage(iconImg);
    }
    
    private void setActionListeners() {
        mFileNew.addActionListener(new NewLevelAction(edsUI));
        mFileOpen.addActionListener(new OpenLevelAction(edsUI));
        mFileSave.addActionListener(new SaveLevelAction(edsUI));
        
        mFileSaveAs.addActionListener(e -> {
            var fc = new JFileChooser("Save as...");
            if (edsUI.getCurrentFile() != null) fc.setCurrentDirectory(edsUI.getCurrentFile().getParentFile());
            
            fc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".map");
                }
    
                @Override
                public String getDescription() {
                    return "Pekka Kana 2 map file (*.map)";
                }
            });
            
            if (fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                edsUI.setCurrentFile(fc.getSelectedFile());
                
                edsUI.saveLevel();
            }
        });
        
        mOtherSettings.addActionListener(e -> {
            if (settingsDialog == null) {
                settingsDialog = new SettingsDialog(edsUI);
            }
            
            settingsDialog.setVisible(true);
        });
        
        mFileExit.addActionListener(e -> System.exit(0));
        
        mEpisodeNew.addActionListener(e -> {
            var fc = new JFileChooser(PK2FileSystem.getAssetsPath(PK2FileSystem.EPISODES_DIR));
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setDialogTitle("Select an episode folder to add...");
            
            if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                edsUI.getEpisodeManager().newEpisode(fc.getSelectedFile());
                
                tabbedPane.setSelectedIndex(2);
            }
        });
        
        mEpisodeOpen.addActionListener(e -> {
            var fc = new JFileChooser("episodes");
            fc.setDialogTitle("Select an episode file to load...");
            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getName().endsWith(".episode");
                }
    
                @Override
                public String getDescription() {
                    return "PekaEDS episode file (*.episode)";
                }
            });
            
            if (fc.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                edsUI.getEpisodeManager().loadEpisode(fc.getSelectedFile());
                
                tabbedPane.setSelectedIndex(2);
            }
        });

        mfBase.addActionListener(new OpenFolderAction(PK2FileSystem.getAssetsPath()));

        mfEpisodes.addActionListener(new OpenFolderAction(PK2FileSystem.getAssetsPath(PK2FileSystem.EPISODES_DIR)));

        mfTilesets.addActionListener(new OpenFolderAction(PK2FileSystem.getAssetsPath(PK2FileSystem.TILESET_DIR)));

        mfBackgrounds.addActionListener(new OpenFolderAction(PK2FileSystem.getAssetsPath(PK2FileSystem.SCENERY_DIR)));

        mfSprites.addActionListener(new OpenFolderAction(PK2FileSystem.getAssetsPath(PK2FileSystem.SPRITES_DIR)));

        mfMusic.addActionListener(new OpenFolderAction(PK2FileSystem.getAssetsPath(PK2FileSystem.MUSIC_DIR)));
    }
    
    void setFrameTitle(String title) {
        frame.setTitle(title);
    }
    
    public void setWindowListener(MainUIWindowListener mainUIWindowListener) {
        frame.addWindowListener(mainUIWindowListener);
    }
    
    JToolBar getToolsToolBar() {
        return toolsToolBar;
    }
}
