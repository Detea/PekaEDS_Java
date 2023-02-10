package pk.pekaeds.tools;

import pk.pekaeds.util.TileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class RectangleTool extends Tool {
    private boolean fill = false;
    private Rectangle rect;
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (getMode() == MODE_TILE) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                selectionStart = e.getPoint();
                selectionEnd = e.getPoint();
            }
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        
        if (SwingUtilities.isLeftMouseButton(e)) {
            selectionEnd = e.getPoint();
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (getMode() == MODE_TILE) {
            placeRectangle();
    
            selectionStart.setLocation(-1, -1);
            selectionEnd.setLocation(-1, -1);
        }
    }
    
    // TODO stinky code reuse, but I can't think of a clean way to separate this code right now.
    private void placeRectangle() {
        rect = TileUtils.calculateSelectionRectangle(selectionStart, selectionEnd);
        
        for (int x = rect.x; x < rect.x + rect.width; x++) {
            for (int y = rect.y; y < rect.y + rect.height; y++) {
                // TODO support a tile selection greater than one
                // TODO Again, code use. Maybe clean this up.
                if (fill) {
                    placeTile(x * 32, y * 32, tileSelection[0][0]);
                } else {
                    if (x == rect.x || x == rect.x + (rect.width - 1) ||
                            y == rect.y || y == rect.y + (rect.height - 1)) {
                        placeTile(x * 32, y * 32, tileSelection[0][0]);
                    }
                }
            }
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        if (getMode() == MODE_TILE) {
            if (selectionStart.x != -1 && selectionStart.y != -1) {
                rect = TileUtils.calculateSelectionRectangle(selectionStart, selectionEnd);
        
                for (int x = rect.x; x < rect.x + rect.width; x++) {
                    for (int y = rect.y; y < rect.y + rect.height; y++) {
                        // TODO support a tile selection greater than one
                        if (fill) {
                            getMapPanelPainter().drawTile(g, x * 32, y * 32, tileSelection[0][0]);
                        } else {
                            if (x == rect.x || x == rect.x + (rect.width - 1) ||
                                    y == rect.y || y == rect.y + (rect.height - 1)) {
                                getMapPanelPainter().drawTile(g, x * 32, y * 32, tileSelection[0][0]);
                            }
                        }
                    }
                }
            } else {
                getMapPanelPainter().drawTile(g, getMousePosition().x, getMousePosition().y, tileSelection[0][0]);
            }
        }
    }
    
    public void setFilled(boolean filled) {
        this.fill = filled;
    }
    
    public boolean isFilled() {
        return fill;
    }
}
