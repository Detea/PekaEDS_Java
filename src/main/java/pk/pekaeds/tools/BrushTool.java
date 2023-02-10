package pk.pekaeds.tools;

import pk.pekaeds.util.TileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class BrushTool extends Tool {
    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        
        placeSelection(e);
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        
        placeSelection(e);
    }
    
    // TODO should probably just pass the position of the mouse
    private void placeSelection(MouseEvent e) {
        // Have to use SwingUtilities, because for some reason checking for the mouse buttons like in mousePressed doesn't work.
        if (SwingUtilities.isLeftMouseButton(e)) {
            switch (getMode()) {
                case MODE_TILE -> placeTiles(e.getPoint());
                case MODE_SPRITE -> placeSprite(e.getPoint());
            }
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        switch (getMode()) {
            case MODE_TILE -> drawSelectedTiles(g);
            case MODE_SPRITE -> drawSelectedSprite(g);
        }
    }
    
    // TODO Fix some weird multi selection bullshit, when selection is at/below 0, 0
    private void drawSelectedTiles(Graphics2D g) {
        if (!selectingTiles) {
            for (int y = 0; y < getSelectionHeight(); y++) {
                for (int x = 0; x < getSelectionWidth(); x++) {
                    // Make the selected tiles appear to be on a 32 * 32 grid.
                    int xAdjusted = (getMousePosition().x / 32 * 32);
                    int yAdjusted = (getMousePosition().y / 32 * 32);
                    
                    // Offset the selections position by half its size (selection width and height), so it is centered at the position of the mouse cursor.
                    int offsetX = (x - (getSelectionWidth() / 2)) * 32;
                    int offsetY = (y - (getSelectionHeight() / 2)) * 32;
                    
                    getMapPanelPainter().drawTile(g, xAdjusted + offsetX, yAdjusted + offsetY, tileSelection[y][x]);
                }
            }
        }
    }
    
    private void drawSelectedSprite(Graphics2D g) {
        TileUtils.alignPointToGrid(getMousePosition());
        
        if (selectedSprite != 255 && selectedSprite >= 0) {
            getMapPanelPainter().drawSprite(g, map.getSprite(selectedSprite), getMousePosition().x, getMousePosition().y);
        }
    }
}
