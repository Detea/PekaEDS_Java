package pekaeds.ui.spritelistpanel;


import org.tinylog.Logger;
import net.miginfocom.swing.MigLayout;
import pekaeds.filechooser.SpriteFileChooser;
import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.pk2.level.PK2Level;
import pekaeds.pk2.sprite.ISpritePrototype;
import pekaeds.pk2.sprite.io.SpriteIO;
import pekaeds.tool.Tool;
import pekaeds.tool.Tools;
import pekaeds.tool.tools.BrushTool;
import pekaeds.ui.listeners.PK2LevelConsumer;
import pekaeds.ui.listeners.SpritePlacementListener;
import pekaeds.ui.main.PekaEDSGUI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.File;

public class SpritesPanel extends JPanel implements PK2LevelConsumer, SpritePlacementListener {
    //private final Settings settings = new Settings();
    
    private ChangeListener changeListener;
    private ChangeEvent changeEvent = new ChangeEvent(this);
    
    //private JButton btnEditSprite;
    private JButton btnAdd;
    private JButton btnRemove;
    private JButton btnSetPlayer;
    
    private PekaEDSGUI gui;
    
    private JList<ISpritePrototype> spriteList;
    private DefaultListModel<ISpritePrototype> listModel = new DefaultListModel<>();
    
    private PK2Level level;
    
    public SpritesPanel(PekaEDSGUI ui) {
        this.gui = ui;
        
        setupUI();
    }
    
    private void setupUI() {
        btnAdd = new JButton("Add");
        btnRemove = new JButton("Remove");
        btnSetPlayer = new JButton("Set Player");
        //btnEditSprite = new JButton("Edit");
        
        var btnPanel = new JPanel();
        btnPanel.add(btnAdd);
        btnPanel.add(btnRemove);
        btnPanel.add(btnSetPlayer);
        //btnPanel.add(btnEditSprite);
    
        spriteList = new JList<>(listModel);
        spriteList.setCellRenderer(new SpriteListCellRenderer());
  
        setPreferredSize(new Dimension(300, getPreferredSize().height));
        
        setLayout(new MigLayout("wrap 1"));
        
        var scrollPane = new JScrollPane(spriteList);

        addListeners();
        
        add(scrollPane, "dock center");
        add(btnPanel, "dock north");
    }
    
    private void addListeners() {
        btnAdd.addActionListener(e -> {

            var fc = new SpriteFileChooser(PK2FileSystem.getAssetsPath(PK2FileSystem.SPRITES_DIR));


            fc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if(f.isDirectory())return true;
                    else{
                        String name = f.getName();
                        return name.endsWith(".spr2") || name.toLowerCase().endsWith(".spr");
                    }
                }
            
                @Override
                public String getDescription() {
                    return "Pekka Kana 2 Sprite file (*.spr)";
                }
            });
        
            var res = fc.showOpenDialog(this);
        
            if (res == JFileChooser.APPROVE_OPTION) {

                try{
                    ISpritePrototype spr = SpriteIO.loadSprite(fc.getSelectedFile());

                    // TODO Prevent sprite to be added multiple times
                    
                    listModel.addElement(spr);


                    level.addSprite(spr);
                    
                    spriteList.ensureIndexIsVisible(listModel.indexOf(spr));
                    spriteList.setSelectedValue(spr, true);
                    
                    Tool.setSelectedSprite(spr);
                    Tool.setMode(Tool.MODE_SPRITE);
    
                    changeListener.stateChanged(changeEvent);
                }
                catch(Exception spriteException){
                    Logger.error(spriteException);                    

                    StringBuilder builder = new StringBuilder();
                    builder.append(fc.getSelectedFile());
                    builder.append(spriteException);

                    JOptionPane.showMessageDialog(this, builder.toString(), "Cannot load a sprite!", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    
        btnSetPlayer.addActionListener(e -> {
            if (spriteList.getSelectedValue().getType() == ISpritePrototype.TYPE_CHARACTER) {
                var currentPlayerSprite = level.getSprite(level.player_sprite_index);
                if (currentPlayerSprite != null) currentPlayerSprite.setPlayerSprite(false);

                level.player_sprite_index = spriteList.getSelectedIndex();
               
                spriteList.getSelectedValue().setPlayerSprite(true);
                spriteList.repaint();
                
                changeListener.stateChanged(changeEvent);
            }
        });
        
        btnRemove.addActionListener(e -> {
            /*map.removeSprite(spriteList.getSelectedValue().getFilename());
            listModel.removeElement(spriteList.getSelectedValue());
            
            if (spriteList.getSelectedIndex() - 1 > 0) {
                spriteList.setSelectedIndex(spriteList.getSelectedIndex() - 1);
            } else {
                spriteList.setSelectedIndex(0);
            }
            
            gui.repaintView();
    
            changeListener.stateChanged(changeEvent);*/
        });
        
        spriteList.addListSelectionListener(l -> {
            Tool.setSelectedSprite(spriteList.getSelectedValue());
            Tool.setMode(Tool.MODE_SPRITE);
            gui.setSelectedTool(Tools.getTool(BrushTool.class));
        });

        spriteList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Tool.setMode(Tool.MODE_SPRITE);
            }
        });
        
        Tool.setSpritePlacementListener(this);
    }
    
    public void setChangeListener(ChangeListener listener) {
        this.changeListener = listener;
    }
    
    @Override
    public void setMap(PK2Level map) {
        this.level = map;
        
        listModel.clear();
        listModel.addAll(map.getSpriteList());

        if (map.player_sprite_index >= 0 && map.player_sprite_index < listModel.size()) {
            listModel.get(map.player_sprite_index).setPlayerSprite(true);
        }
    }
    
    @Override
    public void placed(int id) {
        spriteList.repaint(); // TODO Optimization: Don't redraw the whole list, only the affected entry
    }
    
    @Override
    public void removed(int id) {
        spriteList.repaint(); // TODO Optimization: Don't redraw the whole list, only the affected entry
    }
}
