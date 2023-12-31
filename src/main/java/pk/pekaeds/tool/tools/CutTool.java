package pk.pekaeds.tool.tools;

import pk.pekaeds.data.Layer;
import pk.pekaeds.tool.Tool;
import pk.pekaeds.tool.undomanager.ActionType;
import pk.pekaeds.tool.undomanager.UndoAction;
import pk.pekaeds.util.TileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public final class CutTool extends Tool {
    private static final Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    private static final Cursor moveCursor = new Cursor(Cursor.MOVE_CURSOR);

    private boolean cutForegroundLayer = true;
    private boolean cutBackgroundLayer = true;
    private boolean cutSpritesLayer = true;

    private int[][] foregroundLayer = new int[1][1];
    private int[][] backgroundLayer = new int[1][1];
    private int[][] spritesLayer = new int[1][1];

    private Point selectionStart;//, selectionEnd;
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
        super.mousePressed(e);
        
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
            if (selecting) selectionRect = TileUtils.calculateSelectionRectangle(selectionStart, e.getPoint());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            if (selecting) {
                getUndoManager().startBlock();
                
                if (cutForegroundLayer) {
                    foregroundLayer = layerHandler.getTilesFromRect(selectionRect, Layer.FOREGROUND);
        
                    getUndoManager().pushTilePlaced(this, ActionType.CUT_TOOL_CUT_FOREGROUND, selectionRect.x * 32, selectionRect.y * 32, foregroundLayer, layerHandler.getTilesFromRect(selectionRect, Layer.FOREGROUND), Layer.FOREGROUND);
        
                    layerHandler.removeTilesArea(selectionRect, Layer.FOREGROUND);
                }
    
                if (cutBackgroundLayer) {
                    backgroundLayer = layerHandler.getTilesFromRect(selectionRect, Layer.BACKGROUND);
        
                    getUndoManager().pushTilePlaced(this, ActionType.CUT_TOOL_CUT_BACKGROUND, selectionRect.x * 32, selectionRect.y * 32, backgroundLayer, layerHandler.getTilesFromRect(selectionRect, Layer.BACKGROUND), Layer.BACKGROUND);
        
                    layerHandler.removeTilesArea(selectionRect, Layer.BACKGROUND);
                }
    
                if (cutSpritesLayer) {
                    spritesLayer = layerHandler.getSpritesFromRect(selectionRect);
        
                    getUndoManager().pushSpritePlaced(this, ActionType.CUT_TOOL_CUT_SPRITES, selectionRect.x * 32, selectionRect.y * 32, spritesLayer, layerHandler.getSpritesFromRect(selectionRect));
        
                    layerHandler.removeSpritesArea(selectionRect);
                }
    
                getUndoManager().endBlock();
    
                selecting = false;
    
                if (isSelectionPresent()) {
                    getMapPanelPainter().setCursor(moveCursor);
                } else {
                    getMapPanelPainter().setCursor(defaultCursor);
                }
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
                    getUndoManager().undoLastAction();
                }
            } else {
                getUndoManager().undoLastAction();
            }
            
            resetCut();
        }
    }
    
    private void finalizeCut() {
        getUndoManager().startBlock();
        
        if (cutForegroundLayer) {
            getUndoManager().pushTilePlaced(this, ActionType.CUT_TOOL_PLACE_FOREGROUND, selectionRect.x * 32, selectionRect.y * 32, foregroundLayer, layerHandler.getTilesFromRect(selectionRect, Layer.FOREGROUND), Layer.FOREGROUND);
            
            layerHandler.placeTilesScreen(selectionRect.x * 32, selectionRect.y * 32, Layer.FOREGROUND, foregroundLayer);
        }
    
        if (cutBackgroundLayer) {
            getUndoManager().pushTilePlaced(this, ActionType.CUT_TOOL_PLACE_BACKGROUND, selectionRect.x * 32, selectionRect.y * 32, backgroundLayer, layerHandler.getTilesFromRect(selectionRect, Layer.BACKGROUND), Layer.BACKGROUND);
    
            layerHandler.placeTilesScreen(selectionRect.x * 32, selectionRect.y * 32, Layer.BACKGROUND, backgroundLayer);
        }
    
        if (cutSpritesLayer) {
            getUndoManager().pushSpritePlaced(this, ActionType.CUT_TOOL_PLACE_SPRITES, selectionRect.x * 32, selectionRect.y * 32, spritesLayer, layerHandler.getSpritesFromRect(selectionRect));
            
            layerHandler.placeSprites(selectionRect.x, selectionRect.y, spritesLayer);
        }
        
        getUndoManager().endBlock();
    }
    
    private void resetCut() {
        selectionRect.setRect(0, 0, 0, 0);
    
        getMapPanelPainter().setCursor(defaultCursor);
    }
    
    @Override
    public void onUndo(UndoAction action) {
        super.onUndo(action);
        
        // If the user undoes the placement of the cut selection, restore the old tiles and restore the selection
        if (doesActionPlace(action)) {
            switch (action.getType()) {
                case CUT_TOOL_PLACE_FOREGROUND -> foregroundLayer = action.getNewTiles();
                case CUT_TOOL_PLACE_BACKGROUND -> backgroundLayer = action.getNewTiles();
                case CUT_TOOL_PLACE_SPRITES -> spritesLayer = action.getNewTiles();
            }
    
            selectionRect.setRect(action.getX() / 32, action.getY() / 32, action.getNewTiles()[0].length, action.getNewTiles().length);
    
            getMapPanelPainter().setCursor(moveCursor);
            getMapPanelPainter().repaint();
        } else { // Otherwise, if they have made a cut, only restore the cut tiles
            resetCut();
        }
    }
    
    private boolean doesActionPlace(UndoAction action) {
        return action.getType() == ActionType.CUT_TOOL_PLACE_FOREGROUND ||
                action.getType() == ActionType.CUT_TOOL_PLACE_BACKGROUND ||
                action.getType() == ActionType.CUT_TOOL_PLACE_SPRITES;
    }
    
    @Override
    public void onRedo(UndoAction action) {
        super.onRedo(action);
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
