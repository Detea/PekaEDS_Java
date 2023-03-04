package pk.pekaeds.util.file;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PathUtils {
    private PathUtils() {}
    
    /**
     * Looks for the following:
     *  pk2.exe - The executable of the game.
     *  gfx     - The directory that contains the tileset and background images needed by PekaEDS.
     *  sprites - The directory that contains the sprite files needed by PekaEDS.
     *
     *  This is a somewhat superficial check, but it should suffice.
     * @param directory The directory to check.
     * @return Returns true only if the file and folders it is looking for exist in that directory.
     */
    public static boolean isPK2Directory(File directory) {
        var path = directory.getPath();
        
        var pk2exe = new File(path + File.separatorChar + "pk2.exe");
        var gfxDir = new File(path + File.separatorChar + "gfx");
        var spritesDir = new File(path + File.separatorChar + "sprites");
        
        return pk2exe.exists() && gfxDir.exists() && spritesDir.exists();
    }
    
    public static boolean isPK2StuffPresent(String basePath) {
        return Files.exists(Path.of(basePath + File.separatorChar + "gfx" + File.separatorChar + "pk2stuff.bmp"));
    }
    
    public static boolean isMapImagePresent(String basePath) {
        return Files.exists(Path.of(basePath + File.separatorChar + "gfx" + File.separatorChar + "map.bmp"));
    }
    
    public static String getTilesetAsBackgroundTileset(String tilesetPath) {
        String filename = tilesetPath.substring(0, tilesetPath.length() - 4);
        
        return filename + "_bg.bmp"; // TODO Add png support
    }
}
