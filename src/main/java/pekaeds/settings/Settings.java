package pekaeds.settings;

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

import pekaeds.profile.LevelProfile;
import pekaeds.profile.SpriteProfile;

/**
 * This class was a mistake. Every class that uses it depends on it, because I made everything static and I just realized why that was a bad idea.
 * It works, though. So rewriting the code base isn't worth it, in my opinion.
 */
public class Settings {
    private static final List<String> layerNames = new ArrayList<>();

    static{
        layerNames.add("Both");
        layerNames.add("Foreground");
        layerNames.add("Background");
    }
    
    private static String basePath;
    
    private static String defaultTileset = "tiles01.bmp";
    private static String defaultBackground = "castle.bmp";
    
    private static String defaultAuthor = "Unknown";
    private static String defaultMapName = "Unnamed";
    
    private static String defaultMusic = "song01.xm";
    
    private static String testingParameter = "./bin/pekka-kana-2 --test %level%";
    
    private static LevelProfile mapProfile = LevelProfile.getDefaultProfile();
    private static SpriteProfile spriteProfile = new SpriteProfile();
    
    //private static String pk2stuffFile;
    //private static final String pk2Stuff = "pk2stuff.bmp";
    
    private static boolean highlightSprites = true;
    private static boolean showTileNumberInTileset = true;
    
    
    //private static final File settingsFile = new File("settings.dat");
    
    private static final Map<String, KeyStroke> keyboardShortcuts = new HashMap<>();
    
    private static int defaultStartupBehavior = StartupBehavior.NEW_MAP;
    
    private static int autosaveInterval = 120000; // 2 minutes
    private static int autosaveFileCount = 3;
    
    private static boolean showSprites = true;
    
    private static boolean highlightSelection = true;
    
    
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
        keyboardShortcuts.put(Shortcuts.TOOL_CUT, KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0));
        keyboardShortcuts.put(Shortcuts.TOOL_FLOOD_FILL, KeyStroke.getKeyStroke(KeyEvent.VK_F, 0));
    }
    
    public static void load(File file) throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            basePath = dis.readUTF();
            defaultTileset = dis.readUTF();
            defaultBackground = dis.readUTF();
            defaultAuthor = dis.readUTF();
            defaultMapName = dis.readUTF();
            defaultMusic = dis.readUTF();
            
            testingParameter = dis.readUTF();
            
            highlightSprites = dis.readBoolean();
            showTileNumberInTileset = dis.readBoolean();
            showSprites = dis.readBoolean();
            
            highlightSelection = dis.readBoolean();
            
            defaultStartupBehavior = dis.readInt();
            
            autosaveInterval = dis.readInt();
            autosaveFileCount = dis.readInt();

           
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
            
            dos.writeBoolean(highlightSprites);
            dos.writeBoolean(showTileNumberInTileset);
            dos.writeBoolean(showSprites);
            dos.writeBoolean(highlightSelection);
            
            dos.writeInt(defaultStartupBehavior);
            
            dos.writeInt(autosaveInterval);
            dos.writeInt(autosaveFileCount);
            
            /*dos.writeInt(mapProfile.getScrollingTypes().size());
            for (var str : mapProfile.getScrollingTypes()) {
                dos.writeUTF(str);
            }
    
            dos.writeInt(mapProfile.getWeatherTypes().size());
            for (var str : mapProfile.getWeatherTypes()) {
                dos.writeUTF(str);
            }*/
            
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
    
        defaultTileset = "tiles01.bmp";
        defaultBackground = "castle.bmp";
    
        defaultAuthor = "Unknown";
        defaultMapName = "Unnamed";
    
        defaultMusic = "song01.xm";
    
        testingParameter = "pk2.exe dev test %level%";
        
        defaultStartupBehavior = StartupBehavior.NEW_MAP;
        
        highlightSprites = true;
        showTileNumberInTileset = true;
        
        showSprites = true;
        highlightSelection = true;
        
        autosaveInterval = 120000;
        autosaveFileCount = 3;

        resetKeyboardShortcuts();
    }

    public static final String DLL_NAME_WINDOWS = "pk2_greta.dll"; //Windows
    public static final String DLL_NAME_LINUX = "pk2_greta.so"; //Linux and Mac OS
   
    /*
        Getters & Setters
     */
    
    public static void setBasePath(String path) {
        basePath = path;
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
    
    public static String getDefaultTileset() {
        return defaultTileset;
    }
    
    public static String getDefaultBackground() {
        return defaultBackground;
    }
    
    public static void setDefaultTileset(String tileset) {
        defaultTileset = tileset;
    }
    
    public static void setDefaultBackground(String background) {
        defaultBackground = background;
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
    
    public static LevelProfile getMapProfile() {
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

    public void setMapProfile(LevelProfile mProfile) {
        mapProfile = mProfile;
    }
    
    public static SpriteProfile getSpriteProfile() {
        return spriteProfile;
    }
    
    public static void setSpriteProfile(SpriteProfile sprProfile) {
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
