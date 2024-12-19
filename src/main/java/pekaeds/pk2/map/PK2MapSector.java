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

    private int[][] backgroundLayer;
    private int[][] foregroundLayer;
    private int[][] spriteLayer;

    private int width;
    private int height;

    // Contains all the sprite sheets with their own palette, just adjusted for sprite color variants
    private static HashMap<String, BufferedImage> baseSpriteSheets = new HashMap<>();

    // Contains the sprite sheets that have the same palette as the background image
    private HashMap<String, BufferedImage> adjustedSpriteSheets = new HashMap<>();

    public PK2MapSector(int sectorWidth, int sectorHeight) {
        width = sectorWidth;
        height = sectorHeight;

        backgroundLayer = new int[sectorWidth][sectorHeight];
        foregroundLayer = new int[sectorWidth][sectorHeight];
        spriteLayer = new int[sectorWidth][sectorHeight];

        for (int i = 0; i < sectorWidth; ++i) {
            Arrays.fill(backgroundLayer[i], 255);
        }

        for (int i = 0; i < sectorWidth; ++i) {
            Arrays.fill(foregroundLayer[i], 255);
        }

        for (int i = 0; i < sectorWidth; ++i) {
            Arrays.fill(spriteLayer[i], 255);
        }
    }

    public void setSize(Rectangle rect) {
        setSize(rect.x, rect.y, rect.width, rect.height);
    }

    public void setSize(int startX, int startY, int newWidth, int newHeight) {
        if (width != newWidth || height != newHeight) {
            backgroundLayer = resizeLayer(backgroundLayer, width, height, startX, startY, newWidth, newHeight);
            foregroundLayer = resizeLayer(foregroundLayer, width, height, startX, startY, newWidth, newHeight);
            spriteLayer = resizeLayer(spriteLayer, width, height, startX, startY, newWidth, newHeight);

            width = newWidth;
            height = newHeight;
        }
    }

    // I would use Arrays.copyOf here, but this method fills the empty parts of the array with 0 instead of 255. 0 is the first tile in the tileset, 255 is the "empty" tile
    private int[][] resizeLayer(int[][] layer, int currentWidth, int currentHeight, int startX, int startY, int newWidth, int newHeight) {
        int[][] resizedLayer = new int[ newWidth][newHeight];
        for (int i = 0; i <  newWidth; i++) {
            Arrays.fill(resizedLayer[i], 255);
        }

        if (startX + newWidth > currentWidth) {
            currentWidth = currentWidth - startX;
        }

        if (startY + newHeight > currentHeight) {
            currentHeight = currentHeight - startY;
        }

        int maxWidth = 0;
        int maxHeight = 0;

        if ( newWidth < currentWidth) {
            maxWidth =  newWidth;
        } else if ( newWidth > currentWidth) {
            maxWidth = currentWidth;
        }

        if (newHeight < currentHeight) {
            maxHeight = newHeight;
        } else if (newHeight > currentHeight) {
            maxHeight = currentHeight;
        }

        for (int srcX = startX; srcX < startX + maxWidth; ++srcX) {
            for (int srcY = startY; srcY < startY + maxHeight; ++srcY) {
                resizedLayer[srcX - startX][srcY - startY] = layer[srcX][srcY];
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
            return backgroundLayer[posX][posY];
        }

        return 255;
    }

    public int getFGTile(int posX, int posY) {
        if (posX >= 0 && posX < width && posY >= 0 && posY < height) {
            return foregroundLayer[posX][posY];
        }

        return 255;
    }

    public int getSpriteTile(int posX, int posY) {
        if (posX >= 0 && posX < width && posY >= 0 && posY < height) {
            return spriteLayer[posX][posY];
        }

        return 255;
    }

    public void removeSprite(int id) {
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (spriteLayer[x][y] == id) {
                    spriteLayer[x][y] = 255;
                } else if (spriteLayer[x][y] > id && spriteLayer[x][y] != 255) {
                    spriteLayer[x][y]--;
                }
            }
        }
    }

    public int countTiles(int id) {
        int result = 0;

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (spriteLayer[x][y] == id) {
                    ++result;
                }
            }
        }

        return result;
    }

    public final int[][] getForegroundLayer() {
        return foregroundLayer;
    }

    public final int[][] getBackgroundLayer() {
        return backgroundLayer;
    }

    public final int[][] getSpritesLayer() {
        return spriteLayer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setBackgroundTile(int posX, int posY, int value) {
        backgroundLayer[posX][posY] = value;
    }

    public void setForegroundTile(int posX, int posY, int value) {
        foregroundLayer[posX][posY] = value;
    }

    public void setSpriteTile(int posX, int posY, int value) {
        spriteLayer[posX][posY] = value;
    }

    public void setBackgroundLayer(int[][] newLayer) {
        backgroundLayer = newLayer;
    }

    public void setForegroundLayer(int[][] newLayer) {
        foregroundLayer = newLayer;
    }

    public void setSpriteLayer(int[][] newLayer) {
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
