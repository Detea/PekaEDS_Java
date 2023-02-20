package pk.pekaeds.pk2.sprite;

import pk.pekaeds.profile.SpriteProfile;

import java.awt.image.BufferedImage;
import java.io.File;

public interface PK2SpriteReader {
    PK2Sprite loadImageData(File filename, BufferedImage backgroundImage);
    PK2Sprite loadImageData(File filename);
}
