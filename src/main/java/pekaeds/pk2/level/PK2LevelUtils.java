package pekaeds.pk2.level;

import pekaeds.settings.Settings;

public class PK2LevelUtils {

    public static PK2Level createDefaultLevel(){
        PK2Level level = new PK2Level();
        PK2LevelSector sector = new PK2LevelSector(PK2LevelSector.CLASSIC_WIDTH, PK2LevelSector.CLASSIC_HEIGHT);

        sector.name = "main";

        level.name = Settings.getDefaultMapName();
        level.author = Settings.getDefaultAuthor();

        sector.tilesetName = Settings.getDefaultTileset();
        sector.backgroundName = Settings.getDefaultBackground();
        sector.musicName = Settings.getDefaultMusic();


        level.sectors.add(sector);

        return level;
    }
}
