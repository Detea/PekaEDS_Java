package pekaeds.pk2.level;

import pekaeds.settings.Settings;
import pekaeds.util.GFXUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.tinylog.Logger;

import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.pk2.sprite.ISpritePrototype;
import pekaeds.pk2.sprite.io.SpriteIO;
import pekaeds.pk2.sprite.io.SpriteMissing;

public class PK2LevelUtils {

    public static PK2Level createDefaultLevel(){
        PK2Level level = new PK2Level();
        PK2LevelSector sector = new PK2LevelSector(PK2LevelSector.CLASSIC_WIDTH, PK2LevelSector.CLASSIC_HEIGHT);

        level.spriteNames.add("rooster.spr");

        sector.name = "main";

        level.name = Settings.getDefaultMapName();
        level.author = Settings.getDefaultAuthor();

        sector.tilesetName = Settings.getDefaultTileset();
        sector.backgroundName = Settings.getDefaultBackground();
        sector.musicName = Settings.getDefaultMusic();


        level.sectors.add(sector);

        return level;
    }

    /**
     * 
     * To prevent loading a tileset multiple times
     */
    static BufferedImage findTileset(String name, PK2Level level){

        name = name.toLowerCase();
        for(PK2LevelSector sector: level.sectors){
            if(sector.tilesetImage!=null && name.equals(sector.tilesetName.toLowerCase())){
                return sector.tilesetImage;
            }
            else if(sector.tilesetBgImage!=null && name.equals(sector.tilesetBgName.toLowerCase())){
                return sector.tilesetBgImage;
            }
        }

        return null;
    }
    /**
     * to prevent loading a background multiple times
     */
    static BufferedImage findBackground(String name, PK2Level level){
        name = name.toLowerCase();
        for(PK2LevelSector sector: level.sectors){
            if(sector.backgroundImage!=null && name.equals(sector.backgroundName.toLowerCase())){
                return sector.backgroundImage;
            }
        }
        return null;
    }

    private static void loadTileset(PK2LevelSector sector){

        try {
            File tilesetFile = PK2FileSystem.findAsset(sector.tilesetName, PK2FileSystem.TILESET_DIR);
            sector.tilesetImage = GFXUtils.makeTransparent(ImageIO.read(tilesetFile));
        } catch (IOException e) {
            Logger.error(e);
            JOptionPane.showMessageDialog(null, "Unable to load: \"" + sector.tilesetName + "\"", "Unable to find tileset", JOptionPane.ERROR_MESSAGE);

            //fallback to default
            try{
                File tilesetFile = PK2FileSystem.findAsset(Settings.getDefaultTileset(), PK2FileSystem.TILESET_DIR);
                sector.tilesetImage = GFXUtils.makeTransparent(ImageIO.read(tilesetFile));
            }
            catch(IOException e2){
                Logger.error(e2);
            }
        }

    }

    private static void loadTilesetBG(PK2LevelSector sector){
        if(sector.tilesetBgName==null || sector.tilesetBgName.equals(""))return;

        try{
            File tilesetBgFile = PK2FileSystem.findAsset(sector.tilesetBgName, PK2FileSystem.TILESET_DIR);
            sector.tilesetBgImage = GFXUtils.makeTransparent(ImageIO.read(tilesetBgFile));

        } catch (IOException e){
            Logger.error(e);
            sector.tilesetBgImage = null;
        }
    }

    private static void loadBackground(PK2LevelSector sector){       
        try {
            File backgroundFile = PK2FileSystem.findAsset(sector.backgroundName, PK2FileSystem.SCENERY_DIR);
            sector.backgroundImage = ImageIO.read(backgroundFile);

        } catch (IOException e) {
            Logger.error(e);
            JOptionPane.showMessageDialog(null, "Unable to load: \"" + sector.backgroundName + "\"", "Unable to find background", JOptionPane.ERROR_MESSAGE);

            //fallback to default
            try{
                File backgroundFile = PK2FileSystem.findAsset(Settings.getDefaultBackground(), PK2FileSystem.TILESET_DIR);
                sector.tilesetImage = ImageIO.read(backgroundFile);
            }
            catch(IOException e2){
                Logger.error(e2);
            }
        }
    }



    public static void loadLevelAssets(PK2Level level){
        /**
         * Load sprites
         */
        level.sprites.clear();

        int id = 0;

        for(String spriteName : level.spriteNames){
            try{
                File spriteFile = PK2FileSystem.findSprite(spriteName);
                ISpritePrototype sprite = SpriteIO.loadSprite(spriteFile);
                level.sprites.add(sprite);

                sprite.setPlacedAmount(level.countSprites(id));
            }
            catch(FileNotFoundException e){
                Logger.warn("Sprite not found: \""+spriteName+"\"");
                
                ISpritePrototype sprite = new SpriteMissing(spriteName);
                sprite.setPlacedAmount(level.countSprites(id));
                level.sprites.add(sprite);
            }
            catch(Exception e){
                //Logger.error(e);
                Logger.warn("Sprite not found: \""+spriteName+"\"");
                
                ISpritePrototype sprite = new SpriteMissing(spriteName);
                sprite.setPlacedAmount(level.countSprites(id));
                level.sprites.add(sprite);
            }

            id+=1;
        }

        for(PK2LevelSector sector: level.sectors){
            loadBackground(sector);
            loadTileset(sector);
            loadTilesetBG(sector);
        }
    }
}
