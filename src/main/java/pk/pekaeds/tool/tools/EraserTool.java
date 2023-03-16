package pk.pekaeds.tool.tools;

import pk.pekaeds.tool.Tool;

import java.awt.*;
import java.awt.event.MouseEvent;

public class EraserTool extends Tool {
    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        
        doPlacement(e.getPoint());
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
        
        doPlacement(e.getPoint());
    }
    
    private void doPlacement(Point position) {
        switch (getMode()) {
            case MODE_TILE -> layerHandler.placeTile(position.x, position.y, 255, selectedLayer);
            case MODE_SPRITE -> layerHandler.placeSprite(position, 255);
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
    
        g.setColor(Color.red);
        g.fillRect(getMousePosition().x, getMousePosition().y, 32, 32);
        
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        
        drawSelectionRect(g, getMousePosition().x, getMousePosition().y, 32, 32);
    }
    
    @Override
    public void onSelect() {
    
    }
    
    @Override
    public void onDeselect() {
    
    }
}
