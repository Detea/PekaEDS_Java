package pk.pekaeds.ui.mappanel;

import pk.pekaeds.tool.Tool;

import javax.swing.*;
import java.awt.event.*;

/**
 * This class is a wrapper for the MapPanel. It puts the MapPanel inside a JScrollPane and passes the viewport size onto the MapPanel class.
 * MapPanel needs this information to determine how often to repeat the background image.
 */
public class MapPanelView extends JScrollPane implements ComponentListener {
    private MapPanel mapPanel;
    //private JScrollPane scrollPane;
    
    public MapPanelView(MapPanel panel) {
        this.mapPanel = panel;
        
        /*
        // TODO Write a custom LayeredPane that allows me to choose when to update what panel
        scrollPane = new JScrollPane(mapPanel);
        scrollPane.setOpaque(false);
        mapPanel.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        setLayout(new BorderLayout());
        add(mapPanelBackground, BorderLayout.CENTER, 1);
        add(scrollPane, BorderLayout.CENTER, 0);
        */
        
        setViewportView(mapPanel);
        
        mapPanel.setView(this);
        
        getHorizontalScrollBar().addAdjustmentListener(l -> {
            mapPanel.setViewX(getHorizontalScrollBar().getValue());
    
            Tool.setViewRect(getViewport().getViewRect());
        });
        
        getVerticalScrollBar().addAdjustmentListener(l -> {
            mapPanel.setViewY(getVerticalScrollBar().getValue());
    
            Tool.setViewRect(getViewport().getViewRect());
        });
        
        getViewport().addComponentListener(this);
    }
    
    @Override
    public void componentResized(ComponentEvent e) {
        mapPanel.getModel().setViewSize(getViewport().getWidth(), getViewport().getHeight());
        //mapPanelBackground.setViewSize(getViewport().getWidth(), getViewport().getHeight());
    
        Tool.setViewRect(getViewport().getViewRect());
    }
    
    @Override
    public void componentMoved(ComponentEvent e) {
    
    }
    
    @Override
    public void componentShown(ComponentEvent e) {
    
    }
    
    @Override
    public void componentHidden(ComponentEvent e) {
    
    }
}
