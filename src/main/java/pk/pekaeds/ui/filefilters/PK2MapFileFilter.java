package pk.pekaeds.ui.filefilters;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class PK2MapFileFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) return true;
        
        return f.getName().toLowerCase().endsWith(".map");
    }
    
    @Override
    public String getDescription() {
        return "Pekka Kana 2 map file (*.map)";
    }
}
