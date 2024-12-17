package pekaeds.ui.mappanel;

import javax.swing.*;

import pekaeds.tool.Tool;

import java.awt.event.*;

/**
 * This class is a wrapper for the MapPanel. It puts the MapPanel inside a JScrollPane and passes the viewport size onto the MapPanel class.
 * MapPanel needs this information to determine how often to repeat the background image.
 */
public class MapPanelView extends JScrollPane implements ComponentListener {
    private MapPanel mapPanel;

    public MapPanelView(MapPanel panel) {
        this.mapPanel = panel;

        setViewportView(mapPanel);

        mapPanel.setView(this);

        // Note: The use of the scrollBars here is intentional, because a changeListener on the viewport results in jittery scrolling!
        getHorizontalScrollBar().addAdjustmentListener(l -> {
            mapPanel.setViewX(getHorizontalScrollBar().getValue());

            // TODO Only set the x position
            Tool.setViewRect(getViewport().getViewRect());
        });

        getVerticalScrollBar().addAdjustmentListener(l -> {
            mapPanel.setViewY(getVerticalScrollBar().getValue());

            // TODO Only set the y position
            Tool.setViewRect(getViewport().getViewRect());
        });

        getViewport().addComponentListener(this);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        mapPanel.updateViewportSize(getViewportBorderBounds());

        // TODO Only update the size?
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