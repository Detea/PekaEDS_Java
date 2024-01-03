package pekaeds.ui.misc;

import javax.swing.*;

import pekaeds.ui.main.PekaEDSGUI;

public final class UnsavedChangesDialog {
    /**
     * <pre>
     * Calls show() with closing set to false.
     *
     * See {@link #show(PekaEDSGUI, boolean)}
     * </pre>
     * @param gui
     * @return
     */
    public static int show(PekaEDSGUI gui) {
        return show(gui, false);
    }
    
    /**
     *<pre>
     * Shows a dialog asking the user if they want to save any unsaved changes. Only shows up when unsaved changes are present.
     *
     * If the user clicks "Yes" the main controller (PekaEDSGUI) will save the currently loaded map. Then exists, if close is true.
     * If the user clicks "No" the program exists, if close is true.
     * If the user clicks "Cancel" or closes the dialog nothing happens.
     *</pre>
     *
     * @param gui Instance of PekaEDSGUI Main UI controller class
     * @param close If this is true the program will exit.
     * @return The selected JOptionPane option.
     */
    public static int show(PekaEDSGUI gui, boolean close) {
        int result = JOptionPane.showConfirmDialog(null, "Unsaved changes. Save?");
        
        if (result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION) {
            if (result == JOptionPane.YES_OPTION) {
                gui.saveMap();
            }
        
            if (close) {
                gui.close();
            }
        }
        
        return result;
    }
}
