package pk.pekaeds.ui.misc;

import net.miginfocom.swing.MigLayout;
import org.tinylog.Logger;
import pk.pekaeds.util.LogOutputStream;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.PrintStream;

// TODO Delete this
public class LoggerDialog extends JDialog {
    private JTextArea textArea;
    private JButton btnCopyLog;

    public LoggerDialog() {
        setup();
    
        var ps = new PrintStream(new LogOutputStream());
        LogOutputStream.setTextArea(textArea);
        
        //System.setOut(ps);
        //System.setErr(ps);
        
        Logger.info("Writer set");
    }
    
    private void setup() {
        setTitle("Log");
        
        textArea = new JTextArea();
        
        btnCopyLog = new JButton("Copy");
        
        add(textArea, BorderLayout.CENTER);
        
        var btnPanel = new JPanel();
        btnPanel.setLayout(new MigLayout());
        btnPanel.add(new JPanel(), "width 100%");
        btnPanel.add(btnCopyLog);
        
        add(btnPanel, BorderLayout.SOUTH);
        
        btnCopyLog.addActionListener(e -> {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(textArea.getText()), null);
        });
        
        setSize(640, 480);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }
}
