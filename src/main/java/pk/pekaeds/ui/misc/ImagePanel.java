package pk.pekaeds.ui.misc;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JComponent {
    private BufferedImage image = null;
    private boolean centerImage = false;
    
    private int maxImageWidth, maxImageHeight;
    
    private int xOffset, yOffset;
    
    public ImagePanel(int width, int height) {
        setPreferredSize(new Dimension(width + 2, height + 2));
    }
    
    public void setImage(BufferedImage img, boolean centerImage, int maxWidth, int maxHeight) {
        this.image = img;
        
        this.centerImage = centerImage;
        this.maxImageWidth = Math.min(img.getWidth(), maxWidth);
        this.maxImageHeight = Math.min(img.getHeight(), maxHeight);
        
        if (centerImage) calculateOffset();
        
        repaint();
    }
    
    public void setImage(BufferedImage img) {
        setImage(img, false, img.getWidth(), img.getHeight());
    }
    
    private void calculateOffset() {
        xOffset = (int) (getPreferredSize().getWidth() / 2) - (maxImageWidth / 2);
        yOffset = (int) (getPreferredSize().getHeight() / 2) - (maxImageHeight / 2);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    
        g.setColor(UIManager.getColor("Panel.background"));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        if (image != null) {
            if (!centerImage) {
                g.drawImage(image, 1, 1, maxImageWidth, maxImageHeight, null);
            } else {
                g.drawImage(image, xOffset, yOffset, maxImageWidth, maxImageHeight, null);
            }
        }
        
        g.setColor(Color.black);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
    }
}
