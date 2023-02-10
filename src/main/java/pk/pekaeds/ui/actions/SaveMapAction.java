package pk.pekaeds.ui.actions;

import pk.pekaeds.ui.main.PekaEDSGUI;

import javax.swing.*;
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
