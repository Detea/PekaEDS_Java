package pk.pekaeds.util.file;

import org.tinylog.Logger;

import java.io.*;

public final class LastSessionManager {
    private LastSession lastSession;
    
    public LastSession loadLastSession(File file) throws IOException {
        try (var in = new DataInputStream(new FileInputStream(file))) {
            var lastEpisodeFile = new File(in.readUTF());
            var lastLevelFile = new File(in.readUTF());
            
            var lastViewportX = in.readInt();
            var lastViewportY = in.readInt();
            
            return new LastSession(lastEpisodeFile, lastLevelFile, lastViewportX, lastViewportY);
        }
    }
    
    public void saveSession(File file, File currentEpisode, File currentFile, int viewportX, int viewportY) {
        try (var out = new DataOutputStream(new FileOutputStream(file))) {
            if (currentEpisode != null) {
                out.writeUTF(currentEpisode.getAbsolutePath());
            } else {
                out.writeUTF("");
            }
            
            if (currentFile != null) {
                out.writeUTF(currentFile.getAbsolutePath());
            } else {
                out.writeUTF("");
            }
            
            out.writeInt(viewportX);
            out.writeInt(viewportY);
        } catch (IOException e) {
            Logger.info(e, "Unable to save last session file: {}", file.getAbsolutePath());
        }
    }
    
    public LastSession getLastSession() {
        return lastSession;
    }
    
    public static class LastSession {
        private final File lastEpisodeFile;
        private final File lastLevelFile;
        
        private final int lastViewportX;
        private final int lastViewportY;
        
        public LastSession(File lastEpisode, File lastLevel, int lastViewX, int lastViewY) {
            this.lastEpisodeFile = lastEpisode;
            this.lastLevelFile = lastLevel;
            
            this.lastViewportX = lastViewX;
            this.lastViewportY = lastViewY;
        }
        
        public File getLastEpisodeFile() {
            return lastEpisodeFile;
        }
        
        public File getLastLevelFile() {
            return lastLevelFile;
        }
        
        public int getLastViewportX() {
            return lastViewportX;
        }
        
        public int getLastViewportY() {
            return lastViewportY;
        }
    }
}
