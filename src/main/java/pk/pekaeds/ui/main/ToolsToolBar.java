package pk.pekaeds.ui.main;

import net.miginfocom.swing.MigLayout;
import pk.pekaeds.tools.*;
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
    
    private JButton btnBrush;
    private JButton btnEraser;
    private JButton btnLine;
    private JButton btnRect;
    
    public ToolsToolBar(PekaEDSGUI ui) {
        this.gui = ui;
        
        setup();
    }
    
    private void setup() {
        setOrientation(VERTICAL);
    
        btnBrush = new JButton("Brush");
        btnEraser = new JButton("Eraser");
        btnLine = new JButton("Line");
        btnRect = new JButton("Rect");
        
        setLayout(new MigLayout("flowy"));
        add(btnBrush);
        add(btnEraser);
        add(btnLine);
        add(btnRect);
        
        setActionListeners();
    }
    
    private void setActionListeners() {
        btnBrush.addActionListener(new SetSelectedToolAction(gui, Tools.getTool(BrushTool.class)));
        btnEraser.addActionListener(new SetSelectedToolAction(gui, Tools.getTool(EraserTool.class)));
        btnLine.addActionListener(new SetSelectedToolAction(gui, Tools.getTool(LineTool.class)));
        btnRect.addActionListener(new SetSelectedToolAction(gui, Tools.getTool(RectangleTool.class)));
    }
}
