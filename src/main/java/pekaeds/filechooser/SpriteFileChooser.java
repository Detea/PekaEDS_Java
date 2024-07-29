package pekaeds.filechooser;

import net.miginfocom.swing.MigLayout;
import pekaeds.pk2.sprite.ISpritePrototype;
import pekaeds.pk2.sprite.io.SpriteIO;
import pekaeds.settings.Settings;
import pekaeds.ui.misc.ImagePanel;

import javax.swing.*;

import org.tinylog.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;

public class SpriteFileChooser extends JFileChooser implements PropertyChangeListener {
    private JPanel previewPanel;
    
    private ImagePanel imagePanel;
    
    private JLabel lblName;
    private JLabel lblNameVal;
    
    private JLabel lblType;
    private JLabel lblTypeVal;
    
    private JLabel lblFileCreated;
    private JLabel lblFileCreatedVal;
    
    private JLabel lblFileModified;
    private JLabel lblFileModifiedVal;
    
    public SpriteFileChooser(File basePath) {
        super(basePath);
        
        setup();
        
        setAccessory(previewPanel);
        
        addPropertyChangeListener(this);
    }
    
    private void setup() {
        previewPanel = new JPanel();
        
        imagePanel = new ImagePanel(256, 200);
        
        lblName = new JLabel("Name:");
        lblNameVal = new JLabel();
        
        lblType = new JLabel("Type:");
        lblTypeVal = new JLabel();
        
        lblFileCreated = new JLabel("Created:");
        lblFileCreatedVal = new JLabel("");
        
        lblFileModified = new JLabel("Modified:");
        lblFileModifiedVal = new JLabel();
        
        previewPanel.setLayout(new MigLayout());
        
        previewPanel.add(imagePanel, "dock north");
        
        var dataPanel = new JPanel();
        dataPanel.setLayout(new MigLayout());
        dataPanel.add(lblName, "cell 0 0");
        dataPanel.add(lblNameVal, "cell 1 0");
    
        dataPanel.add(lblType, "cell 0 1");
        dataPanel.add(lblTypeVal, "cell 1 1");
    
        dataPanel.add(lblFileCreated, "cell 0 2");
        dataPanel.add(lblFileCreatedVal, "cell 1 2");
    
        dataPanel.add(lblFileModified, "cell 0 3");
        dataPanel.add(lblFileModifiedVal, "cell 1 3");
        
        previewPanel.add(dataPanel, "dock center");
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
            var selectedFile = (File) e.getNewValue();
            
            if (selectedFile != null) {

                try{
                    ISpritePrototype spr = SpriteIO.loadSprite(selectedFile);

                    imagePanel.setImage(spr.getImage(), true, 256, 200);
    
                    lblNameVal.setText(spr.getName());
                    lblTypeVal.setText(Settings.getSpriteProfile().getTypes().get(spr.getType() - 1));
    
                    Path f = Paths.get(selectedFile.getPath());
                    BasicFileAttributes attributes = null;
                    try {
                        attributes = Files.readAttributes(f, BasicFileAttributes.class);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex); // TODO Catch this?
                    }
    
                    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss dd.MM.yy"); // TODO Use local time/date format
    
                    lblFileCreatedVal.setText(df.format(attributes.creationTime().toMillis()));
                    lblFileModifiedVal.setText(df.format(attributes.lastModifiedTime().toMillis()));

                }
                catch(Exception spriteException){
                    Logger.error(spriteException);
                }
            }
        }
    }
}
