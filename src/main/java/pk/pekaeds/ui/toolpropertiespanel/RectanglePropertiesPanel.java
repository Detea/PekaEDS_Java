package pk.pekaeds.ui.toolpropertiespanel;

import net.miginfocom.swing.MigLayout;
import pk.pekaeds.tool.tools.RectangleTool;
import pk.pekaeds.tool.Tool;

import javax.swing.*;

public class RectanglePropertiesPanel extends JPanel implements ToolChangeListener {
    private JCheckBox cbFill;
    private RectangleTool tool;
    
    public RectanglePropertiesPanel() {
        setup();
    }
    
    public void setTool(Tool selectedTool) {
        tool = (RectangleTool) selectedTool;
    }
    
    // TODO Implement capping
    private void setup() {
        cbFill = new JCheckBox("Fill");
     
        setLayout(new MigLayout());
        add(cbFill, "cell 0 0");
        
        cbFill.addActionListener(e -> {
            tool.setFilled(cbFill.isSelected());
        });
    }
    
    @Override
    public void setSelectedTool(Tool selectedTool) {
        if (selectedTool instanceof RectangleTool) tool = (RectangleTool) selectedTool;
    }
}
