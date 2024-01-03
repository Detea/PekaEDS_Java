package pekaeds.pk2.map;


import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.tinylog.Logger;

import pekaeds.data.Layer;
import pekaeds.pk2.sprite.*;
import pekaeds.settings.Settings;
import pekaeds.util.file.PK2FileUtils;

public class PK2MapReader13 implements PK2MapReader {
    //private final Settings settings = new Settings();
    
    @Override
    public PK2Map13 load(File filename) throws IOException {
        var map = new PK2Map13();
        
        var in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
        
        boolean validMap = true;
        
        for (int i = 0; i < PK2Map13.ID.size(); i++) {
            int by = in.readByte() & 0xFF; // Java doesn't have unsigned types and that makes me sad :(
    
            if (by != PK2Map13.ID.get(i)) validMap = false;
        }
        
        if (!validMap) {
            Logger.warn("Unable to recognize file as a Pekka Kana 2 map of version 1.3.");

            in.close();            
            return null;
        } else {
            String tileset = PK2FileUtils.readString(in, 13);
            String background = PK2FileUtils.readString(in, 13);
            String music = PK2FileUtils.readString(in, 13);
    
            String mapName = PK2FileUtils.readString(in, 40);
            String author = PK2FileUtils.readString(in, 40);
    
            int levelNr = PK2FileUtils.readInt(in);
            int weather = PK2FileUtils.readInt(in);
    
            // Switch values 1-3, these values are not used in this map format and are hardcoded to be 2000
            in.readNBytes(8);
            in.readNBytes(8);
            in.readNBytes(8);
    
            int time = PK2FileUtils.readInt(in);
            
            PK2FileUtils.readInt(in); // "Extra", not used?
            
            int scrollingType = PK2FileUtils.readInt(in);
            
            int playerSpriteId = PK2FileUtils.readInt(in);
            
            int mapX = PK2FileUtils.readInt(in);
            int mapY = PK2FileUtils.readInt(in);
    
            int iconId = PK2FileUtils.readInt(in);
    
            int spritesAmount = PK2FileUtils.readInt(in);
            
            var spriteFilenames = new ArrayList<String>();
            for (int i = 0; i < spritesAmount; i++) {
                spriteFilenames.add(PK2FileUtils.readString(in, 13));
            }
            
            int[][] layerBackground = readLayer(in);
            int[][] layerForeground = readLayer(in);
            int[][] layerSprites = readLayer(in);
    
            map.setTileset(tileset);
            map.setBackground(background);
            map.setMusic(music);
            map.setName(mapName);
            map.setAuthor(author);
            
            map.setLevelNumber(levelNr);
            map.setWeatherType(weather);
            
            map.setTime(time);
            
            map.setScrollType(scrollingType);
            
            map.setPlayerSpriteId(playerSpriteId);
            
            map.setMapX(mapX);
            map.setMapY(mapY);
            
            map.setIcon(iconId);
            
            map.setSpritesAmount(spritesAmount);
            
            map.setSpriteFilenames(spriteFilenames);
            map.setLayer(Layer.BACKGROUND, layerBackground);
            map.setLayer(Layer.FOREGROUND, layerForeground);
            map.setSpritesLayer(layerSprites);
        }
        
        in.close();
        
        return map;
    }
    
    @Override
    public List<ISpritePrototypeEDS> loadSpriteList(List<String> spriteFilenames, BufferedImage backgroundImage, int playerSpriteIndex, File mapFile) throws IOException {
        var spriteList = new ArrayList<ISpritePrototypeEDS>();

        boolean usingNativeReader = false;

        String episodeName = null;

        if(SpriteReaderNative.handler!=null){
            SpriteReaderNative.handler.clear();

            File episode = mapFile.getParentFile();
            episodeName = episode.getName();
            
            SpriteReaderNative.handler.setSearchingDir("episodes" + File.separatorChar + episodeName);
            usingNativeReader = true;
        }
        
        for (String filename : spriteFilenames) {

            if(usingNativeReader){
                ISpritePrototypeEDS sprite = SpriteReaders.readerNative.loadImageData(new File(filename), episodeName, backgroundImage);
                if(sprite==null){
                    spriteList.add(new PK2SpriteMissing());
                }
                else{
                    spriteList.add(sprite);
                }
            }
            else{
                File spriteFile = new File(Settings.getSpritesPath() + filename);
            
                if (!spriteFile.exists()) {
                    Logger.warn("Unable to find sprite file {}.", filename);
                    
                    spriteList.add(new PK2SpriteMissing());
                } else {
                    var sprReader = SpriteReaders.getReader(spriteFile);
                    
                    if (sprReader == null) {
                        Logger.warn("Unable to recognize file as Pekka Kana 2 sprite.");
                    } else {
                        var spr = sprReader.loadImageData(spriteFile, backgroundImage);
                        
                        spriteList.add(spr);
                    }
                }
            }
        }
        
        // TODO I really don't like this solution, but whatever. Shit works, you know? lol Maybe find a better way to do this.
        for (int i = 0; i < spriteList.size(); i++) {
            if (i == playerSpriteIndex) {
                spriteList.get(i).setPlayerSprite(true);
                
                break;
            }
        }
        
        return spriteList;
    }
    
    /**
     * Returns a PK2Map13 instance with only the following values set:
     *  - mapX
     *  - mapY
     *  - icon
     * @param file Pekka Kana 2 map file
     * @return PK2Map13 instance with the following values set: mapX, mapY, icon
     */
    public PK2Map loadIconDataOnly(File file) {
        PK2Map map = null;
        
        try (var in = new DataInputStream(new FileInputStream(file))) {
            in.skipBytes(0xC4);
    
            int x = PK2FileUtils.readInt(in);
            int y = PK2FileUtils.readInt(in);
            
            int icon = PK2FileUtils.readInt(in);
            
            map = new PK2Map13();
            map.setMapX(x);
            map.setMapY(y);
            map.setIcon(icon);
        } catch (IOException e) {
            Logger.info(e, "Unable to load icon data from file: {}", file.getAbsolutePath());
        }
    
        return map;
    }
    
    private int[][] readLayer(DataInputStream in) throws IOException {
        int startX = PK2FileUtils.readInt(in);
        int startY = PK2FileUtils.readInt(in);
        int width = PK2FileUtils.readInt(in);
        int height = PK2FileUtils.readInt(in);
    
        var layer = new int[PK2Map13.HEIGHT][PK2Map13.WIDTH];
    
        for (int i = 0; i < PK2Map13.HEIGHT; i++) {
            for (int j = 0; j < PK2Map13.WIDTH; j++) {
                layer[i][j] = 255;
            }
        }
        
        for (int y = startY; y <= startY + height; y++) {
            for (int x = startX; x <= startX + width; x++) {
                if (x < PK2Map13.WIDTH && y < PK2Map13.HEIGHT) {
                    layer[y][x] = in.readByte() & 0xFF;
                }
            }
        }
        
        return layer;
    }
}
