package pk.pekaeds.util;

import pk.pekaeds.pk2.sprite.PK2Sprite;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;

public final class GFXUtils {
    private GFXUtils() {}
    
    public static BufferedImage setPaletteToBackgrounds(BufferedImage targetImage, final BufferedImage backgroundImage) {
        var palette = (IndexColorModel) backgroundImage.getColorModel();
        
        var rs = new byte[256];
        var gs = new byte[256];
        var bs = new byte[256];
        palette.getReds(rs);
        palette.getGreens(gs);
        palette.getBlues(bs);
        
        // Make the last color in the palette transparent, like in the game.
        var cm = new IndexColorModel(8, 256, rs, gs, bs, 255);
    
        var raster = targetImage.getRaster();
        targetImage = new BufferedImage(targetImage.getWidth(), targetImage.getHeight(), BufferedImage.TYPE_BYTE_INDEXED, cm);
        targetImage.setData(raster);
        
        return targetImage;
    }
    
    // TODO Pretty much duplicate code, clean this up
    public static BufferedImage makeTransparent(BufferedImage image) {
        var palette = (IndexColorModel) image.getColorModel();
    
        var rs = new byte[256];
        var gs = new byte[256];
        var bs = new byte[256];
        palette.getReds(rs);
        palette.getGreens(gs);
        palette.getBlues(bs);
    
        var colorModel = new IndexColorModel(8, 256, rs, gs, bs, 255);
        
        var tmpData = image.getRaster();
        var newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_INDEXED, colorModel);
        newImage.setData(tmpData);
        
        return newImage;
    }
    
    public static BufferedImage getFirstSpriteFrame(PK2Sprite spr, BufferedImage spriteSheet) {
        return spriteSheet.getSubimage(spr.getFrameX(), spr.getFrameY(), spr.getFrameWidth(), spr.getFrameHeight());
    }
    
    public static void adjustSpriteColor(BufferedImage spriteSheet, int paletteIndex) {
        if (paletteIndex != 255 && paletteIndex != 0) {
            var data= ((DataBufferByte) spriteSheet.getRaster().getDataBuffer()).getData();
            var palette = (IndexColorModel) spriteSheet.getColorModel();
            
            for (int i = 0; i < spriteSheet.getWidth() * spriteSheet.getHeight(); i++) {
                int color = data[i] & 0xFF;
                
                if (color != 255) {
    
                    color %= 32;
                    color += paletteIndex;
    
                    data[i] = (byte) color;
                }
            }
        }
    }
}
