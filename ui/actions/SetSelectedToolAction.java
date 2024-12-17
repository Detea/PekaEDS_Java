package pekaeds.ui.actions;

import javax.swing.*;

import pekaeds.tool.Tool;
import pekaeds.ui.main.PekaEDSGUI;

import java.awt.event.ActionEvent;

public class SetSelectedToolAction extends AbstractAction {
    private final PekaEDSGUI gui;
    private final Tool selectedTool;
    
    public SetSelectedToolAction(PekaEDSGUI ui, Tool tool) {
        this.gui = ui;
        
        this.selectedTool = tool;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (selectedTool != null) {
            gui.setSelectedTool(selectedTool, false);
        }
    }
}
