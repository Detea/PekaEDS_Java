package pekaeds.pk2.sprite;

import java.awt.image.BufferedImage;
import java.io.File;

public interface ISpriteReader {
    ISpritePrototypeEDS loadImageData(File filename, String episode_dir, BufferedImage backgroundImage);
    ISpritePrototypeEDS loadImageData(File filename, String episode_dir);
    ISpritePrototypeEDS loadImageData(File filename);
}
