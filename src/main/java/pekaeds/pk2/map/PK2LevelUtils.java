package pekaeds.pk2.map;

import pekaeds.settings.Settings;
import pekaeds.util.GFXUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.tinylog.Logger;

import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.pk2.sprite.SpritePrototype;
import pekaeds.pk2.sprite.io.SpriteIO;
import pekaeds.pk2.sprite.io.SpriteMissing;

public class PK2LevelUtils {

    public static PK2Map createDefaultLevel() {
        PK2Map level = new PK2Map();
        PK2MapSector sector = new PK2MapSector(PK2MapSector.CLASSIC_WIDTH, PK2MapSector.CLASSIC_HEIGHT);

        level.spriteFiles.add("rooster.spr");

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
     * To prevent loading a tileset multiple times
     */
    static BufferedImage findTileset(String name, PK2Map level) {

        name = name.toLowerCase();
        for (PK2MapSector sector : level.sectors) {
            if (sector.tilesetImage != null && name.equals(sector.tilesetName.toLowerCase())) {
                return sector.tilesetImage;
            } else if (sector.tilesetBgImage != null && name.equals(sector.tilesetBgName.toLowerCase())) {
                return sector.tilesetBgImage;
            }
        }

        return null;
    }

    public static void loadTileset(PK2MapSector sector) {
        try {
            File tilesetFile = PK2FileSystem.findAsset(sector.tilesetName, PK2FileSystem.TILESET_DIR);

            BufferedImage tileset = ImageIO.read(tilesetFile);
            sector.tilesetImage = GFXUtils.setPaletteToBackgrounds(tileset, sector.getBackgroundImage());
            sector.tilesetImage = GFXUtils.makeTransparent(sector.tilesetImage);
        } catch (IOException e) {
            Logger.error(e);
            JOptionPane.showMessageDialog(null, "Unable to load: \"" + sector.tilesetName + "\"", "Unable to find tileset", JOptionPane.ERROR_MESSAGE);

            //fallback to default
            try {
                File tilesetFile = PK2FileSystem.findAsset(Settings.getDefaultTileset(), PK2FileSystem.TILESET_DIR);
                sector.tilesetImage = GFXUtils.makeTransparent(ImageIO.read(tilesetFile));
            } catch (IOException e2) {
                Logger.error(e2);
            }
        }

    }

    public static void loadTilesetBG(PK2MapSector sector) {
        if (sector.tilesetBgName == null || sector.tilesetBgName.equals("")) return;

        try {
            File tilesetBgFile = PK2FileSystem.findAsset(sector.tilesetBgName, PK2FileSystem.TILESET_DIR);

            BufferedImage tileset = ImageIO.read(tilesetBgFile);
            sector.tilesetBgImage = GFXUtils.setPaletteToBackgrounds(tileset, sector.getBackgroundImage());
            sector.tilesetBgImage = GFXUtils.makeTransparent(sector.tilesetBgImage);
        } catch (IOException e) {
            Logger.error(e);
            sector.tilesetBgImage = null;
        }
    }

    public static void loadBackground(PK2MapSector sector) {
        try {
            File backgroundFile = PK2FileSystem.findAsset(sector.backgroundName, PK2FileSystem.SCENERY_DIR);
            sector.setBackgroundImage(ImageIO.read(backgroundFile));
        } catch (IOException e) {
            Logger.error(e);
            JOptionPane.showMessageDialog(null, "Unable to load: \"" + sector.backgroundName + "\"", "Unable to find background", JOptionPane.ERROR_MESSAGE);

            //fallback to default
            try {
                File backgroundFile = PK2FileSystem.findAsset(Settings.getDefaultBackground(), PK2FileSystem.TILESET_DIR);
                sector.tilesetImage = ImageIO.read(backgroundFile);
            } catch (IOException e2) {
                Logger.error(e2);
            }
        }
    }

    public static void loadLevelAssets(PK2Map map) {
        map.sprites.clear();

        for (PK2MapSector sector : map.sectors) {
            loadBackground(sector);
            loadTileset(sector);
            loadTilesetBG(sector);
        }

        int id = 0;

        for (String spriteName : map.spriteFiles) {
            try {
                File spriteFile = PK2FileSystem.findSprite(spriteName);
                SpritePrototype sprite = SpriteIO.getSpriteReader(spriteFile).readSpriteFile(spriteFile);

                map.sprites.add(sprite);
                map.loadSpriteImage(sprite);

                sprite.setPlacedAmount(map.countSprites(id));
            } catch (Exception e) {
                Logger.warn("Unable to load sprite file: \"" + spriteName + "\"");

                SpritePrototype sprite = new SpriteMissing(spriteName);
                sprite.setPlacedAmount(map.countSprites(id));

                //map.sprites.add(sprite);
                map.loadSpriteImage(sprite);
            }

            id += 1;
        }
    }

    public static PK2MapSector createDefaultSector() {
        PK2MapSector sector = new PK2MapSector(PK2MapSector.CLASSIC_WIDTH, PK2MapSector.CLASSIC_HEIGHT);
        sector.name = "Empty";
        sector.tilesetName = Settings.getDefaultTileset();
        sector.backgroundName = Settings.getDefaultBackground();
        sector.musicName = Settings.getDefaultMusic();

        return sector;
    }
}
