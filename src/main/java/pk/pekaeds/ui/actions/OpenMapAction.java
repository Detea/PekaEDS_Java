package pk.pekaeds.ui.actions;

import pk.pekaeds.settings.Settings;
import pk.pekaeds.ui.main.PekaEDSGUI;
import pk.pekaeds.ui.misc.UnsavedChangesDialog;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.File;

public class OpenMapAction extends AbstractAction {
    private final PekaEDSGUI gui;
    
    public OpenMapAction(PekaEDSGUI ui) {
        this.gui = ui;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        var fc = new JFileChooser(Settings.getEpisodesPath());
    
        // TODO Optimization: Create a single instance of this FileFilter and share it? Same with the FileChooser?
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".map");
            }
        
            @Override
            public String getDescription() {
                return "Pekka Kana 2 map (*.map)";
            }
        });
        
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
