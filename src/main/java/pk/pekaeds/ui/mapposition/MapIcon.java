package pk.pekaeds.ui.mapposition;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

public class MapIcon {
    private BufferedImage image;
    private Point position;
    
    private String file;

    public MapIcon(BufferedImage img, Point pos) {
        this.image = img;
        
        this.position = pos;
        
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
    public void setImage(BufferedImage img) {
        this.image = img;
    }
    
    public Point getPosition() {
        return position;
    }
    
    public void setPosition(Point pos) {
        position.x = pos.x - (27 / 2);
        position.y = pos.y - (27 / 2);
    }
    
    public void setFilename(String filename) {
        this.file = filename;
    }
    
    public String getFilename() {
        return file;
    }
}
