package pekaeds.pk2.sprite.io;

import org.tinylog.Logger;

import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.pk2.sprite.ISpritePrototype;
import pekaeds.util.GFXUtils;

import java.io.*;
import java.util.Arrays;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;


public class SpriteIO {

    private static final int[] ID_1_1 = { 0x31, 0x2E, 0x31, 0x00 };
    private static final int[] ID_1_2 = { 0x31, 0x2E, 0x32, 0x00 };
    private static final int[] ID_1_3 = { 0x31, 0x2E, 0x33, 0x00 };

    private static final ISpriteReader reader1_1 = new SpriteReader11();
    private static final ISpriteReader reader1_2 = new SpriteReader12();
    private static final ISpriteReader reader1_3 = new SpriteReader13();
    private static final ISpriteReader reader_json = new SpriteReaderJson();

    private static ISpriteReader getSpriteReader(File file) throws IOException{
        String filename = file.getName();
        if(filename.endsWith(".spr2")){
            return reader_json;
        }
        else if(filename.endsWith(".spr")){

            DataInputStream dis = new DataInputStream(new FileInputStream(file));

            int[] id = new int[4];
            for (int i = 0; i < id.length; i++) {
                id[i] = dis.readByte() & 0xFF;
            }

            dis.close();

            if (Arrays.equals(ID_1_3, id)) {
                return reader1_3;
            } else if (Arrays.equals(ID_1_2, id)) {
                return reader1_2;
            } else if (Arrays.equals(ID_1_1, id)) {
                return reader1_1;
            } else {
                Logger.warn("Unable to find sprite version for id: {}", Arrays.toString(id));
            }
        }

        throw new IOException("Unable to recognize file as Pekka Kana 2 sprite.");
    }

    public static ISpritePrototype loadSprite(File file) throws Exception{
        return loadSprite(file, null);
    }

    public static ISpritePrototype loadSprite(File file, BufferedImage backgroundImage) throws Exception{

        ISpriteReader reader = getSpriteReader(file);
        ISpritePrototype spr = reader.readSpriteFile(file);

        BufferedImage spriteImageSheet = null;
        
        File spriteImageFile = PK2FileSystem.INSTANCE.findAsset(spr.getTextureName(),PK2FileSystem.SPRITES_DIR);
        if (spriteImageFile!=null) {

            try {
                spriteImageSheet = ImageIO.read(spriteImageFile);
                GFXUtils.adjustSpriteColor(spriteImageSheet, spr.getColor());

                if (backgroundImage != null) {
                    spriteImageSheet = GFXUtils.setPaletteToBackgrounds(spriteImageSheet, backgroundImage);
                } else {
                    spriteImageSheet = GFXUtils.makeTransparent(spriteImageSheet);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            
        }

        if(spriteImageSheet!=null){
            spr.setImage(GFXUtils.getFirstSpriteFrame(spr, spriteImageSheet));
        }        
        else {
            spr.setImage(SpriteMissing.getMissingImage());
            spr.setFrameX(0);
            spr.setFrameY(0);
            spr.setFrameWidth(32);
            spr.setFrameHeight(32);
        }

        return spr;
    }


}
