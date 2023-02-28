package pk.pekaeds.tools;

import pk.pekaeds.data.Layer;
import pk.pekaeds.data.MapData;
import pk.pekaeds.pk2.map.PK2Map13;
import pk.pekaeds.ui.listeners.SpritePlacementListener;
import pk.pekaeds.ui.listeners.TileChangeListener;
import pk.pekaeds.util.TileUtils;
import pk.pekaeds.pk2.map.PK2Map;
import pk.pekaeds.pk2.sprite.PK2Sprite;
import pk.pekaeds.settings.Settings;
import pk.pekaeds.ui.mappanel.MapPanelModel;
import pk.pekaeds.ui.mappanel.MapPanelPainter;
import pk.pekaeds.util.undoredo.ActionType;
import pk.pekaeds.util.undoredo.UndoAction;
import pk.pekaeds.util.undoredo.UndoManager;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Parent class for all Tools.
 *
 * To create a new tool extend this class.
 *
 * To place a single tile use the placeTile() method. To place a whole tile selection use placeTiles().
 * Put these methods in mousePressed() or mouseDragged() after overriding them. When overriding them make sure to call super.mouseMethodYouHaveOverriden(), to keep the mousePosition updated in this parent class updated.
 *
 * Undo and redo gets handled in placeTile() and placeTiles().
 *
 * Then register it with the application by adding a Tools.addTool(yourTool.class) in PekaEDSGUI.registerTools().
 *
 * You should also add a Keyboard shortcut for it. Look at the description of the ShortcutUtils class to see how that is done.
 *
 * If you want to add a button to the main UI, look at the ToolsToolBar class.
 */
public abstract class Tool implements PropertyChangeListener {
    public static final int MODE_TILE = 0;
    public static final int MODE_SPRITE = 1;
    
    protected static PK2Map map;

    private MapPanelPainter mapPainter;
    
    protected static int selectedLayer;
    
    protected static int[][] tileSelection = new int[1][1];
    protected static Point selectionStart = new Point(-1, -1);
    protected static Point selectionEnd = new Point(-1, -1);
    
    protected static int selectionWidth;
    protected static int selectionHeight;
    
    protected static boolean selectingTiles;
    
    private static Point mousePosition = new Point(-1, -1);
    
    private static int gridX = 32, gridY = 32;
    
    protected static int selectedSprite;
    
    private static int mode = MODE_TILE;
    
    private static final ToolInformation toolInformation = new ToolInformation();
    private static ChangeListener toolInformationListener;
    
    private static TileChangeListener tileChangeListener;
    
    private static ToolModeListener toolModeListener;
    
    private static SpritePlacementListener spritePlacementListener;
    
    public static boolean inTileMode() {
        return mode == MODE_TILE;
    } // TODO Cleanup: delete this
    
    // TODO I don't think it's necessary to update the mouse position in these two methods. Check if it is, if not make them abstract
    public void mousePressed(MouseEvent e) {
        mousePosition = e.getPoint();
        
        TileUtils.alignPointToGrid(mousePosition);
    }
    public void mouseReleased(MouseEvent e) {
        mousePosition = e.getPoint();
        
        TileUtils.alignPointToGrid(mousePosition);
    }
    
    /*
        NOTE: Child classes need to call the following two classes when overriding them, so the mouse position keeps being updated.
     */
    public void mouseMoved(MouseEvent e) {
        mousePosition = e.getPoint();
        
        TileUtils.alignPointToGrid(mousePosition);
        
        updateToolInformation(e.getPoint());
    }
    public void mouseDragged(MouseEvent e) {
        mousePosition = e.getPoint();
        
        TileUtils.alignPointToGrid(mousePosition);
        
        updateToolInformation(e.getPoint());
    }
    
    public Point getMousePosition() {
        return mousePosition;
    }
    
    public void setUseSelectionGrid(boolean useSelectionSizeGrid) {
        if (useSelectionSizeGrid) {
            setGridSize(selectionWidth, selectionHeight);
        } else {
            setGridSize(32, 32);
        }
    }
    
