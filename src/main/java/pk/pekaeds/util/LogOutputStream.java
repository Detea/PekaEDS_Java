package pk.pekaeds.util;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

/*
    TODO Find a better solution... THIS IS ONLY TEMPORARY!!!

    This is an awful solution, redirecting all of System.out, I know.
    But creating a custom Tinylog writer, that outputs it's shit into a JTextArea is very annoying.
    And it doesn't output the log.txt file, when used with jpackage for whatever reason.
    
    So this hacky AF solution will have to do.
    
    Also, straight up stolen from:
    https://stackoverflow.com/questions/5107629/how-to-redirect-console-content-to-a-textarea-in-java
 */
public class LogOutputStream extends OutputStream {
    private static JTextArea textArea;
    
    public static void setTextArea(JTextArea ta) {
        textArea = ta;
    }
    
    @Override
    public void write(int b) throws IOException {
        textArea.setText(textArea.getText() + String.valueOf((char) b));
        textArea.setCaretPosition(textArea.getDocument().getLength());
        textArea.update(textArea.getGraphics()); // wtf?
    }
}
