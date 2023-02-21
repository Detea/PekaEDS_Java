package pk.pekaeds.ui.mappanel;

import pk.pekaeds.data.Layer;
import pk.pekaeds.pk2.map.PK2Map;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class MapPanelModel {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private Rectangle viewRect = new Rectangle();
    
    private int bgRepeatX;
    private int bgRepeatY;
    
    private int selectedLayer = Layer.BOTH;
    
    private Point lastPanPoint = new Point(0, 0);
    
    private BufferedImage backgroundImage;
    private BufferedImage tilesetImage;

    private PK2Map map;
    
    private Point zoomPosition = new Point();
    private float zoomAmount = 1;
    
    private boolean showSprites = true; // Putting this in here because it won't be saved in the settings file. Might do that, if users want it to be saved.
    
    public PK2Map getMap() {
        return map;
    }
    
    public void setMap(PK2Map m) {
        this.map = m;
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
    
    public boolean shouldShowSprites() {
        return showSprites;
    }
    
    public void setShowSprites(boolean show) {
        this.showSprites = show;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
