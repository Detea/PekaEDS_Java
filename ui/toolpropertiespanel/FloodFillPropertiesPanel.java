package pekaeds.ui.toolpropertiespanel;

import net.miginfocom.swing.MigLayout;
import pekaeds.tool.Tool;
import pekaeds.tool.tools.FloodFillTool;

import javax.swing.*;

public class FloodFillPropertiesPanel extends JPanel implements ToolChangeListener {
    private JCheckBox cbFillEmptyPreview;
    private FloodFillTool tool;
    
    public FloodFillPropertiesPanel() {
        setup();
    }
    
    private void setup() {
        cbFillEmptyPreview = new JCheckBox("Fill empty tiles in preview?");
        
        setLayout(new MigLayout());
        add(cbFillEmptyPreview);
        
        cbFillEmptyPreview.addActionListener(e -> {
            tool.setFillEmptyTiles(cbFillEmptyPreview.isSelected());
        });
    }
    
    @Override
    public void setSelectedTool(Tool selectedTool) {
        if (selectedTool instanceof FloodFillTool) tool = (FloodFillTool) selectedTool;
        
        if (tool != null) cbFillEmptyPreview.setSelected(tool.fillEmptyTiles());
    }
}
