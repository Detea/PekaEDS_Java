package pk.pekaeds.pk2.sprite;

import pk.pekaeds.util.GFXUtils;
import pk.pekaeds.util.file.PK2FileUtils;
import pk.pekaeds.settings.Settings;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

import org.tinylog.Logger;

public final class PK2SpriteReader13 implements PK2SpriteReader {
    /**
     * Reads only the necessary data from the sprites file. Use this in the level editor. Use load() in the sprite editor.
     * @param filename
     * @param backgroundImage
     * @return
     */
    @Override
    public PK2Sprite loadImageData(File filename, BufferedImage backgroundImage) {
        var spr = new PK2Sprite();
        
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
            in.readNBytes(4); // Skip the magic number
            
            spr.setType(Integer.reverseBytes(in.readInt()));
  
            spr.setImageFile(PK2FileUtils.readString(in, Settings.getSpriteProfile().getStringLengthFiles()));
            
            // Skip sound files
            for (int i = 0; i < Settings.getSpriteProfile().getAmountOfSounds(); i++) {
                in.readNBytes(100);
            }
            
            // Skip unused data
            for (int i = 0; i < Settings.getSpriteProfile().getAmountOfSounds(); i++) {
                in.readInt();
            }
            
            // Not used
            spr.setFramesAmount((int) in.readByte() & 0xFF);
            
            // Skip animation data
            for (int i = 0; i < Settings.getSpriteProfile().getAmountOfAnimations(); i++) {
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
            
            spr.setName(PK2FileUtils.readString(in, Settings.getSpriteProfile().getStringLengthName()));
 
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
            
            for (int i = 0; i < 10; i++) {
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
            
            var spriteImageFile = new File(Settings.getSpritesPath() + File.separatorChar + spr.getImageFile());
            
            if (spriteImageFile.exists()) {
                var spriteImageSheet = ImageIO.read(new File(Settings.getSpritesPath() + File.separatorChar + spr.getImageFile())); // TODO Look for sprites in current episodes directory
                GFXUtils.adjustSpriteColor(spriteImageSheet, spr.getColor());
    
                if (backgroundImage != null) {
                    spriteImageSheet = GFXUtils.setPaletteToBackgrounds(spriteImageSheet, backgroundImage);
                } else {
                    spriteImageSheet = GFXUtils.makeTransparent(spriteImageSheet);
                }
    
                spr.setImage(GFXUtils.getFirstSpriteFrame(spr, spriteImageSheet));
            } else {
                spr.setImage(PK2SpriteMissing.getMissingImage());
                spr.setFrameX(0);
                spr.setFrameY(0);
                spr.setFrameWidth(32);
                spr.setFrameHeight(32);
            }
        } catch (IOException e) {
            Logger.warn(e, "Unable to load sprite image data.");
        }
    
        return spr;
    }
    
    @Override
    public PK2Sprite loadImageData(File filename) {
        return loadImageData(filename, null);
    }
}
