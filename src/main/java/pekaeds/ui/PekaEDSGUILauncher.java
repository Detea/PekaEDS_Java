package pekaeds.ui;


import pk2.sprite.PrototypesHandler;

import javax.swing.*;

import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.pk2.sprite.old.SpriteReaderNative;
import pekaeds.settings.Settings;
import pekaeds.ui.main.PekaEDSGUI;
import pekaeds.ui.misc.SetPathDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class PekaEDSGUILauncher {
   private final static SetPathDialog pathDialog = new SetPathDialog();
    
    private PekaEDSGUILauncher() {}
    
    public static void launch() {
        if (!new File("settings.dat").exists()) {
            createNewSettingsFile();
        } else {
            checkSettingsFile();
        }

    }
    
    private static void checkSettingsFile() {
        try {
            Settings.load();

            File file = new File(Settings.getBasePath());
            
            /**
             * If there's something wrong, not PK2 directory and so on, it throws an exception
             */
            PK2FileSystem.INSTANCE.setAssetsPath(file);
            
            System.out.println("DLL path: "+ Settings.getDllPath());
            file = new File(Settings.getDllPath());
            if(file.exists()){
                pk2.PekkaKana2.init(Settings.getDllPath(), Settings.getBasePath());

                SpriteReaderNative.handler = new PrototypesHandler(true,true);

                SwingUtilities.invokeLater(PekaEDSGUI::new);
            }
            else{
                JOptionPane.showMessageDialog(null,
                "Missing essential file: "+Settings.DLL_NAME_WINDOWS+" or "+Settings.DLL_NAME_LINUX,
                "This editor requires PK2 Greta v0.59 (snapshot-11) or newer.", JOptionPane.ERROR_MESSAGE);
            }

        } catch(FileNotFoundException e){
            createNewSettingsFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void createNewSettingsFile() {
        /*
            If the selectedFile is not null the settings will be set.
            If selectedFile is null that means the user clicked the close button of the dialog and wants to exit, so we don't do anything and just let the application stop on its own.
         */
        File selectedFile = pathDialog.showDialog();

        try{
            PK2FileSystem.INSTANCE.setAssetsPath(selectedFile);

            Settings.reset();
            Settings.setBasePath(selectedFile.getPath());
            Settings.save();
        }
        catch(FileNotFoundException e){
            createNewSettingsNonExistent();
        }
    }
    
    private static void createNewSettingsNonExistent() {
        JOptionPane.showMessageDialog(null, "The selected directory does not contain the necessary Pekka Kana 2 content.", "Invalid path", JOptionPane.ERROR_MESSAGE);
    
        createNewSettingsFile();
    }
}
