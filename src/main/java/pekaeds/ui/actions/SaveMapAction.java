package pekaeds.ui.actions;

import javax.swing.*;

import pekaeds.ui.main.PekaEDSGUI;

import java.awt.event.ActionEvent;

public class SaveMapAction extends AbstractAction {
    private PekaEDSGUI gui;
    
    public SaveMapAction(PekaEDSGUI ui) {
        this.gui = ui;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        gui.saveMap();
    }
}
