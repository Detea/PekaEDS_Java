package pekaeds.tool.tools;

import javax.swing.*;

import pekaeds.pk2.map.PK2Map13;
import pekaeds.settings.Settings;
import pekaeds.tool.Tool;
import pekaeds.util.TileUtils;

import java.awt.*;
import java.awt.event.MouseEvent;

// TODO Add sprite selection
public class SelectionTool extends Tool {
    private Rectangle selectionRect = new Rectangle(-1, -1, 0, 0);
    
    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        
        if (SwingUtilities.isRightMouseButton(e)) {
            selection.setStart(e.getPoint());
            selection.setEnd(e.getPoint());

            selectionRect = TileUtils.calculateSelectionRectangle(e.getPoint(), e.getPoint());
            
            if (getMode() == Tool.MODE_TILE) selectingTiles = true;
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (layerHandler.getSpriteAt(e.getPoint()) == 255 || !Settings.showSprites()) {
            setMode(MODE_TILE);
    
            doTileSelection();
        } else {
            setMode(MODE_SPRITE);
    
            doSpriteSelection();
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        
        if (SwingUtilities.isRightMouseButton(e)) {
            selection.setEnd(e.getPoint());
            
            if (selection.getEnd().x < 0) selection.getEnd().x = 0;
            if (selection.getEnd().y < 0) selection.getEnd().y = 0;
            
            if (selection.getEnd().x >= PK2Map13.WIDTH * 32) selection.getEnd().x = (PK2Map13.WIDTH * 32) - 32;
            if (selection.getEnd().y >= PK2Map13.HEIGHT * 32) selection.getEnd().y = (PK2Map13.HEIGHT * 32) - 32;
            
            selectionRect = TileUtils.calculateSelectionRectangle(selection.getStart(), selection.getEnd());
        }
    }
    
    private void doTileSelection() {
        selectionRect = TileUtils.calculateSelectionRectangle(selection.getStart(), selection.getEnd());

        selection.setTileSelection(layerHandler.getTilesFromRect(selectionRect, selectedLayer));

        selectionRect.x = -1;
        selectionRect.y = -1;
        selectionRect.width = 0;
        selectionRect.height = 0;

        selectingTiles = false;
    }
    
    private void doSpriteSelection() {
        selectionRect = TileUtils.calculateSelectionRectangle(selection.getStart(), selection.getEnd());

        selection.setSelectionSprites(new int[][]{{ layerHandler.getSpriteAt(selection.getStart().x, selection.getStart().y) }}); // TODO Fix multiselection of sprites
    }

    @Override
    public void draw(Graphics2D g) {
        if (selectionRect.x != -1) {
            int x = selectionRect.x * 32;
            int y = selectionRect.y * 32;
            
            drawSelectionRect(g, x, y, selectionRect.width * 32, selectionRect.height * 32);
        }
    }
    
    @Override
    public void onSelect() {
    
    }
    
    @Override
    public void onDeselect(boolean ignorePrompts) {
    
    }
}
