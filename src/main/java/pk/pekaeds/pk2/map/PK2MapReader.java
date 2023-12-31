package pk.pekaeds.pk2.map;


import pk.pekaeds.pk2.sprite.ISpritePrototypeEDS;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface PK2MapReader {
    PK2Map load(File filename) throws IOException;
    
    List<ISpritePrototypeEDS> loadSpriteList(List<String> spriteFilenames, BufferedImage backgroundImage, int playerSpriteIndex) throws IOException;
    
    PK2Map loadIconDataOnly(File file);
}
