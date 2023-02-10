package pk.pekaeds.ui.actions;

import pk.pekaeds.ui.main.PekaEDSGUI;
import pk.pekaeds.ui.misc.UnsavedChangesDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class NewMapAction extends AbstractAction {
    private final PekaEDSGUI gui;
    
    public NewMapAction(PekaEDSGUI ui) {
        this.gui = ui;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gui.unsavedChangesPresent()) {
            int result = UnsavedChangesDialog.show(gui);
    
            if (result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION) {
                gui.newMap();
            }
        } else {
            gui.newMap();
        }
    }
}
