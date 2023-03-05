package pk.pekaeds.tool.tools;

import pk.pekaeds.data.Layer;
import pk.pekaeds.pk2.map.PK2Map13;
import pk.pekaeds.settings.Settings;
import pk.pekaeds.tool.Tool;
import pk.pekaeds.util.TileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

// TODO Add sprite selection
public class SelectionTool extends Tool {
    private Rectangle selectionRect = new Rectangle(-1, -1, 0, 0);
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            selectionStart = e.getPoint();
            selectionEnd = e.getPoint();
    
            selectionRect = TileUtils.calculateSelectionRectangle(selectionStart, selectionEnd);
            
            if (getMode() == Tool.MODE_TILE) selectingTiles = true;
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (getSpriteAt(e.getPoint()) == 255 || !Settings.showSprites()) {
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
            selectionEnd = e.getPoint();
            
            if (selectionEnd.x < 0) selectionEnd.x = 0;
            if (selectionEnd.y < 0) selectionEnd.y = 0;
            
            if (selectionEnd.x >= PK2Map13.WIDTH * 32) selectionEnd.x = (PK2Map13.WIDTH * 32) - 32;
            if (selectionEnd.y >= PK2Map13.HEIGHT * 32) selectionEnd.y = (PK2Map13.HEIGHT * 32) - 32;
            
            selectionRect = TileUtils.calculateSelectionRectangle(selectionStart, selectionEnd);
        }
    }
    
    private void doTileSelection() {
        selectionRect = TileUtils.calculateSelectionRectangle(selectionStart, selectionEnd);
        
        setSelectionSize(selectionRect.width, selectionRect.height);
        
        int[][] selection = new int[0][];
        
        // TODO Might want to add an option to choose?
        if (selectedLayer == Layer.BOTH) {
            selectedLayer = Layer.FOREGROUND;
        }
        
        if (selectionRect.width > 1 || selectionRect.height > 1) {
            selection = new int[selectionHeight][selectionWidth];
        
            Point tilePos = new Point(0, 0);
        
            selectionRect.x *= 32;
            selectionRect.y *= 32;
            
            for (int sx = 0; sx < selection[0].length; sx++) {
                for (int sy = 0; sy < selection.length; sy++) {
                    tilePos.setLocation(selectionRect.x + (sx * 32), selectionRect.y + (sy * 32));
                
                    selection[sy][sx] = getTileAt(selectedLayer, tilePos);
                }
            }
        } else if (selectionRect.width == 1 && selectionRect.height == 1) {
            selection = new int[1][1];
        
            selection[0][0] = getTileAt(selectedLayer, selectionStart);
        }
    
        setSelection(selection);
        
        selectionRect.x = -1;
        selectionRect.y = -1;
        selectionRect.width = 0;
        selectionRect.height = 0;
        
        selectingTiles = false;
    }
    
    private void doSpriteSelection() {
        selectedSprite = getSpriteAt(selectionStart);
    }
    
    @Override
    public void draw(Graphics2D g) {
        if (selectingTiles && selectionRect.x != -1) {
            int x = selectionRect.x * 32;
            int y = selectionRect.y * 32;
            
            drawSelectionRect(g, x, y, selectionRect.width * 32, selectionRect.height * 32);
        }
    }
}
