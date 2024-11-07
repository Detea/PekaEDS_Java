package pekaeds.ui.mapmetadatapanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class MapIconRenderer extends DefaultListCellRenderer {
    private Map<Object, BufferedImage> icons;
    
    public MapIconRenderer(Map<Object, BufferedImage> iconMap) {
        this.icons = iconMap;
    }
    
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    
        Icon icon = new ImageIcon(icons.get(value));
   
        label.setIcon(icon);
    
        return label;
    }
}
