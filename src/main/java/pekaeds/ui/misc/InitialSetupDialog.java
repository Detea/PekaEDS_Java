package pekaeds.ui.misc;

import net.miginfocom.swing.MigLayout;
import pekaeds.data.PekaEDSVersion;
import pekaeds.settings.Settings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class InitialSetupDialog extends JDialog {
    private JTextField tfPath = new JTextField();
    private JTextField tfDefaultAuthor = new JTextField();
    private JCheckBox cbUseAutosaves = new JCheckBox("Use autosaves");

    private final Color panelColor = new Color(30, 30, 30);

    private boolean setupDone = false;

    // owner should always be null, so the dialog shows up in the taskbar
    public InitialSetupDialog(Dialog owner) {
        super(owner);

        var lblPath = new JLabel("Pekka Kana 2 Greta folder location:");
        var btnBrowse = new JButton("Browse");
        var btnOk = new JButton("OK");

        tfDefaultAuthor.setText("Unknown");
        cbUseAutosaves.setSelected(true);

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
                Settings.reset();

                Settings.setDefaultAuthor(tfDefaultAuthor.getText());
                Settings.setBasePath(tfPath.getText());

                if (!cbUseAutosaves.isSelected()) {
                    Settings.setAutosaveInterval(0);
                    Settings.setAutosaveFileCount(0);
                }

                Settings.save();

                setupDone = true;
                setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Please provide the location of your Pekka Kana 2 Greta folder!", "No location provided!", JOptionPane.ERROR_MESSAGE);
            }
        });

        setLayout(new MigLayout("flowy"));

        var lblSetup = new JLabel("Initial setup");
        lblSetup.setFont(new Font(lblSetup.getFont().getFontName(), Font.BOLD, 20));

        var lblVersion = new JLabel("PekaEDS v." + PekaEDSVersion.VERSION_STRING);
        lblVersion.setFont(new Font(lblVersion.getFont().getFontName(), Font.PLAIN, 12));

        var pnlInitSetup = new JPanel(new MigLayout("fillx"));
        pnlInitSetup.setBackground(panelColor);
        pnlInitSetup.add(lblSetup);
        pnlInitSetup.add(lblVersion, "gapx push");

        add(pnlInitSetup, "north, width 100%");

        var lblDefaultAuthor = new JLabel("Default author:");
        var pnlPath = new JPanel();
        pnlPath.setLayout(new MigLayout("align 50% 10%"));
        pnlPath.setBackground(new Color(45, 45, 46));

        pnlPath.add(lblDefaultAuthor, "cell 0 0");
        pnlPath.add(tfDefaultAuthor, "cell 0 1, width 200px");
        pnlPath.add(cbUseAutosaves, "cell 1 0, align right");

        pnlPath.add(lblPath, "cell 0 2");
        pnlPath.add(tfPath, "cell 0 3, width 400px");
        pnlPath.add(btnBrowse, "cell 1 3");

        var pnlOkButton = new JPanel();
        pnlOkButton.setLayout(new MigLayout("fillx"));
        pnlOkButton.setBackground(panelColor);
        pnlOkButton.add(btnOk, "gapx push");

        add(pnlPath, "dock center");
        add(pnlOkButton, "south");

        setIcon();
        setTitle("PekaEDS - Initial setup");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void setIcon() {
        BufferedImage iconImg = null;
        try {
            var iconResource = getClass().getClassLoader().getResourceAsStream("levelEditorIcon.png");

            if (iconResource != null) iconImg = ImageIO.read(iconResource);
        } catch (IOException e) {
            // TODO Log this with the debug level
            System.out.println("unable to load icon");
        }

        if (iconImg != null) setIconImage(iconImg);
    }

    public boolean setupCompleted() {
        setVisible(true);

        return setupDone;
    }
}