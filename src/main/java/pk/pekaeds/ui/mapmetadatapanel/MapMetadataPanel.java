package pk.pekaeds.ui.mapmetadatapanel;

import net.miginfocom.swing.MigLayout;
import pk.pekaeds.ui.listeners.TextFieldChangeListener;
import pk.pekaeds.ui.mapposition.MapIconRenderer;
import pk.pekaeds.ui.mapposition.MapPositionDialog;
import pk.pekaeds.util.GFXUtils;
import pk.pekaeds.pk2.map.PK2Map;
import pk.pekaeds.settings.Settings;
import pk.pekaeds.ui.filefilters.BMPImageFilter;
import pk.pekaeds.ui.filefilters.MusicFilter;
import pk.pekaeds.ui.main.PekaEDSGUI;
import pk.pekaeds.ui.listeners.PK2MapConsumer;
import pk.pekaeds.filechooser.ImagePreviewFileChooser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Position;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.tinylog.Logger;
import pk.pekaeds.util.PathUtils;

// TODO The ChangeListener stuff ist pretty messy. It works but it should probably be cleaned up. Some time... maybe...
public class MapMetadataPanel extends JPanel implements PK2MapConsumer {
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
    
    private PK2Map map = null;
    
    private PekaEDSGUI gui;
    
    public MapMetadataPanel(PekaEDSGUI ui) {
        loadIcons();
        
        mapPositionDialog = new MapPositionDialog();
        
        this.gui = ui;
        
        setupUI();
        setListeners();
    }
    
    // TODO Optimization: Put this in a SwingWorker?
    private void loadIcons() {
        try {
            var iconSheet = ImageIO.read(new File(Settings.getPK2stuffFilePath()));
            var colorModel = (IndexColorModel) iconSheet.getColorModel();
    
            var rs = new byte[256];
            var gs = new byte[256];
            var bs = new byte[256];
            colorModel.getReds(rs);
            colorModel.getGreens(gs);
            colorModel.getBlues(bs);
            
            var cm = new IndexColorModel(8, 256, rs, gs, bs, 255);
            
            var raster = iconSheet.getRaster();
            iconSheet = new BufferedImage(iconSheet.getWidth(), iconSheet.getHeight(), BufferedImage.TYPE_BYTE_INDEXED, cm);
            iconSheet.setData(raster);
            
            for (int i = 0; i < Settings.getMapProfile().getIconNames().length; i++) {
                var img = iconSheet.getSubimage(1 + (i * 28), 452, 27, 27);
                
                iconMap.put(Settings.getMapProfile().getIconNames()[i], img);
            }
        } catch (IOException e) {
            Logger.warn(e, "Unable to load icon image file: {}", Settings.getPK2stuffFilePath());
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
            
            map.setLevelNumber((int) spLevelNumber.getValue());
            map.setTime((int) spTime.getValue());
            map.setMapX((int) spMapPosX.getValue());
            map.setMapY((int) spMapPosY.getValue());
            
            changeListener.stateChanged(changeEvent);
        } catch (ParseException e) {
            // TODO Does this need to be logged?
        }
    }
    
