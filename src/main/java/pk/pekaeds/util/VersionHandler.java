package pk.pekaeds.util;

import pk.pekaeds.pk2.map.PK2Map;
import pk.pekaeds.pk2.sprite.PK2Sprite;
import pk.pekaeds.pk2.sprite.PK2Sprite13;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

// TODO VersionHandler Delete this class and put it's functionality in the Readers class.

/**
 * This class keeps track of all the different map and sprite versions, making it easy to add new ones.
 * <p>
 * To add a new map or sprite version use {@link VersionHandler#registerMap(List, Class)} for maps and {@link VersionHandler#registerSprite(List, Class)} for sprites.
 * <p>
 * To retrieve the correct map/sprite object for a given file use {@link VersionHandler#isMapValid(File)} or {@link VersionHandler#isSpriteValid(File)} respectively.
 */
public final class VersionHandler {
    private static final Logger logger = Logger.getLogger(VersionHandler.class.getName());
    
    public static final int SPRITE_1_2 = 1;
    public static final int SPRITE_1_3 = 2;
    
    private static final Map<List<Integer>, Class<? extends PK2Map>> mapClassMap = new HashMap<>();
    private static final Map<List<Integer>, Class<? extends PK2Sprite>> spriteClassMap = new HashMap<>();
    
    private VersionHandler() {}
    
    /*
        Maps
     */
    
    /**
     * Puts the id and map class into an internal map, so that it is retrievable via isMapValid().
     * @param id The first 5 bytes of the map file.
     * @param mapClass Any class that extends PK2Map.
     */
    public static void registerMap(List<Integer> id, Class<? extends PK2Map> mapClass) {
        try { // TODO get id from mapClass.getField("ID); and add documentation to PK2Map that if you extend it make sure to override the ID field.
            mapClass.getField("ID");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    
        mapClassMap.put(id, mapClass);
    }
    
    /**
     * Checks the registered maps (See: registerMap()) and returns the correct map class or null.
     *
     * @param id The first 5 bytes of the map file.
     * @return Returns either the class of the correct map if it was registered, or null if it wasn't.
     */
    public static Class<? extends PK2Map> isMapValid(List<Integer> id) {
        if (!mapClassMap.containsKey(id)) return null;
        
        List<Integer> idd;
        try {
            idd = (List<Integer>) mapClassMap.get(id).getField("ID").get(null); // TODO IMPORTANT: Read up on reflections, I just hacked this together, no idea if this is the way to do it or not.
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        
        return mapClassMap.get(id);
    }
    
    /**
     * See {@link VersionHandler#isMapValid(List) isMapValid(List id)()}
     * @param file File to load.
     * @return See isMapValid(List).
     */
    public static Class<? extends PK2Map> isMapValid(File file) {
        return isMapValid(getFileId(file));
    }
    
    /*
        Sprites
     */
    public static void registerSprite(List<Integer> id, Class<? extends PK2Sprite> spriteClass) {
        spriteClassMap.put(id, spriteClass);
    }
    
    public static Class<? extends PK2Sprite> isSpriteValid(List<Integer> id) {
        if (!spriteClassMap.containsKey(id)) return PK2Sprite13.class;
        
        return PK2Sprite13.class;
    }
    
    public static Class<? extends PK2Sprite> isSpriteValid(File file) {
        return isSpriteValid(getFileId(file));
    }
    
    public static int isValidSprite(byte[] id) {
        return SPRITE_1_3;
    }
    
    /*
        Helper functions
     */
    private static List<Integer> getFileId(File file) {
        var id = new ArrayList<Integer>();
    
        try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            for (int i = 0; i < 5; i++) {
                id.add(in.readByte() & 0xFF);
            }
        } catch (FileNotFoundException e) {
            logger.severe("File '" + file.getName() + "' not found.");
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
        
        return id;
    }
}
