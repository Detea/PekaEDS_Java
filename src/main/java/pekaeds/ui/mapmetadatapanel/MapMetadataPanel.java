package pekaeds.ui.mapmetadatapanel;

import net.miginfocom.swing.MigLayout;
import pekaeds.ui.listeners.TextFieldChangeListener;
import pekaeds.ui.mapposition.MapPositionDialog;
import pekaeds.util.GFXUtils;
import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.pk2.level.PK2Level;
import pekaeds.pk2.level.PK2LevelSector;
import pekaeds.settings.Settings;
import pekaeds.ui.filefilters.BMPImageFilter;
import pekaeds.ui.filefilters.MusicFilter;
import pekaeds.ui.main.PekaEDSGUI;
import pekaeds.ui.listeners.PK2LevelConsumer;
import pekaeds.ui.listeners.PK2SectorConsumer;
import pekaeds.filechooser.ImagePreviewFileChooser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.tinylog.Logger;

// TODO The ChangeListener stuff ist pretty messy. It works but it should probably be cleaned up. Some time... maybe...
public class MapMetadataPanel extends JPanel implements PK2LevelConsumer, PK2SectorConsumer {
    private ChangeListener changeListener;
    private ChangeEvent changeEvent = new ChangeEvent(this);
    private boolean canFireChanges = false;
    
    private JTextField tfMapName;
    private JTextField tfAuthor;
    private JTextField tfTileset;
    private JTextField tfBackground;
    private JTextField tfMusic;
    
    private JSpinner spLevelNumber;
    private JSpinner spTime;
    
    private JComboBox<String> cbWeather;
    private JComboBox<String> cbScrollingType;
    private JComboBox<String> cbIcons;

    private JSpinner spMapPosX;
    private JSpinner spMapPosY;
    
    private JButton btnPositionMap;
    private JButton btnBrowseTileset;
    private JButton btnBrowseBackground;
    private JButton btnBrowseMusic;
    
    private Map<Object, BufferedImage> iconMap = new HashMap<>();
    
    private MapPositionDialog mapPositionDialog;
    
    private PK2Level level = null;
    private PK2LevelSector sector = null;

    private PekaEDSGUI gui;
    
    public MapMetadataPanel(PekaEDSGUI ui) {
        loadIcons();
        
        mapPositionDialog = new MapPositionDialog(ui.getEpisodeManager());
        
        this.gui = ui;
        
        setupUI();
        setListeners();
    }
    
    // TODO Optimization: Put this in a SwingWorker?
    private void loadIcons() {
        try {
            var iconSheet = ImageIO.read( PK2FileSystem.getPK2StuffFile());
            iconSheet = GFXUtils.makeTransparent(iconSheet);
            
            for (int i = 0; i < Settings.getMapProfile().getIconNames().length; i++) {
                var img = iconSheet.getSubimage(1 + (i * 28), 452, 27, 27);
                
                iconMap.put(Settings.getMapProfile().getIconNames()[i], img);
            }
        } catch (IOException e) {
            Logger.warn(e, "Unable to load icon image file: {}", PK2FileSystem.getPK2StuffFile());
        }
    }
    
