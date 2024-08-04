package pekaeds.ui.mapmetadatapanel;

import net.miginfocom.swing.MigLayout;
import pekaeds.ui.listeners.TextFieldChangeListener;
import pekaeds.ui.mapposition.MapPositionDialog;
import pekaeds.util.GFXUtils;
import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.pk2.level.PK2Level;
import pekaeds.settings.Settings;
import pekaeds.ui.main.PekaEDSGUI;
import pekaeds.ui.listeners.PK2LevelConsumer;

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
public class LevelMetadataPanel extends JPanel implements PK2LevelConsumer {
    private ChangeListener changeListener;
    private ChangeEvent changeEvent = new ChangeEvent(this);
    private boolean canFireChanges = false;
    
    private JTextField tfMapName;
    private JTextField tfAuthor;

    private JSpinner spLevelNumber;
    private JSpinner spTime;
    
    private JComboBox<String> cbIcons;

    private JSpinner spMapPosX;
    private JSpinner spMapPosY;
    
    private JButton btnPositionMap;
    
    private Map<Object, BufferedImage> iconMap = new HashMap<>();
    
    private MapPositionDialog mapPositionDialog;
    
    private PK2Level level = null;
    
    public LevelMetadataPanel(PekaEDSGUI ui) {
        loadIcons();
        
        mapPositionDialog = new MapPositionDialog(ui.getEpisodeManager());
                
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
        
        var lblLevelNr = new JLabel("Level nr.:");
        spLevelNumber = new JSpinner();
    
        var lblTime = new JLabel("Time (sec):");
        spTime = new JSpinner();
            
        var lblIcon = new JLabel("Icon:");
        cbIcons = new JComboBox<>();
    
        var lblMapX = new JLabel("Map X:");
        spMapPosX = new JSpinner();
    
        var lblMapY = new JLabel("Map Y:");
        spMapPosY = new JSpinner();
        
        for (var s : Settings.getMapProfile().getIconNames()) {
            cbIcons.addItem(s);
        }
        
        cbIcons.setRenderer(new MapIconRenderer(iconMap));       
        
        btnPositionMap = new JButton("Set position");
        
        // Lay out components
        var p = new JPanel();
        p.setLayout(new MigLayout("wrap 3"));

        p.add(lblMapName);
        p.add(tfMapName, "span 2, width 100px");
        
        p.add(lblAuthor);
        p.add(tfAuthor, "span 2, width 100px");
           
        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");
        
        p.add(lblLevelNr);
        p.add(spLevelNumber, "span 2");
        
        p.add(lblTime);
        p.add(spTime, "span 2");

        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");
                
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
    }

    
    // This seems pretty hacky, but this is a workaround for when the TextField values get set for the first time. This would cause the changeListener to fire, even though it isn't supposed to.
    private class ChangeListenerWrapper implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (canFireChanges) {
                level.name = tfMapName.getText();
                level.author = tfAuthor.getText();

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
            
            changeListener.stateChanged(changeEvent);
        }
    }
    
    @Override
    public void setLevel(PK2Level m) {
        this.level = m;
        
        this.canFireChanges = false;
        
        tfMapName.setText(level.name);
        tfAuthor.setText(level.author);

        spLevelNumber.setValue(level.level_number);
        spTime.setValue(level.time);
                
        spMapPosX.setValue(level.icon_x);
        spMapPosY.setValue(level.icon_y);

        cbIcons.setSelectedIndex(level.icon_id);
        
        // TODO Check episodemanager
        mapPositionDialog.setMapIcon(iconMap.get(Settings.getMapProfile().getIconNames()[level.icon_id]), new Point(level.icon_x, level.icon_y));

        this.canFireChanges = true;
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
