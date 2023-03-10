package pk.pekaeds.ui.toolpropertiespanel;

import net.miginfocom.swing.MigLayout;
import pk.pekaeds.tool.Tool;
import pk.pekaeds.tool.tools.CutTool;

import javax.swing.*;

public class CutToolPropertiesPanel extends JPanel implements ToolChangeListener {
    private CutTool tool;

    private JCheckBox cbForegroundLayer;
    private JCheckBox cbBackgroundLayer;
    private JCheckBox cbSpritesLayer;

    private JCheckBox cbCutSelection;

    public CutToolPropertiesPanel() {
        setup();
    }

    private void setup() {
        cbForegroundLayer = new JCheckBox("Foreground layer");
        cbBackgroundLayer = new JCheckBox("Background layer");
        cbSpritesLayer = new JCheckBox("Sprites layer");

        cbCutSelection = new JCheckBox("Cut");

        setLayout(new MigLayout());

        add(cbForegroundLayer, "cell 0 1");
        add(cbBackgroundLayer, "cell 0 2");
        add(cbSpritesLayer, "cell 0 3");
        add(new JSeparator(), "cell 0 4");
        add(cbCutSelection, "cell 0 5");
    }

    @Override
    public void setSelectedTool(Tool selectedTool) {
        if (selectedTool instanceof CutTool) {
            this.tool = (CutTool) selectedTool;

            cbForegroundLayer.setSelected(tool.cutForegroundLayer());
            cbBackgroundLayer.setSelected(tool.cutBackgroundLayer());
            cbSpritesLayer.setSelected(tool.cutSpritesLayer());

            cbCutSelection.setSelected(tool.cutSelection());
        } else {
            System.out.println(selectedTool.getClass());
        }
    }

}
