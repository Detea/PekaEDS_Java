package pk.pekaeds.ui.listeners;

import pk.pekaeds.ui.main.PekaEDSGUI;
import pk.pekaeds.ui.main.PekaEDSGUIView;
import pk.pekaeds.ui.misc.UnsavedChangesDialog;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
