package pk.pekaeds.ui.mappanel;

import pk.pekaeds.pk2.map.PK2Map;
import pk.pekaeds.ui.listeners.PK2MapConsumer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
    This class only draws the background image. The background only changes when the user changes the background image, so it only has to be redrawn then.
 */
public final class MapPanelBackground extends JPanel implements PK2MapConsumer {
    private BufferedImage image;
    private int repeatX, repeatY;
   
    private Dimension size = new Dimension(640, 480);
    
    public MapPanelBackground() {
        size.setSize(640 * 4, 480 * 4);
        setBounds(0, 0, size.width, size.height);
        
        repeatX = 4;
        repeatY = 4;
    }
    
    public void setImage(BufferedImage img) {
        this.image = img;
    }
    
    public void setViewSize(int width, int height) {
        this.repeatX = width / image.getWidth();
        this.repeatY = height / image.getHeight();
        
        repeatX++;
        repeatY++;
        
        size.setSize(repeatX * image.getWidth(), repeatY * image.getHeight());
        setBounds(0, 0, size.width, size.height);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        var g2 = (Graphics2D) g;
        
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                g2.drawImage(image, x * image.getWidth(), y * image.getHeight(), null);
            }
        }
    }
    
    @Override
    public void setMap(PK2Map map) {
        image = map.getBackgroundImage();
        
        repaint();
    }
    
    // Need to override this to make it work with JLayeredPane. If this method isn't overriden this component won't show up in the JLayeredPane.
    @Override
    public Dimension getSize() {
        return size;
    }
}
