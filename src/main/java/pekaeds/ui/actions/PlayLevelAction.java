package pekaeds.ui.actions;

import java.util.ArrayList;

import java.io.File;
import java.io.IOException;

import javax.swing.*;

import org.tinylog.Logger;

import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.settings.LevelTestingSettings;
import pekaeds.settings.Settings;
import pekaeds.ui.main.PekaEDSGUI;
import pekaeds.ui.misc.UnsavedChangesDialog;

import java.awt.event.ActionEvent;



public class PlayLevelAction extends AbstractAction {
    private static final String WINDOWS_EXECUTABLE = "pk2.exe";
    private static final String LINUX_AND_MAC_EXECUTABLE = "./bin/pekka-kana-2";
    private static boolean isWindows(){
        String osname = System.getProperty("os.name").toLowerCase();
        return osname.contains("win");
    }

    private PekaEDSGUI gui;

    private final File executableDirectory;
    private final String executable;

    private Process process;
    
    public PlayLevelAction(PekaEDSGUI ui) {
        this.gui = ui;

        File assetsPath = PK2FileSystem.getAssetsPath();
        if(isWindows()){
            this.executable = WINDOWS_EXECUTABLE;
            this.executableDirectory = assetsPath;
        }
        else{
            this.executable = LINUX_AND_MAC_EXECUTABLE;
            if(assetsPath.getName().equals("res")){
                this.executableDirectory = assetsPath.getParentFile();
            }
            else{
                this.executableDirectory = assetsPath;
            }
        }

    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gui.unsavedChangesPresent()) {
            int result = UnsavedChangesDialog.show(gui);
            
            if (result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION) {
                playLevel();
            }
        } else {
            playLevel();
        }
    }


    private void playLevel(){
        if(process!=null && process.isAlive()){
            return;
        }
        
        try {
            LevelTestingSettings lts = Settings.levelTestingSettings;

            ProcessBuilder builder = new ProcessBuilder();
            if(lts.customWorkingDirectory){
                builder.directory(new File(lts.workingDirectory));
            }
            else{
                builder.directory(this.executableDirectory);
            }           

            ArrayList<String> commands = new ArrayList<>();
            if(lts.customExecutable){
                commands.add(lts.executable);
            }
            else{
                commands.add(this.executable);
            }

            if(lts.devMode){
                commands.add("--dev");
            }

            commands.add("--test");
            commands.add(PK2FileSystem.getEpisodeName() + "/" + gui.getCurrentFile().getName());

            builder.command(commands);
            process = builder.start();

        } catch (IOException e) {
            Logger.error(e);
        }
    }
}
