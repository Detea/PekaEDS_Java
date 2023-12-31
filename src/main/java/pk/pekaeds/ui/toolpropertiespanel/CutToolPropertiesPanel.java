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

    private JLabel lblClickDrag;
    private JLabel lblClick;
    
    private JRadioButton rbCut, rbRemove;

    public CutToolPropertiesPanel() {
        setup();
    }

    private void setup() {
        cbForegroundLayer = new JCheckBox("Foreground layer");
        cbBackgroundLayer = new JCheckBox("Background layer");
        cbSpritesLayer = new JCheckBox("Sprites layer");

        lblClickDrag = new JLabel("Right click and drag to select");
        lblClick = new JLabel("Right click again to place selection");
        
        rbCut = new JRadioButton("Cut");
        rbRemove = new JRadioButton("Remove");

        //var rbGroup = new ButtonGroup();
        //rbGroup.add(rbCut);
        //rbGroup.add(rbRemove);

        setLayout(new MigLayout());

        //add(rbCut, "cell 0 0");
        //add(rbRemove, "cell 1 0");

        //add(new JSeparator(JSeparator.HORIZONTAL), "cell 0 1");

        add(cbForegroundLayer, "cell 0 2");
        add(cbBackgroundLayer, "cell 0 3");
        add(cbSpritesLayer, "cell 0 4");
        add(lblClickDrag, "cell 0 6");
        add(lblClick, "cell 0 7");

        setListeners();
    }

    private void setListeners() {
        cbForegroundLayer.addActionListener(e -> {
            tool.setCutForegroundLayer(cbForegroundLayer.isSelected());
        });

        cbBackgroundLayer.addActionListener(e -> {
            tool.setCutBackgroundLayer(cbBackgroundLayer.isSelected());
        });

        cbSpritesLayer.addActionListener(e -> {
            tool.setCutSpritesLayer(cbSpritesLayer.isSelected());
        });
    }

    @Override
    public void setSelectedTool(Tool selectedTool) {
        if (selectedTool instanceof CutTool) {
            this.tool = (CutTool) selectedTool;

            cbForegroundLayer.setSelected(tool.cutForegroundLayer());
            cbBackgroundLayer.setSelected(tool.cutBackgroundLayer());
            cbSpritesLayer.setSelected(tool.cutSpritesLayer());

            rbCut.setSelected(tool.cutSelection());
            rbRemove.setSelected(!tool.cutSelection());
        }
    }

}