    public abstract void draw(Graphics2D g);
    
    protected void placeTile(Point pos, int tileId) {
        placeTile(pos.x, pos.y, tileId);
    }
    
    // TODO Adjust multiselection position (has this been fixed?)
    protected void placeTile(int x, int y, int tileId) {
        // TODO Adjust for zoom
        x = x / 32;
        y = y / 32;
        
        int[][] oldData = new int[1][1];
        
        // TODO Don't use hardcoded values
        // TODO should it be <= widht <= height or < width < height?
        if (x >= 0 && y >= 0 && x <= 256 && y <= 224) {
            if (selectedLayer == Layer.BOTH) selectedLayer = Layer.FOREGROUND;
            
            oldData[0][0] = map.getTileAt(selectedLayer, x, y);
            
            //map.getLayers().get(selectedLayer)[y][x] = tileId;
            map.setTileAt(selectedLayer, x, y, tileId);
            
            tileChangeListener.tileChanged(x, y, tileId);
            
            int[][] newData = {{tileId}};
            UndoManager.addUndoAction(new UndoAction(ActionType.UNDO_PLACE_TILE, oldData, newData, selectedLayer, x, y));
        }
    }
    
    // TODO Add grid size?
    protected void placeTiles(Point position) {
        int px = ((position.x / gridX * gridX) - (getSelectionWidth() * gridX) / 2) + (gridX / 2);
        int py = ((position.y / gridY * gridY) - (getSelectionHeight() * gridY) / 2) + (gridY / 2);
        
        int[][] oldData = new int[selectionHeight][selectionWidth];
        
        var newPos = new Point();
        for (int sx = 0; sx < getSelectionWidth(); sx++) {
            for (int sy = 0; sy < getSelectionHeight(); sy++) {
                newPos.x = px + (sx * gridX);
                newPos.y = py + (sy * gridY);
                
                oldData[sy][sx] = getTileAt(selectedLayer, newPos);
                
                placeTile(newPos, tileSelection[sy][sx]);
            }
        }
    
        UndoManager.addUndoAction(new UndoAction(ActionType.UNDO_PLACE_TILE, oldData, tileSelection, selectedLayer, px / 32, py / 32));
    }
    
    protected int getTileAt(int layer, Point position) {
        int x = position.x / 32;
        int y = position.y / 32;

        int tile = 255;
        
        if (layer == Layer.BOTH) layer = Layer.FOREGROUND;
        
        if (map != null) {
            if (x >= 0 && y >= 0 && x < PK2Map13.WIDTH && y < PK2Map13.HEIGHT) {
                tile = map.getLayers().get(layer)[y][x];
            }
        }
        
        return tile;
    }
    
    protected int getSpriteAt(Point position) {
        TileUtils.convertToMapCoordinates(position);
    
        int spr = selectedSprite;
        
        if (map != null) {
            if (position.x >= 0 && position.x < Settings.getMapProfile().getMapWidth() && position.y >= 0 && position.y < Settings.getMapProfile().getMapHeight()) {
                spr = map.getSpritesLayer()[position.y][position.x]; // Should use map.getSpriteAt to check bounds, but whatever
            }
        }
        
        return spr;
    }
    
    protected void placeSprite(Point position, int newSprite) {
        TileUtils.convertToMapCoordinates(position);
    
        if (position.x >= 0 && position.x <= Settings.getMapProfile().getMapWidth() && position.y >= 0 && position.y <= Settings.getMapProfile().getMapHeight()) {
            int oldSprite = map.getSpriteIdAt(position.x, position.y);
            
            int[][] oldData = {{ oldSprite }};
            int[][] newData = {{ newSprite }};
            
            PK2Sprite spriteOld = map.getSpriteAt(position.x, position.y);
            PK2Sprite spriteNew = map.getSprite(newSprite);
            
            if (oldSprite != 255) {
                if (newSprite != oldSprite) {
                    if (spriteOld != null) {
                        spriteOld.decreasePlacedAmount();
                    }
                    
                    if (spriteNew != null) spriteNew.increasePlacedAmount();
                }
            } else {
                if (spriteNew != null) spriteNew.increasePlacedAmount();
            }
            
            spritePlacementListener.placed(newSprite);
            map.setSpriteAt(position.x, position.y, newSprite);
            
            UndoManager.addUndoAction(new UndoAction(ActionType.UNDO_PLACE_SPRITE, oldData, newData, -1, position.x, position.y));
        }
    }
    
