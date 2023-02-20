package pk.pekaeds.util;

import org.tinylog.Logger;
import pk.pekaeds.settings.Settings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class is responsible for creating and keeping track of autosave files.
 *
 * Each delay the originalFile gets copied into an {originalFile}_autosave_{currentFile}.map file and then saved.
 *
 * fileCount is the number of {originalFile}_autosave_{currentFile}.map files the user wants.
 * fileRotation contains a list of these autosave files, so their path (string) doesn't have to be generated each time actionPerformed() is called.
 * filename is the name of the current file, without the .map at the end. (level01 instead of level01.map). This is used in the generation of the autosave file names.
 *
 * currentFile is the current autosave in rotation. It gets updated after each save and reset to 0 when it has reached the fileCount.
 *
 * delay is the time between each save in milliseconds.
 *
 * fileCount is also stored in the settings file and can be changed/accessed via Settings.get/setFileCount()
 * delay is also stored in the settings file and can be accessed via Settings.get/setAutosaveInterval()
 */
public final class AutoSaveManager {
    private final Timer timer;
    private final Consumer<File> saveFunction; // The function to call to actually write the map to file.
    
    private File originalFile;

    private int fileCount;
    private int currentFile = 0;
    
    private int delay;
    
    private final List<File> fileRotation = new ArrayList<>();
    
    private String filename;
    
    public AutoSaveManager(Consumer<File> saveFunc, File file) {
        this.saveFunction = saveFunc;
        this.originalFile = file;
        
        timer = new Timer(120000, new SaveAction());

        delay = Settings.getAutosaveInterval();
        fileCount = Settings.getAutosaveFileCount();
        generateFileRotation();
    }
    
    public void start() {
        timer.start();
    }
    
    public void setInterval(int newDelay) {
        this.delay = newDelay;
    
        timer.setDelay(delay);
    
        timer.restart(); // Restart it with the new interval.
    }
    
    public void setFileCount(int autosaveFileCount) {
        this.fileCount = autosaveFileCount;
        
        generateFileRotation();
    }
    
    public void setFile(File file) {
        this.originalFile = file;
        
        if (file != null) {
            filename = file.getName().substring(0, file.getName().length() - 4); // Only save the name of the file, get rid of the .map at the end. See SaveAction.actionPerformed() for more information.
    
            generateFileRotation();
        }
    }

    private void generateFileRotation() {
        fileRotation.clear();

        for (int i = 0; i < fileCount; i++) {
            var file = new File(generateAutosaveFilePath(i + 1)); // The autosave files should start at 1. file_autosave_1.map instead of file_autosave_0.map. I think it looks better.
            
            fileRotation.add(file);
        }
    }
    
    /**
        This method constructs the corresponding name/path for the autosave_{filenumber}.map file.
     
        Example, file: "level01.map", file count = 3:
        {path of current file}\level01.map
        {path of current file}\level01_autosave_1.map
        {path of current file}\level01_autosave_2.map
        {path of current file}\level01_autosave_3.map
    */
    private String generateAutosaveFilePath(int fileNumber) {
        var sb = new StringBuilder();
        
        if (originalFile != null) {
            sb.append(originalFile.getParentFile().getPath());
            sb.append(File.separatorChar);
            sb.append(filename);
            sb.append("_autosave_");
            sb.append(fileNumber);
            sb.append(".map");
        }
        
        return sb.toString();
    }
    
    private class SaveAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (originalFile != null && fileCount > 0 && delay > 0) {
                // Copy the original file into the current autosave file
                try {
                    Files.copy(originalFile.toPath(), fileRotation.get(currentFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    Logger.warn("Unable to save autosave file: " + fileRotation.get(currentFile).getName());
                }
                
                // Save the file
                saveFunction.accept(originalFile);
                
                currentFile++;
                if (currentFile >= fileCount) {
                    currentFile = 0;
                }
            }
        }
    }
}
