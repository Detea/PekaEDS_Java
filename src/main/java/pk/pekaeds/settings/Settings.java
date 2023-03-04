package pk.pekaeds.settings;

import pk.pekaeds.profile.MapProfile;
import pk.pekaeds.profile.SpriteProfile;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.tinylog.Logger;

/**
 * This class was a mistake. Every class that uses it depends on it, because I made everything static and I just realized why that was a bad idea.
 * It works, though. So rewriting the code base isn't worth it, in my opinion.
 */
public class Settings {
    private static final List<String> layerNames = new ArrayList<>();
    
    private static String basePath;
    private static String tilesetPath;
    private static String backgroundsPath;
    private static String spritesPath;
    private static String episodesPath;
    private static String musicPath;
    
    private static String defaultTileset = "tiles01.bmp";
    private static String defaultBackground = "castle.bmp";
    
    private static String defaultAuthor = "Unknown";
    private static String defaultMapName = "Unnamed";
    
    private static String defaultMusic = "song01.xm";
    
    private static String testingParameter = "pk2.exe dev test %level%";
    
    private static MapProfile mapProfile = new MapProfile();
    private static SpriteProfile spriteProfile = new SpriteProfile();
    
    private static String pk2stuffFile;
    private static final String pk2Stuff = "pk2stuff.bmp";
    
    private static boolean highlightSprites = true;
    private static boolean showTileNumberInTileset = true;
    
    private static boolean useBGTileset = false;
    
    private static final File settingsFile = new File("settings.dat");
    
    private static final Map<String, KeyStroke> keyboardShortcuts = new HashMap<>();
    
    private static int defaultStartupBehavior = StartupBehavior.NEW_MAP;
    
    private static int autosaveInterval = 120000; // 2 minutes
    private static int autosaveFileCount = 3;
    
    private static boolean showSprites = true;
    
    private static boolean highlightSelection = false;
    
    public Settings() {
        // TODO Just for testing
        if (layerNames.isEmpty()) {
            layerNames.add("Both");
            layerNames.add("Foreground");
            layerNames.add("Background");
        }
    }
    
