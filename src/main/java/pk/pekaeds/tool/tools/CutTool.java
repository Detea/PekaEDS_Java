package pk.pekaeds.tool.tools;

import pk.pekaeds.data.Layer;
import pk.pekaeds.tool.Tool;
import pk.pekaeds.util.TileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public final class CutTool extends Tool {
    private boolean cutForegroundLayer = true;
    private boolean cutBackgroundLayer = true;
    private boolean cutSpritesLayer = true;

    private int[][] foregroundLayer = null;
    private int[][] backgroundLayer = null;
    private int[][] spritesLayer = null;

    private Point selectionStart, selectionEnd;
    private Rectangle selectionRect = new Rectangle();
    private boolean selecting = false;
    private boolean cutSelection = true;

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);

        if (SwingUtilities.isLeftMouseButton(e)) {
            selectionRect = new Rectangle();

            selectionStart = e.getPoint();
            selecting = true;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);

        if (SwingUtilities.isLeftMouseButton(e)) {
            selectionEnd = e.getPoint();

            selectionRect = TileUtils.calculateSelectionRectangle(selectionStart, selectionEnd);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);

        if (SwingUtilities.isLeftMouseButton(e)) {
            if (selecting) {
                selectionRect = TileUtils.calculateSelectionRectangle(selectionStart, selectionEnd);

                if (cutForegroundLayer) foregroundLayer = layerHandler.getTilesFromRect(selectionRect, Layer.FOREGROUND);
                if (cutBackgroundLayer) backgroundLayer = layerHandler.getTilesFromRect(selectionRect, Layer.BACKGROUND);
                if (cutSpritesLayer)    spritesLayer = layerHandler.getSpritesFromRect(selectionRect);

                selecting = false;

                // TODO Handle cursor for all tools, not just this one.
                //getMapPanelPainter().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            } else {

            }
        }
    }

    /*
    private int[][] getTiles(int layer) {
        int[][] tiles = new int[selectionRect.height][selectionRect.width];

        for (int x = 0; x < selectionRect.width; x++) {
            for (int y = 0; y < selectionRect.height; y++) {
                tiles[y][x] = layerHandler.getTileAt(layer, selectionRect.x + x, selectionRect.y + y);
            }
        }

        return tiles;
    }*/

    // Probably should have put all three layers into one list, so that I could just reuse getTiles(), but oh well.
    private int[][] getSprites() {
        int[][] sprites = new int[selectionRect.height][selectionRect.width];

        for (int x = 0; x < selectionRect.width; x++) {
            for (int y = 0; y < selectionRect.height; y++) {
                sprites[y][x] = layerHandler.getSpriteAt(selectionRect.x + x, selectionRect.y + y);
            }
        }

        return sprites;
    }

    @Override
    public void draw(Graphics2D g) {
        if (selecting) {
            drawSelectionRect(g, selectionRect.x * 32, selectionRect.y * 32, selectionRect.width * 32, selectionRect.height * 32);
        } else {
            // TODO handle different layers?
            if (cutForegroundLayer && foregroundLayer != null) {
                drawLayer(g, foregroundLayer, getMousePosition().x, getMousePosition().y);
            }
        }
    }

    private void drawLayer(Graphics2D g, int[][] layer, int startX, int startY) {
        for (int x = 0; x < layer[0].length; x++) {
            for (int y = 0; y < layer.length; y++) {
                getMapPanelPainter().drawTile(g, startX + (x * 32), startY + (y * 32), layer[y][x]);
            }
        }
    }

    public boolean cutForegroundLayer() {
        return cutForegroundLayer;
    }

    public void setCutForegroundLayer(boolean cutForegroundLayer) {
        this.cutForegroundLayer = cutForegroundLayer;
    }

    public boolean cutBackgroundLayer() {
        return cutBackgroundLayer;
    }

    public void setCutBackgroundLayer(boolean cutBackgroundLayer) {
        this.cutBackgroundLayer = cutBackgroundLayer;
    }

    public boolean cutSpritesLayer() {
        return cutSpritesLayer;
    }

    public void setCutSpritesLayer(boolean cutSpritesLayer) {
        this.cutSpritesLayer = cutSpritesLayer;
    }

    public void setCutSelection(boolean cut) {
        this.cutSelection = cut;
    }

    public boolean cutSelection() {
        return cutSelection;
    }
}
