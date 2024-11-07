package pekaeds;

import com.formdev.flatlaf.FlatDarkLaf;

import pekaeds.data.PekaEDSVersion;
import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.settings.Settings;
import pekaeds.ui.main.PekaEDSGUI;
import pekaeds.ui.misc.SetPathDialog;

import java.util.Locale;
import org.tinylog.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


import javax.swing.*;

public class PekaEDS {

    private final static SetPathDialog pathDialog = new SetPathDialog();
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
        
        launch();
    
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            Logger.info(e, "Uncaught exception");
        });
        
        Logger.info("Version: " + PekaEDSVersion.VERSION_STRING);
    }


    public static void launch(){

        boolean success = false;
        File settingsFile = new File("settings.dat");

        if(settingsFile.exists()){
            try{
                Settings.load(settingsFile);

                File file = new File(Settings.getBasePath());
                /**
                 * If there's something wrong (e.g nota  PK2 directory), it throws an exception
                 */
                PK2FileSystem.setAssetsPath(file);

                success = true;
            }
            catch(IOException e){
                Logger.error(e);
            }
        }

        while (!success) {

            File selectedFile = pathDialog.showDialog();

            try{
                PK2FileSystem.setAssetsPath(selectedFile);

                Settings.reset();
                Settings.setBasePath(selectedFile.getPath());
                Settings.save();
                success = true;
            }
            catch(FileNotFoundException e){
                JOptionPane.showMessageDialog(null,
                "The selected directory does not contain the necessary Pekka Kana 2 content.",
                "Invalid path", JOptionPane.ERROR_MESSAGE);
            }
            catch(Exception e){
                Logger.error(e);
                return;
            }
        }

        SwingUtilities.invokeLater(PekaEDSGUI::new);
    }
}