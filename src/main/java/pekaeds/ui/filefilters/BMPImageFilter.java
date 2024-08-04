package pekaeds.ui.filefilters;

import javax.swing.filechooser.FileFilter;

import java.io.File;

public class BMPImageFilter extends FileFilter {
    @Override
    public boolean accept(File f) {
        return f.getName().toLowerCase().endsWith(".bmp") || f.getName().toLowerCase().endsWith(".png") || f.isDirectory();
    }
    
    @Override
    public String getDescription() {
        return "256 color bitmap (*.bmp), 256 color png (*.png)";
    }
}