    private void setupUI() {
        var lblMapName = new JLabel("Name:");
        tfMapName = new JTextField();

        var lblAuthor = new JLabel("Author:");
        tfAuthor = new JTextField();
    
        var lblTileset = new JLabel("Tileset:");
        tfTileset = new JTextField();
    
        var lblBackground = new JLabel("Background:");
        tfBackground = new JTextField();
    
        var lblMusic = new JLabel("Music:");
        tfMusic = new JTextField();
    
        var lblLevelNr = new JLabel("Level nr.:");
        spLevelNumber = new JSpinner();
    
        var lblTime = new JLabel("Time (sec):");
        spTime = new JSpinner();
        
        var lblWeather = new JLabel("Weather:");
        cbWeather = new JComboBox<>();
    
        var lblScrolling = new JLabel("Scrolling:");
        cbScrollingType = new JComboBox<>();
    
        var lblIcon = new JLabel("Icon:");
        cbIcons = new JComboBox<>();
    
        var lblMapX = new JLabel("Map X:");
        spMapPosX = new JSpinner();
    
        var lblMapY = new JLabel("Map Y:");
        spMapPosY = new JSpinner();
        
        var scrollingModel = (DefaultComboBoxModel<String>) cbScrollingType.getModel();
        scrollingModel.addAll(Settings.getMapProfile().getScrollingTypes());
    
        var weatherModel = (DefaultComboBoxModel<String>) cbWeather.getModel();
        weatherModel.addAll(Settings.getMapProfile().getWeatherTypes());
        
        for (var s : Settings.getMapProfile().getIconNames()) {
            cbIcons.addItem(s);
        }
        
        cbIcons.setRenderer(new MapIconRenderer(iconMap));
        
        btnBrowseTileset = new JButton("...");
        btnBrowseBackground = new JButton("...");
        btnBrowseMusic = new JButton("...");
        
        btnPositionMap = new JButton("Set position");
        
        // Lay out components
        var p = new JPanel();
        p.setLayout(new MigLayout("wrap 3"));

        p.add(lblMapName);
        p.add(tfMapName, "span 2, width 100px");
        
        p.add(lblAuthor);
        p.add(tfAuthor, "span 2, width 100px");
    
        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");
    
        p.add(lblTileset);
        p.add(tfTileset, "width 100px");
        p.add(btnBrowseTileset);
    
        p.add(lblBackground);
        p.add(tfBackground, "width 100px");
        p.add(btnBrowseBackground);
    
        p.add(lblMusic);
        p.add(tfMusic, "width 100px");
        p.add(btnBrowseMusic);
        
        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");
        
        p.add(lblLevelNr);
        p.add(spLevelNumber, "span 2");
        
        p.add(lblTime);
        p.add(spTime, "span 2");

        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");
        
        p.add(lblWeather);
        p.add(cbWeather, "span 2");
        
        p.add(lblScrolling);
        p.add(cbScrollingType, "span 2");
        
        p.add(lblIcon);
        p.add(cbIcons, "span 2, width 100px");
    
        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");

        p.add(lblMapX);
        p.add(spMapPosX, "span 2");
    
        p.add(lblMapY);
        p.add(spMapPosY, "span 2");
        
        p.add(btnPositionMap);
        
        add(p, BorderLayout.CENTER);
    }
    
    public void commitSpinnerValues() {
        try {
            spLevelNumber.commitEdit();
            spTime.commitEdit();
            spMapPosX.commitEdit();
            spMapPosY.commitEdit();
            
            level.level_number = (int) spLevelNumber.getValue();
            level.time = (int) spTime.getValue();
            level.icon_x = (int) spMapPosX.getValue();
            level.icon_y = (int) spMapPosY.getValue();
            
            changeListener.stateChanged(changeEvent);
        } catch (ParseException e) {
            Logger.error(e);
        }
    }
    
