package pk.pekaeds.tool.tools;

import pk.pekaeds.tool.Tool;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public final class CutTool extends Tool {
    private boolean cutForegroundLayer, cutBackgroundLayer, cutSpritesLayer;
    private List<int[][]> layers = new ArrayList<int[][]>();

    @Override
    public void draw(Graphics2D g) {

    }

    public boolean cutForegroundLayer() {
        return cutForegroundLayer;
    }

    public void setCutForegroundLayer(boolean cutForegroundLayer) {
        this.cutForegroundLayer = cutForegroundLayer;
    }

    public boolean cutBackgroundLayer() {
        return cutBackgroundLayer;
    }

    public void setCutBackgroundLayer(boolean cutBackgroundLayer) {
        this.cutBackgroundLayer = cutBackgroundLayer;
    }

    public boolean ctSpritesLayer() {
        return cutSpritesLayer;
    }

    public void setCutSpritesLayer(boolean cutSpritesLayer) {
        this.cutSpritesLayer = cutSpritesLayer;
    }
}
