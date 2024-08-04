package pekaeds.ui.actions;

import javax.swing.*;

import pekaeds.ui.main.PekaEDSGUI;

import java.awt.event.ActionEvent;

public class SaveLevelAction extends AbstractAction {
    private PekaEDSGUI gui;
    
    public SaveLevelAction(PekaEDSGUI ui) {
        this.gui = ui;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        gui.saveLevel();
    }
}
