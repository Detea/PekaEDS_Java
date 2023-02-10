package pk.pekaeds.ui.filefilters;

import pk.pekaeds.settings.Settings;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Arrays;

public class MusicFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        boolean returnVal = false;
        
        if (!f.isDirectory()) {
            var fileExtension = f.getName().split("\\.");
    
            if (fileExtension.length == 0 && !f.isDirectory()) return false;
            
            returnVal = Settings.getMapProfile().getMusicFormats().contains(fileExtension[1]);
        }
        
        return returnVal || f.isDirectory();
    }
    
    @Override
    public String getDescription() {
        return "Music file " + Settings.getMapProfile().getMusicFormats().toString(); // TODO Fix: this string, show: (*.s3m, *.ogg, *.mp3) etc.
    }
}
