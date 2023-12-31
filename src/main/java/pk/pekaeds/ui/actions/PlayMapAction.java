package pk.pekaeds.ui.actions;

import pk.pekaeds.ui.main.PekaEDSGUI;
import pk.pekaeds.ui.misc.UnsavedChangesDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;


import pk2.PekkaKana2;

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

            String mapFileStr = gui.getCurrentFile().getAbsolutePath().toString();
            PekkaKana2.testLevel(mapFileStr, true);
        }
    }
}
