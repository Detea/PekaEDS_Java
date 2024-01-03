package pekaeds.pk2.map;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import pekaeds.pk2.sprite.ISpritePrototypeEDS;

public interface PK2MapReader {
    PK2Map load(File filename) throws IOException;
    
    List<ISpritePrototypeEDS> loadSpriteList(List<String> spriteFilenames, BufferedImage backgroundImage, int playerSpriteIndex, File mapFile) throws IOException;
    
    PK2Map loadIconDataOnly(File file);
}
