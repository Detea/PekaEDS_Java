package pk.pekaeds.util.episodemanager;

import org.tinylog.Logger;
import pk.pekaeds.ui.episodepanel.EpisodeChangeListener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public final class EpisodeManager {
    private Episode episode = null;
    private static final EpisodeIO episodeIO = new EpisodeIO();
    
    private EpisodeChangeListener changeListener;
    
    public EpisodeManager() {
        var episodesFolder = new File("episodes");
        
        if (!episodesFolder.exists()) {
            episodesFolder.mkdir();
        }
    }
    
    public void newEpisode(File folder) {
        var tmpFiles = new ArrayList<File>();
        for (var f : folder.listFiles()) {
            if (f.getName().endsWith(".map")) {
                tmpFiles.add(f);
            }
        }
    
        episode = new Episode(tmpFiles, folder, folder.getName());
        episodeIO.save(episode);
    }
    
    public void loadEpisode(File episodeFile) {
        try {
            episode = episodeIO.load(episodeFile);
            
            changeListener.episodeChanged(episode);
            
            Logger.info("Loaded episode: {}, files: {}, folder: {}", episode.getEpisodeName(), episode.getFileList().size(), episode.getEpisodeFolder().getAbsolutePath());
        } catch (IOException e) {
            Logger.info(e, "Unable to load episode file.");
        }
    }
    
    public Episode getEpisode() {
        return episode;
    }
    
    public boolean hasEpisodeLoaded() {
        return episode != null;
    }
    
    public void addFileToEpisode(File file) {
        if (episode != null) {
            episode.getFileList().add(file);
            
            changeListener.episodeChanged(episode);
        }
    }
    
    public void removeFileFromEpisode(String filename, boolean delete) {
        if (episode != null) {
            var file = new File(episode.getEpisodeFolder().getAbsolutePath() + File.separatorChar + filename);
            
            episode.getFileList().remove(file);
            
            if (file.exists() && delete) {
                var result = file.delete();
    
                if (result) {
                    Logger.info("File \"{}\" removed from episode and disk.", file.getAbsolutePath());
                } else {
                    Logger.info("Unable to remove file \"{}\" from disk.", file.getAbsolutePath());
                }
            } else {
                Logger.info("File \"{}\" doesn't exist!", file.getAbsolutePath());
            }
            
            episodeIO.save(episode);
        }
    }
    
    public void setChangeListener(EpisodeChangeListener listener) {
        this.changeListener = listener;
    }
    
    public void importFileIntoEpisode(File selectedFile) {
        if (episode != null) {
            var target = new File(episode.getEpisodeFolder().getAbsolutePath() + File.separatorChar + selectedFile.getName());
    
            try {
                Files.move(Path.of(selectedFile.toURI()), Path.of(target.toURI()), StandardCopyOption.REPLACE_EXISTING);
                
                addFileToEpisode(target);
                
                episodeIO.save(episode);
            } catch (IOException e) {
                Logger.info(e, "Unable to copy file into episode folder.");
            }
        }
    }
}
