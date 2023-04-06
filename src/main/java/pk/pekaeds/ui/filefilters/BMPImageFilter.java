package pk.pekaeds.ui.filefilters;

import pk.pekaeds.settings.Settings;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class BMPImageFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        return f.getName().toLowerCase().endsWith(".bmp") && f.getName().length() <= Settings.getMapProfile().getStringLengthTileset() || f.isDirectory();
    }
    
    @Override
    public String getDescription() {
        return "256 color bitmap (*.bmp)";
    }
}
