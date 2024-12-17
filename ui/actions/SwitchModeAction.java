package pekaeds.ui.actions;

import javax.swing.*;

import pekaeds.tool.Tool;
import pekaeds.ui.main.PekaEDSGUI;

import java.awt.event.ActionEvent;

public class SwitchModeAction extends AbstractAction {
    //private final PekaEDSGUI gui;
    private int mode;
    
    public SwitchModeAction(PekaEDSGUI ui, int m) {
        //this.gui = ui;
        this.mode = m;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Tool.setMode(mode);
    }
}
