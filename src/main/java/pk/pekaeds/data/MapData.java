package pk.pekaeds.data;

import pk.pekaeds.util.GFXUtils;
import pk.pekaeds.pk2.map.PK2Map;
import pk.pekaeds.pk2.map.PK2Map13;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

// TODO MapData: Delete this class
public final class MapData {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private BufferedImage backgroundImage = null;
    private BufferedImage tilesetImage = null;
    
    private PK2Map map = new PK2Map13();
  
    private JList list;
    
    public void setTilesetImage(BufferedImage tsetImage) {
        pcs.firePropertyChange("tilesetImage", tilesetImage, tsetImage);
        
        this.tilesetImage = tsetImage;
    }
    
    public void setBackgroundImage(BufferedImage bgImage) {
        pcs.firePropertyChange("backgroundImage", backgroundImage, bgImage);
        
        this.backgroundImage = bgImage;
        map.setBackgroundImage(backgroundImage);
    }
    
    public BufferedImage getTilesetImage() {
        return tilesetImage;
    }
    
    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }
    
    public void setTilesetPaletteToBackground() {
        tilesetImage = GFXUtils.setPaletteToBackgrounds(tilesetImage, backgroundImage);
        
        // Only fire the change here, after the tileset has been "fixed".
        pcs.firePropertyChange("tilesetImage", null, tilesetImage);
    }
    
    public int[][] getLayer(int layer) {
        return map.getLayers().get(layer);
    }
    
    public PK2Map getMap() {
        return map;
    }
    
    public void setMap(PK2Map m) {
        pcs.firePropertyChange("map", map, m);
        
        this.map = m;
        
        map.setBackgroundImage(backgroundImage);
    }
    
    /*
     * PropertyChangeListener methods
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
