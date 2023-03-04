package pk.pekaeds.util.episodemanager;

import org.tinylog.Logger;
import pk.pekaeds.pk2.map.MapIO;
import pk.pekaeds.pk2.map.PK2Map;
import pk.pekaeds.pk2.map.PK2Map13;
import pk.pekaeds.settings.Settings;
import pk.pekaeds.ui.episodepanel.EpisodeChangeListener;
import pk.pekaeds.ui.mapposition.MapIcon;
import pk.pekaeds.util.GFXUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public final class EpisodeManager {
    private Episode episode = null;
    
    private static final EpisodeIO episodeIO = new EpisodeIO();
    
    private final List<MapIcon> mapIcons = new ArrayList<>();
    
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
    
        loadEpisode(new File("episodes" + File.separatorChar + episode.getEpisodeName() + ".episode")); // TODO Maybe save the episode file in the episode class?
    }
    
    public void loadEpisode(File episodeFile) {
        try {
            episode = episodeIO.load(episodeFile);
            
            changeListener.episodeChanged(episode);
            
            mapIcons.clear();
            for (var file : episode.getFileList()) {
                addIconToList(file);
            }
            
            Logger.info("Loaded episode: {}, files: {}, folder: {}", episode.getEpisodeName(), episode.getFileList().size(), episode.getEpisodeFolder().getAbsolutePath());
        } catch (IOException e) {
            Logger.info(e, "Unable to load episode file.");
        }
    }
    
    public void addFileToEpisode(File file) {
        if (episode != null) {
            episode.getFileList().add(file);
            
            changeListener.episodeChanged(episode);
    
            addIconToList(file);
        }
    }
    
    private void addIconToList(File file) {
        var mapReader = MapIO.getReader(file);
        PK2Map map = mapReader.loadIconDataOnly(file);
    
        if (map != null) {
            BufferedImage iconSheet = null;
            try {
                iconSheet = ImageIO.read(new File(Settings.getPK2stuffFilePath()));
    
                iconSheet = GFXUtils.makeTransparent(iconSheet);
                var iconImage = iconSheet.getSubimage(1 + (map.getIcon() * 28), 452, 27, 27);
                
                mapIcons.add(new MapIcon(iconImage, new Point(map.getMapX(), map.getMapY())));
            } catch (IOException e) {
                Logger.info(e, "Unable to load PK2Stuff file.");
            }
        } else {
            Logger.info("Unable to icon for file: {} in episode: {}", file.getAbsolutePath(), episode.getEpisodeName());
        }
    }
    
    public void removeFileFromEpisode(String filename, boolean delete) {
        if (episode != null) {
            var file = new File(episode.getEpisodeFolder().getAbsolutePath() + File.separatorChar + filename);
            
            mapIcons.remove(episode.getFileList().indexOf(file));
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
    
    public Episode getEpisode() {
        return episode;
    }
    
    public boolean hasEpisodeLoaded() {
        return episode != null;
    }

    public List<MapIcon> getMapIcons() {
        return mapIcons;
    }
    
    public void setChangeListener(EpisodeChangeListener listener) {
        this.changeListener = listener;
    }
}
