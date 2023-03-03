package pk.pekaeds.ui.episodepanel;

import net.miginfocom.swing.MigLayout;
import pk.pekaeds.ui.filefilters.FileFilters;
import pk.pekaeds.util.episodemanager.Episode;
import pk.pekaeds.util.episodemanager.EpisodeManager;

import javax.swing.*;

public final class EpisodePanel extends JPanel implements EpisodeChangeListener {
    private final EpisodeManager manager;
    
    private JList<String> lstFiles;
    private DefaultListModel<String> dfmFiles;
    private JLabel lblEpisodeName;
    
    private JButton btnImport;
    private JButton btnRemove;
    
    public EpisodePanel(EpisodeManager episodeManager) {
        this.manager = episodeManager;
        
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
