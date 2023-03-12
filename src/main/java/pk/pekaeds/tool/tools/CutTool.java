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
    private static final Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    private static final Cursor moveCursor = new Cursor(Cursor.MOVE_CURSOR);

    private boolean cutForegroundLayer = true;
    private boolean cutBackgroundLayer = true;
    private boolean cutSpritesLayer = true;

    private int[][] foregroundLayer = new int[1][1];
    private int[][] backgroundLayer = new int[1][1];
    private int[][] spritesLayer = new int[1][1];

    private Point selectionStart, selectionEnd;
    private Rectangle selectionRect = new Rectangle();
    private boolean selecting = false;
    private boolean cutSelection = true;

    public CutTool() {
        useRightMouseButton = true;
    }

    /*
        TODO Add keyboard shortcut

        User selects an area they want to cut. They draw the selection, the cursor changes to the move tool.
        Then the user can drag the selection around and place it where ever they like and to finalize the movement hit enter(?)

        Draw an outline around the selection, while it is active, remove it when it has been placed.

            -Draw selection rect, when user draws/releases right mousebutton
            -Let user move (drag) selection around
        Remove tiles below original selection

        When user hits ENTER place selection
        When user changes tools revert changes back
        When user has selected an area and pressed right mouse button, revert changes
     */

    private void moveSelectionTo(Point position) {
        int mx = position.x / 32;
        int my = position.y / 32;

        int x = mx - (selectionRect.width / 2);
        int y = my - (selectionRect.height / 2);

        selectionRect.x = x;
        selectionRect.y = y;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            selectionStart = e.getPoint();
            selectionEnd = e.getPoint();

            selecting = true;

            getMapPanelPainter().setCursor(defaultCursor);
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            moveSelectionTo(e.getPoint());
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //super.mouseDragged(e);

        if (SwingUtilities.isLeftMouseButton(e)) {
            moveSelectionTo(e.getPoint());
        } else if (SwingUtilities.isRightMouseButton(e)) {
            // TODO Keep selection in map bounds, in calculateSelectionRect?

            selectionRect = TileUtils.calculateSelectionRectangle(selectionStart, e.getPoint());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            if (cutForegroundLayer) {
                foregroundLayer = layerHandler.getTilesFromRect(selectionRect, Layer.FOREGROUND);

                layerHandler.removeTilesArea(selectionRect, Layer.FOREGROUND);
            }

            if (cutBackgroundLayer) {
                backgroundLayer = layerHandler.getTilesFromRect(selectionRect, Layer.BACKGROUND);

                layerHandler.removeTilesArea(selectionRect, Layer.BACKGROUND);
            }

            if (cutSpritesLayer) {
                spritesLayer = layerHandler.getSpritesFromRect(selectionRect);

                layerHandler.removeSpritesArea(selectionRect);
            }

            selecting = false;

            getMapPanelPainter().setCursor(moveCursor); // TODO Change cursor back when switching tools
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (selecting) {
            drawSelectionRect(g, selectionRect.x * 32, selectionRect.y * 32, selectionRect.width * 32, selectionRect.height * 32);
        } else {
            if (cutBackgroundLayer) drawLayer(g, backgroundLayer, selectionRect.x * 32, selectionRect.y * 32);
            if (cutForegroundLayer) drawLayer(g, foregroundLayer, selectionRect.x * 32, selectionRect.y * 32);
            if (cutSpritesLayer) drawSelectedSprites(g, selectionRect);

            drawSelectionRect(g, selectionRect.x * 32, selectionRect.y * 32, selectionRect.width * 32, selectionRect.height * 32);
        }
    }

    private void drawLayer(Graphics2D g, int[][] layer, int startX, int startY) {
        for (int x = 0; x < layer[0].length; x++) {
            for (int y = 0; y < layer.length; y++) {
                getMapPanelPainter().drawTile(g, startX + (x * 32), startY + (y * 32), layer[y][x]);
            }
        }
    }

    private void drawSelectedSprites(Graphics2D g, Rectangle selection) {
        for (int x = 0; x < selection.width; x++) {
            for (int y = 0; y < selection.height; y++) {
                if (spritesLayer[y][x] != 255) {
                    var spr = map.getSprite(spritesLayer[y][x]);

                    if (spr != null) {
                        getMapPanelPainter().drawSprite(g, spr,(selection.x + x) * 32, (selection.y + y) * 32);
                    }
                }
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
