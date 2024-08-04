package pekaeds.ui.mapmetadatapanel;


import java.util.Map;
import java.awt.*;
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.miginfocom.swing.MigLayout;
import pekaeds.pk2.level.PK2LevelSector;
import pekaeds.settings.Settings;
import pekaeds.ui.listeners.PK2SectorConsumer;
import pekaeds.ui.listeners.TextFieldChangeListener;
import pekaeds.ui.main.PekaEDSGUI;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.formdev.flatlaf.util.SystemInfo;


public class SectorMetadataPanel extends JPanel implements PK2SectorConsumer, ChangeListener, ActionListener{

    private PekaEDSGUI gui;
    private boolean canFireChanges = false;

    private PK2LevelSector sector;

    private JTextField tfSectorName;
    private JTextField tfTileset;
    private JTextField tfBgTileset;
    private JTextField tfBackground;
    private JTextField tfMusic;

    private JComboBox<String> cbWeather;
    private JComboBox<String> cbScrollingType;

    private JComboBox<String> cbSplashColor;
    private JComboBox<String> cbFireColor1;
    private JComboBox<String> cbFireColor2;

    private JButton btnBrowseTileset;
    private JButton btnBrowseBgTileset;
    private JButton btnBrowseBackground;
    private JButton btnBrowseMusic;

    public SectorMetadataPanel(PekaEDSGUI gui){
        this.setupUI();
        this.addChangeListeners();

        this.gui = gui;
    }

    public static DefaultComboBoxModel<String> getFireColorsModel(){
        DefaultComboBoxModel<String> fireColorsModel = new DefaultComboBoxModel<String>();
        for (var col : Settings.getMapProfile().getFireColors().entrySet()) {
            fireColorsModel.addElement(col.getValue());
        }

        return fireColorsModel;
    }

    public void setupUI(){
        JLabel lblMapName = new JLabel("Name:");
        this.tfSectorName = new JTextField();
    
        JLabel lblTileset = new JLabel("Tileset:");
        this.tfTileset = new JTextField();

        JLabel lblTilesetBG = new JLabel("Tileset Bg:");
        this.tfBgTileset = new JTextField();
    
        JLabel lblBackground = new JLabel("Background:");
        this.tfBackground = new JTextField();
    
        JLabel lblMusic = new JLabel("Music:");
        this.tfMusic = new JTextField();
    
        
        JLabel lblWeather = new JLabel("Weather:");
        this.cbWeather = new JComboBox<>();
    
        JLabel lblScrolling = new JLabel("Scrolling:");
        this.cbScrollingType = new JComboBox<>();

        JLabel lblSplashColor = new JLabel("Splash color:");
        this.cbSplashColor = new JComboBox<>();

        JLabel lblFireColor1 = new JLabel("Fire color 1:");
        this.cbFireColor1 = new JComboBox<>();

        JLabel lblFireColor2 = new JLabel("Fire color 2:");
        this.cbFireColor2 = new JComboBox<>();
        
        DefaultComboBoxModel<String> scrollingModel = (DefaultComboBoxModel<String>) cbScrollingType.getModel();
        scrollingModel.addAll(Settings.getMapProfile().getScrollingTypes());
    
        DefaultComboBoxModel<String> weatherModel = (DefaultComboBoxModel<String>) cbWeather.getModel();
        weatherModel.addAll(Settings.getMapProfile().getWeatherTypes());

        DefaultComboBoxModel<String> splashColorsModel = new DefaultComboBoxModel<String>();
        for (var col : Settings.getMapProfile().getSplashColors().entrySet()) {
            splashColorsModel.addElement(col.getValue());
        }

        this.cbSplashColor.setModel(splashColorsModel);
        this.cbFireColor1.setModel(getFireColorsModel());
        this.cbFireColor2.setModel(getFireColorsModel());

        btnBrowseTileset = new JButton("...");
        btnBrowseBgTileset = new JButton("...");
        btnBrowseBackground = new JButton("...");
        btnBrowseMusic = new JButton("...");
        
        
        // Lay out components
        JPanel p = new JPanel();
        p.setLayout(new MigLayout("wrap 3"));

        p.add(lblMapName);
        p.add(this.tfSectorName, "span 2, width 100px");       
    
        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");
    
        p.add(lblTileset);
        p.add(this.tfTileset, "width 100px");
        p.add(this.btnBrowseTileset);

        p.add(lblBackground);
        p.add(this.tfBackground, "width 100px");
        p.add(this.btnBrowseBackground);
    
        p.add(lblMusic);
        p.add(this.tfMusic, "width 100px");
        p.add(this.btnBrowseMusic);
               
        p.add(lblWeather);
        p.add(this.cbWeather, "span 2");
        
        p.add(lblScrolling);
        p.add(this.cbScrollingType, "span 2");

        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");

        p.add(lblFireColor1);
        p.add(this.cbFireColor1);

        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");

        p.add(lblFireColor2);
        p.add(this.cbFireColor2);

        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");
        p.add(new JLabel("Optional:"));

        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");

        p.add(lblTilesetBG);
        p.add(this.tfBgTileset, "width 100px");
        p.add(this.btnBrowseBgTileset);

        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");
        
        p.add(lblSplashColor);
        p.add(this.cbSplashColor);

        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");
        
        
        add(p, BorderLayout.CENTER);
    }

