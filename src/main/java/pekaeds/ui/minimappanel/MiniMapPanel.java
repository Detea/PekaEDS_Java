package pekaeds.ui.minimappanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pekaeds.data.Layer;
import pekaeds.pk2.map.PK2Map;
import pekaeds.settings.Settings;
import pekaeds.tool.Tool;
import pekaeds.ui.listeners.PK2MapConsumer;
import pekaeds.ui.listeners.TileChangeListener;

import java.awt.*;
import java.awt.image.BufferedImage;

// TODO Reflect changes to the map on the mini map
// TODO Reflect tileset and background change
public class MiniMapPanel extends JPanel implements PK2MapConsumer, TileChangeListener, ChangeListener {
    private BufferedImage tilesetImage;
    private BufferedImage backgroundTilesetImage;
    
    private PK2Map map;
    
    private int viewX;
    private int viewY;
    
    int viewWidth;
    int viewHeight;
    
    private JViewport viewport;
    
    public MiniMapPanel() {
        setPreferredSize(new Dimension(257 ,225));
        setMinimumSize(new Dimension(257 ,225));
        setMaximumSize(new Dimension(257 ,225));
        
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
        
        tilesetImage = m.getTilesetImage();
        backgroundTilesetImage = m.getBackgroundTilesetImage();
        
        repaint();
    }
    
    private void paintLayer(Graphics g, int layer) {
        if (tilesetImage != null) {
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
    public void tileChanged(int x, int y, int tileID) {
        repaint(x, y, 1, 1);
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        var vp = (JViewport) e.getSource();
    
        viewX = vp.getViewRect().x / 32;
        viewY = vp.getViewRect().y / 32;
        
        viewWidth = vp.getViewRect().width / 32;
        viewHeight = vp.getViewRect().height / 32;
        
        repaint();
    }
}
