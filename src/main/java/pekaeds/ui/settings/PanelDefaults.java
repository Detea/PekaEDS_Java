package pekaeds.ui.settings;

import net.miginfocom.swing.MigLayout;
import pekaeds.filechooser.ImagePreviewFileChooser;
import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.settings.Settings;
import pekaeds.ui.actions.BrowseResourceFilesAction;
import pekaeds.ui.filefilters.MusicFilter;

import javax.swing.*;

public class PanelDefaults extends JPanel {
    private JLabel lblTileset;
    private JTextField tfTileset;
    private JButton btnTilesetBrowse;
    
    private JLabel lblBackground;
    private JTextField tfBackground;
    private JButton btnBackgroundBrowse;
    
    private JLabel lblAuthor;
    private JTextField tfAuthor;
    
    private JLabel lblMapName;
    private JTextField tfMapName;
    
    private JLabel lblMusic;
    private JTextField tfMusic;
    private JButton btnMusicBrowse;
    
    public PanelDefaults() {
        setup();
    }
    
    private void setup() {
        lblTileset = new JLabel("Tileset:");
        tfTileset = new JTextField(Settings.getDefaultTileset());
        btnTilesetBrowse = new JButton("Browse");

        lblBackground = new JLabel("Background:");
        tfBackground = new JTextField(Settings.getDefaultBackground());
        btnBackgroundBrowse = new JButton("Browse");
        
        lblMusic = new JLabel("Music:");
        tfMusic = new JTextField(Settings.getDefaultMusic());
        btnMusicBrowse = new JButton("Browse");
        
        lblMapName = new JLabel("Map name:");
        tfMapName = new JTextField(Settings.getDefaultMapName());
        
        lblAuthor = new JLabel("Author:");
        tfAuthor = new JTextField(Settings.getDefaultAuthor());
        
        SwingUtilities.invokeLater(this::setActionListeners);
        
        setLayout(new MigLayout());

        setBorder(BorderFactory.createTitledBorder("Set default map values"));
        
        add(lblTileset, "cell 0 0");
        add(tfTileset, "cell 1 0, width 150px");
        add(btnTilesetBrowse, "cell 2 0");
    
        add(lblBackground, "cell 0 1");
        add(tfBackground, "cell 1 1, width 150px");
        add(btnBackgroundBrowse, "cell 2 1");
    
        add(lblMusic, "cell 0 2");
        add(tfMusic, "cell 1 2, width 150px");
        add(btnMusicBrowse, "cell 2 2");
        
        add(lblMapName, "cell 0 3");
        add(tfMapName, "cell 1 3, width 150px");
        
        add(lblAuthor, "cell 0 4");
        add(tfAuthor, "cell 1 4, width 150px");
    }
    
    // TODO There is a big performance problem with BrowseResourceFileAction or ImagePreviewFileChooser
    private void setActionListeners() {
        btnTilesetBrowse.addActionListener(new BrowseResourceFilesAction(tfTileset, new ImagePreviewFileChooser(PK2FileSystem.INSTANCE.getAssetsPath(PK2FileSystem.TILESET_DIR),
            ImagePreviewFileChooser.PREVIEW_TILESET)));
        
        btnBackgroundBrowse.addActionListener(new BrowseResourceFilesAction(tfBackground, new ImagePreviewFileChooser(PK2FileSystem.INSTANCE.getAssetsPath(PK2FileSystem.SCENERY_DIR), 
            ImagePreviewFileChooser.PREVIEW_BACKGROUND)));
        btnMusicBrowse.addActionListener(new BrowseResourceFilesAction(tfMusic, new MusicFilter(), PK2FileSystem.INSTANCE.getAssetsPath(PK2FileSystem.MUSIC_DIR)));
    }
    
    void resetValues() {
        tfTileset.setText(Settings.getDefaultTileset());
        tfBackground.setText(Settings.getDefaultBackground());
        tfMusic.setText(Settings.getDefaultMusic());
        tfMapName.setText(Settings.getDefaultMapName());
        tfAuthor.setText(Settings.getDefaultAuthor());
    }
    
    public String getTileset() {
        return tfTileset.getText();
    }
    
    public String getDefaultBackground() {
        return tfBackground.getText();
    }
    
    public String getMusic() {
        return tfMusic.getText();
    }
    
    public String getAuthor() {
        return tfAuthor.getText();
    }
    
    public String getMapName() {
        return tfMapName.getText();
    }
}
