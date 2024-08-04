package pekaeds.pk2.level;

import java.io.*;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Rectangle;

import pekaeds.util.file.PK2FileUtils;

public final class PK2LevelIO {

    public static void main(String args[]){
        System.out.println("Testing Level IO");

        try{
            //File f = new File("/Users/saturninufolud/c++/pk2_greta/res/episodes/Debug Island/debug.map");
            File f = new File("/Users/saturninufolud/c++/pk2_greta/res/episodes/Debug Island/lua_cult.map");
            PK2Level level = loadLevel(f);

            System.out.println(level.name);

            //level.sectors.get(0).weather = 5;

            File f2 = new File("/Users/saturninufolud/c++/pk2_greta/res/episodes/Debug Island/debug_x.map");

            saveLevel(level, f2);
        }
        catch(Exception e){
            e.printStackTrace();
        }        
    }

    private static final int TILES_COMPRESSION_NONE = 0;
    private static final int TILES_OFFSET_NEW = 1;
    private static final int TILES_OFFSET_LEGACY = 2;

    private static void readTilesArray(DataInputStream in, PK2TileArray array, int compression) throws Exception{

        switch (compression) {
            case TILES_COMPRESSION_NONE:{                
                byte[] data = new byte[array.size()];
                in.read(data);

                for(int i=0;i<data.length;++i){
                    array.setByIndex(i, (int)data[i] & 0xFF);
                }
            }    
            break;
            
            case TILES_OFFSET_NEW:{
                int startX = Integer.reverseBytes(in.readInt());
                int startY = Integer.reverseBytes(in.readInt());
                int width = Integer.reverseBytes(in.readInt());
                int height = Integer.reverseBytes(in.readInt());

                for (int y = startY; y <= startY + height; y++) {
                    for (int x = startX; x <= startX + width; x++) {
                        array.set(x, y, in.readByte() & 0xFF);
                    }
                }
            }
            break;

            case TILES_OFFSET_LEGACY:{
                int startX = PK2FileUtils.readInt(in);
                int startY = PK2FileUtils.readInt(in);
                int width = PK2FileUtils.readInt(in);
                int height = PK2FileUtils.readInt(in);
                            
                for (int y = startY; y <= startY + height; y++) {
                    for (int x = startX; x <= startX + width; x++) {
                        array.set(x, y, in.readByte() & 0xFF);
                    }
                }
            }
            break;
        
            default:
                throw new IOException("[PK2 Level IO] Not supported tiles compression!");
        }
    }

    static void writeTilesArray(DataOutputStream out, PK2TileArray array) throws Exception{
        byte[] data = new byte[array.size()];
        for(int i=0;i<data.length;++i){
            data[i] = (byte)array.getByIndex(i);
        }
        out.write(data);
    }

    static void writeTilesArrayWithOffset(DataOutputStream out, PK2TileArray array) throws Exception{
        
        Rectangle r = array.calculateOffsets();

        int startX = r.x;
        int startY = r.y;
        int width = r.width;
        int height = r.height;

        out.writeInt(Integer.reverseBytes(startX));
        out.writeInt(Integer.reverseBytes(startY));
        out.writeInt(Integer.reverseBytes(width));
        out.writeInt(Integer.reverseBytes(height));

        for (int y = startY; y <= startY + height; y++) {
            for (int x = startX; x <= startX + width; x++) {
                if (x <  array.getWidth() && y < array.getHeight()) {
                    out.writeByte(array.get(x, y));
                }
            }
        }
    }
    
    private static PK2Level load15Level(DataInputStream in, boolean iconOnly) throws Exception{

        PK2Level level = new PK2Level();

        int sectors_number;
        {
            JSONObject header =  PK2FileUtils.readCBOR(in);
            level.name = header.getString("name");
            level.author = header.getString("author");
            level.level_number = header.getInt("level_number");

            level.icon_x = header.getInt("icon_x");
            level.icon_y = header.getInt("icon_y");
            level.icon_id = header.getInt("icon_id");

            if(iconOnly){
                return level;
            }

            sectors_number = header.getInt("regions");
            JSONArray spritesArray = header.getJSONArray("sprite_prototypes");
            for(int i=0;i<spritesArray.length();++i){
                level.spriteNames.add(spritesArray.getString(i));
            }


            level.player_sprite_index = header.getInt("player_index");
            level.time = header.getInt("map_time");
            level.lua_script = header.getString("lua_script");

            if(header.has("game_mode")){
                level.game_mode = header.getInt("game_mode");
            }
        }

        for(int i=0;i<sectors_number;++i){
            JSONObject sector_header = PK2FileUtils.readCBOR(in);
            
            int width = sector_header.getInt("width");
            int height = sector_header.getInt("height");

            int compression = sector_header.getInt("compression");

            PK2LevelSector sector = new PK2LevelSector(width, height);

            sector.tilesetName = sector_header.getString("tileset");

            if(sector_header.has("tileset_bg")){
                sector.tilesetBgName = sector_header.getString("tileset_bg");
            }

            sector.backgroundName = sector_header.getString("background");

            if(sector_header.has("name")){
                sector.name = sector_header.getString("name");
            }
            else{
                sector.name = "";
            }


            sector.musicName = sector_header.getString("music");

            sector.background_scrolling = sector_header.getInt("scrolling");
            sector.weather = sector_header.getInt("weather");

            sector.splash_color = sector_header.getInt("splash_color");
            sector.fire_color_1 = sector_header.getInt("fire_color_1");
            sector.fire_color_2 = sector_header.getInt("fire_color_2");

            readTilesArray(in, sector.bgTiles, compression);

            readTilesArray(in, sector.fgTiles, compression);

            readTilesArray(in, sector.spriteTiles, compression);

            level.sectors.add(sector);
        }

        return level;
    }

