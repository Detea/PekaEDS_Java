package pekaeds.ui.mappanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import pekaeds.data.Layer;
import pekaeds.pk2.map.PK2Map;
import pekaeds.pk2.map.PK2MapSector;

public class MapPanelModel {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private Rectangle viewRect = new Rectangle();
    
    private int bgRepeatX;
    private int bgRepeatY;
    
    private int selectedLayer = Layer.BOTH;
    
    private Point lastPanPoint = new Point(0, 0);
    
    private BufferedImage backgroundImage;
    private BufferedImage tilesetImage;

    private PK2MapSector sector;
    private PK2Map level;
    
    private Point zoomPosition = new Point();
    private float zoomAmount = 1;
    
    public PK2MapSector getSector() {
        return sector;
    }

    public PK2Map getLevel(){
        return level;
    }

    
    public void setSector(PK2MapSector sector) {
        this.sector = sector;
    }

    public void setLevel(PK2Map level){
        this.level = level;
    }
    
    public void setBackgroundImage(BufferedImage image) {
        this.backgroundImage = image;
    }
    
    BufferedImage getBackgroundImage() {
        return backgroundImage;
    }
    
    public void setTilesetImage(BufferedImage image) {
        this.tilesetImage = image;
    }
    
    BufferedImage getTilesetImage() {
        return tilesetImage;
    }
    
    public void setSelectedLayer(int layer) {
        pcs.firePropertyChange("selectedLayer", this.selectedLayer, layer);
        
        this.selectedLayer = layer;
    }
    
    public int getSelectedLayer() {
        return selectedLayer;
    }
    
    public void setViewSize(int width, int height) {
        pcs.firePropertyChange("viewportSize", new Dimension(0, 0), new Dimension(width, height));
        
        viewRect.setSize(width, height);
    }
    
    // TODO Should add a listener to the JViewport, but does it belong in the model?
    public void setViewPosition(int x, int y) {
        pcs.firePropertyChange("viewPosition", new Point(0, 0), new Point(x, y));
        
        viewRect.x = x;
        viewRect.y = y;
    }
    
    public Rectangle getViewRect() {
        return viewRect;
    }
    
    public void setBackgroundRepeat(int timesX, int timesY) {
        this.bgRepeatX = timesX;
        this.bgRepeatY = timesY;
    }
    
    public int getBgRepeatX() {
        return bgRepeatX;
    }
    
    public int getBgRepeatY() {
        return bgRepeatY;
    }
    
    public Point getLastPanPoint() {
        return lastPanPoint;
    }
    
    public void setLastPanPoint(Point p) {
        this.lastPanPoint = p;
    }
    
    public void setZoomPosition(Point pos) {
        this.zoomPosition = pos;
    }
    
    public Point getZoomPosition() {
        return zoomPosition;
    }
    
    public void setZoomAmount(float zoom) {
        this.zoomAmount = zoom;
    }
    
    public float getZoomAmount() {
        return zoomAmount;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
