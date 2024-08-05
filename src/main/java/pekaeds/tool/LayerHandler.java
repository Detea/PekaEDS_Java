package pekaeds.tool;

import java.awt.*;

import pekaeds.data.Layer;
import pekaeds.pk2.level.PK2LevelSector;
import pekaeds.ui.listeners.SpritePlacementListener;
import pekaeds.ui.listeners.TileChangeListener;
import pekaeds.util.TileUtils;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public final class LayerHandler {
    private final ToolSelection selection;
    private PK2LevelSector map;

    private int gridX = 32, gridY = 32;
    //private int currentLayer;

    private ChangeEvent changeEvent = new ChangeEvent(this);
    private ChangeListener changeListener;

    private TileChangeListener tileChangeListener;
    private SpritePlacementListener spritePlacementListener;

    public LayerHandler(ToolSelection toolSelection) {
        this.selection = toolSelection;
    }

    public void setTileAt(int layer, int x, int y, int tileID){

        switch (layer) {
            case Layer.BACKGROUND:
                this.map.setBGTile(x, y, tileID);


                break;
            
            case Layer.FOREGROUND:
                this.map.setFGTile(x, y, tileID);
                break;
            
            case Layer.SPRITES:
                this.map.setSpriteTile(x, y, tileID);
                break;

            case Layer.BOTH:
                this.map.setBGTile(x, y, tileID);
                this.map.setFGTile(x, y, tileID);
                break;
                
            default:
                break;
        }

        changeListener.stateChanged(changeEvent);

    }
    
    /**
     * Like placeTileMap, but adjusts the x and y values by dividing them by the size of a single tile (32).
     * Use this when x and y are screen/mouse coordinates.
     * @param x
     * @param y
     * @param tileId
     * @param layer
     */
    public void placeTileScreen(int x, int y, int tileId, int layer) {
        x /= gridX;
        y /= gridY;
        
        placeTile(x, y, tileId, layer);
    }
    
    public void placeTile(int x, int y, int tileId, int layer) {
        if (layer == Layer.BOTH) layer = Layer.FOREGROUND; // TODO I think this can go, but needs testing.

        if (x >= 0 && y >= 0 && x < map.getWidth() && y < map.getHeight()) {
            this.setTileAt(layer, x, y, tileId);

            tileChangeListener.tileChanged(x, y, tileId);
        }
    }
    
    public void placeTilesScreen(int x, int y, int layer, int[][] tiles) {
        int selectionWidth = tiles[0].length;
        int selectionHeight = tiles.length;
        
        for (int sx = 0; sx < selectionWidth; sx++) {
            for (int sy = 0; sy < selectionHeight; sy++) {
                int xx = x + (sx * 32);
                int yy = y + (sy * 32);
                
                placeTileScreen(xx, yy, tiles[sy][sx], layer);
            }
        }
    }

    public int getTileAt(int layer, Point position) {
        return getTileAt(layer, position.x / 32, position.y / 32);
    }

    public int getTileAt(int layer, int x, int y) {
        int tile = 255;

        if (layer == Layer.BOTH) layer = Layer.FOREGROUND;

        if (map != null) {
            if (x >= 0 && y >= 0 && x < map.getWidth() && y < map.getHeight()) {

                switch (layer) {
                    case Layer.BACKGROUND:
                        tile = map.getBGTile(x, y);                        
                        break;
                    
                    case Layer.BOTH:
                        tile = map.getFGTile(x, y);
                        if(tile==255){
                            tile = map.getBGTile(x, y);
                        }
                        break;
                    case Layer.FOREGROUND:
                        tile = map.getFGTile(x, y);
                        break;

                    case Layer.SPRITES:
                        tile = map.getSpriteTile(x, y);
                        break;

                    default:
                        break;
                }

                //tile = map.getLayers().get(layer)[y][x];
            }
        }

        return tile;
    }

    /**
     * Gets tile from an area, should be used with ToolSelection.
     *
     * selectionRect's values should be in map coordinates. Meaning they should be x >= 0; x < MAP_WIDTH, y >= 0; y < MAP_HEIGHT
     */
    public int[][] getTilesFromRect(Rectangle selectionRect, int layer) {
        var tempSelection = new int[selectionRect.height][selectionRect.width];

        for (int sx = 0; sx < selectionRect.width; sx++) {
            for (int sy = 0; sy < selectionRect.height; sy++) {
                tempSelection[sy][sx] = getTileAt(layer, selectionRect.x + sx, selectionRect.y + sy);
            }
        }

        return tempSelection;
    }
    
    /**
     * Gets tiles from an area. x and y position need to be in screen coordinates. They will be divided by the tile size (32)
     * @param x
     * @param y
     * @param width
     * @param height
     * @param layer
     * @return
     */
    public int[][] getTilesFromArea(int x, int y, int width, int height, int layer) {
        var tempSelection = new int[height][width];
        
        x /= 32;
        y /= 32;
        
        for (int sx = 0; sx < width; sx++) {
            for (int sy = 0; sy < height; sy++) {
                tempSelection[sy][sx] = getTileAt(layer, x + sx, y + sy);
            }
        }
        
        return tempSelection;
    }

    public void removeTilesArea(Rectangle area, int layer) {
        for (int sx = 0; sx < area.width; sx++) {
            for (int sy = 0; sy < area.height; sy++) {
               // map.getLayers().get(layer)[area.y + sy][area.x + sx] = 255;
               setTileAt(layer, area.x + sx, area.y + sy, 255);
            }
        }

        // TODO Handle UndoManager
    }

    public int getSpriteAt(Point position) {
        return getSpriteAt(position.x, position.y);
    }

    public int getSpriteAt(int x, int y) {
        int spr = selection.getSelectionSprites()[0][0];

        if (map != null) {
            spr = map.getSpriteTile(x / 32, y / 32);
        }

        return spr;
    }

    public int[][] getSpritesFromRect(Rectangle selectionRect) {
        var tempSelection = new int[selectionRect.height][selectionRect.width];

        for (int sx = 0; sx < selectionRect.width; sx++) {
            for (int sy = 0; sy < selectionRect.height; sy++) {
                tempSelection[sy][sx] = getSpriteAt((selectionRect.x + sx) * 32, (selectionRect.y + sy) * 32);
            }
        }

        return tempSelection;
    }

    public void placeSprite(Point position, int newSprite) {
        TileUtils.convertToMapCoordinates(position);

        if (position.x >= 0 && position.x <= map.getWidth() && position.y >= 0 && position.y <= map.getHeight()) {

            //TODO fix it
            
            /*int oldSprite = map.getSpriteTile(position.x, position.y);
            
            ISpritePrototype spriteOld = map.getSpriteAt(position.x, position.y);
            ISpritePrototype spriteNew = map.getSprite(newSprite);

            if (oldSprite != 255) {
                if (newSprite != oldSprite) {
                    if (spriteOld != null) {
                        spriteOld.decreasePlacedAmount();
                    }

                    if (spriteNew != null) spriteNew.increasePlacedAmount();
                }
            } else {
                if (spriteNew != null) spriteNew.increasePlacedAmount();
            }*/

            spritePlacementListener.placed(newSprite);
            map.setSpriteTile(position.x, position.y, newSprite);

            changeListener.stateChanged(changeEvent);
        }
    }
    
    public void placeSprite(Point position) {
        placeSprite(position, selection.getSelectionSprites()[0][0]);
    }
    
    public void placeSpritesScreen(int x, int y, int[][] sprites) {
        placeSprites(x / 32, y / 32, sprites);
    }
    
    /**
     * Places sprites on the map, coordinates are within map bounds x: >=0, < 256, y: >=0, < 224
     * @param x
     * @param y
     * @param spritesLayer
     */
    public void placeSprites(int x, int y, int[][] spritesLayer) {
        for (int sy = 0; sy < spritesLayer.length; sy++) {
            for (int sx = 0; sx < spritesLayer[0].length; sx++) {
                int xAdjusted = x + sx;
                int yAdjusted = y + sy;
                
                map.setSpriteTile(xAdjusted, yAdjusted, spritesLayer[sy][sx]);
            }
        }
    }
    
    public int[][] getSpritesFromArea(int x, int y, int width, int height) {
        var sprites = new int[height][width];
        
        x /= 32;
        y /= 32;
        
        for (int yy = 0; yy < height; yy++) {
            for (int xx = 0; xx < width; xx++) {
                sprites[yy][xx] = map.getSpriteTile(x + xx, y + yy);
            }
        }
        
        return sprites;
    }
    
    public void removeSpritesArea(Rectangle area) {
        for (int sx = 0; sx < area.width; sx++) {
            for (int sy = 0; sy < area.height; sy++) {
                
                /*int xAdjusted = area.x + sx;
                int yAdjusted = area.y + sy;
                
                var sprOld = map.getSpriteAt(xAdjusted, yAdjusted);
                if (sprOld != null) sprOld.decreasePlacedAmount();*/
                
                map.setSpriteTile(area.x + sx, area.y + sy, 255); // TODO Handle undo
            }
        }

        // TODO Handle UndoManager
    }
    
    public void setMap(PK2LevelSector m) {
        this.map = m;
    }

    public void setGrid(int x, int y) {
        this.gridX = x;
        this.gridY = y;
    }

    /*public void setCurrentLayer(int layer) {
        this.currentLayer = layer;
    }*/

    public void setTileChangeListener(TileChangeListener listener) {
        this.tileChangeListener = listener;
    }

    public void setChangeListener(ChangeListener listener){
        this.changeListener = listener;
    }

    public void setSpritePlacementListener(SpritePlacementListener listener) {
        this.spritePlacementListener = listener;
    }
}
