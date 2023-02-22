package pk.pekaeds.ui.minimappanel;

import pk.pekaeds.data.Layer;
import pk.pekaeds.pk2.map.PK2Map;
import pk.pekaeds.pk2.map.PK2Map13;
import pk.pekaeds.settings.Settings;
import pk.pekaeds.tools.Tool;
import pk.pekaeds.ui.listeners.PK2MapConsumer;
import pk.pekaeds.ui.listeners.TileChangeListener;
import pk.pekaeds.ui.mappanel.MapPanelModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

// TODO Reflect changes to the map on the mini map
// TODO Reflect tileset and background change
public class MiniMapPanel extends JPanel implements PropertyChangeListener, PK2MapConsumer, TileChangeListener, ChangeListener {
    private BufferedImage mapImage = new BufferedImage(256, 224, BufferedImage.TYPE_INT_ARGB);
    private BufferedImage bgImage = null;
    private BufferedImage tilesetImage;
    private BufferedImage backgroundTilesetImage;
    
    private PK2Map map;
    
    private int viewX;
    private int viewY;
    
    int viewWidth;
    int viewHeight;
    
    private JViewport viewport;
    
    public MiniMapPanel() {
        setPreferredSize(new Dimension(256 ,224));
        
        setBackground(Color.DARK_GRAY);
        
        var mouseHandler = new MiniMapPanelMouseHandler(this);
        
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    
        Tool.setTileChangeListener(this);
    }
    
    public void setViewport(JViewport view) {
        this.viewport = view;
    }
    
    public void setTilesetImage(BufferedImage image) {
        this.tilesetImage = image;
    }
    
    public void setMap(PK2Map m) {
        this.map = m;
        
        mapImage.getGraphics().setColor(Color.DARK_GRAY);
        mapImage.getGraphics().fillRect(0, 0, 256, 224);
        
        tilesetImage = m.getTilesetImage();
        backgroundTilesetImage = m.getBackgroundTilesetImage();
        
        repaint();
    }
    
    private void paintLayer(Graphics g, int layer) {
        for (int x = 0; x < 256; x++) {
            for (int y = 0; y < 224; y++) {
                // TODO Don't use 256 and 224 magic numbers, use profile
                int tile = map.getLayers().get(layer)[y][x];
            
                if (tile >= 0 && tile <= 149) { // The tileid should not be able to go out of these bounds, but for some reason one custom map does that?
                    int tileX = (tile % 10) * 32;
                    int tileY = (tile / 10) * 32;
                    
                    if (Settings.useBGTileset() && layer == Layer.BACKGROUND && backgroundTilesetImage != null) {
                        g.setColor(new Color(backgroundTilesetImage.getRGB(tileX, tileY))); // TODO Maybe don't create a new Color object every loop
                    } else {
                        g.setColor(new Color(tilesetImage.getRGB(tileX, tileY))); // TODO Maybe don't create a new Color object every loop
                    }
                    g.drawLine(x, y, x + 1, y + 1);
                }
            }
        }
    }
    
    void setViewPosition(int x, int y) {
        viewX = x;
        viewY = y;
        
        viewport.setViewPosition(new Point(x * 32, y * 32));
        
        repaint();
    }
    
    public void setViewportSize(int width, int height) {
        viewWidth = width / 32;
        viewHeight = height / 32;
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, 256, 224);
        
        paintLayer(g, Layer.BACKGROUND);
        paintLayer(g, Layer.FOREGROUND);
        
        g.setColor(Color.white);
        g.drawRect(viewX, viewY, viewWidth, viewHeight);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof MapPanelModel) {
            switch (evt.getPropertyName()) {
                case "viewportSize" -> {
                    var size = ((Dimension) evt.getNewValue());
    
                    setViewportSize(size.width, size.height);
                }
            }
            
            repaint();
        }
    }
    
    @Override
    public void tileChanged(int x, int y, int tileID) {
        repaint(x, y, 1, 1);
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        var vp = (JViewport) e.getSource();
    
        viewX = vp.getViewRect().x / 32;
        viewY = vp.getViewRect().y / 32;
        
        repaint();
    }
}
