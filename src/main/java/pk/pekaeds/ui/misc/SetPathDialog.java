package pk.pekaeds.ui.misc;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class SetPathDialog extends JDialog {
    private File selectedFile = null;
    
    public SetPathDialog() {
        setTitle("Set path to the game contents...");
        
        var lblPath = new JLabel("Path:");
        var tfPath = new JTextField();
        var btnBrowse = new JButton("Browse");
        var btnOk = new JButton("OK");
        
        btnBrowse.addActionListener(e -> {
            var fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            
            var res = fc.showOpenDialog(null);
            
            if (res == JFileChooser.APPROVE_OPTION) {
                tfPath.setText(fc.getSelectedFile().getPath());
            }
        });
        
        // selectedFile only gets set to a non-null value when the user clicks on the "OK" button, so that they can click on the X of the dialog to quit. See PekaEDSGUILauncher.createNewSettingsFile() for further information.
        btnOk.addActionListener(e -> {
            if (!tfPath.getText().isBlank()) {
                selectedFile = new File(tfPath.getText());
    
                setVisible(false);
                dispose();
            }
        });
        
        setLayout(new MigLayout());
        add(lblPath, "cell 0 0");
        add(tfPath, "cell 1 0, width 300px");
        add(btnBrowse, "cell 2 0");
        
        var panelBtn = new JPanel();
        add(panelBtn, "cell 0 1, span 2, width 100%");
        add(btnOk, "cell 2 1");
        
        setModal(true);
        setSize(new Dimension(400, 120));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        
        pack();
    }
    
    public File showDialog() {
        setVisible(true);
        
        return selectedFile;
    }
}
