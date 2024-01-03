package pekaeds.ui.listeners;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import pekaeds.ui.main.PekaEDSGUI;
import pekaeds.ui.misc.UnsavedChangesDialog;

public class MainUIWindowListener extends WindowAdapter {
    private PekaEDSGUI gui;
    
    public MainUIWindowListener(PekaEDSGUI ui) {
        this.gui = ui;
    }
    
    @Override
    public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
    
        if (gui.unsavedChangesPresent()) {
            UnsavedChangesDialog.show(gui, true);
        } else {
            gui.close();
        }
    }
}
