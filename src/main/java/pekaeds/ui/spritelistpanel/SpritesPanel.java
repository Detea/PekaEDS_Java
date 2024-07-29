package pekaeds.ui.spritelistpanel;

import net.miginfocom.swing.MigLayout;
import pekaeds.filechooser.SpriteFileChooser;
import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.pk2.map.PK2Map;
import pekaeds.pk2.sprite.SpriteReaders;
import pekaeds.pk2.sprite.old.ISpritePrototypeEDS;
import pekaeds.pk2.sprite.old.ISpriteReader;
import pekaeds.tool.Tool;
import pekaeds.tool.Tools;
import pekaeds.tool.tools.BrushTool;
import pekaeds.ui.listeners.PK2MapConsumer;
import pekaeds.ui.listeners.SpritePlacementListener;
import pekaeds.ui.main.PekaEDSGUI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.w3c.dom.events.MouseEvent;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.File;

public class SpritesPanel extends JPanel implements PK2MapConsumer, SpritePlacementListener {
    //private final Settings settings = new Settings();
    
    private ChangeListener changeListener;
    private ChangeEvent changeEvent = new ChangeEvent(this);
    
    //private JButton btnEditSprite;
    private JButton btnAdd;
    private JButton btnRemove;
    private JButton btnSetPlayer;
    
    private PekaEDSGUI gui;
    
    private JList<ISpritePrototypeEDS> spriteList;
    private DefaultListModel<ISpritePrototypeEDS> listModel = new DefaultListModel<>();
    
    private PK2Map map;
    
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

            var fc = new SpriteFileChooser(PK2FileSystem.INSTANCE.getAssetsPath(PK2FileSystem.SPRITES_DIR));


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
                ISpriteReader reader = SpriteReaders.getReader(fc.getSelectedFile());
                ISpritePrototypeEDS spr = reader==null?null:reader.loadImageData(fc.getSelectedFile(), map.getEpisodeDirStr());

                if(spr!=null){

                    // TODO Prevent sprite to be added multiple times
                    
                    listModel.addElement(spr);
                    map.addSprite(spr);
                    
                    spriteList.ensureIndexIsVisible(listModel.indexOf(spr));
                    spriteList.setSelectedValue(spr, true);
                    
                    Tool.setSelectedSprite(spr);
                    Tool.setMode(Tool.MODE_SPRITE);
    
                    changeListener.stateChanged(changeEvent);                    
                }
                
                else {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Can't load sprite: ");
                    builder.append(fc.getSelectedFile());
                    builder.append("!\n");
                    builder.append("File not found, format not recognized, the sprite is malformed or has lacking dependecies!");
    
                    JOptionPane.showMessageDialog(this, builder.toString(), "Can't load sprite!", JOptionPane.ERROR_MESSAGE);
                }

            }
        });
    
        btnSetPlayer.addActionListener(e -> {
            if (spriteList.getSelectedValue().getType() == ISpritePrototypeEDS.TYPE_CHARACTER) {
                var currentPlayerSprite = map.getSprite(map.getPlayerSpriteId());
                if (currentPlayerSprite != null) currentPlayerSprite.setPlayerSprite(false);

                map.setPlayerSpriteId(spriteList.getSelectedIndex());
               
                spriteList.getSelectedValue().setPlayerSprite(true);
                spriteList.repaint();
                
                changeListener.stateChanged(changeEvent);
            }
        });
        
        btnRemove.addActionListener(e -> {
            map.removeSprite(spriteList.getSelectedValue().getFilename());
            listModel.removeElement(spriteList.getSelectedValue());
            
            if (spriteList.getSelectedIndex() - 1 > 0) {
                spriteList.setSelectedIndex(spriteList.getSelectedIndex() - 1);
            } else {
                spriteList.setSelectedIndex(0);
            }
            
            gui.repaintView();
    
            changeListener.stateChanged(changeEvent);
        });
        
        spriteList.addListSelectionListener(l -> {
            Tool.setSelectedSprite(spriteList.getSelectedValue());
            Tool.setMode(Tool.MODE_SPRITE);
            gui.setSelectedTool(Tools.getTool(BrushTool.class));
        });

        spriteList.addMouseListener(new MouseAdapter() {
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
    public void setMap(PK2Map map) {
        this.map = map;
        
        listModel.clear();
        listModel.addAll(map.getSpriteList());

        if (map.getPlayerSpriteId() >= 0 && map.getPlayerSpriteId() < listModel.size()) {
            listModel.get(map.getPlayerSpriteId()).setPlayerSprite(true);
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
