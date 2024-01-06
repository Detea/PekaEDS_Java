package pekaeds.pk2.sprite;

import org.tinylog.Logger;

import pekaeds.settings.Settings;
import pekaeds.util.GFXUtils;
import pekaeds.util.file.PK2FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public final class PK2SpriteReader11 implements ISpriteReader {
    
    @Override
    public PK2SpriteOld loadImageData(File filename,String episodeDir, BufferedImage backgroundImage) {
        var spr = new PK2SpriteOld();
        
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
            in.readNBytes(4); // Skip the magic number
            
            spr.setType(Integer.reverseBytes(in.readInt()));
            
            spr.setImageFile(PK2FileUtils.readString(in, 13));
    
            System.out.println(spr.getTextureName());
            
            // Skip sound files
            for (int i = 0; i < 7; i++) {
                in.readNBytes(13);
            }
            
            // Skip unused data
            for (int i = 0; i < 7; i++) {
                in.readInt();
            }
            
            // Not used
            spr.setFramesAmount((int) in.readByte() & 0xFF);
            
            // Skip animation data
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 10; j++) {
                    in.readByte();
                }
                
                in.readByte();
                in.readBoolean();
            }
            
            in.readByte(); // animations amount of sprite
            in.readByte(); // frame rate
            
            in.readByte(); // Unknown byte, maybe padding?
            
            spr.setFrameX(Integer.reverseBytes(in.readInt()));
            spr.setFrameY(Integer.reverseBytes(in.readInt()));
            
            spr.setFrameWidth(Integer.reverseBytes(in.readInt()));
            spr.setFrameHeight(Integer.reverseBytes(in.readInt()));
            
            in.readInt(); // Frame distance, doesn't seem to be used.
            
            spr.setName(PK2FileUtils.readString(in, 30));
            
            in.readInt(); // width
            in.readInt(); // height
            
            in.readDouble(); // weight
            
            in.readBoolean(); // is enemy?
            
            in.readByte(); // unused
            in.readByte(); // unused
            in.readByte(); // unused
            
            in.readInt(); // energy
            in.readInt(); // damage
            
            in.readByte(); // damage type
            in.readByte(); // immunity
            
            in.readByte(); // unused
            in.readByte(); // unused
            
            in.readInt(); // score
            
            for (int i = 0; i < 5; i++) {
                in.readInt(); // AI
            }
            
            in.readByte(); // maxJump
            
            in.readByte(); // Unused
            in.readByte(); // Unused
            in.readByte(); // Unused
            
            in.readDouble(); // max speed
            
            in.readInt(); // loading time
            
            spr.setColor(in.readByte() & 0xFF);
            
            spr.setFilename(filename.getName());
            
            var spriteImageSheet = ImageIO.read(new File(Settings.getSpritesPath() + File.separatorChar + spr.getTextureName())); // TODO Look for sprites in current episodes directory
            GFXUtils.adjustSpriteColor(spriteImageSheet, spr.getColor());
            
            if (backgroundImage != null) {
                spriteImageSheet = GFXUtils.setPaletteToBackgrounds(spriteImageSheet, backgroundImage);
            } else {
                spriteImageSheet = GFXUtils.makeTransparent(spriteImageSheet);
            }
            
            spr.setImage(GFXUtils.getFirstSpriteFrame(spr, spriteImageSheet));
            
        } catch (IOException e) {
            Logger.warn(e, "Unable to load sprite image data.");
        }
        
        return spr;
    }
    
    @Override
    public PK2SpriteOld loadImageData(File filename, String episodeDir) {
        return loadImageData(filename, episodeDir, null);
    }

    @Override
    public PK2SpriteOld loadImageData(File filename){
        return this.loadImageData(filename, null);
    }
}
