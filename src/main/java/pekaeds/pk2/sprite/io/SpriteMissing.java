package pekaeds.pk2.sprite.io;

import org.tinylog.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public final class SpriteMissing extends SpriteOld {
    private static BufferedImage image = null;
    
    static {
        try {
            image = ImageIO.read(SpriteMissing.class.getClassLoader().getResourceAsStream("missing.png"));
        } catch (IOException e) {
            Logger.error("Unable to load missing.png. This shouldn't happen.");
        }
    }
    
    public SpriteMissing(String filename) {
        this.filename = filename;
        name = "Missing";
        
        frameX = 0;
        frameY = 0;
        frameWidth = 32;
        frameHeight = 32;
        
        setType(-1);
        
        setImage(image);
    }
    
    public static BufferedImage getMissingImage() {
        return image;
    }
}
