package pk.pekaeds.pk2.sprite;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;


import javax.imageio.ImageIO;

import pk.pekaeds.settings.Settings;
import pk.pekaeds.util.GFXUtils;
import pk2.sprite.Prototype;
import pk2.sprite.PrototypesHandler;

import org.tinylog.Logger;

public class SpriteReaderNative implements ISpriteReader {
    
    public static PrototypesHandler handler = null;

    @Override
    public ISpritePrototypeEDS loadImageData(File filename, BufferedImage backgroundImage){
        Prototype prototype = handler.loadPrototype(filename.getName());

        if(prototype!=null){

            SpritePrototypeEDS prototypeEDS = new SpritePrototypeEDS(prototype);

            BufferedImage image = null;
            File imgFile = Paths.get(Settings.getSpritesPath(), prototype.getTextureName()).toFile();
            try{
                if(imgFile.exists() && imgFile.isFile()){
                    image = ImageIO.read(imgFile);
                    GFXUtils.adjustSpriteColor(image, prototype.getColor());

                    if (backgroundImage != null) {
                        image = GFXUtils.setPaletteToBackgrounds(image, backgroundImage);
                    } else {
                        image = GFXUtils.makeTransparent(image);
                    }

                    image = GFXUtils.getFirstSpriteFrame(prototypeEDS, image);
                }
            }
            catch (IOException e){
                Logger.warn(e, "Unable to load sprite image data.");
            }

            if(image==null){
                image = PK2SpriteMissing.getMissingImage();
            }

            prototypeEDS.setImage(image);

            return prototypeEDS;
        } 

        return null;
    }

    @Override
    public ISpritePrototypeEDS loadImageData(File filename){
        return loadImageData(filename, null);
    }

}
