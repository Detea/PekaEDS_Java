package pk.pekaeds.ui.main;

import net.miginfocom.swing.MigLayout;
import pk.pekaeds.tool.*;
import pk.pekaeds.tool.tools.*;
import pk.pekaeds.ui.actions.SetSelectedToolAction;

import javax.swing.*;

/**
 * The tools toolbar in of the main UI.
 *
 * To add a new button do the following:
 * Add a JButton member to this class, name it btnYourTool.
 * Add it to the layout in setup().
 * Add a SetSelectedToolAction action in setActionListeners().
 */
public class ToolsToolBar extends JToolBar {
    private final PekaEDSGUI gui;
    
    private JToggleButton btnBrush;
    private JToggleButton btnEraser;
    private JToggleButton btnLine;
    private JToggleButton btnRect;
    private JToggleButton btnFloodFill;
    private JToggleButton btnCut;

    private ButtonGroup buttonGroup;

    public ToolsToolBar(PekaEDSGUI ui) {
        this.gui = ui;
        
        setup();
    }
    
    private void setup() {
        setOrientation(VERTICAL);
    
        btnBrush = new JToggleButton("Brush");
        btnEraser = new JToggleButton("Eraser");
        btnLine = new JToggleButton("Line");
        btnRect = new JToggleButton("Rect");
        btnFloodFill = new JToggleButton("Flood fill");
        btnCut = new JToggleButton("Cut");

        buttonGroup = new ButtonGroup();
        buttonGroup.add(btnBrush);
        buttonGroup.add(btnEraser);
        buttonGroup.add(btnLine);
        buttonGroup.add(btnRect);
        buttonGroup.add(btnFloodFill);
        buttonGroup.add(btnCut);

        setLayout(new MigLayout("flowy"));
        add(btnBrush);
        add(btnEraser);
        add(btnLine);
        add(btnRect);
        add(btnFloodFill);
        add(btnCut);

        setActionListeners();
    }
    
    private void setActionListeners() {
        btnBrush.addActionListener(new SetSelectedToolAction(gui, Tools.getTool(BrushTool.class)));
        btnEraser.addActionListener(new SetSelectedToolAction(gui, Tools.getTool(EraserTool.class)));
        btnLine.addActionListener(new SetSelectedToolAction(gui, Tools.getTool(LineTool.class)));
        btnRect.addActionListener(new SetSelectedToolAction(gui, Tools.getTool(RectangleTool.class)));
        btnFloodFill.addActionListener(new SetSelectedToolAction(gui, Tools.getTool(FloodFillTool.class)));
        btnCut.addActionListener(new SetSelectedToolAction(gui, Tools.getTool(CutTool.class)));
    }
}
