package pk.pekaeds.ui.minimappanel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MiniMapPanelMouseHandler extends MouseAdapter {
    private final MiniMapPanel miniMapPanel;
    
    public MiniMapPanelMouseHandler(MiniMapPanel mapPanel) {
        this.miniMapPanel = mapPanel;
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        setViewPosition(e.getPoint());
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        setViewPosition(e.getPoint());
    }
    
    private void setViewPosition(Point pos) {
        if (pos.x - (miniMapPanel.viewWidth / 2) < 0) pos.x = miniMapPanel.viewWidth / 2;
        if (pos.y - (miniMapPanel.viewHeight / 2) < 0) pos.y = miniMapPanel.viewHeight / 2;
        
        if (pos.x + (miniMapPanel.viewWidth / 2) >= 256) {
            pos.x = 256 - miniMapPanel.viewWidth; // Subtract 1 to prevent jitter.
        } else {
            pos.x -= (miniMapPanel.viewWidth / 2);
        }
        
        if (pos.y + (miniMapPanel.viewHeight) / 2 >= 224) {
            pos.y = 224 - miniMapPanel.viewHeight;
        } else {
            pos.y -= (miniMapPanel.viewHeight / 2);
        }
    
        miniMapPanel.setViewPosition(pos.x, pos.y);
    }
}
