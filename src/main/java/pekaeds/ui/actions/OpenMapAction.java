package pekaeds.ui.actions;

import javax.swing.*;

import pekaeds.settings.Settings;
import pekaeds.ui.filefilters.FileFilters;
import pekaeds.ui.main.PekaEDSGUI;
import pekaeds.ui.misc.UnsavedChangesDialog;

import java.awt.event.ActionEvent;

public class OpenMapAction extends AbstractAction {
    private final PekaEDSGUI gui;
    
    public OpenMapAction(PekaEDSGUI ui) {
        this.gui = ui;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        var fc = new JFileChooser(Settings.getEpisodesPath());
    
        fc.setFileFilter(FileFilters.PK2_MAP_FILTER);
        
        if (gui.unsavedChangesPresent()) {
            int result = UnsavedChangesDialog.show(gui);
    
            if (result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION) {
                showFileChooser(fc);
            }
        } else {
            showFileChooser(fc);
        }
    }
    
    private void showFileChooser(JFileChooser fc) {
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            gui.loadMap(fc.getSelectedFile());
        }
    }
}
