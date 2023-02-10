package pk.pekaeds;

import com.formdev.flatlaf.FlatDarkLaf;
import pk.pekaeds.ui.PekaEDSGUILauncher;

import javax.swing.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class PekaEDS {
    public static void main(String[] args) {
        FlatDarkLaf.setup();
        //System.setProperty( "flatlaf.menuBarEmbedded", "false" );

        System.setProperty("sun.java2d.noddraw", "true");
        
        PekaEDSGUILauncher.launch();
    }
}