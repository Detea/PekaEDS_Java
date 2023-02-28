package pk.pekaeds.pk2.map;

import pk.pekaeds.pk2.sprite.PK2Sprite;
import pk.pekaeds.pk2.sprite.PK2SpriteReader13;
import pk.pekaeds.pk2.sprite.SpriteReaders;
import pk.pekaeds.settings.Settings;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class PK2Map {
    private ChangeListener changeListener;
    private ChangeEvent changeEvent = new ChangeEvent(this);
    
    public static final int[] ID = new int[] {0, 0, 0, 0, 0};
    
    protected char[] version = new char[5];
    
    private BufferedImage backgroundImage;
    private BufferedImage backgroundTilesetImage = null;
    private BufferedImage tilesetImage;
    
    private String name;
    private String author;
    private String tileset;
    private String background;
    private String music;
    
    private int levelNumber;
    private int time;
    
    private int scrollType;
    private int weatherType;
    private int icon;
    
    private int mapX;
    private int mapY;
    
    private int playerSpriteId;
    
    protected int spritesAmount;
    
    protected List<String> spriteFilenames = new ArrayList<>();
    
    protected List<int[][]> layers = new ArrayList<>();
    protected int[][] spritesLayer;
    
    protected List<PK2Sprite> spriteList = new ArrayList<>();
    
    public PK2Map() {
        layers.add(new int[224][256]);
        layers.add(new int[224][256]);
    }
    
    /**
     * Resets every value in the map. This method is used when creating a new empty map.
     */
    public abstract void reset();
    
    public boolean checkVersion(byte[] id) {
        if (id.length != 5) return false;
        
        for (int i = 0; i < 5; i++) {
            if (version[i] != (id[i] & 0xFF)) {
                return false;
            }
        }
        
        return true;
    }
    
    public void addSprite(PK2Sprite sprite) {
        spritesAmount++;
        
        spriteFilenames.add(sprite.getFilename());
        
        spriteList.add(sprite);
    }
    
    /**
     * Removes a sprite from the sprite filename list, the PK2Sprite sprite object list and from the sprite layer array.
     * @param filename The filename of the sprite. (Example: rooster.spr)
     */
    public void removeSprite(String filename) {
        if (spriteFilenames.contains(filename)) {
            // TODO Should spriteList be checked as well? It should contain this sprite when spriteFilenames has it.
            spriteFilenames.remove(filename);
            spritesAmount--;
            
            int spriteIndex = -1;
            for (int i = 0; i < spriteList.size(); i++) {
                if (spriteList.get(i).getFilename().equals(filename)) {
                    spriteIndex = i;
    
                    break;
                }
            }
            
            // It really shouldn't be -1, but we'll check just to make sure...
            if (spriteIndex != -1) {
                for (int x = 0; x < Settings.getMapProfile().getMapWidth(); x++) {
                    for (int y = 0; y < Settings.getMapProfile().getMapHeight(); y++) {
                        if (spritesLayer[y][x] != 255) {
                            if (spritesLayer[y][x] == spriteIndex) {
                                spritesLayer[y][x] = 255;
                            } else if (spritesLayer[y][x] > spriteIndex) {
                                spritesLayer[y][x]--;
                            }
                        }
                    }
                }
                
                spriteList.remove(spriteIndex);
                
                changeListener.stateChanged(changeEvent);
            }
        }
    }
    
    public int getTileAt(int layer, int x, int y) {
        if (x >= 0 && x < PK2Map13.WIDTH && y >= 0 && y < PK2Map13.HEIGHT) {
            return layers.get(layer)[y][x];
        }
        
        return -1;
    }
    
    public void setTileAt(int layer, int x, int y, int tileID) {
        if (x >= 0 && x < PK2Map13.WIDTH && y >= 0 && y < PK2Map13.HEIGHT) {
            layers.get(layer)[y][x] = tileID;
    
            changeListener.stateChanged(changeEvent);
        }
    }
    
    public void setSpriteAt(int x, int y, int spriteID) {
        if (x >= 0 && x < PK2Map13.WIDTH && y >= 0 && y < PK2Map13.HEIGHT) {
            spritesLayer[y][x] = spriteID;
    
            changeListener.stateChanged(changeEvent);
        }
    }
    
    public int getSpriteIdAt(int x, int y) {
        if (x >= 0 && x < PK2Map13.WIDTH && y >= 0 && y < PK2Map13.HEIGHT) {
            return spritesLayer[y][x];
        }
        
        return 255;
    }
    
    public PK2Sprite getSpriteAt(int x, int y) {
        if (getSpriteIdAt(x, y) != 255) {
            return spriteList.get(getSpriteIdAt(x, y));
        }
        
        return null;
    }
    
    public List<PK2Sprite> getSpriteList() {
        return spriteList;
    }
    
    public String getName() {
        return name;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getTileset() {
        return tileset;
    }
    
    public String getBackground() {
        return background;
    }
    
    public String getMusic() {
        return music;
    }
    
    public int getLevelNumber() {
        return levelNumber;
    }
    
    public int getTime() {
        return time;
    }
    
    public int getScrollType() {
        return scrollType;
    }
    
    public int getWeatherType() {
        return weatherType;
    }
    
    public int getIcon() {
        return icon;
    }
    
    public int getMapX() {
        return mapX;
    }
    
    public int getMapY() {
        return mapY;
    }
    
    public void setLayer(int index, int[][] layer) {
        layers.set(index, layer);
    }

    public void setSpritesLayer(int[][] layer) {
        this.spritesLayer = layer;
    }
    
    public int[][] getSpritesLayer() {
        return spritesLayer;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public void setTileset(String tileset) {
        this.tileset = tileset;
    }
    
    public void setBackground(String background) {
        this.background = background;
    }
    
    public void setMusic(String music) {
        this.music = music;
    }
    
    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }
    
    public void setTime(int time) {
        this.time = time;
    }
    
    public void setScrollType(int scrollType) {
        this.scrollType = scrollType;
    }
    
    public void setWeatherType(int weatherType) {
        this.weatherType = weatherType;
    }
    
    public void setIcon(int icon) {
        this.icon = icon;
    }
    
    public void setMapX(int mapX) {
        this.mapX = mapX;
    }
    
    public void setMapY(int mapY) {
        this.mapY = mapY;
    }
    
    public int getPlayerSpriteId() {
        return playerSpriteId;
    }
    
    public void setPlayerSpriteId(int playerSpriteId) {
        this.playerSpriteId = playerSpriteId;
    }
    
    public int getSpritesAmount() {
        return spritesAmount;
    }
    
    public void setSpritesAmount(int spritesAmount) {
        this.spritesAmount = spritesAmount;
    }
    
    public List<String> getSpriteFilenames() {
        return spriteFilenames;
    }
    
    public void setSpriteFilenames(List<String> spriteFilenames) {
        this.spriteFilenames = spriteFilenames;
    }
    
    public List<int[][]> getLayers() {
        return layers;
    }
    
    public void setLayers(List<int[][]> layers) {
        this.layers = layers;
    }
    
    /**
     * Returns the sprite at index in the sprite list, or null if the sprite doesn't exist in the list.
     * @param index
     * @return
     */
    public PK2Sprite getSprite(int index) {
        if (index >= 0 && index < spriteList.size()) {
            return spriteList.get(index);
        }
        
        return null;
    }
    
    public void setSpriteList(List<PK2Sprite> list) {
        this.spriteList = list;
    }
    
    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }
    
    public void setBackgroundImage(BufferedImage backgroundImage) {
        this.backgroundImage = backgroundImage;
    }
    
    public BufferedImage getTilesetImage() {
        return tilesetImage;
    }
    
    public void setTilesetImage(BufferedImage tilesetImage) {
        this.tilesetImage = tilesetImage;
    }
    
    public void setChangeListener(ChangeListener listener) {
        this.changeListener = listener;
    }
    
    public void setBackgroundTilesetImage(BufferedImage bgTilesetImage) {
        this.backgroundTilesetImage = bgTilesetImage;
    }
    
    public BufferedImage getBackgroundTilesetImage() {
        return backgroundTilesetImage;
    }
}
