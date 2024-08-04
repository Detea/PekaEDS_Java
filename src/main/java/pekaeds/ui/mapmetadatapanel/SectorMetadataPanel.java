package pekaeds.ui.mapmetadatapanel;

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

    private JButton btnBrowseTileset;
    private JButton btnBrowseBgTileset;
    private JButton btnBrowseBackground;
    private JButton btnBrowseMusic;

    public SectorMetadataPanel(PekaEDSGUI gui){
        this.setupUI();
        this.addChangeListeners();

        this.gui = gui;
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
        
        DefaultComboBoxModel<String> scrollingModel = (DefaultComboBoxModel<String>) cbScrollingType.getModel();
        scrollingModel.addAll(Settings.getMapProfile().getScrollingTypes());
    
        DefaultComboBoxModel<String> weatherModel = (DefaultComboBoxModel<String>) cbWeather.getModel();
        weatherModel.addAll(Settings.getMapProfile().getWeatherTypes());
               
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

        p.add(lblTilesetBG);
        p.add(this.tfBgTileset, "width 100px");
        p.add(this.btnBrowseBgTileset);
        
        
        add(p, BorderLayout.CENTER);
    }

    public void addChangeListeners(){
        var tfl = new TextFieldChangeListener(this);
        this.tfSectorName.getDocument().addDocumentListener(tfl);

        this.tfTileset.getDocument().addDocumentListener(tfl);
        this.tfBackground.getDocument().addDocumentListener(tfl);
        this.tfMusic.getDocument().addDocumentListener(tfl);
        this.tfBgTileset.getDocument().addDocumentListener(tfl);

        this.cbWeather.addActionListener(this);
        this.cbScrollingType.addActionListener(this);

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

        this.canFireChanges = true;
    }

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

    @Override
    public void actionPerformed(ActionEvent e) {
        if(this.canFireChanges){
            this.sector.weather = this.cbWeather.getSelectedIndex();
            this.sector.background_scrolling = this.cbScrollingType.getSelectedIndex();

            this.gui.stateChanged(null);
        }
    }    
}
