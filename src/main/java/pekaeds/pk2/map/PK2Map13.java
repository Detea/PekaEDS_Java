package pekaeds.pk2.map;

import org.tinylog.Logger;

import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.pk2.sprite.ISpritePrototype;
import pekaeds.pk2.sprite.io.SpriteIO;
import pekaeds.settings.Settings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Deprecated
public class PK2Map13 extends PK2Map {
    
    public static final int WIDTH = 256;
    public static final int HEIGHT = 224;
    
    public static final List<Integer> ID = List.of(0x31, 0x2E, 0x33, 0x00, 0xCD);   
   
    public PK2Map13() {
        layers.add(new int[HEIGHT][WIDTH]);
        layers.add(new int[HEIGHT][WIDTH]);
    }
    
    @Override
    public void reset() {
        setName(Settings.getDefaultMapName());
        setAuthor(Settings.getDefaultAuthor());
        setTileset(Settings.getDefaultTileset());
        setBackground(Settings.getDefaultBackground());
        setMusic(Settings.getDefaultMusic());
        
        // TODO Not necessary to do this?
        try {
            setBackgroundImage(ImageIO.read(PK2FileSystem.findAsset(this.getBackground(), PK2FileSystem.SCENERY_DIR)));
            setTilesetImage(ImageIO.read(PK2FileSystem.findAsset(this.getTileset(), PK2FileSystem.TILESET_DIR)));
        } catch (IOException e) {
            Logger.error("Unable to load default background and/or tileset image(s).");
    
            JOptionPane.showMessageDialog(null, "Unable to find default background and/or tileset image.\nCheck under 'Settings -> Defaults' if these files exist.", "Unable to load defaults", JOptionPane.ERROR_MESSAGE);
        }
    
        setLevelNumber(1);
        setTime(0);
        setScrollType(0);
        setWeatherType(0);
        setIcon(0);
        
        setMapX(0);
        setMapY(0);
        
        setSpritesAmount(1);
        
        getSpriteFilenames().clear();
        getSpriteFilenames().add("rooster.spr");

        //ISpriteReader reader = SpriteReaders.getReader(Paths.get(Settings.getSpritesPath(), "rooster.spr").toFile());

        getSpriteList().clear();

        try{
            File roosterFile = PK2FileSystem.findSprite("rooster.spr");
            ISpritePrototype roosterSprite = SpriteIO.loadSprite(roosterFile, getBackgroundImage());
            getSpriteList().add(roosterSprite);

        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "Unable to load \"rooster.spr\"", "Missing rooster sprite", JOptionPane.ERROR_MESSAGE);
        }

        setPlayerSpriteId(0);
        
        // TODO Maybe find a better way to do this
        var emptyLayer = new int[224][256];
        var emptyLayer2 = new int[224][256];
        for (int x = 0; x < 256; x++) {
            for (int y = 0; y < 224; y++) {
                emptyLayer[y][x] = 255;
                emptyLayer2[y][x] = 255;
            }
        }
        
        getLayers().clear();
        getLayers().add((emptyLayer));
        getLayers().add((emptyLayer2));
    
        var emptyLayer3 = new int[224][256];
        for (int x = 0; x < 256; x++) {
            for (int y = 0; y < 224; y++) {
                emptyLayer3[y][x] = 255;
            }
        }
        
        setSpritesLayer(emptyLayer3);
    }
}
