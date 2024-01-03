package pekaeds.ui.settings;

import net.miginfocom.swing.MigLayout;
import pekaeds.settings.Settings;
import pekaeds.ui.main.PekaEDSGUI;

import org.tinylog.Logger;

import javax.swing.*;
import java.awt.*;

public class SettingsDialog extends JDialog {
    private JTabbedPane tabbedPane;
    private PanelGeneral panelGeneral;
    private PanelDefaults panelDefaults;
    private PanelShortcuts panelShortcuts;
    private PanelMapProfile panelMapProfile;
    
    private PekaEDSGUI eds;
    
    public SettingsDialog(PekaEDSGUI pkeds) {
        this.eds = pkeds;
        
        setup();
    }
    
    // TODO Reset values after user clicks on "Cancel"
    private void setup() {
        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        
        panelGeneral = new PanelGeneral();
        panelDefaults = new PanelDefaults();
        panelShortcuts = new PanelShortcuts();
        panelMapProfile = new PanelMapProfile();
        
        tabbedPane.add("General", panelGeneral);
        tabbedPane.add("Defaults", panelDefaults);
        tabbedPane.add("Shortcuts", new JScrollPane(panelShortcuts));
        //tabbedPane.add("Map profile", panelMapProfile);
        
        JPanel panelButtons = new JPanel();
        var btnOk = new JButton("OK");
        var btnCancel = new JButton("Cancel");
        
        btnOk.addActionListener(e -> {
            saveSettings();
            dispose();
        });
        
        btnCancel.addActionListener(e -> {
            dispose();
        });
        
        panelButtons.setLayout(new MigLayout());
        panelButtons.add(new JPanel(), "cell 0 0, width 100%");
        panelButtons.add(btnOk, "cell 1 0");
        panelButtons.add(btnCancel, "cell 2 0");
        
        add(tabbedPane, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);
        
        setSize(new Dimension(640, 480));
        setResizable(false);
        setTitle("Settings");
    }
    
    /*
        It would probably be better/easier to have an instance of the Settings class, set its values in the panels, and then copy all the values in the main settings instance.
        However, I made all the Settings methods and values static, because I thought that that's a good way to do it. It isn't bad, but now it has to be done like this.
        
        A rewrite is not worth it, in my opinion.
     */
    private void saveSettings() {
        // Save general data
        Settings.setBasePath(panelGeneral.getGamePath());
        Settings.setTestingParameter(panelGeneral.getTestingParameters());
        Settings.setDefaultStartupBehavior(panelGeneral.getStartupBehavior());
        
        // Save default values
        Settings.setDefaultTileset(panelDefaults.getTileset());
        Settings.setDefaultBackground(panelDefaults.getDefaultBackground());
        Settings.setDefaultMusic(panelDefaults.getMusic());
    
        Settings.setDefaultAuthor(panelDefaults.getAuthor());
        Settings.setDefaultMapName(panelDefaults.getMapName());
        
        Settings.setUseBGTileset(panelGeneral.useBGTileset());
        Settings.setShowTileNumberInTileset(panelGeneral.showTilesetNumber());
        
        Settings.setHighlightSelection(panelGeneral.highlightSelection());
        
        Settings.setAutosaveInterval((panelGeneral.getAutosaveInterval() * 60) * 1000);
        Settings.setAutosaveFileCount(panelGeneral.getAutosaveFileCount());
        
        for (var entry : panelShortcuts.getShortcutMap().entrySet()) {
            Settings.setKeyboardShortcutFor(entry.getKey(), entry.getValue());
    
            Logger.info("Settings shortcut for action {} to keys {}", entry.getKey(), entry.getValue());
        }
        
        /*
        Settings.getMapProfile().getScrollingTypes().clear();
        for (int i = 0; i < panelMapProfile.getScrollingTypes().size(); i++) {
            Settings.getMapProfile().getScrollingTypes().add(panelMapProfile.getScrollingTypes().get(i));
        }
    
        Settings.getMapProfile().getWeatherTypes().clear();
        for (int i = 0; i < panelMapProfile.getWeatherTypes().size(); i++) {
            Settings.getMapProfile().getWeatherTypes().add(panelMapProfile.getWeatherTypes().get(i));
        }*/
        
        eds.installKeyboardShortcuts();
        eds.updateAutosaveManager();
        
        //eds.updateMapProfileData();
        
        Settings.save();
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        
        panelDefaults.resetValues();
        panelGeneral.resetValues();
        panelShortcuts.resetValues();
        panelMapProfile.resetValues();
    }
}
