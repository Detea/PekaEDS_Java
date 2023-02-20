package pk.pekaeds.ui.spritelistpanel;

import net.miginfocom.swing.MigLayout;
import org.tinylog.Logger;
import pk.pekaeds.pk2.sprite.PK2Sprite;
import pk.pekaeds.settings.Settings;
import pk.pekaeds.ui.misc.ImagePanel;

import javax.swing.*;
import java.awt.*;

public class SpriteListCellRenderer extends JPanel implements ListCellRenderer<PK2Sprite> {
    private PK2Sprite sprite;
    private ImagePanel imgPanel;
    private JLabel spriteName;
    private JLabel filename;
    
    private JLabel spriteTypeLbl;
    private JLabel isPlayerLbl;
    
    public SpriteListCellRenderer() {
        setup();
    }
    
    public void setSprite(PK2Sprite spr) {
        this.sprite = spr;
        
        setValues();
    }
    
    public void setSelected(boolean selected) {
        if (selected) {
            setBackground(UIManager.getColor("List.selectionBackground"));
            setForeground(UIManager.getColor("List.selectionForeground"));
        } else {
            setBackground(UIManager.getColor("List.background"));
            setForeground(UIManager.getColor("List.foreground"));
        }
    }
    
    private void setup() {
        setLayout(new MigLayout());
        
        imgPanel = new ImagePanel(64, 64);
        
        spriteName = new JLabel();
        filename = new JLabel();
        
        spriteTypeLbl = new JLabel();
        isPlayerLbl = new JLabel();
        
        add(imgPanel, "gapleft 5, gaptop 5, gapbottom 5, gapright 5, dock west");
    
        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new MigLayout());
        dataPanel.setOpaque(false);
        
        dataPanel.add(spriteName, "cell 0 0");
        dataPanel.add(filename, "cell 1 0");
        
        dataPanel.add(spriteTypeLbl, "cell 0 1");
        dataPanel.add(isPlayerLbl, "cell 1 1");
        
        add(dataPanel, "dock center");
        
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
    }
    
    private void setValues() {
        imgPanel.setImage(sprite.getImage(), true, 64, 64);

        spriteName.setText(sprite.getName());
        filename.setText("(" + sprite.getFilename() + ")");
        
        spriteTypeLbl.setText(Settings.getSpriteProfile().getTypes().get(sprite.getType() - 1));

        if (sprite.isPlayerSprite()) {
            isPlayerLbl.setText("(Player)");
        } else {
            isPlayerLbl.setText("");
        }
    }
    
    @Override
    public Component getListCellRendererComponent(JList<? extends PK2Sprite> list, PK2Sprite value, int index, boolean isSelected, boolean cellHasFocus) {
        setSprite(value);
        setSelected(isSelected);
        
        return this;
    }
}
