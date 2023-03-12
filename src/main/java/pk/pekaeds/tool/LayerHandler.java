package pk.pekaeds.tool;

import pk.pekaeds.data.Layer;
import pk.pekaeds.pk2.map.PK2Map;
import pk.pekaeds.pk2.map.PK2Map13;
import pk.pekaeds.pk2.sprite.PK2Sprite;
import pk.pekaeds.settings.Settings;
import pk.pekaeds.ui.listeners.SpritePlacementListener;
import pk.pekaeds.ui.listeners.TileChangeListener;
import pk.pekaeds.util.TileUtils;
import pk.pekaeds.util.undoredo.ActionType;
import pk.pekaeds.util.undoredo.UndoAction;
import pk.pekaeds.util.undoredo.UndoManager;

import java.awt.*;

public final class LayerHandler {
    private final ToolSelection selection;
    private PK2Map map;

    private int gridX = 32, gridY = 32;
    private int currentLayer;

    private TileChangeListener tileChangeListener;
    private SpritePlacementListener spritePlacementListener;

    public LayerHandler(ToolSelection toolSelection) {
        this.selection = toolSelection;
    }

    public void placeTile(int x, int y, int tileId, int layer) {
        placeTile(x, y, tileId, layer, true);
    }

    public void placeTile(int x, int y, int tileId, int layer, boolean pushUndo) {
        x = x / 32;
        y = y / 32;

        if (layer == Layer.BOTH) layer = Layer.FOREGROUND;

        if (x >= 0 && y >= 0 && x < PK2Map13.WIDTH && y < PK2Map13.HEIGHT) {
            var oldData = new int[][] {{ map.getTileAt(layer, x, y )}};

            map.setTileAt(layer, x, y, tileId);

            tileChangeListener.tileChanged(x, y, tileId);

            if (pushUndo) UndoManager.addUndoAction(new UndoAction(ActionType.UNDO_PLACE_TILE, oldData, selection.getTileSelection(), layer, x, y));
        }
    }

    public void placeTiles(Point position) {
        int px = ((position.x / gridX * gridX) - (selection.getWidth() * gridX) / 2) + (gridX / 2);
        int py = ((position.y / gridY * gridY) - (selection.getHeight() * gridY) / 2) + (gridY / 2);

        int[][] oldData = new int[selection.getHeight()][selection.getWidth()];

        int layer = currentLayer == Layer.BOTH ? Layer.FOREGROUND : currentLayer;

        var newPos = new Point();
        for (int sx = 0; sx < selection.getWidth(); sx++) {
            for (int sy = 0; sy < selection.getHeight(); sy++) {
                newPos.x = px + (sx * gridX);
                newPos.y = py + (sy * gridY);

                oldData[sy][sx] = getTileAt(layer, newPos);

                placeTile(newPos.x, newPos.y, selection.getTileSelection()[sy][sx], layer, false);
            }
        }

        UndoManager.addUndoAction(new UndoAction(ActionType.UNDO_PLACE_TILE, oldData, selection.getTileSelection(), layer, px / 32, py / 32));
    }

    public void placeTiles(Point position, int layer, int[][] tiles) {
        int selectionWidth = tiles[0].length;
        int selectionHeight = tiles.length;

        int px = ((position.x / gridX * gridX) - (selectionWidth * gridX) / 2) + (gridX / 2);
        int py = ((position.y / gridY * gridY) - (selectionHeight * gridY) / 2) + (gridY / 2);

        int[][] oldData = new int[selectionHeight][selectionWidth];

        var newPos = new Point();
        for (int sx = 0; sx < selectionWidth; sx++) {
            for (int sy = 0; sy < selectionHeight; sy++) {
                newPos.x = px + (sx * gridX);
                newPos.y = py + (sy * gridY);

                oldData[sy][sx] = getTileAt(layer, newPos);

                placeTile(newPos.x, newPos.y, tiles[sy][sx], layer, false);
            }
        }

        UndoManager.addUndoAction(new UndoAction(ActionType.UNDO_PLACE_TILE, oldData, tiles, layer, px / 32, py / 32));
    }

    public int getTileAt(int layer, Point position) {
        return getTileAt(layer, position.x / 32, position.y / 32);
    }

    public int getTileAt(int layer, int x, int y) {
        int tile = 255;

        if (layer == Layer.BOTH) layer = Layer.FOREGROUND;

        if (map != null) {
            if (x >= 0 && y >= 0 && x < PK2Map13.WIDTH && y < PK2Map13.HEIGHT) {
                tile = map.getLayers().get(layer)[y][x];
            }
        }

        return tile;
    }

    public int[][] getTilesFromRect(Rectangle selectionRect, int layer) {
        var tempSelection = new int[selectionRect.height][selectionRect.width];

        for (int sx = 0; sx < selectionRect.width; sx++) {
            for (int sy = 0; sy < selectionRect.height; sy++) {
                tempSelection[sy][sx] = getTileAt(layer, selectionRect.x + sx, selectionRect.y + sy);
            }
        }

        return tempSelection;
    }

    // TODO For remove*Area, handle unsavedChanges
    public void removeTilesArea(Rectangle area, int layer) {
        for (int sx = 0; sx < area.width; sx++) {
            for (int sy = 0; sy < area.height; sy++) {
                map.getLayers().get(layer)[area.y + sy][area.x + sx] = 255;
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
            spr = map.getSpriteIdAt(x / 32, y / 32);
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

    public void placeSprite(Point position) {
        placeSprite(position, selection.getSelectionSprites()[0][0]);
    }

    public void removeSpritesArea(Rectangle area) {
        for (int sx = 0; sx < area.width; sx++) {
            for (int sy = 0; sy < area.height; sy++) {
                map.getSpritesLayer()[area.y + sy][area.x + sx] = 255; // TODO Handle undo
            }
        }

        // TODO Handle UndoManager
    }

    public void setMap(PK2Map m) {
        this.map = m;
    }

    public void setGrid(int x, int y) {
        this.gridX = gridX;
        this.gridY = gridY;
    }

    public void setCurrentLayer(int layer) {
        this.currentLayer = layer;
    }

    public void setTileChangeListener(TileChangeListener listener) {
        this.tileChangeListener = listener;
    }

    public void setSpritePlacementListener(SpritePlacementListener listener) {
        this.spritePlacementListener = listener;
    }

    public void placeSprites(Point point, int[][] spritesLayer) {
        // TODO Start an undo block? Look into that. Gonna do a simple implementation for now.

        for (int x = 0; x < spritesLayer[0].length; x++) {
            for (int y = 0; y < spritesLayer.length; y++) {
                placeSprite(point, spritesLayer[y][x]);
            }
        }
    }
}
