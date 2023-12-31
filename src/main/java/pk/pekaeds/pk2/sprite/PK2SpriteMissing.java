package pk.pekaeds.pk2.sprite;

import org.tinylog.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public final class PK2SpriteMissing extends PK2SpriteOld {
    private static BufferedImage image = null;
    
    static {
        try {
            image = ImageIO.read(PK2SpriteMissing.class.getClassLoader().getResourceAsStream("missing.png"));
        } catch (IOException e) {
            Logger.error("Unable to load missing.png. This shouldn't happen.");
        }
    }
    
    public PK2SpriteMissing() {
        filename = "Missing";
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
