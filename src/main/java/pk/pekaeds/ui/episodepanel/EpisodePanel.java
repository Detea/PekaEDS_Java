package pk.pekaeds.ui.episodepanel;

import net.miginfocom.swing.MigLayout;
import pk.pekaeds.settings.Settings;
import pk.pekaeds.ui.filefilters.FileFilters;
import pk.pekaeds.ui.main.PekaEDSGUI;
import pk.pekaeds.ui.misc.UnsavedChangesDialog;
import pk.pekaeds.util.episodemanager.Episode;
import pk.pekaeds.util.episodemanager.EpisodeManager;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class EpisodePanel extends JPanel implements EpisodeChangeListener {
    private final EpisodeManager manager;
    
    private JList<String> lstFiles;
    private DefaultListModel<String> dfmFiles;
    private JLabel lblEpisodeName;
    
    private JButton btnImport;
    private JButton btnRemove;
    
    private PekaEDSGUI eds;
    
    public EpisodePanel(PekaEDSGUI edsGUI, EpisodeManager episodeManager) {
        this.manager = episodeManager;
        this.eds = edsGUI;
        
        manager.setChangeListener(this);
        
        setup();
    }
    
    private void setup() {
        dfmFiles = new DefaultListModel<>();
        lstFiles = new JList<>(dfmFiles);
        lblEpisodeName = new JLabel("Episode: None");
        
        btnImport = new JButton("Import");
        btnRemove = new JButton("Remove");
        
        var pButtons = new JPanel();
        pButtons.setLayout(new MigLayout());
        pButtons.add(btnImport);
        pButtons.add(btnRemove);
        
        setLayout(new MigLayout());
        add(lblEpisodeName, "dock north");
        add(lstFiles, "dock center");
        add(pButtons, "dock south");
        
        setListeners();
    }
    
    private void setListeners() {
        btnImport.addActionListener(e -> {
            if (manager.hasEpisodeLoaded()) {
                var fc = new JFileChooser(manager.getEpisode().getEpisodeFolder());
                fc.setDialogTitle("Add file to episode...");
                fc.setFileFilter(FileFilters.PK2_MAP_FILTER);
                
                if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    manager.importFileIntoEpisode(fc.getSelectedFile());
                }
            } else {
                JOptionPane.showMessageDialog(null, "No episode loaded!", "No episode", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnRemove.addActionListener(e -> {
            if (manager.hasEpisodeLoaded()) {
                int selected = lstFiles.getSelectedIndex();
                
                if (selected != -1) {
                    var jopDelete = JOptionPane.showConfirmDialog(null, "Delete file from disk?", "Remove file completely?", JOptionPane.YES_NO_CANCEL_OPTION);
                    
                    if (jopDelete == JOptionPane.YES_OPTION) {
                        removeFile(selected, true);
                    } else if (jopDelete == JOptionPane.NO_OPTION) {
                        removeFile(selected, false);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "No episode loaded!", "No episode", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        lstFiles.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (manager.hasEpisodeLoaded()) {
                    if (e.getClickCount() == 2) {
                        var file = manager.getEpisode().getFileList().get(lstFiles.getSelectedIndex());
    
                        if (file.exists()) {
                            if (eds.unsavedChangesPresent()) {
                                String path = manager.getEpisode().getEpisodeFolder().getAbsolutePath();
        
                                int result = UnsavedChangesDialog.show(eds);
        
                                if (result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION) {
                                    if (result == JOptionPane.YES_OPTION) {
                                        var fc = new JFileChooser(path);
                                        fc.setDialogTitle("Save map...");
                                        fc.setFileFilter(FileFilters.PK2_MAP_FILTER);
                
                                        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                                            eds.saveMap(fc.getSelectedFile());
                                        }
                                    }
            
                                    eds.loadMap(file);
                                }
                            } else {
                                eds.loadMap(file);
                            }
                        } else {
                            var result = JOptionPane.showConfirmDialog(null, "Selected file doesn't exist. Remove it from episode?", "File not found", JOptionPane.YES_NO_OPTION);
                            
                            if (result == JOptionPane.YES_OPTION) {
                                manager.removeFileFromEpisode(file.getName(), false);
                            }
                        }
                    }
                }
            }
        });
    }
    
    private void removeFile(int indexInList, boolean deleteFromDisk) {
        manager.removeFileFromEpisode(lstFiles.getModel().getElementAt(indexInList), deleteFromDisk);
        
        dfmFiles.remove(indexInList);
    }
    
    @Override
    public void episodeChanged(Episode episode) {
        lblEpisodeName.setText("Episode: " + episode.getEpisodeName());
        
        dfmFiles.clear();
        for (var f : episode.getFileList()) {
            dfmFiles.addElement(f.getName());
        }
    }
}
