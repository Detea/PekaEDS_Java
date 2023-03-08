package pk.pekaeds.tool;

import pk.pekaeds.data.Layer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class ToolSelection {
    private Point start;
    private Point end;

    /**
     * List containing the tile layers.
     *
     * Foreground = 0
     * Background = 1
     */
    private int[][] tileSelection;
    private int[][] spritesSelection;

    private int width;
    private int height;

    public void reset() {
        tileSelection = new int[1][1];

        spritesSelection = new int[1][1];

        width = 1;
        height = 1;
    }

    public void setTileSelection(int[][] selection) {
        tileSelection = selection;

        setDimensions(selection);
    }

    public int[][] getTileSelection() {
        return tileSelection;
    }

    public void setSelectionSprites(int[][] selection) {
        this.spritesSelection = selection;

        setDimensions(selection);
    }

    public int[][] getTileSelection(int layer) {
        return tileSelection;
    }

    public int[][] getSelectionSprites() {
        return spritesSelection;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int w) { this.width = w; }
    public void setHeight(int h) { this.height = h;}

    public void setStart(Point p) {
        this.start = p;
    }

    public Point getStart() {
        return start;
    }

    public void setEnd(Point p) {
        this.end = p;
    }

    public Point getEnd() {
        return end;
    }

    private void setDimensions(int[][] selection) {
        width = selection[0].length;
        height = selection.length;
    }
}
