package pk.pekaeds.ui.mappanel;

import pk.pekaeds.tools.Tool;
import pk.pekaeds.util.undoredo.UndoManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.sql.PreparedStatement;

public final class MapPanelMouseHandler extends MouseAdapter {
    private Tool rightMouseTool;
    private Tool leftMouseTool;
    
    private MapPanel mapPanel; // TODO Create a repaintListener, use PropertyChangeSupport instead for repaint?
    
    public MapPanelMouseHandler(MapPanel panel) {
        this.mapPanel = panel;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            UndoManager.startUndoBlock();
            
            leftMouseTool.mousePressed(e);
            UndoManager.clearRedoStack();
        } else if (SwingUtilities.isRightMouseButton(e)) {
            rightMouseTool.mousePressed(e);
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            mapPanel.getModel().setLastPanPoint(e.getPoint());
        }
        
        // TODO only repaint the affected areas.
        mapPanel.repaint();
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            leftMouseTool.mouseReleased(e);
            
            UndoManager.endUndoBlock();
        } else if (SwingUtilities.isRightMouseButton(e)) {
            rightMouseTool.mouseReleased(e);
        }
    
        // TODO only repaint the affected areas.
        mapPanel.repaint();
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            leftMouseTool.mouseDragged(e);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            rightMouseTool.mouseDragged(e);
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            panView(e.getX(), e.getY());
        }
        
        // TODO only repaint the affected areas.
        mapPanel.repaint();
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        leftMouseTool.mouseMoved(e);
        rightMouseTool.mouseMoved(e);
        
        mapPanel.repaint();
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        mapPanel.getModel().setZoomPosition(e.getPoint());
        
        if (e.getPreciseWheelRotation() < 0) {
            mapPanel.getModel().setZoomAmount(mapPanel.getModel().getZoomAmount() + 0.01f);
        } else {
            mapPanel.getModel().setZoomAmount(mapPanel.getModel().getZoomAmount() - 0.01f);
        }
        
        mapPanel.repaint();
    }
    
    // TODO Fix paning being weird
    private void panView(int x, int y) {
        int panX = mapPanel.getModel().getLastPanPoint().x - x;
        int panY = mapPanel.getModel().getLastPanPoint().y - y;
        
        var viewRect = mapPanel.getViewport().getVisibleRect();
        viewRect.x += panX;
        viewRect.y += panY;
        
        mapPanel.getModel().setViewPosition(mapPanel.getScrollPane().getHorizontalScrollBar().getValue() / 32, mapPanel.getScrollPane().getVerticalScrollBar().getValue() / 32);
        
        mapPanel.getViewport().scrollRectToVisible(viewRect);
        
        mapPanel.repaint();
    }
    
    public void setLeftMouseTool(Tool tool) {
        this.leftMouseTool = tool;
    }
    
    public void setRightMouseTool(Tool tool) {
        this.rightMouseTool = tool;
    }
}
