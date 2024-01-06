package pekaeds.ui.actions;

import javax.swing.*;

import pekaeds.ui.main.PekaEDSGUI;
import pekaeds.ui.misc.UnsavedChangesDialog;

import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Paths;

import pk2.PekkaKana2;

public class PlayMapAction extends AbstractAction {
    private PekaEDSGUI gui;
    private boolean isPlaying = false;
    
    public PlayMapAction(PekaEDSGUI ui) {
        this.gui = ui;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gui.unsavedChangesPresent()) {
            int result = UnsavedChangesDialog.show(gui);
            
            if (result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION) {
                playMap();
            }
        } else {
            playMap();
        }
    }

    private void playMap(){
        JOptionPane.showMessageDialog(null, "Level testing is temporarily unavailable. It will be fixed and available in the future version.", "Unavailable feature", JOptionPane.ERROR_MESSAGE);
    }
    
    public void playMapX() {
        if (gui.getCurrentFile() != null && !isPlaying) {
            isPlaying = true;

            File mapFile = gui.getCurrentFile();
            File episodeDir = mapFile.getParentFile();

            String arg = Paths.get(episodeDir.getName(), mapFile.getName().toString()).toString();

            PekkaKana2.testLevel(arg, true);
            isPlaying = false;
        }
    }
}
