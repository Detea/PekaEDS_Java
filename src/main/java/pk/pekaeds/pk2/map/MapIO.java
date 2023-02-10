package pk.pekaeds.pk2.map;

import java.io.*;
import java.util.*;

// TODO Organization: A lot of this code in this class should probably be deleted. I kind overengineered it and worked on it way to early.

/**
 * Like {@link VersionHandler} this class keeps track of all the different map and map reader versions. Making it very easy to add new map formats.
 * <p>
 * To add a new map version all you have to do is to register it with {@link VersionHandler#registerMap(List, Class)}, create a new class that extends {@link PK2Map} to hold the map data and a {@link PK2MapReader} to read it from a file.
 * Those four steps are all it takes to implement a new map format, besides any UI changes that might need to be done.
 */
public final class MapIO {
    //private static final Map<Class<? extends PK2Map>, Class<? extends PK2MapReader>> readerMap = new HashMap<>();
    //private static final Map<List<Integer>, Class<? extends PK2Map>> classMap = new HashMap<>();
    
    // For now only return PK2MapReader13, because there is only one map file format.
    public static PK2MapReader getReader(File file) {
        /*Class<? extends PK2Map> mapClass = getMapClass(getFileId(file)); // VersionHandler.isMapValid(file)
        
        if (mapClass == null || !readerMap.containsKey(mapClass)) return null;
    
        try {
            return (PK2MapReader) readerMap.get(mapClass).getDeclaredConstructors()[0].newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.severe(e.getMessage());
        }*/
    
        return new PK2MapReader13();
    }
    
    public static PK2MapWriter getWriter() {
        return new PK2MapWriter13();
    }
    
    /*
    public static Class<? extends PK2Map> getMapClass(List<Integer> id) {
        if (!classMap.containsKey(id)) return null;
        
        List<Integer> idd;

        return classMap.get(id);
    }
    
    public static PK2MapReader getReader(String file) {
        return getReader(new File(file));
    }
 
    public static void registerReader(Class<? extends PK2Map> mapClass, Class<? extends PK2MapReader> readerClass) {
        readerMap.put(mapClass, readerClass);
    }
    
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
    }*/
}
