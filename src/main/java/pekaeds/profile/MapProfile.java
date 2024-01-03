package pekaeds.profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapProfile {
    private int spriteLimit;
    private int stringLengthTileset = 13;
    private int stringLengthBackground = 13;
    private int stringLengthMusic = 13;
    private int stringLengthMapName;
    
    private int mapWidth = 256;
    private int mapHeight = 224;
    
    private List<String> defaultScrollingTypes = new ArrayList<>();
    private List<String> defaultWeatherTypes = new ArrayList<>();
    
    private List<String> musicFormats = Arrays.asList("xm", "mod", "it", "s3m", "ogg", "mp3");
    
    private List<String> scrollingTypes = new ArrayList<>();
    private List<String> weatherTypes = new ArrayList<>();
    private List<String> mapIconNames = new ArrayList<>();
    
    private String[] iconNames = new String[] {
            "Question mark",
            "Forest Hill",
            "Forest Hill at night",
            "Deep Forest",
            "Deep Forest at Night",
            "Field",
            "Field at night",
            "Mountains",
            "Castle",
            "Red Castle",
            "Cave",
            "Boss Battle",
            "Factory",
            "Custom Icon #14",
            "Custom Icon #15",
            "Custom Icon #16",
            "Custom Icon #17",
            "Custom Icon #18",
            "Custom Icon #19",
            "Custom Icon #20",
            "Custom Icon #21",
            "Custom Icon #22"
    };
    
    public void reset() {
        defaultScrollingTypes.add("None");
        defaultScrollingTypes.add("Vertical");
        defaultScrollingTypes.add("Horizontal");
        defaultScrollingTypes.add("Horizontal & Vertical");
        
        defaultWeatherTypes.add("None");
        defaultWeatherTypes.add("Rain");
        defaultWeatherTypes.add("Leaves");
        defaultWeatherTypes.add("Rain & Leaves");
        defaultWeatherTypes.add("Snow");
        
        scrollingTypes.clear();
        scrollingTypes.addAll(defaultScrollingTypes);
        
        weatherTypes.clear();
        weatherTypes.addAll(defaultWeatherTypes);
    }
    
    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("SpriteLimit: " + spriteLimit);
        sb.append("\ntileset: " + stringLengthTileset);
        sb.append("\nbg: " + stringLengthBackground);
        sb.append("\nmusic: " + stringLengthMusic);
        sb.append("\nmapname: " + stringLengthMapName);
        
        for (var s : musicFormats) {
            sb.append("\n" + s);
        }
        
        for (var s : scrollingTypes) {
            sb.append("\n" + s);
        }
        
        for (var s : weatherTypes) {
            sb.append("\n" + s);
        }
        
        for (var s : mapIconNames) {
            sb.append("\n" + s);
        }
        
        return sb.toString();
    }
    
    public int getSpriteLimit() {
        return spriteLimit;
    }
    
    public int getMapWidth() {
        return mapWidth;
    }
    
    public int getMapHeight() {
        return mapHeight;
    }
    
    public List<String> getMusicFormats() {
        return musicFormats;
    }
    
    public List<String> getScrollingTypes() {
        return scrollingTypes;
    }
    
    public List<String> getWeatherTypes() {
        return weatherTypes;
    }
    
    public List<String> getDefaultScrollingTypes() { return defaultScrollingTypes; }
    public List<String> getDefaultWeatherTypes() { return defaultWeatherTypes; }
    
    public List<String> getMapIconNames() {
        return mapIconNames;
    }
    
    public int getStringLengthTileset() {
        return stringLengthTileset;
    }
    
    public int getStringLengthBackground() {
        return stringLengthBackground;
    }
    
    public int getStringLengthMusic() {
        return stringLengthMusic;
    }
    
    public int getStringLengthMapName() {
        return stringLengthMapName;
    }
    
    public String[] getIconNames() {
        return iconNames;
    }
}
