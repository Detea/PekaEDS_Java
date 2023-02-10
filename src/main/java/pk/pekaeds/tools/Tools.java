package pk.pekaeds.tools;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.tinylog.Logger;

public final class Tools {
    private static final ArrayList<Tool> toolList = new ArrayList<>();
    
    public static void addTool(Class<? extends Tool> tool) {
        try {
            boolean contains = false;
            for (var t : toolList) {
                if (t.getClass() == tool) {
                    contains = true;
                    
                    break;
                }
            }
            
            if (!contains) {
                toolList.add(tool.getConstructor().newInstance());
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Logger.warn(e);
        }
    }
    
    public static Tool getTool(Class<? extends Tool> toolClass) {
        Tool tool = null;
        
        for (var t : toolList) {
            if (t.getClass().equals(toolClass)) {
                tool = t;
                
                break;
            }
        }

        if (tool == null) {
            Logger.info("Tool '{}' not found.", toolClass.getName());
        }
        
        return tool;
    }
}
