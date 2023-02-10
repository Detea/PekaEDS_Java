package pk.pekaeds.ui.actions;

import pk.pekaeds.ui.main.PekaEDSGUI;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SwitchModeAction extends AbstractAction {
    private final PekaEDSGUI gui;
    private int mode;
    
    public SwitchModeAction(PekaEDSGUI ui, int m) {
        this.gui = ui;
        this.mode = m;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        gui.switchMode(mode);
    }
}
