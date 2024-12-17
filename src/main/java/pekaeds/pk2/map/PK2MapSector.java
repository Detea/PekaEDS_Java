package pekaeds.pk2.map;

import pekaeds.pk2.sprite.SpritePrototype;
import pekaeds.util.GFXUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PK2MapSector {
    public static final int CLASSIC_WIDTH = 256;
    public static final int CLASSIC_HEIGHT = 224;

    public BufferedImage tilesetImage;
    public BufferedImage tilesetBgImage;
    private BufferedImage backgroundImage;

    public String name;
    public String tilesetName;
    public String tilesetBgName;
    public String backgroundName;
    public String musicName;

    public int weather = 0;
    public int splash_color = -1; //based on the tileset
    public int fire_color_1 = 64;  //red
    public int fire_color_2 = 128; //orange

    public int background_scrolling = 0;

    private int[] backgroundLayer;
    private int[] foregroundLayer;
    private int[] spriteLayer;

    private int width;
    private int height;

    // Contains all the sprite sheets with their own palette, just adjusted for sprite color variants
    private static HashMap<String, BufferedImage> baseSpriteSheets = new HashMap<>();

    // Contains the sprite sheets that have the same palette as the background image
    private HashMap<String, BufferedImage> adjustedSpriteSheets = new HashMap<>();

    public PK2MapSector(int sectorWidth, int sectorHeight) {
        width = sectorWidth;
        height = sectorHeight;

        backgroundLayer = new int[sectorWidth * sectorHeight];
        foregroundLayer = new int[sectorWidth * sectorHeight];
        spriteLayer = new int[sectorWidth * sectorHeight];

        Arrays.fill(backgroundLayer, 255);
        Arrays.fill(foregroundLayer, 255);
        Arrays.fill(spriteLayer, 255);
    }

    public void setSize(Rectangle rect) {
        setSize(rect.x, rect.y, rect.width, rect.height);
    }

    public void setSize(int startX, int startY, int newWidth, int newHeight) {
        if (width != newWidth || height != newHeight) {
            if (startX + newWidth > width) {
                newWidth = width - startX;
            }

            if (startY + newHeight > height) {
                newHeight = height - startY;
            }

            // TODO Move this to resizeLayer and account for increasing sector size
            int maxWidth = 0;
            int maxHeight = 0;

            if (width > newWidth) {
                maxWidth = newWidth;
            } else if (width < newWidth) {
                maxWidth = width;
            }

            if (height > newHeight) {
                maxHeight = newHeight;
            } else if (height < newHeight) {
                maxHeight = height;
            }

            backgroundLayer = resizeLayer(backgroundLayer, width, startX, startY, maxWidth, maxHeight);
            foregroundLayer = resizeLayer(foregroundLayer, width, startX, startY, maxWidth, maxHeight);
            spriteLayer = resizeLayer(spriteLayer, width, startX, startY, maxWidth, maxHeight);

            width = newWidth;
            height = newHeight;
        }
    }

    // I would use Arrays.copyOf here, but this method fills the empty parts of the array with 0 instead of 255. 0 is the first tile in the tileset, 255 is the "empty" tile
    private int[] resizeLayer(int[] layer, int srcWidth, int startX, int startY, int layerWidth, int layerHeight) {
        int[] resizedLayer = new int[layerWidth * layerHeight];
        Arrays.fill(resizedLayer, 255);

        int x = startX, y = startY;
        int targetIndex = 0;

        while (targetIndex < layerWidth * layerHeight) {
            resizedLayer[targetIndex] = layer[srcWidth * y + x];

            targetIndex++;

            if (x >= startX + layerWidth) {
                x = startX;
                y++;
            } else {
                x++;
            }
        }

        return resizedLayer;
    }

    public void addSpriteSheet(SpritePrototype sprite) {
        BufferedImage spriteSheet = baseSpriteSheets.get(sprite.getImageFileIdentifier());

        BufferedImage image = new BufferedImage(spriteSheet.getColorModel(),
                spriteSheet.getRaster(),
                spriteSheet.isAlphaPremultiplied(),
                null);

        image = GFXUtils.setPaletteToBackgrounds(image, backgroundImage);
        image = GFXUtils.makeTransparent(image);

        //sprite.setImage(image);

        adjustedSpriteSheets.put(sprite.getImageFileIdentifier(), image);
    }

    public final BufferedImage getSpriteImage(String spriteImageIdentifier) {
        return adjustedSpriteSheets.get(spriteImageIdentifier);
    }

    public static void registerSpriteSheet(String imageFileIdentifier, BufferedImage image) {
        baseSpriteSheets.put(imageFileIdentifier, image);
    }

    public static boolean isSpriteSheetLoaded(String imageFileIdentifier) {
        return baseSpriteSheets.containsKey(imageFileIdentifier);
    }

    public void updateSpritePalettes(ArrayList<SpritePrototype> spriteList) {
        adjustedSpriteSheets.clear();

        for (SpritePrototype sprite : spriteList) {
            addSpriteSheet(sprite);
        }
    }

    public static BufferedImage getBaseSpriteSheet(String imageFileIdentifier) {
        return baseSpriteSheets.get(imageFileIdentifier);
    }

    public int getBGTile(int posX, int posY) {
        if (posX >= 0 && posX < width && posY >= 0 && posY < height) {
            return backgroundLayer[width * posY + posX];
        }

        return 255;
    }

    public int getFGTile(int posX, int posY) {
        if (posX >= 0 && posX < width && posY >= 0 && posY < height) {
            return foregroundLayer[width * posY + posX];
        }

        return 255;
    }

    public int getSpriteTile(int posX, int posY) {
        if (posX >= 0 && posX < width && posY >= 0 && posY < height) {
            return spriteLayer[width * posY + posX];
        }

        return 255;
    }

    public void removeSprite(int id) {
        for (int i = 0; i < spriteLayer.length; ++i) {
            if (spriteLayer[i] == id) {
                spriteLayer[i] = 255;
            } else if (spriteLayer[i] > id && spriteLayer[i] != 255) {
                spriteLayer[i]--;
            }
        }
    }

    public int countTiles(int id) {
        int result = 0;

        for (int i = 0; i < spriteLayer.length; ++i) {
            if (spriteLayer[i] == id) {
                ++result;
            }
        }

        return result;
    }

    public final int[] getForegroundLayer() {
        return foregroundLayer;
    }

    public final int[] getBackgroundLayer() {
        return backgroundLayer;
    }

    public final int[] getSpritesLayer() {
        return spriteLayer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setBackgroundTile(int posX, int posY, int value) {
        backgroundLayer[width * posY + posX] = value;
    }

    public void setForegroundTile(int posX, int posY, int value) {
        foregroundLayer[width * posY + posX] = value;
    }

    public void setSpriteTile(int posX, int posY, int value) {
        spriteLayer[width * posY + posX] = value;
    }

    public void setBackgroundLayer(int[] newLayer) {
        backgroundLayer = newLayer;
    }

    public void setForegroundLayer(int[] newLayer) {
        foregroundLayer = newLayer;
    }

    public void setSpriteLayer(int[] newLayer) {
        spriteLayer = newLayer;
    }

    public String getTilesetName() {
        return tilesetName;
    }

    public String getBgTilesetName() {
        return tilesetBgName;
    }

    public void setBackgroundImage(BufferedImage image) {
        backgroundImage = image;
    }

    public String getBackgroundName() {
        return backgroundName;
    }

    public BufferedImage getTilesetImage() {
        return tilesetImage;
    }

    public BufferedImage getBackgroundTilesetImage() {
        return tilesetBgImage;
    }

    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }

    public BufferedImage getTilesetBgImage() {
        return tilesetBgImage;
    }
}
