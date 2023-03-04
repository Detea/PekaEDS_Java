package pk.pekaeds.ui;


import pk.pekaeds.util.file.PathUtils;
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
    }
    
    private static void checkSettingsFile() {
        try {
            Settings.load();
    
            var file = new File(Settings.getBasePath());
    
            if (!file.exists() || !PathUtils.isPK2Directory(file) || !Settings.doesBasePathExist()) {
                createNewSettingsFile();
            } else {
                if (!PathUtils.isPK2StuffPresent(Settings.getBasePath())) {
                    JOptionPane.showMessageDialog(null, "Missing essential file: pk2stuff.bmp\nExpecting it in: " + Settings.getGFXPath(), "Unable to find pk2stuff.bmp", JOptionPane.ERROR_MESSAGE);
                } else if (!PathUtils.isMapImagePresent(Settings.getBasePath())) {
                    JOptionPane.showMessageDialog(null, "Missing essential file: map.bmp\nExpecting it in: " + Settings.getGFXPath(), "Unable to find map.bmp", JOptionPane.ERROR_MESSAGE);
                } else {
                    SwingUtilities.invokeLater(PekaEDSGUI::new);
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
