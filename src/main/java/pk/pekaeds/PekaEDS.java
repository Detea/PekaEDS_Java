package pk.pekaeds;

import com.formdev.flatlaf.FlatDarkLaf;
import pk.pekaeds.ui.PekaEDSGUILauncher;

import java.util.Locale;
import org.tinylog.Logger;
import pk.pekaeds.ui.misc.LoggerDialog;

public class PekaEDS {
    public static void main(String[] args) throws Exception {
        FlatDarkLaf.setup();
        //System.setProperty( "flatlaf.menuBarEmbedded", "false" );

        System.setProperty("sun.java2d.noddraw", "true");
        Locale.setDefault(Locale.ENGLISH);
        
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            Logger.error(e);
        });
        
        Logger.info("test");

        PekaEDSGUILauncher.launch();
    }
}