package pekaeds.ui.spritelistpanel;


import org.tinylog.Logger;
import net.miginfocom.swing.MigLayout;
import pekaeds.filechooser.SpriteFileChooser;
import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.pk2.map.PK2Map;
import pekaeds.pk2.map.PK2MapSector;
import pekaeds.pk2.sprite.SpritePrototype;
import pekaeds.pk2.sprite.io.SpriteIO;
import pekaeds.tool.Tool;
import pekaeds.tool.Tools;
import pekaeds.tool.tools.BrushTool;
import pekaeds.ui.listeners.PK2MapConsumer;
import pekaeds.ui.listeners.PK2SectorConsumer;
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

public class SpritesPanel extends JPanel implements PK2MapConsumer, PK2SectorConsumer, SpritePlacementListener {
    //private final Settings settings = new Settings();

    private ChangeListener changeListener;
    private ChangeEvent changeEvent = new ChangeEvent(this);

    //private JButton btnEditSprite;
    private JButton btnAdd;
    private JButton btnRemove;

    private JButton btnReplace;

    private JButton btnSetPlayer;

    private PekaEDSGUI gui;

    private JList<SpritePrototype> spriteList;
    private DefaultListModel<SpritePrototype> listModel = new DefaultListModel<>();

    private PK2Map currentMap;
    private SpriteListCellRenderer spriteCellRenderer;

    public SpritesPanel(PekaEDSGUI ui) {
        this.gui = ui;

        setupUI();
    }

    private void setupUI() {
        btnAdd = new JButton("Add");
        btnRemove = new JButton("Remove");
        btnSetPlayer = new JButton("Set Player");
        btnReplace = new JButton("Replace");

        var btnPanel = new JPanel();
        btnPanel.add(btnAdd);
        btnPanel.add(btnRemove);
        //btnPanel.add(btnReplace);
        btnPanel.add(btnSetPlayer);

        spriteCellRenderer = new SpriteListCellRenderer();

        spriteList = new JList<>(listModel);
        spriteList.setCellRenderer(spriteCellRenderer);

        setPreferredSize(new Dimension(300, getPreferredSize().height));

        setLayout(new MigLayout("wrap 1"));

        var scrollPane = new JScrollPane(spriteList);

        addListeners();

        add(scrollPane, "dock center");
        add(btnPanel, "dock north");
    }

    private void addListeners() {
        btnAdd.addActionListener(e -> {
            SpritePrototype loadedSprite = loadSprite();

            if (loadedSprite != null) {
                int index = currentMap.addSprite(loadedSprite);

                if (index == currentMap.getLastSpriteIndex()) {
                    // if added a new sprite
                    listModel.addElement(loadedSprite);
                } else {
                    // sprite already existed in the list
                    loadedSprite = currentMap.getSprite(index);
                }

                spriteList.ensureIndexIsVisible(listModel.indexOf(loadedSprite));
                spriteList.setSelectedValue(loadedSprite, true);

                Tool.setSelectedSpriteByIndex(listModel.indexOf(loadedSprite));
                Tool.setMode(Tool.MODE_SPRITE);

                changeListener.stateChanged(changeEvent);
            }
        });

        btnSetPlayer.addActionListener(e -> {
            if (spriteList.getSelectedValue().getType() == SpritePrototype.TYPE_CHARACTER) {

                if (currentMap.player_sprite_index < currentMap.getSpriteList().size()) {
                    SpritePrototype currentPlayerSprite = currentMap.getSprite(currentMap.player_sprite_index);
                    currentPlayerSprite.setPlayerSprite(false);
                }

                currentMap.player_sprite_index = spriteList.getSelectedIndex();

                spriteList.getSelectedValue().setPlayerSprite(true);
                spriteList.repaint();

                changeListener.stateChanged(changeEvent);
            }
        });

        btnRemove.addActionListener(e -> {
            int newSpriteIndex = spriteList.getSelectedIndex() - 1;

            if (newSpriteIndex >= 0) {
                currentMap.removeSprite(spriteList.getSelectedValue());
                listModel.removeElement(spriteList.getSelectedValue());

                spriteList.setSelectedIndex(newSpriteIndex);
            } else {
                spriteList.setSelectedIndex(0);
            }

            Tool.setSelectedSpriteByIndex(spriteList.getSelectedIndex());

            gui.repaintView();

            changeListener.stateChanged(changeEvent);
        });

        btnReplace.addActionListener(e -> {
            if (spriteList.getSelectedIndex() != -1)  {
                SpritePrototype sprite = loadSprite();

                if (sprite != null) {
                    System.out.println(sprite.getFilename());
                }
            }
        });

        spriteList.addListSelectionListener(l -> {
            Tool.setSelectedSpriteByIndex(spriteList.getSelectedIndex());
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
    public void setMap(PK2Map map) {
        this.currentMap = map;

        listModel.clear();
        listModel.addAll(map.getSpriteList());

        if (map.player_sprite_index >= 0 && map.player_sprite_index < listModel.size()) {
            listModel.get(map.player_sprite_index).setPlayerSprite(true);
        }
    }

    @Override
    public void placed(int id) {
        spriteList.repaint();
    }

    @Override
    public void removed(int id) {
        spriteList.repaint();
    }

    @Override
    public void setSector(PK2MapSector sector) {
        SpriteListCellRenderer.setSector(sector);
    }

    private SpritePrototype loadSprite() {
        SpritePrototype sprite = null;

        var fc = new SpriteFileChooser(PK2FileSystem.getAssetsPath(PK2FileSystem.SPRITES_DIR));

        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                } else {
                    String name = f.getName();
                    return name.endsWith(".spr2") || name.toLowerCase().endsWith(".spr");
                }
            }

            @Override
            public String getDescription() {
                return "Pekka Kana 2 Sprite file (*.spr *.spr2)";
            }
        });

        var res = fc.showOpenDialog(this);

        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fc.getSelectedFile();
                sprite = SpriteIO.getSpriteReader(file).readSpriteFile(file);
            } catch (Exception spriteException) {
                Logger.error(spriteException);

                StringBuilder builder = new StringBuilder();
                builder.append(fc.getSelectedFile());
                builder.append(spriteException);

                JOptionPane.showMessageDialog(this, builder.toString(), "Cannot load sprite!", JOptionPane.ERROR_MESSAGE);
            }
        }

        return sprite;
    }
}