    private void setListeners() {
        cbIcons.addActionListener(e -> {
            mapPositionDialog.updateIconImage(iconMap.get(Settings.getMapProfile().getIconNames()[cbIcons.getSelectedIndex()]));
    
            map.setIcon(cbIcons.getSelectedIndex());
            
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
            
            map.setMapX(posX);
            map.setMapY(posY);
            
            mapPositionDialog.updatePosition(new Point(posX, posY), false);
            mapPositionDialog.setVisible(true);
            
            fireChanges();
        });
    
        btnBrowseTileset.addActionListener(e -> {
            var fc = new ImagePreviewFileChooser(Settings.getTilesetPath(), ImagePreviewFileChooser.PREVIEW_TILESET);
            fc.setDialogTitle("Select a tileset image...");
            fc.setFileFilter(new BMPImageFilter());
            
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    var tilesetImage = ImageIO.read(fc.getSelectedFile());
    
                    tilesetImage = GFXUtils.setPaletteToBackgrounds(tilesetImage, map.getBackgroundImage());
                    
                    map.setTileset(fc.getSelectedFile().getName());
                    map.setTilesetImage(tilesetImage);
                
                    if (Settings.useBGTileset()) {
                        var bgTileset = PathUtils.getTilesetAsBackgroundTileset(fc.getSelectedFile().getAbsolutePath());
                        if (Files.exists(Path.of(bgTileset))) {
                            var bgTilesetImage = ImageIO.read(new File(bgTileset));
        
                            bgTilesetImage = GFXUtils.setPaletteToBackgrounds(bgTilesetImage, map.getBackgroundImage());
        
                            map.setBackgroundTilesetImage(bgTilesetImage);
                        } else {
                            map.setBackgroundTilesetImage(null);
                        }
                    }
                    
                    tfTileset.setText(fc.getSelectedFile().getName()); // TODO What about files in the episodes folder? Display tileset/tileset.bmp vs tileset.bmp? episode/tileset.bmp vs tileset/bmp?
    
                    gui.updateRepaintListeners();
    
                    fireChanges();
                } catch (IOException ex) {
                    Logger.warn(ex, "Unable to load tileset image.");
                }
            }
        });
        
        btnBrowseBackground.addActionListener(e -> {
            var fc = new ImagePreviewFileChooser(Settings.getBackgroundPath(), ImagePreviewFileChooser.PREVIEW_BACKGROUND); // TODO Set to background directory or last choosen directory
            fc.setDialogTitle("Select a background image...");
            fc.setFileFilter(new BMPImageFilter());
    
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    var backgroundImage= ImageIO.read(fc.getSelectedFile());
            
                    var tilesetImage = GFXUtils.setPaletteToBackgrounds(map.getTilesetImage(), backgroundImage);
            
                    map.setBackgroundImage(backgroundImage);
                    map.setBackground(fc.getSelectedFile().getName());
                    
                    map.setTilesetImage(tilesetImage);
                    
                    tfBackground.setText(fc.getSelectedFile().getName()); // TODO What about files in the episodes folder?
                    
                    for (var spr : map.getSpriteList()) {
                        spr.setImage(GFXUtils.setPaletteToBackgrounds(spr.getImage(), map.getBackgroundImage()));
                    }
                    
                    gui.updateRepaintListeners();
    
                    fireChanges();
                } catch (IOException ex) {
                    Logger.warn(ex, "Unable to load background image.");
                }
            }
        });
        
        btnBrowseMusic.addActionListener(e -> {
            var fc = new JFileChooser(Settings.getMusicPath());
            fc.setDialogTitle("Select a music file...");
            fc.setFileFilter(new MusicFilter());
            
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                tfMusic.setText(fc.getSelectedFile().getName()); // TODO Handle music files from level directory?
    
                map.setMusic(tfMusic.getText());
                
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

        cbScrollingType.setSelectedIndex(map.getScrollType());
        cbWeather.setSelectedIndex(map.getWeatherType());
    }
    
    // This seems pretty hacky, but this is a workaround for when the TextField values get set for the first time. This would cause the changeListener to fire, even though it isn't supposed to.
    private class ChangeListenerWrapper implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (canFireChanges) {
                // More hacky shit
                map.setLevelNumber((int) spLevelNumber.getValue());
                map.setTime((int) spTime.getValue());
                
                changeListener.stateChanged(changeEvent);
            }
        }
    }
    
    private void setChangeListeners() {
        var tfl = new TextFieldChangeListener(new ChangeListenerWrapper());
        tfMapName.getDocument().addDocumentListener(tfl);
        tfAuthor.getDocument().addDocumentListener(tfl);
    
        spLevelNumber.addChangeListener(new ChangeListenerWrapper());
        spTime.addChangeListener(new ChangeListenerWrapper());
    
        spMapPosX.addChangeListener(new MapPositionChangeListener());
        spMapPosY.addChangeListener(new MapPositionChangeListener());
    }
    
    // Prevent changes being fired when loading a new map. Changes should only be fired once actual changes to the map have been made.
    private void fireChanges() {
        if (canFireChanges) {
            // Even more hacky shit, yo
            map.setAuthor(tfAuthor.getText());
            map.setName(tfMapName.getText());
    
            map.setWeatherType(cbWeather.getSelectedIndex());
            map.setScrollType(cbScrollingType.getSelectedIndex());
            
            changeListener.stateChanged(changeEvent);
        }
    }
    
    @Override
    public void setMap(PK2Map m) {
        this.map = m;
        
        canFireChanges = false;
        
        tfMapName.setText(map.getName());
        tfAuthor.setText(map.getAuthor());
        tfTileset.setText(map.getTileset());
        tfBackground.setText(map.getBackground());
        tfMusic.setText(map.getMusic());
        
        spLevelNumber.setValue(map.getLevelNumber());
        spTime.setValue(map.getTime());
        
        cbWeather.setSelectedIndex(map.getWeatherType());
        cbScrollingType.setSelectedIndex(map.getScrollType());
        cbIcons.setSelectedIndex(map.getIcon());
        
        spMapPosX.setValue(map.getMapX());
        spMapPosY.setValue(map.getMapY());
        
        // TODO Check episodemanager
        mapPositionDialog.setMapIcon(iconMap.get(Settings.getMapProfile().getIconNames()[map.getIcon()]), new Point(map.getMapX(), map.getMapY()));
        
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
                map.setMapX((int) spMapPosX.getValue());
                map.setMapY((int) spMapPosY.getValue());
            }
        }
    }
}
