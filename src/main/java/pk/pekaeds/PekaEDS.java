package pk.pekaeds;

import com.formdev.flatlaf.FlatDarkLaf;
import pk.pekaeds.ui.PekaEDSGUILauncher;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Locale;
import org.tinylog.Logger;

public class PekaEDS {
    public static void main(String[] args) {
        FlatDarkLaf.setup();
        //System.setProperty( "flatlaf.menuBarEmbedded", "false" );

        System.setProperty("sun.java2d.noddraw", "true");
        Locale.setDefault(Locale.ENGLISH);
        
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            Logger.error(e);
        });
        
        PekaEDSGUILauncher.launch();
    }
}