package pekaeds.ui.actions;

import javax.swing.*;

import pekaeds.ui.main.PekaEDSGUI;
import pekaeds.ui.misc.UnsavedChangesDialog;

import java.awt.event.ActionEvent;

public class NewLevelAction extends AbstractAction {
    private final PekaEDSGUI gui;
    
    public NewLevelAction(PekaEDSGUI ui) {
        this.gui = ui;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gui.unsavedChangesPresent()) {
            int result = UnsavedChangesDialog.show(gui);
    
            if (result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION) {
                gui.newLevel();
            }
        } else {
            gui.newLevel();
        }
    }
}
