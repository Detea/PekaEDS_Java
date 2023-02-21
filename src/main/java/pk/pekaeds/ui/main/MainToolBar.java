package pk.pekaeds.ui.main;

import pk.pekaeds.data.Layer;
import pk.pekaeds.settings.Settings;
import pk.pekaeds.tools.Tool;
import pk.pekaeds.tools.ToolModeListener;
import pk.pekaeds.ui.actions.NewMapAction;
import pk.pekaeds.ui.actions.OpenMapAction;
import pk.pekaeds.ui.actions.PlayMapAction;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MainToolBar extends JToolBar implements PropertyChangeListener, ToolModeListener {
    private JButton btnNew;
    private JButton btnOpen;
    private JButton btnSave;
    
    private JButton btnPlayTest;
    
    private JLabel lblLayer;
    private JComboBox<String> cbLayer;
    
    private JLabel lblMode;
    private JComboBox<String> cbToolMode;
    
    private JToggleButton tbHighlightSprites;
    private JToggleButton tbShowSprites;
    
    private final Settings settings;
    
    private PekaEDSGUI gui;
    
    public MainToolBar(PekaEDSGUI gui) {
        settings = new Settings();
        
        this.gui = gui;
        
        setup();
        addComponents();
        addListeners();
    }
    
    private void setup() {
        btnNew = new JButton("New");
        btnOpen = new JButton("Open");
        btnSave = new JButton("Save");
        
        btnPlayTest = new JButton("Test");
    
        tbHighlightSprites = new JToggleButton("Highlight sprites");
        tbHighlightSprites.setSelected(Settings.highlightSprites());
        
        tbShowSprites = new JToggleButton("Show sprites");
        tbShowSprites.setSelected(gui.shouldShowSprites());
        
        btnNew.addActionListener(new NewMapAction(gui));
        btnOpen.addActionListener(new OpenMapAction(gui));
        btnPlayTest.addActionListener(new PlayMapAction(gui));
        
        tbHighlightSprites.addActionListener(e -> {
            Settings.setHighlightSprites(tbHighlightSprites.isSelected());
            
            gui.getMapPanel().repaint();
        });
        
        tbShowSprites.addActionListener(e -> {
            gui.setShowSprites(tbShowSprites.isSelected());
            gui.getMapPanel().repaint();
        });
        
        lblLayer = new JLabel("Layer:");
        cbLayer = new JComboBox<>(settings.getLayerNames().toArray(new String[0]));
        
        cbLayer.setMaximumSize(new Dimension(100, 30));
        
        lblMode = new JLabel("Mode:");
        cbToolMode = new JComboBox<>();
        cbToolMode.addItem("Tile");
        cbToolMode.addItem("Sprite");
    }
    
    private void addComponents() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        
        add(btnNew);
        add(btnOpen);
        add(btnSave);
        
        addSeparator();
        
        add(btnPlayTest);
        
        addSeparator();
        
        addSeparator();
        
        add(lblLayer);
        add(cbLayer);
        
        addSeparator();
        
        add(lblMode);
        add(cbToolMode);
        
        addSeparator();
        
        add(tbHighlightSprites);
        add(tbShowSprites);
    }
    
    private void addListeners() {
        cbLayer.addActionListener(e -> {
            // TODO Seems pretty hacky (and it is), but the "Both" layer should be first, I think. So gotta do this crap.
        
            if (cbLayer.getSelectedIndex() == 0) {
                gui.setLayer(Layer.BOTH); // Set layer to both
            } else {
                gui.setLayer(cbLayer.getSelectedIndex() - 1); // Subtract one to get rid of the both layer, so that foreground is 0 and background is 1. Pretty bad, lmao.
            }
        });
        
        btnSave.addActionListener(e -> {
            gui.saveMap();
        });
        
        cbToolMode.addActionListener(e -> {
            Tool.setMode(cbToolMode.getSelectedIndex());
        });
    }
    
    // TODO Delete this
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof PekaEDSGUIModel) {
            if (evt.getPropertyName().equals("currentMode")) {
                cbToolMode.setSelectedIndex((int) evt.getNewValue());
            }
        }
    }
    
    public void setToolMode(int mode) {
        cbToolMode.setSelectedIndex(mode);
    }
    
    public void setSelectedLayer(int layer) {
        if (layer == Layer.BOTH) {
            cbLayer.setSelectedIndex(0);
        } else {
            cbLayer.setSelectedIndex(layer + 1);
        }
    }
    
    @Override
    public void changeMode(int mode) {
        cbToolMode.setSelectedIndex(mode);
    }
}
