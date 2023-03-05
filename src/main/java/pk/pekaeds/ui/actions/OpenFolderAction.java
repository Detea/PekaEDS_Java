package pk.pekaeds.ui.actions;

import org.tinylog.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public final class OpenFolderAction extends AbstractAction {
    private File file;

    public OpenFolderAction(String folderPath) {
        this.file = new File(folderPath);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException ex) {
            Logger.info(ex, "Unable to open file: {}", file.getAbsolutePath());
        }
    }
}
