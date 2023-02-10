package pk.pekaeds.ui.actions;

import pk.pekaeds.ui.filefilters.BMPImageFilter;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;

public class BrowseResourceFilesAction extends AbstractAction {
    private JTextField textField;
    private FileFilter fileFilter;
    
    private JFileChooser fileChooser;
    
    public BrowseResourceFilesAction(JTextField txtField, FileFilter fFilter, String startingDirectory) {
        this.textField = txtField;
        this.fileFilter = fFilter;

        fileChooser = new JFileChooser(startingDirectory);
        fileChooser.setFileFilter(fileFilter);
    }
    
    public BrowseResourceFilesAction(JTextField txtField, JFileChooser fc) {
        this.textField = txtField;
        this.fileChooser = fc;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            textField.setText(fileChooser.getSelectedFile().getName());
        }
    }
}
