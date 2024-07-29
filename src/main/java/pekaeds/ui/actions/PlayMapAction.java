package pekaeds.ui.actions;

import javax.swing.*;

import pekaeds.ui.main.PekaEDSGUI;
import pekaeds.ui.misc.UnsavedChangesDialog;

import java.awt.event.ActionEvent;



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

    private void playMap(){
        JOptionPane.showMessageDialog(null, "Level testing is temporarily unavailable. It will be fixed and available in the future version.", "Unavailable feature", JOptionPane.ERROR_MESSAGE);
    }
}
