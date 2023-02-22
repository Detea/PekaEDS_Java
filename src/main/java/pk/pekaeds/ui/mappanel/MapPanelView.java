package pk.pekaeds.ui.mappanel;

import pk.pekaeds.pk2.map.PK2Map13;
import pk.pekaeds.ui.minimappanel.MiniMapPanel;
import pk.pekaeds.util.undoredo.UndoManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This class is a wrapper for the MapPanel. It puts the MapPanel inside a JScrollPane and passes the viewport size onto the MapPanel class.
 * MapPanel needs this information to determine how often to repeat the background image.
 */
public class MapPanelView extends JScrollPane implements ComponentListener {
    private MapPanelBackground mapPanelBackground;
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
        });
        
        getVerticalScrollBar().addAdjustmentListener(l -> {
            mapPanel.setViewY(getVerticalScrollBar().getValue());
        });
        
        getViewport().addComponentListener(this);
    }
    
    @Override
    public void componentResized(ComponentEvent e) {
        mapPanel.getModel().setViewSize(getViewport().getWidth(), getViewport().getHeight());
        //mapPanelBackground.setViewSize(getViewport().getWidth(), getViewport().getHeight());
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