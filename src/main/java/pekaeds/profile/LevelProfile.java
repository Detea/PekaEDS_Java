package pekaeds.profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class LevelProfile {
    private List<String> defaultScrollingTypes = new ArrayList<>();
    private List<String> defaultWeatherTypes = new ArrayList<>();
    
    private List<String> musicFormats = Arrays.asList("xm", "mod", "it", "s3m", "ogg", "mp3");
    
    private List<String> scrollingTypes = new ArrayList<>();
    private List<String> weatherTypes = new ArrayList<>();
    private List<String> mapIconNames = new ArrayList<>();

    private Map<Integer, String> fireColors;
    private Map<Integer, String> splashColors;

    public LevelProfile(){
        fireColors = new HashMap<>();
        fireColors.put(0, "Gray");
        fireColors.put(32, "Blue");
        fireColors.put(64,"Red");
        fireColors.put(96, "Green");
        fireColors.put(128,"Orange");
        fireColors.put(160,"Violet");
        fireColors.put(192,"Turquoise");

        splashColors = new HashMap<>();
        splashColors.put(-1, "Default");
        splashColors.putAll(fireColors);
    }

    public Map<Integer, String> getFireColors(){
        return this.fireColors;
    }
    public Map<Integer, String> getSplashColors(){
        return this.splashColors;
    }
    
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
        defaultWeatherTypes.add("Dandelions");
        
        scrollingTypes.clear();
        scrollingTypes.addAll(defaultScrollingTypes);
        
        weatherTypes.clear();
        weatherTypes.addAll(defaultWeatherTypes);
    }
    
    @Override
    public String toString() {
        var sb = new StringBuilder();
        
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
        
    public String[] getIconNames() {
        return iconNames;
    }
}
