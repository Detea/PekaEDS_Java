package pk.pekaeds.pk2.map;


import pk.pekaeds.data.Layer;
import pk.pekaeds.util.GFXUtils;
import pk.pekaeds.util.PK2FileUtils;
import pk.pekaeds.pk2.sprite.*;
import pk.pekaeds.settings.Settings;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.tinylog.Logger;
import pk.pekaeds.util.VersionHandler;

public class PK2MapReader13 implements PK2MapReader {
    private final Settings settings = new Settings();
    
    @Override
    public PK2Map13 load(File filename) throws IOException {
        var map = new PK2Map13();
        
        // TODO use try-with
        var in = new DataInputStream(new FileInputStream(filename));
        
        var versionId = new byte[5];
        in.read(versionId);
        
        boolean validMap = true;
        
        // TODO Should use VersionHandler for this
        for (int i = 0; i < versionId.length; i++) {
            if (versionId[i] != PK2Map13.ID.get(i)) validMap = false;
        }
        
        if (validMap) {
            Logger.warn("Unable to recognize file as a Pekka Kana 2 map of version 1.3.");
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
    
            var tilesetImage = ImageIO.read(new File(Settings.getTilesetPath() + map.getTileset()));
            var backgroundImage = ImageIO.read(new File(settings.getBackgroundsPath() + map.getBackground()));
            
            tilesetImage = GFXUtils.setPaletteToBackgrounds(tilesetImage, backgroundImage);
            
            map.setSpriteList(loadSpriteList(map.getSpriteFilenames(), backgroundImage, map.getPlayerSpriteId()));
        }
        
        in.close();
        
        return map;
    }
    
    @Override
    public List<PK2Sprite> loadSpriteList(List<String> spriteFilenames, BufferedImage backgroundImage, int playerSpriteIndex) throws IOException {
        var spriteList = new ArrayList<PK2Sprite>();
        
        // TODO Look for sprites in the currently loaded levels folder
        for (String filename : spriteFilenames) {
            var spriteFile = new File(settings.getSpritesPath() + filename);
            
            // TODO handle missing sprites?
            if (!spriteFile.exists()) {
                Logger.warn("Unable to find sprite file {}.", filename);
            } else {
                var version = new byte[5];
                
                var in = new DataInputStream(new FileInputStream(spriteFile));
                
                in.read(version);
                in.close();
    
                int spriteVersion = VersionHandler.isValidSprite(version);
                
                PK2SpriteReader sprReader = new PK2SpriteReader13(); // TODO Use SpriteReaders.getReader()
                switch (spriteVersion) {
                    case VersionHandler.SPRITE_1_2 -> sprReader = new PK2SpriteReader12();
                    case VersionHandler.SPRITE_1_3 -> sprReader = new PK2SpriteReader13();
                }
                
                int spritesAmount = 0;
                if (sprReader == null) {
                    Logger.warn("Unable to recognize file as Pekka Kana 2 sprite.");
                } else {
                    var spr = sprReader.loadImageData(spriteFile, backgroundImage);
                    
                    spriteList.add(spr);
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