    private void setListeners() {
        cbIcons.addActionListener(e -> {
            mapPositionDialog.updateIconImage(iconMap.get(Settings.getMapProfile().getIconNames()[cbIcons.getSelectedIndex()]));

            level.icon_id = cbIcons.getSelectedIndex();
            
            fireChanges();
        });
        
        spMapPosX.addChangeListener(new MapPositionChangeListener());
        spMapPosY.addChangeListener(new MapPositionChangeListener());
        
        mapPositionDialog.setPositionSpinners(spMapPosX, spMapPosY); // Not the best way to this, but it's easy and it works for now.
        
        btnPositionMap.addActionListener(e -> {
            try {
                spMapPosX.commitEdit();
                spMapPosY.commitEdit();
            } catch (ParseException ex) {
                Logger.info("Unable to commit edit of spinners.");
            }
    
            int posX = (int) spMapPosX.getValue();
            int posY = (int) spMapPosY.getValue();

            level.icon_x = posX;
            level.icon_y = posY;
            
            mapPositionDialog.updatePosition(new Point(posX, posY));
            mapPositionDialog.setVisible(true);
            
            fireChanges();
        });
    
        btnBrowseTileset.addActionListener(e -> {
            var fc = new ImagePreviewFileChooser(PK2FileSystem.getAssetsPath(PK2FileSystem.TILESET_DIR),
            ImagePreviewFileChooser.PREVIEW_TILESET);

            
            fc.setDialogTitle("Select a tileset image...");
            fc.setFileFilter(new BMPImageFilter());
            
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    var tilesetImage = ImageIO.read(fc.getSelectedFile());
    
                    tilesetImage = GFXUtils.setPaletteToBackgrounds(tilesetImage, sector.getBackgroundImage());
                    
                    sector.tilesetName = fc.getSelectedFile().getName();
                    sector.tilesetImage = tilesetImage;
                
                    // if (Settings.useBGTileset()) {
                    //     var bgTileset = PathUtils.getTilesetAsBackgroundTileset(fc.getSelectedFile().getAbsolutePath());
                    //     if (Files.exists(Path.of(bgTileset))) {
                    //         var bgTilesetImage = ImageIO.read(new File(bgTileset));
        
                    //         bgTilesetImage = GFXUtils.setPaletteToBackgrounds(bgTilesetImage, map.getBackgroundImage());
        
                    //         map.setBackgroundTilesetImage(bgTilesetImage);
                    //     } else {
                    //         map.setBackgroundTilesetImage(null);
                    //     }
                    // }
                    
                    tfTileset.setText(fc.getSelectedFile().getName()); // TODO What about files in the episodes folder? Display tileset/tileset.bmp vs tileset.bmp? episode/tileset.bmp vs tileset/bmp?
    
                    gui.updateRepaintListeners();
    
                    fireChanges();
                } catch (IOException ex) {
                    Logger.warn(ex, "Unable to load tileset image.");
                }
            }
        });
        
        btnBrowseBackground.addActionListener(e -> {
            var fc = new ImagePreviewFileChooser(PK2FileSystem.getAssetsPath(PK2FileSystem.SCENERY_DIR),
            ImagePreviewFileChooser.PREVIEW_BACKGROUND);

            fc.setDialogTitle("Select a background image...");
            fc.setFileFilter(new BMPImageFilter());
    
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    var backgroundImage= ImageIO.read(fc.getSelectedFile());
            
                    var tilesetImage = GFXUtils.setPaletteToBackgrounds(sector.getTilesetImage(), backgroundImage);

                    sector.backgroundImage = backgroundImage;
                    sector.backgroundName = fc.getSelectedFile().getName();

                    sector.tilesetImage = tilesetImage;
                    
                    tfBackground.setText(fc.getSelectedFile().getName());
                    
                    for (var spr : level.getSpriteList()) {
                        spr.setImage(GFXUtils.setPaletteToBackgrounds(spr.getImage(), sector.getBackgroundImage()));
                    }
                    
                    gui.updateRepaintListeners();
    
                    fireChanges();
                } catch (IOException ex) {
                    Logger.warn(ex, "Unable to load background image.");
                }
            }
        });
        
        btnBrowseMusic.addActionListener(e -> {
            var fc = new JFileChooser(PK2FileSystem.getAssetsPath(PK2FileSystem.MUSIC_DIR));
            fc.setDialogTitle("Select a music file...");
            fc.setFileFilter(new MusicFilter());
            
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                tfMusic.setText(fc.getSelectedFile().getName()); // TODO Handle music files from level directory?

                sector.musicName = tfMusic.getText();
                
                fireChanges();
            }
        });
        
        cbWeather.addActionListener(e -> {
            fireChanges();
        });
        
        cbScrollingType.addActionListener(e -> {
            fireChanges();
        });
    }
    
    // TODO Fix this
    public void updateMapProfileData() {
        var scrollingModel = (DefaultComboBoxModel<String>) cbScrollingType.getModel();
        scrollingModel.removeAllElements();
        
        for (var str : Settings.getMapProfile().getScrollingTypes()) {
            cbScrollingType.addItem(str);
        }
        
        var weatherModel = (DefaultComboBoxModel<String>) cbWeather.getModel();
        weatherModel.removeAllElements();
        
        for (var str : Settings.getMapProfile().getWeatherTypes()) {
            cbWeather.addItem(str);
        }

        cbScrollingType.setSelectedIndex(sector.background_scrolling);
        cbWeather.setSelectedIndex(sector.weather);
    }
    
    // This seems pretty hacky, but this is a workaround for when the TextField values get set for the first time. This would cause the changeListener to fire, even though it isn't supposed to.
    private class ChangeListenerWrapper implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (canFireChanges) {
                level.name = tfMapName.getText();
                level.author = tfAuthor.getText();

                sector.musicName = tfMusic.getText();
                sector.tilesetName = tfTileset.getText();

                sector.backgroundName = tfBackground.getText();

                level.level_number = (int) spLevelNumber.getValue();

                level.time = (int) spTime.getValue();
                
                changeListener.stateChanged(changeEvent);
            }
        }
    }
    
    private void setChangeListeners() {
        var tfl = new TextFieldChangeListener(new ChangeListenerWrapper());
        tfMapName.getDocument().addDocumentListener(tfl);
        tfAuthor.getDocument().addDocumentListener(tfl);

        tfTileset.getDocument().addDocumentListener(tfl);
        tfMusic.getDocument().addDocumentListener(tfl);
        tfTileset.getDocument().addDocumentListener(tfl);
    
        spLevelNumber.addChangeListener(new ChangeListenerWrapper());
        spTime.addChangeListener(new ChangeListenerWrapper());
    
        spMapPosX.addChangeListener(new MapPositionChangeListener());
        spMapPosY.addChangeListener(new MapPositionChangeListener());
    }
    
    // Prevent changes being fired when loading a new map. Changes should only be fired once actual changes to the map have been made.
    private void fireChanges() {
        if (canFireChanges) {
            // Even more hacky shit, yo
            level.author = tfAuthor.getText();
            level.name = tfMapName.getText();

            sector.weather = cbWeather.getSelectedIndex();
            sector.background_scrolling = cbScrollingType.getSelectedIndex();
            
            changeListener.stateChanged(changeEvent);
        }
    }
    
    @Override
    public void setMap(PK2Level m) {
        this.level = m;
        
        canFireChanges = false;
        
        tfMapName.setText(level.name);
        tfAuthor.setText(level.author);

        spLevelNumber.setValue(level.level_number);
        spTime.setValue(level.time);
                
        spMapPosX.setValue(level.icon_x);
        spMapPosY.setValue(level.icon_y);

        cbIcons.setSelectedIndex(level.icon_id);
        
        // TODO Check episodemanager
        mapPositionDialog.setMapIcon(iconMap.get(Settings.getMapProfile().getIconNames()[level.icon_id]), new Point(level.icon_x, level.icon_y));
    }

    @Override
    public void setSector(PK2LevelSector sector){
        this.sector = sector;

        tfTileset.setText(sector.tilesetName);
        tfBackground.setText(sector.backgroundName);
        tfMusic.setText(sector.musicName);

        cbWeather.setSelectedIndex(sector.weather);
        cbScrollingType.setSelectedIndex(sector.background_scrolling);

        canFireChanges = true;
    }
    
    public void setChangeListener(ChangeListener listener) {
        this.changeListener = listener;
        
        setChangeListeners();
    }
    
    private class MapPositionChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (canFireChanges) {

                level.icon_x = (int) spMapPosX.getValue();
                level.icon_y = (int) spMapPosY.getValue();
                
                mapPositionDialog.updatePosition(new Point(level.icon_x, level.icon_y));
            }
        }
    }
}
