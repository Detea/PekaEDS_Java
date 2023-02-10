package pk.pekaeds.ui.actions;

import pk.pekaeds.settings.Settings;
import pk.pekaeds.ui.main.PekaEDSGUI;
import pk.pekaeds.ui.misc.UnsavedChangesDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import org.tinylog.Logger;

public class PlayMapAction extends AbstractAction {
    private PekaEDSGUI gui;
    
    public PlayMapAction(PekaEDSGUI ui) {
        this.gui = ui;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gui.unsavedChangesPresent()) {
            int result = UnsavedChangesDialog.show(gui);
            
            if (result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION) {
                playMap();
            }
        } else {
            playMap();
        }
    }
    
    private void playMap() {
        if (gui.getCurrentFile() != null) {
            // TODO Maybe clean this up a bit
            String file = "\"" + gui.getCurrentFile().getParentFile().getName() + File.separatorChar + gui.getCurrentFile().getName() + "\""; // Why the backslashes?
            String args = Settings.getTestingParameter().replace("%level%", file);
    
            String[] exe = Settings.getTestingParameter().split(" ");
    
            String cmd = exe[0];
    
            args = args.substring(exe[0].length() + 1);
    
            if (cmd.isEmpty()) {
                return;
            }
    
            try{
                Runtime runTime = Runtime.getRuntime();
                Process process = runTime.exec("\"" + Settings.getBasePath() + File.separatorChar + cmd + "\"" + " " + args); // TODO Again why the backslashes?
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Can't test level!\n" + ex.getMessage(), "Can't test level!", JOptionPane.ERROR_MESSAGE);
        
                Logger.warn(ex, "Unable to test level.");
            }
        }
    }
}
