package pk.pekaeds.tool.tools;

import pk.pekaeds.data.Layer;
import pk.pekaeds.tool.Tool;
import pk.pekaeds.util.TileUtils;
import pk.pekaeds.util.undoredo.ActionType;
import pk.pekaeds.util.undoredo.UndoAction;
import pk.pekaeds.util.undoredo.UndoManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
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
    
    private void moveSelectionTo(Point position, int xOffset, int yOffset) {
        int mx = position.x / 32;
        int my = position.y / 32;
        
        int x = mx - xOffset;
        int y = my - yOffset;
        
        selectionRect.x = x;
        selectionRect.y = y;
    }

    private int clickXOffset, clickYOffset;
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            if (isSelectionPresent()) {
                finalizeCut();
                resetCut();
            } else {
                selecting = true;
    
                selectionStart = e.getPoint();
            }
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (isSelectionPresent()) {
                int mx = e.getX() / 32;
                int my = e.getY() / 32;
                
                if (!selectionRect.contains(mx, my)) {
                    selectionRect.setLocation(mx - (selectionRect.width / 2), my - (selectionRect.height / 2));
                } else {
                    clickXOffset = mx - selectionRect.x;
                    clickYOffset = my - selectionRect.y;
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            moveSelectionTo(e.getPoint(), clickXOffset, clickYOffset);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            selectionRect = TileUtils.calculateSelectionRectangle(selectionStart, e.getPoint());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            if (cutForegroundLayer) {
                foregroundLayer = layerHandler.getTilesFromRect(selectionRect, Layer.FOREGROUND);
                layerHandler.removeTilesArea(selectionRect, Layer.FOREGROUND);
                
                //UndoManager.addUndoAction(new UndoAction(ActionType.UNDO_CUT_TILES, foregroundLayer, Layer.FOREGROUND, selectionRect.x, selectionRect.y));
            }

            if (cutBackgroundLayer) {
                backgroundLayer = layerHandler.getTilesFromRect(selectionRect, Layer.BACKGROUND);
                layerHandler.removeTilesArea(selectionRect, Layer.BACKGROUND);
    
                //UndoManager.addUndoAction(new UndoAction(ActionType.UNDO_CUT_TILES, backgroundLayer, Layer.BACKGROUND, selectionRect.x, selectionRect.y));
            }

            if (cutSpritesLayer) {
                spritesLayer = layerHandler.getSpritesFromRect(selectionRect);
                layerHandler.removeSpritesArea(selectionRect); // TODO Test if sprite placed counter decreases
                
                //UndoManager.addUndoAction(new UndoAction(ActionType.UNDO_CUT_SPRITES, spritesLayer, Layer.SPRITES, selectionRect.x, selectionRect.y));
            }

            selecting = false;
            
            if (isSelectionPresent()) {
                getMapPanelPainter().setCursor(moveCursor);
            } else {
                getMapPanelPainter().setCursor(defaultCursor);
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (selecting) {
            // Check isSelectionPresent() in here because if I don't the selection rect still gets drawn at 0,0 with a width and height of 1, for whatever reason.
            if (isSelectionPresent()) drawSelectionRect(g, selectionRect.x * 32, selectionRect.y * 32, selectionRect.width * 32, selectionRect.height * 32);
        } else if (isSelectionPresent()) {
            if (cutBackgroundLayer) drawLayer(g, backgroundLayer, selectionRect.x * 32, selectionRect.y * 32);
            if (cutForegroundLayer) drawLayer(g, foregroundLayer, selectionRect.x * 32, selectionRect.y * 32);
            if (cutSpritesLayer) drawSelectedSprites(g, selectionRect);

            drawSelectionRect(g, selectionRect.x * 32, selectionRect.y * 32, selectionRect.width * 32, selectionRect.height * 32);
        }
    }
    
    @Override
    public void onSelect() {
        if (isSelectionPresent()) {
            getMapPanelPainter().setCursor(moveCursor);
        }
    }
    
    @Override
    public void onDeselect(boolean ignorePrompts) {
        getMapPanelPainter().setCursor(defaultCursor);
        
        if (isSelectionPresent()) {
            if (!ignorePrompts) {
                int res = JOptionPane.showConfirmDialog(null, "Cut selection has not been placed. Do you want to place it?", "Selection hasn't been placed", JOptionPane.YES_NO_OPTION);
    
                if (res == JOptionPane.YES_OPTION) {
                    finalizeCut();
                } else {
                    undoCut();
                }
            } else {
                undoCut();
            }
            
            resetCut();
        }
    }
    
    private void finalizeCut() {
        if (cutForegroundLayer) {
            layerHandler.placeTiles(selectionRect.x * 32, selectionRect.y * 32, Layer.FOREGROUND, foregroundLayer);
        
            // TODO UNDO
        }
    
        if (cutBackgroundLayer) {
            layerHandler.placeTiles(selectionRect.x * 32, selectionRect.y * 32, Layer.BACKGROUND, backgroundLayer);
        }
    
        if (cutSpritesLayer) {
            layerHandler.placeSprites(selectionRect.x * 32, selectionRect.y * 32, spritesLayer); // TODO Test if sprite placed counter increases
        }
    }
    
    private void resetCut() {
        selectionRect.setRect(0, 0, 0, 0);
    
        getMapPanelPainter().setCursor(defaultCursor);
    }
    
    public void undoCut() {
        int startx = selectionStart.x / 32;
        int starty = selectionStart.y / 32;
    
        for (int y = 0; y < selectionRect.height; y++) {
            for (int x = 0; x < selectionRect.width; x++) {
                if (cutForegroundLayer) map.setTileAt(Layer.FOREGROUND, startx + x, starty + y, foregroundLayer[y][x]);
                if (cutBackgroundLayer) map.setTileAt(Layer.BACKGROUND, startx + x, starty + y, backgroundLayer[y][x]);
                if (cutSpritesLayer) map.setSpriteAt(startx + x, starty + y, spritesLayer[y][x]);
            }
        }
        
        getMapPanelPainter().repaint();
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
    
    private boolean isSelectionPresent() {
        return selectionRect.width > 0 && selectionRect.height > 0;
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
