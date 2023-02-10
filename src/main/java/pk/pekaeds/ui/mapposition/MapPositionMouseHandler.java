package pk.pekaeds.ui.mapposition;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MapPositionMouseHandler extends MouseAdapter {
    private final MapPositionDialog dialog;
    
    public MapPositionMouseHandler(MapPositionDialog d) {
        this.dialog = d;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        dialog.updatePosition(e.getPoint());
    }
}
