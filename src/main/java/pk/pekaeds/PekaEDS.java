package pk.pekaeds;

import com.formdev.flatlaf.FlatDarkLaf;
import pk.pekaeds.data.PekaEDSVersion;
import pk.pekaeds.ui.PekaEDSGUILauncher;

import java.util.Locale;
import org.tinylog.Logger;

import javax.swing.*;

public class PekaEDS {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            
            Logger.info("FlatDarkLaf installed.");
        } catch (UnsupportedLookAndFeelException e) {
            Logger.info(e, "Unable to install FlatDarkLaf, trying to set to system laf.");
    
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                Logger.info("System LaF installed.");
            } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException |
                     InstantiationException ex) {
                Logger.info(e, "Unable to set system looking feel.");
            }
        }
    
        //System.setProperty( "flatlaf.menuBarEmbedded", "false" );

        System.setProperty("sun.java2d.noddraw", "true");
        Locale.setDefault(Locale.ENGLISH);
        
        PekaEDSGUILauncher.launch();
    
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            Logger.info(e, "Uncaught exception");
        });
        
        Logger.info("Version: " + PekaEDSVersion.VERSION_STRING);
    }
}