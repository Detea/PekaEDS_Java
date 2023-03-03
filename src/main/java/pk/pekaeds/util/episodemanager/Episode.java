package pk.pekaeds.util.episodemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class Episode {
    private List<File> fileList = new ArrayList<>();
    private File episodeFolder;
    private String episodeName;
    
    Episode(List<File> list, File folder, String name) {
        this.fileList = list;
        this.episodeFolder = folder;
        this.episodeName = name;
    }
    
    public List<File> getFileList() {
        return fileList;
    }
    
    public File getEpisodeFolder() {
        return episodeFolder;
    }
    
    public String getEpisodeName() {
        return episodeName;
    }
}