    private static PK2Level load13Level(DataInputStream in, boolean iconOnly) throws Exception{

        PK2Level level = new PK2Level();
        PK2LevelSector sector = new PK2LevelSector(PK2LevelSector.CLASSIC_WIDTH,PK2LevelSector.CLASSIC_HEIGHT);
        

        sector.tilesetName = PK2FileUtils.readString(in, 13);
        sector.backgroundName = PK2FileUtils.readString(in, 13);
        sector.musicName = PK2FileUtils.readString(in, 13);
    
        level.name = PK2FileUtils.readString(in, 40);
        sector.name = "legacy sector";
        level.author = PK2FileUtils.readString(in, 40);
        level.level_number = PK2FileUtils.readInt(in);
        
        sector.weather = PK2FileUtils.readInt(in);

        // Switch values 1-3, these values are not used in this map format and are hardcoded to be 2000
        in.readNBytes(8);
        in.readNBytes(8);
        in.readNBytes(8);

        level.time = PK2FileUtils.readInt(in);
        
        level.extra = PK2FileUtils.readInt(in); // "Extra", not used?
        
        sector.background_scrolling = PK2FileUtils.readInt(in);
        
        level.player_sprite_index = PK2FileUtils.readInt(in);
        
        level.icon_x = PK2FileUtils.readInt(in);
        level.icon_y = PK2FileUtils.readInt(in);

        level.icon_id = PK2FileUtils.readInt(in);

        if(iconOnly){
            return level;
        }

        int spritesAmount = PK2FileUtils.readInt(in);

        for (int i = 0; i < spritesAmount; i++) {
            level.spriteNames.add(PK2FileUtils.readString(in, 13));
        }

        readTilesArray(in, sector.bgTiles, TILES_OFFSET_LEGACY);

        readTilesArray(in, sector.fgTiles, TILES_OFFSET_LEGACY);

        readTilesArray(in, sector.spriteTiles, TILES_OFFSET_LEGACY);

        level.sectors.add(sector);
        return level;
    }

    private static final List<Integer> ID1_5 = List.of((int)'1',(int)'.',(int)'5',(int)'\0');
    private static final List<Integer> ID1_3 = List.of((int)'1',(int)'.',(int)'3',(int)'\0');

    private static boolean checkID(List<Integer> id, byte[] version){
        for(int i=0;i<id.size();++i){
            int by = version[i] & 0xFF;
            if (by != id.get(i)) return false;
        }
        return true;
    }

    public static PK2Level loadLevel(File file) throws Exception{
        return loadLevel(file, false);
    }

    public static PK2Level loadLevel(File file, boolean iconOnly) throws Exception{

        PK2Level res = null;
        DataInputStream in = new DataInputStream(new FileInputStream(file));

        byte [] version = new byte[5];
        in.read(version);

        if(checkID(ID1_5, version)){
            res = load15Level(in, iconOnly);
        }
        else if(checkID(ID1_3, version)){
            res = load13Level(in, iconOnly);
        }

        in.close();
        return res;    
    }

    public static void saveLevel(PK2Level level, File file) throws Exception{
        DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
        out.writeByte('1');
        out.writeByte('.');
        out.writeByte('5');
        out.writeByte('\0');
        out.writeByte('\0');

        int sectors_number = level.sectors.size();

        {
            JSONObject header = new JSONObject();
            header.put("name", level.name);
            header.put("author", level.author);
            header.put("level_number", level.level_number);
            header.put("icon_id", level.icon_id);
            header.put("icon_x", level.icon_x);
            header.put("icon_y", level.icon_y);

            header.put("sprite_prototypes", level.spriteNames);
            
            
            header.put("player_index", level.player_sprite_index);
            header.put("regions", sectors_number);
            header.put("map_time", level.time);
            header.put("lua_script", level.lua_script);
            header.put("game_mode", level.game_mode);

            PK2FileUtils.writeCBOR(out, header);
        }
        
        for(int i=0;i<sectors_number;++i){
            PK2LevelSector sector = level.sectors.get(i);
            JSONObject sector_header = new JSONObject();
            sector_header.put("name", sector.name);

            sector_header.put("width", sector.getWidth());
            sector_header.put("height", sector.getHeight());

            sector_header.put("compression", TILES_OFFSET_NEW);
            sector_header.put("tileset", sector.tilesetName);

            if(sector.tilesetBgName!=null &&
            !sector.tilesetBgName.equals("") &&
            !sector.tilesetBgName.equals(sector.tilesetName)){
                sector_header.put("tileset_bg", sector.tilesetBgName);
            }

            sector_header.put("music", sector.musicName);
            sector_header.put("background", sector.backgroundName);

            sector_header.put("scrolling", sector.background_scrolling);
            sector_header.put("weather", sector.weather);

            sector_header.put("splash_color", sector.splash_color);
            sector_header.put("fire_color_1", sector.fire_color_1);
            sector_header.put("fire_color_2", sector.fire_color_2);

            PK2FileUtils.writeCBOR(out, sector_header);

            writeTilesArrayWithOffset(out, sector.bgTiles);

            writeTilesArrayWithOffset(out, sector.fgTiles);

            writeTilesArrayWithOffset(out, sector.spriteTiles);
        }

        //header.pu

        out.close();
    }
    
}
