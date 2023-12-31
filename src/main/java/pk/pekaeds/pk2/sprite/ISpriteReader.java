package pk.pekaeds.pk2.sprite;

import java.awt.image.BufferedImage;
import java.io.File;

public interface ISpriteReader {
    ISpritePrototypeEDS loadImageData(File filename, BufferedImage backgroundImage);
    ISpritePrototypeEDS loadImageData(File filename);
}
