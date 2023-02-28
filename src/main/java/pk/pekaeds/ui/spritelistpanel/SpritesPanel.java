package pk.pekaeds.ui.spritelistpanel;

import net.miginfocom.swing.MigLayout;
import pk.pekaeds.filechooser.SpriteFileChooser;
import pk.pekaeds.pk2.map.PK2Map;
import pk.pekaeds.pk2.sprite.PK2Sprite;
import pk.pekaeds.pk2.sprite.SpriteReaders;
import pk.pekaeds.settings.Settings;
import pk.pekaeds.tools.Tool;
import pk.pekaeds.ui.listeners.PK2MapConsumer;
import pk.pekaeds.ui.listeners.SpritePlacementListener;
import pk.pekaeds.ui.main.PekaEDSGUI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

public class SpritesPanel extends JPanel implements PK2MapConsumer, SpritePlacementListener {
    private final Settings settings = new Settings();
    
    private ChangeListener changeListener;
    private ChangeEvent changeEvent = new ChangeEvent(this);
    
    private JButton btnEditSprite;
    private JButton btnAdd;
    private JButton btnRemove;
    private JButton btnSetPlayer;
    
    private PekaEDSGUI gui;
    
    private JList<PK2Sprite> spriteList;
    private DefaultListModel<PK2Sprite> listModel = new DefaultListModel<>();
    
    private PK2Map map;
    
    public SpritesPanel(PekaEDSGUI ui) {
        this.gui = ui;
        
        setupUI();
    }
    
    private void setupUI() {
        btnAdd = new JButton("Add");
        btnRemove = new JButton("Remove");
        btnSetPlayer = new JButton("Set Player");
        btnEditSprite = new JButton("Edit");
        
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
            var fc = new SpriteFileChooser(settings.getSpritesPath());
            fc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().endsWith(".spr");
                }
            
                @Override
                public String getDescription() {
                    return "Pekka Kana 2 Sprite file (*.spr)";
                }
            });
        
            var res = fc.showOpenDialog(this);
        
            if (res == JFileChooser.APPROVE_OPTION) {
                var hasReader = SpriteReaders.getReader(fc.getSelectedFile());
                
                if (hasReader != null) {
                    var spr = hasReader.loadImageData(fc.getSelectedFile());
    
                    // TODO Prevent sprite to be added multiple times
                    
                    listModel.addElement(spr);
                    map.addSprite(spr);
                    
                    spriteList.ensureIndexIsVisible(listModel.indexOf(spr));
                    spriteList.setSelectedValue(spr, true);
                    
                    Tool.setSelectedSprite(spr);
                    Tool.setMode(Tool.MODE_SPRITE);
    
                    changeListener.stateChanged(changeEvent);
                } else {
                    JOptionPane.showMessageDialog(this, "Can't recognize file as Pekka Kana 2 sprite file.", "Wrong format?", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    
        btnSetPlayer.addActionListener(e -> {
            if (spriteList.getSelectedValue().getType() == PK2Sprite.TYPE_CHARACTER) {
                map.getSprite(map.getPlayerSpriteId()).setPlayerSprite(false);
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
    }
    
    @Override
    public void placed(int id) {
        spriteList.repaint(); // TODO Optimization: Don't redraw the whole list, only the affected entry
    }
}
