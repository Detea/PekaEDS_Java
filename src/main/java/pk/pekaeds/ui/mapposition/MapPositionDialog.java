package pk.pekaeds.ui.mapposition;

import pk.pekaeds.settings.Settings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

public class MapPositionDialog extends JDialog {
    private BufferedImage backgroundImage = null;
    private List<MapIcon> episodeIcons = new ArrayList<>();
    private MapIcon mapIcon = null;
    
    public MapPositionDialog() {
        try {
            backgroundImage = ImageIO.read(new File(Settings.getGFXPath() + File.separatorChar + "map.bmp"));
        } catch (IOException e) {
            Logger.warn(e, "Unable to load Map.bmp. Expecting to find it in this folder: {}", Settings.getGFXPath());
        }
        
        add(new PositionPanel(), BorderLayout.CENTER);
        
        setTitle("Set icon position on map");
        setSize(new Dimension(640, 480));
        
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        
        pack();
    }
    
    public Point showDialog() {
        setVisible(true);
        
        return mapIcon.getPosition();
    }
    
    public void setMapIcon(BufferedImage img, Point pos) {
        mapIcon = new MapIcon(img, pos);
        
        /*if (!episodeIcons.contains(icon)) {
            icons.add(icon);
        }*/
    }
    
    public void removeIcon(int iconIndex) {
        // TODO Implement
    }
    
    public void updateIcon(BufferedImage img, Point pos) {
        mapIcon.setImage(img);
        mapIcon.setPosition(pos);
        
        /*for (var icon : icons) {
            if (icon.getImage().equals(img) && icon.getPosition().equals(pos) && icon.getFilename().equals(file)) {
                icon.setImage(img);
                icon.setPosition(pos);
            }
        }*/
    }
    
    public void updateIconImage(BufferedImage img) {
        if (mapIcon != null) {
            mapIcon.setImage(img);
            
            repaint();
        }
    }
    
    public void updatePosition(Point pos) {
        if (mapIcon != null) {
            mapIcon.setPosition(pos);
            
            repaint();
        }
    }
    
    private class PositionPanel extends JPanel {
        public PositionPanel() {
            setPreferredSize(new Dimension(640, 480));

            addMouseListener(new MapPositionMouseHandler(MapPositionDialog.this));
        }
        
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
        
            if (MapPositionDialog.this.backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, null);
            }
            
            if (mapIcon != null) {
                g.drawImage(mapIcon.getImage(), mapIcon.getPosition().x, mapIcon.getPosition().y, null);
            }
            
            // TODO Add episode support
            /*
            for (var icon : MapPositionDialog.this.icons) {
                // TODO Adjust to correct position, this is testing only for now
                g.drawImage(icon.getImage(), icon.getPosition().x, icon.getPosition().y, null);
            }*/
        }
    }
}
