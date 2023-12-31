package pk.pekaeds.ui;


import pk.pekaeds.util.file.PathUtils;
import pk2.sprite.PrototypesHandler;
import pk.pekaeds.pk2.sprite.SpriteReaderNative;
import pk.pekaeds.settings.Settings;
import pk.pekaeds.ui.main.PekaEDSGUI;
import pk.pekaeds.ui.misc.SetPathDialog;

import javax.swing.*;
import java.io.File;
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

        /*String basePath = Settings.getBasePath();
        String dllPath = Settings.getDllPath();
        pk2.PekkaKana2.init(dllPath, basePath);*/
    }
    
    private static void checkSettingsFile() {
        try {
            Settings.load();
   
            var file = new File(Settings.getBasePath());
            if (!file.exists() || !PathUtils.isPK2Directory(file) || !Settings.doesBasePathExist()) {
                createNewSettingsFile();
            } else {
                System.out.println("THE END!");
                System.out.println("DLL path: "+ Settings.getDllPath());

                if (!PathUtils.isPK2StuffPresent(Settings.getBasePath())) {
                    JOptionPane.showMessageDialog(null, "Missing essential file: pk2stuff.bmp\nExpecting it in: " + Settings.getGFXPath(), "Unable to find pk2stuff.bmp", JOptionPane.ERROR_MESSAGE);
                } else if (!PathUtils.isMapImagePresent(Settings.getBasePath())) {
                    JOptionPane.showMessageDialog(null, "Missing essential file: map.bmp\nExpecting it in: " + Settings.getGFXPath(), "Unable to find map.bmp", JOptionPane.ERROR_MESSAGE);
                } else {

                    file = new File(Settings.getDllPath());
                    if(file.exists()){
                        pk2.PekkaKana2.init(Settings.getDllPath(), Settings.getBasePath());

                        SpriteReaderNative.handler = new PrototypesHandler(true,true);

                        SwingUtilities.invokeLater(PekaEDSGUI::new);
                    }
                    else{
                        JOptionPane.showMessageDialog(null,
                        "Missing essential file: "+Settings.DLL_NAME_WINDOWS+" or "+Settings.DLL_NAME_LINUX,
                        "Unable to find the PK2 DLL", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            
            createNewSettingsFile();
        }
    }
    
    private static void createNewSettingsFile() {
        /*
            If the selectedFile is not null the settings will be set.
            If selectedFile is null that means the user clicked the close button of the dialog and wants to exit, so we don't do anything and just let the application stop on its own.
         */
        File selectedFile = pathDialog.showDialog();
        
        if (selectedFile.exists()) {
            if (PathUtils.isPK2Directory(selectedFile)) {
                Settings.reset();
                Settings.setBasePath(selectedFile.getPath());        
                Settings.save();
                
                checkSettingsFile();
            } else {
                createNewSettingsNonExistent();
            }
        } else {
            createNewSettingsNonExistent();
        }
    }
    
    private static void createNewSettingsNonExistent() {
        JOptionPane.showMessageDialog(null, "The selected directory does not contain the necessary Pekka Kana 2 content.", "Invalid path", JOptionPane.ERROR_MESSAGE);
    
        createNewSettingsFile();
    }
}