    //TODO Redesign
    public void addChangeListeners(){
        var tfl = new TextFieldChangeListener(this);
        this.tfSectorName.getDocument().addDocumentListener(tfl);

        this.tfTileset.getDocument().addDocumentListener(tfl);
        this.tfBackground.getDocument().addDocumentListener(tfl);
        this.tfMusic.getDocument().addDocumentListener(tfl);
        this.tfBgTileset.getDocument().addDocumentListener(tfl);

        this.cbWeather.addActionListener(this);
        this.cbScrollingType.addActionListener(this);

        this.cbSplashColor.addActionListener(this);
        this.cbFireColor1.addActionListener(this);
        this.cbFireColor2.addActionListener(this);
    }

    @Override
    public void setSector(PK2LevelSector sector) {
        this.sector = sector;
        this.canFireChanges = false;

        this.tfSectorName.setText(sector.name);

        this.tfTileset.setText(sector.tilesetName);
        this.tfBackground.setText(sector.backgroundName);
        this.tfMusic.setText(sector.musicName);
        this.tfBgTileset.setText(sector.tilesetBgName);

        this.cbWeather.setSelectedIndex(sector.weather);
        this.cbScrollingType.setSelectedIndex(sector.background_scrolling);

        Map<Integer, String> splashColors = Settings.getMapProfile().getSplashColors();
        this.cbSplashColor.setSelectedItem(splashColors.get(sector.splash_color));

        Map<Integer, String> fireColors = Settings.getMapProfile().getFireColors();
        
        this.cbFireColor1.setSelectedItem(fireColors.get(sector.fire_color_1));
        this.cbFireColor2.setSelectedItem(fireColors.get(sector.fire_color_2));

        this.canFireChanges = true;
    }

    //TODO Redesign it

    @Override
    public void stateChanged(ChangeEvent e) {
        if(this.canFireChanges){
            this.sector.name = this.tfSectorName.getText();
            
            this.sector.tilesetName = this.tfTileset.getText();
            this.sector.backgroundName = this.tfBackground.getText();
            this.sector.musicName = this.tfMusic.getText();
            this.sector.tilesetBgName = this.tfBgTileset.getText();

            this.gui.stateChanged(e);
        }
    }

    private static int getMapColor(Map<Integer, String> colors, String value){
        for(var entry: colors.entrySet()){
            if(entry.getValue().equals(value)){
                return entry.getKey();
            }
        }

        return 0;
    }

    //TODO Redesign it

    @Override
    public void actionPerformed(ActionEvent e) {
        if(this.canFireChanges){
            this.sector.weather = this.cbWeather.getSelectedIndex();
            this.sector.background_scrolling = this.cbScrollingType.getSelectedIndex();

            Map<Integer, String> splashColors = Settings.getMapProfile().getSplashColors();
            Map<Integer, String> fireColors = Settings.getMapProfile().getFireColors();

            this.sector.splash_color = getMapColor(splashColors, (String)this.cbSplashColor.getSelectedItem());
            this.sector.fire_color_1 = getMapColor(fireColors, (String)this.cbFireColor1.getSelectedItem());
            this.sector.fire_color_2 = getMapColor(fireColors, (String)this.cbFireColor2.getSelectedItem());

            this.gui.stateChanged(null);
        }
    }    
}
