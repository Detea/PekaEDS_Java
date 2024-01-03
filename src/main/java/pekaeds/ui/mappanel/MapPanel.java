package pekaeds.ui.mappanel;

import javax.swing.*;

import pekaeds.pk2.map.PK2Map;
import pekaeds.pk2.map.PK2Map13;
import pekaeds.settings.Settings;
import pekaeds.tool.Tool;
import pekaeds.ui.listeners.PK2MapConsumer;
import pekaeds.ui.listeners.RepaintListener;

import java.awt.*;
import java.awt.event.*;

public class MapPanel extends JPanel implements ComponentListener, PK2MapConsumer, RepaintListener {
    private MapPanelModel model;
    private MapPanelPainter painter;
    
    private MapPanelMouseHandler mpMouseHandler;

    private Tool leftMouseTool, rightMouseTool;

    private JViewport viewport;
    private JScrollPane scrollPane;
    
    private Dimension panelSize = new Dimension(PK2Map13.WIDTH * 32, PK2Map13.HEIGHT * 32);
    
    JScrollPane getScrollPane() {
        return scrollPane;
    }
    
    public MapPanel() {
        addComponentListener(this);
        
        model = new MapPanelModel();
        painter = new MapPanelPainter();
        painter.setMapPanelModel(this);

        mpMouseHandler = new MapPanelMouseHandler(this);

        viewport = new JViewport();
        viewport.setView(this);
        
        setPreferredSize(new Dimension(256 * 32, 224 * 32)); // TODO Don't use hard coded values for width and height
        
        addMouseListener(mpMouseHandler);
        addMouseMotionListener(mpMouseHandler);
        addMouseWheelListener(mpMouseHandler);
    
        // This both needs to be done for the JLayeredPane
        setOpaque(false);
        setBounds(0, 0, panelSize.width, panelSize.height);
    }
    
    public void setView(JScrollPane view) {
        this.scrollPane = view;
        
        viewport = scrollPane.getViewport();
        
        model.setViewPosition(view.getX(), view.getY());
        model.setViewSize(view.getWidth(), view.getHeight());
    }
    
    public void setLeftMouseTool(Tool tool) {
        this.leftMouseTool = tool;
        
        leftMouseTool.setMapPanelPainter(painter);
        mpMouseHandler.setLeftMouseTool(leftMouseTool);
    }
    
    public void setRightMouseTool(Tool tool) {
        this.rightMouseTool = tool;
        
        rightMouseTool.setMapPanelPainter(painter);
        mpMouseHandler.setRightMouseTool(rightMouseTool);
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        var g2 = (Graphics2D) g;
    
        /*
        // TODO Solve performance problems
        AffineTransform transform = g2.getTransform();
        transform.translate(model.getZoomPosition().x, model.getZoomPosition().y);
        transform.scale(model.getZoomAmount(), model.getZoomAmount());
        transform.translate(-model.getZoomPosition().x, -model.getZoomPosition().y);
        g2.setTransform(transform);*/
        
        painter.drawBackground(g2);
        painter.drawBackgroundSprites(g2);
        
        if (model.getTilesetImage() != null) painter.drawLayers(g2);
        
        painter.drawForegroundSprites(g2);
        
        if (Settings.highlightSprites()) {
            painter.drawSpriteHighlights(g2, model.getViewRect());
        }
        
        if (model.getTilesetImage() != null) {
            leftMouseTool.draw(g2);
            rightMouseTool.draw(g2);
        }
    }
    
    public MapPanelModel getModel() {
        return model;
    }
    
    @Override
    public void componentResized(ComponentEvent e) {
        if (model.getBackgroundImage() != null) {
            var repeatBGX = getWidth() / model.getBackgroundImage().getWidth();
            var repeatBGY = getHeight() / model.getBackgroundImage().getHeight();
    
            repeatBGX++;
            repeatBGY++;
    
            model.setBackgroundRepeat(repeatBGX, repeatBGY);
    
            model.setViewSize(scrollPane.getViewport().getWidth(), scrollPane.getViewport().getHeight());
    
            repaint();
        }
    }
    
    public JViewport getViewport() {
        return viewport;
    }
    
    @Override
    public Dimension getSize() {
        return panelSize;
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
    
    @Override
    public void repaint() {
        if(viewport != null) repaint(viewport.getViewRect());
    }
    
    public void setViewX(int x) {
        model.getViewRect().x = x;
    }
    
    public void setViewY(int y) {
        model.getViewRect().y = y;
    }
    
    @Override
    public void setMap(PK2Map map) {
        model.setMap(map);
        
        model.setTilesetImage(map.getTilesetImage());
        model.setBackgroundImage(map.getBackgroundImage());
        
        repaint();
    }
    
    @Override
    public void doRepaint() {
        repaint();
    }
}
