package pk.pekaeds.pk2.sprite;

import java.io.*;
import java.util.*;

import org.tinylog.Logger;

//TODO Map and SpriteReaders could be combined into one Readers class. Readers.getMapReader(), Readers.getSpriteReader()
public final class SpriteReaders {
    //private static final Map<Class<? extends PK2Sprite>, Class<? extends PK2SpriteReader>> readerMap = new HashMap<>();
    
    // I tried to do some fancy reflection stuff, to add support for new file formats but whatever.
    
    /**
     * Returns the corresponding PK2SpriteReader, for the specified file, or null if it can't recognize the maps format.
     * @param file
     * @return The correct PK2SpriteReader for the file or null.
     */
    public static PK2SpriteReader getReader(File file) {
        /*Class<? extends PK2Sprite> spriteClass = PK2Sprite13.class;
        
        //if (spriteClass == null || !readerMap.containsKey(spriteClass)) return null;
        try {
            var v = spriteClass.getDeclaredField("ID");
            v.setAccessible(true);
            
            var v2 = (List<Integer>) v.get(null);
            
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        try {
            return (PK2SpriteReader) readerMap.get(spriteClass).getDeclaredConstructors()[0].newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.severe(e.getMessage());
        }*/
        
        PK2SpriteReader reader = null;
        
        try (var dis = new DataInputStream(new FileInputStream(file))) {
            var id = new ArrayList<Integer>();
            
            for (int i = 0; i < 4; i++) {
                id.add(dis.readByte() & 0xFF);
            }
            
            if (id.equals(PK2Sprite13.ID)) {
                reader = new PK2SpriteReader13();
            } else if (id.equals(PK2Sprite12.ID)) {
                reader = new PK2SpriteReader12();
            }
        } catch (FileNotFoundException e) {
            Logger.warn("Unable to find sprite version, because the sprites file can't be found.");
        } catch (IOException e) {
            Logger.warn(e, "Unable to find sprite version.");
        }
    
        return reader;
    }
    
    /*
    public static PK2SpriteReader getReader(String file) {
        return getReader(new File(file));
    }
    
    public static void registerReader(Class<? extends PK2Sprite> spriteClass, Class<? extends PK2SpriteReader> readerClass) {
        readerMap.put(spriteClass, readerClass);
    }*/
}