    /**
     * Register actions with keystrokes.
     *
     * If you want to add a new shortcut you also need to do it here.
     *
     * If you want the keystroke to not have a modifier pass 0. Like Shortcuts.TEST_MAP_ACTION: The key is F5, the InputEvent is 0. That means that the shortcut is just F5.
     */
    public static void resetKeyboardShortcuts() {
        keyboardShortcuts.put(Shortcuts.UNDO_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        keyboardShortcuts.put(Shortcuts.REDO_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        
        keyboardShortcuts.put(Shortcuts.SAVE_FILE_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        keyboardShortcuts.put(Shortcuts.OPEN_FILE_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        
        keyboardShortcuts.put(Shortcuts.TEST_MAP_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        
        keyboardShortcuts.put(Shortcuts.SELECT_BOTH_LAYER_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_1, 0));
        keyboardShortcuts.put(Shortcuts.SELECT_FOREGROUND_LAYER_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_2, 0));
        keyboardShortcuts.put(Shortcuts.SELECT_BACKGROUND_LAYER_ACTION, KeyStroke.getKeyStroke(KeyEvent.VK_3, 0));
        
        keyboardShortcuts.put(Shortcuts.SELECT_TILE_MODE, KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_DOWN_MASK));
        keyboardShortcuts.put(Shortcuts.SELECT_SPRITE_MODE, KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_DOWN_MASK));
        
        keyboardShortcuts.put(Shortcuts.TOOL_BRUSH, KeyStroke.getKeyStroke(KeyEvent.VK_W, 0));
        keyboardShortcuts.put(Shortcuts.TOOL_ERASER, KeyStroke.getKeyStroke(KeyEvent.VK_E, 0));
        keyboardShortcuts.put(Shortcuts.TOOL_LINE, KeyStroke.getKeyStroke(KeyEvent.VK_R, 0));
        keyboardShortcuts.put(Shortcuts.TOOL_RECT, KeyStroke.getKeyStroke(KeyEvent.VK_T, 0));
    }
    
    public static void load() throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(settingsFile))) {
            basePath = dis.readUTF();
            defaultTileset = dis.readUTF();
            defaultBackground = dis.readUTF();
            defaultAuthor = dis.readUTF();
            defaultMapName = dis.readUTF();
            defaultMusic = dis.readUTF();
            
            testingParameter = dis.readUTF();
            
            useBGTileset = dis.readBoolean();
            highlightSprites = dis.readBoolean();
            showTileNumberInTileset = dis.readBoolean();
            showSprites = dis.readBoolean();
            
            highlightSelection = dis.readBoolean();
            
            defaultStartupBehavior = dis.readInt();
            
            autosaveInterval = dis.readInt();
            autosaveFileCount = dis.readInt();
            
            mapProfile.getScrollingTypes().clear();
            
            int scrollingTypesAmount = dis.readInt();
            for (int i = 0; i < scrollingTypesAmount; i++) {
                mapProfile.getScrollingTypes().add(dis.readUTF());
            }
    
            mapProfile.getWeatherTypes().clear();
    
            int weatherTypesAmount = dis.readInt();
            for (int i = 0; i < weatherTypesAmount; i++) {
                mapProfile.getWeatherTypes().add(dis.readUTF());
            }
            
            int shortcutAmount = dis.readInt();
            for (int i = 0; i < shortcutAmount; i++) {
                setKeyboardShortcutFor(dis.readUTF(), KeyStroke.getKeyStroke(dis.readInt(), dis.readInt()));
            }
            
            setBasePath(basePath);
        } catch (IOException e) {
            Logger.warn(e, "Unable to load settings file.");
            
            // TODO Why did I throw this here?
            throw e;
        }
    }
    
    public static void save() {
        try (var dos = new DataOutputStream(new FileOutputStream("settings.dat"))){
            dos.writeUTF(Settings.getBasePath());
            dos.writeUTF(Settings.getDefaultTileset());
            dos.writeUTF(Settings.getDefaultBackground());
            dos.writeUTF(Settings.getDefaultAuthor());
            dos.writeUTF(Settings.getDefaultMapName());
            dos.writeUTF(Settings.getDefaultMusic());
            
            dos.writeUTF(testingParameter);
            
            dos.writeBoolean(useBGTileset);
            dos.writeBoolean(highlightSprites);
            dos.writeBoolean(showTileNumberInTileset);
            dos.writeBoolean(showSprites);
            dos.writeBoolean(highlightSelection);
            
            dos.writeInt(defaultStartupBehavior);
            
            dos.writeInt(autosaveInterval);
            dos.writeInt(autosaveFileCount);
            
            dos.writeInt(mapProfile.getScrollingTypes().size());
            for (var str : mapProfile.getScrollingTypes()) {
                dos.writeUTF(str);
            }
    
            dos.writeInt(mapProfile.getWeatherTypes().size());
            for (var str : mapProfile.getWeatherTypes()) {
                dos.writeUTF(str);
            }
            
            dos.writeInt(keyboardShortcuts.size());
            for (var e : keyboardShortcuts.entrySet()) {
                dos.writeUTF(e.getKey());
                dos.writeInt(e.getValue().getKeyCode());
                dos.writeInt(e.getValue().getModifiers());
            }
            
            dos.flush();
        } catch (IOException e) {
            Logger.warn("Unable to save settings file.");
        }
    }
    
    public static void reset() {
        layerNames.clear();
        layerNames.add("Both");
        layerNames.add("Foreground");
        layerNames.add("Background");
    
        defaultTileset = "tiles01.bmp";
        defaultBackground = "castle.bmp";
    
        defaultAuthor = "Unknown";
        defaultMapName = "Unnamed";
    
        defaultMusic = "song01.xm";
    
        testingParameter = "pk2.exe dev test %level%";
        
        defaultStartupBehavior = StartupBehavior.NEW_MAP;
        
        highlightSprites = true;
        useBGTileset = false;
        showTileNumberInTileset = true;
        
        showSprites = true;
        highlightSelection = false;
        
        autosaveInterval = 120000;
        autosaveFileCount = 3;
        
        mapProfile.reset();
        
        resetKeyboardShortcuts();
    }
    
    /*
        Getters & Setters
     */
    
    public static void setBasePath(String path) {
        basePath = path;
        
        // TODO Watch out for the / and the end, because Linux. Needs testing on Linux.
        tilesetPath = basePath + File.separatorChar + "gfx" + File.separatorChar + "tiles" + File.separatorChar;
        backgroundsPath = basePath + File.separatorChar + "gfx" + File.separatorChar + "scenery" + File.separatorChar;
        spritesPath = basePath + File.separatorChar + "sprites" + File.separatorChar;
        episodesPath = basePath + File.separatorChar + "episodes" + File.separatorChar;
        
        musicPath = basePath + File.separatorChar + "music" + File.separatorChar;
        
        pk2stuffFile = basePath + File.separatorChar + "gfx" + File.separatorChar + pk2Stuff;
    }
    
    public static void setKeyboardShortcutFor(String actionName, KeyStroke keyStroke) {
        keyboardShortcuts.put(actionName, keyStroke);
    }
    
    public static KeyStroke getKeyboardShortcutFor(String actionName) {
        return keyboardShortcuts.get(actionName);
    }
    
    public static void setShowTileNumberInTileset(boolean show) {
        showTileNumberInTileset = show;
    }
    
    public static boolean showTilesetNumberInTileset() {
        return showTileNumberInTileset;
    }
    
    public static void setShowSprites(boolean show) {
        showSprites = show;
    }
    
    public static boolean showSprites() {
        return showSprites;
    }
    
    public List<String> getLayerNames() {
        return new ArrayList<>(layerNames);
    }
    
    public static boolean doesBasePathExist() {
        return Files.exists(Path.of(basePath));
    }
    
    public static String getBasePath() {
        return basePath;
    }
    
    public static String getTilesetPath() {
        return tilesetPath;
    }
    
    public static String getBackgroundsPath() {
        return backgroundsPath;
    }
    
    public static String getDefaultTileset() {
        return defaultTileset;
    }
    
    public static String getDefaultBackground() {
        return defaultBackground;
    }

    public static String getSpritesPath() {
        return spritesPath;
    }
    
    public static String getEpisodesPath() {
        return episodesPath;
    }
    
    public static void setDefaultTileset(String tileset) {
        defaultTileset = tileset;
    }
    
    public static void setDefaultBackground(String background) {
        defaultBackground = background;
    }
    
    public static String getPK2stuffFilePath() {
        return pk2stuffFile;
    }
    
    public static String getGFXPath() {
        return basePath + File.separatorChar + "gfx" + File.separatorChar;
    }
    
    public static String getMusicPath() {
        return musicPath;
    }
    
    public static void setHighlightSprites(boolean hSprites) {
        highlightSprites = hSprites;
    }
    
    public static boolean highlightSprites() {
        return highlightSprites;
    }
    
    public static void setTestingParameter(String parameter) {
        testingParameter = parameter;
    }
    
    public static MapProfile getMapProfile() {
        return mapProfile;
    }
    
    public static String getDefaultAuthor() {
        return defaultAuthor;
    }
    
    public static void setDefaultAuthor(String defaultAuthor) {
        Settings.defaultAuthor = defaultAuthor;
    }
    
    public static String getDefaultMapName() {
        return defaultMapName;
    }
    
    public static void setDefaultMapName(String defaultMapName) {
        Settings.defaultMapName = defaultMapName;
    }
    
    public static String getDefaultMusic() {
        return defaultMusic;
    }
    
    public static void setDefaultMusic(String defaultMusic) {
        Settings.defaultMusic = defaultMusic;
    }
    
    public static String getTestingParameter() {
        return testingParameter;
    }
    
    public static String getBackgroundPath() {
        return backgroundsPath;
    }
    
    public static boolean useBGTileset() {
        return useBGTileset;
    }
    
    public static void setUseBGTileset(boolean useBG) {
        useBGTileset = useBG;
    }
    
    public void setMapProfile(MapProfile mProfile) {
        mapProfile = mProfile;
    }
    
    public static SpriteProfile getSpriteProfile() {
        return spriteProfile;
    }
    
    public void setSpriteProfile(SpriteProfile sprProfile) {
        spriteProfile = sprProfile;
    }
    
    public static int getDefaultStartupBehavior() {
        return defaultStartupBehavior;
    }
    
    public static void setDefaultStartupBehavior(int behavior) {
        defaultStartupBehavior = behavior;
    }
    
    public static void setAutosaveInterval(int delay) {
        autosaveInterval = delay;
    }
    
    public static int getAutosaveInterval() {
        return autosaveInterval;
    }
    
    public static int getAutosaveFileCount() {
        return autosaveFileCount;
    }
    
    public static void setAutosaveFileCount(int count) {
        autosaveFileCount = count;
    }
    
    public static void setHighlightSelection(boolean highlight) {
        highlightSelection = highlight;
    }
    
    public static boolean highlistSelection() {
        return highlightSelection;
    }
}
