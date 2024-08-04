package pekaeds.ui.minimappanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pekaeds.pk2.level.PK2LevelSector;
import pekaeds.pk2.level.PK2TileArray;
import pekaeds.tool.Tool;
import pekaeds.ui.listeners.PK2SectorConsumer;
import pekaeds.ui.listeners.TileChangeListener;

import java.awt.*;
import java.awt.image.BufferedImage;

// TODO Reflect changes to the map on the mini map
// TODO Reflect tileset and background change
public class MiniMapPanel extends JPanel implements PK2SectorConsumer, TileChangeListener, ChangeListener {
    private BufferedImage tilesetImage;
    private BufferedImage backgroundTilesetImage;
    
    private PK2LevelSector map;
    
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
    
    @Override
    public void setSector(PK2LevelSector m) {
        this.map = m;
        
        tilesetImage = m.tilesetImage;
        backgroundTilesetImage = m.tilesetBgImage;
        
        repaint();
    }
    
    private void paintLayer(Graphics g, PK2TileArray layer, boolean bg) {
        if (tilesetImage != null && layer.getWidth() == 256 && layer.getHeight() == 224) {
            for (int x = 0; x < 256; x++) {
                for (int y = 0; y < 224; y++) {
                    // TODO Don't use 256 and 224 magic numbers, use profile
                    int tile = layer.get(x, y); //map.getLayers().get(layer)[y][x];
            
                    if (tile >= 0 && tile <= 149) { // The tileid should not be able to go out of these bounds, but for some reason one custom map does that?
                        int tileX = (tile % 10) * 32;
                        int tileY = (tile / 10) * 32;
                
                        if (bg && backgroundTilesetImage != null) {
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
        
        paintLayer(g, map.getBGLayer(), true);
        paintLayer(g, map.getFGLayer(), false);
        
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