    protected void placeSprite(Point position) {
        placeSprite(position, selectedSprite);
    }
    
    public void setGridSize(int x, int y) {
        gridX = x;
        gridY = y;
    }
    
    public int getSelectionWidth() {
        return selectionWidth;
    }
    
    public int getSelectionHeight() {
        return selectionHeight;
    }
    
    public static void setSelectionSize(int width, int height) {
        selectionWidth = width;
        selectionHeight = height;
    }
    
    public static void setSelection(int[][] selection) {
        tileSelection = selection;
        
        /*
        gridX = selectionWidth;
        gridY = selectionHeight;*/
    }
    
    public void setMapPanelPainter(MapPanelPainter painter) {
        this.mapPainter = painter;
    }
    
    public MapPanelPainter getMapPanelPainter() {
        return mapPainter;
    }
    
    public static void setTileChangeListener(TileChangeListener listener) {
        tileChangeListener = listener;
    }
    
    public static void setMap(PK2Map m) {
        map = m;
        
        reset();
    }
    
    private static void reset() {
        setMode(MODE_TILE);
        setSelectionSize(1, 1);
        setSelection(new int[1][1]);
        setSelectedSprite(0);
    }
    
    public static void setSelectedLayer(int layer) {
        selectedLayer = layer;
    }
    
    // Delete this
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof MapPanelModel) {
            if (evt.getPropertyName().equals("selectedLayer")) {

                selectedLayer = (int) evt.getNewValue();
            }
        } else if (evt.getSource() instanceof MapData) {
            if (evt.getPropertyName().equals("map")) {
                map = (PK2Map) evt.getNewValue();
            }
        }
    }
    
    public static void setSelectedSprite(PK2Sprite newSprite) {
        // TODO Change grid size to size of selected sprite?
        
        for (int i = 0; i < map.getSpriteList().size(); i++) {
            if (map.getSpriteList().get(i) == newSprite) {
                selectedSprite = i;
                
                break;
            }
        }
    }
    
    public static void setToolInformationListener(ChangeListener listener) {
        toolInformationListener = listener;
    }
    
    private void updateToolInformation(Point mousePosition) {
        toolInformation.setX(mousePosition.x);
        toolInformation.setY(mousePosition.y);
        
        toolInformation.setForegroundTile(getTileAt(Layer.FOREGROUND, mousePosition));
        toolInformation.setBackgroundTile(getTileAt(Layer.BACKGROUND, mousePosition));
        
        int sprId = getSpriteAt(mousePosition);
        toolInformation.setSpriteId(sprId);
        
        String sprFilename = "none";
        if (sprId != 255) {
            if (map != null) {
                sprFilename = map.getSpriteList().get(sprId).getFilename();
            }
        }
        
        toolInformation.setSpriteFilename(sprFilename);
        
        toolInformationListener.stateChanged(new ChangeEvent(this));
    }
    
    public static ToolInformation getToolInformation() {
        return toolInformation;
    }
    
    public static void setSelectedSprite(int index) {
        selectedSprite = index;
    }
 
    public int getMode() {
        return mode;
    }
    
    public static void setMode(int m) {
        mode = m;
        
        toolModeListener.changeMode(mode);
    }
    
    public static void setToolModeListener(ToolModeListener listener) {
        toolModeListener = listener;
    }
    
    public static void setSpritePlacementListener(SpritePlacementListener listener) {
        spritePlacementListener = listener;
    }
}
