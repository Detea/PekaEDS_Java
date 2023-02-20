package pk.pekaeds.pk2.sprite;

import java.io.*;
import java.util.*;

import org.tinylog.Logger;

public final class SpriteReaders {
    private static final PK2SpriteReader11 reader1_1 = new PK2SpriteReader11();
    private static final PK2SpriteReader12 reader1_2 = new PK2SpriteReader12();
    private static final PK2SpriteReader13 reader1_3 = new PK2SpriteReader13();
    
    private SpriteReaders() {}
    
    /**
     * Returns the corresponding PK2SpriteReader, for the specified file, or null if it can't recognize the maps format.
     * @param file
     * @return The correct PK2SpriteReader for the file or null.
     */
    public static PK2SpriteReader getReader(File file) {
        PK2SpriteReader reader = null;
        
        try (var dis = new DataInputStream(new FileInputStream(file))) {
            var id = new int[4];
            
            for (int i = 0; i < id.length; i++) {
                id[i] = dis.readByte() & 0xFF;
            }
            
            if (Arrays.equals(SpriteVersions.ID_1_3, id)) {
                reader = reader1_3;
            } else if (Arrays.equals(SpriteVersions.ID_1_2, id)) {
                reader = reader1_2;
            } else if (Arrays.equals(SpriteVersions.ID_1_1, id)) {
                reader = reader1_1;
            } else {
                Logger.warn("Unable to find sprite version for id: {}", Arrays.toString(id));
            }
        } catch (FileNotFoundException e) {
            Logger.warn("Unable to find sprite version, because the sprites file can't be found.");
        } catch (IOException e) {
            Logger.warn(e, "Unable to find sprite version.");
        }
    
        return reader;
    }
}
