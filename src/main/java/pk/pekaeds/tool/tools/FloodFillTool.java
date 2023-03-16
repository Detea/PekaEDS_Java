package pk.pekaeds.tool.tools;

import pk.pekaeds.data.Layer;
import pk.pekaeds.pk2.map.PK2Map13;
import pk.pekaeds.tool.Tool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// TODO Optimize this class
public final class FloodFillTool extends Tool {
    // https://lodev.org/cgtutor/floodfill.html#Scanline_Floodfill_Algorithm_With_Stack

    private final List<int[][]> previewLayers = new ArrayList<>();
    
    private boolean fillEmptyTiles = false;
    
    @Override
    public void mouseMoved(MouseEvent e) {
        int mx = e.getX() / 32;
        int my = e.getY() / 32;
        int layer = selectedLayer == Layer.BOTH ? Layer.FOREGROUND : selectedLayer;

        resetPreview();
        fillPreview(mx, my, selection.getTileSelection()[0][0], previewLayers.get(layer)[my][mx]);
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            SwingUtilities.invokeLater(() -> {
                fill(e.getX() / 32, e.getY() / 32, selection.getTileSelection()[0][0], map.getTileAt(selectedLayer, e.getX() / 32, e.getY() / 32));
            });
        }
    }
    
    @Override
    public void draw(Graphics2D g) {
        if (!previewLayers.isEmpty()) {
            for (int y = (viewRect.y / 32); y < ((viewRect.y + viewRect.height) / 32) + 1; y++) {
                for (int x = viewRect.x / 32; x < ((viewRect.x + viewRect.width) / 32) + 1; x++) {
                    if (x < PK2Map13.WIDTH && y < PK2Map13.HEIGHT) {
                        switch (selectedLayer) {
                            case Layer.BOTH, Layer.FOREGROUND -> {
                                if (previewLayers.get(Layer.FOREGROUND)[y][x] != 255) getMapPanelPainter().drawTile(g, x * 32, y * 32, previewLayers.get(Layer.FOREGROUND)[y][x]);
                            }
        
                            case Layer.BACKGROUND -> {
                                if (previewLayers.get(Layer.BACKGROUND)[y][x] != 255) getMapPanelPainter().drawTile(g, x * 32, y * 32, previewLayers.get(Layer.BACKGROUND)[y][x]);
                            }
                        }
                    }
                }
            }
        }
    }
    
    // TODO Undo
    //https://lodev.org/cgtutor/floodfill.html#Recursive_Scanline_Floodfill_Algorithm
    private void fill(int x, int y, int newTile, int oldTile) {
        if (oldTile == newTile) return;
        if (map.getTileAt(selectedLayer, x, y) != oldTile) return;

        int x1 = x;
        
        while (x1 < PK2Map13.WIDTH && map.getTileAt(selectedLayer, x1, y) == oldTile) {
            layerHandler.placeTile(x1 * 32, y * 32, newTile, selectedLayer, false); // TODO Find a better way to handle placeTiles. Multiplying by 32 everytime is unnecessary
            
            x1++;
        }
        
        x1 = x - 1;
        while (x1 >= 0 && map.getTileAt(selectedLayer, x1, y) == oldTile) {
            layerHandler.placeTile(x1 * 32, y * 32, newTile, selectedLayer, false);
            
            x1--;
        }
        
        // test for scanlines above
        x1 = x;
        
        while (x1 < PK2Map13.WIDTH && map.getTileAt(selectedLayer, x1, y) == newTile) {
            if (y > 0 && map.getTileAt(selectedLayer, x1, y - 1) == oldTile) {
                fill(x1, y - 1, newTile, oldTile);
            }
            
            x1++;
        }
        
        x1 = x - 1;
        while (x1 >= 0 && map.getTileAt(selectedLayer, x1, y) == newTile) {
            if (y > 0 && map.getTileAt(selectedLayer, x1, y - 1) == oldTile) {
                fill(x1, y - 1, newTile, oldTile);
            }
            
            x1--;
        }
        
        // test for new scanlines below
        x1 = x;
        while (x1 < PK2Map13.WIDTH && map.getTileAt(selectedLayer, x1, y) == newTile) {
            if (y < PK2Map13.HEIGHT - 1 && map.getTileAt(selectedLayer, x1, y + 1) == oldTile) {
                fill(x1, y + 1, newTile, oldTile);
            }
            
            x1++;
        }
        
        x1 = x - 1;
        while (x1 >= 0 && map.getTileAt(selectedLayer, x1, y) == newTile) {
            if (y < PK2Map13.HEIGHT - 1 && map.getTileAt(selectedLayer, x1, y + 1) == oldTile) {
                fill(x1, y + 1, newTile, oldTile);
            }
            
            x1--;
        }
    }
    
    private void fillPreview(int x, int y, int newTile, int oldTile) {
        int layer = selectedLayer == Layer.BOTH ? Layer.FOREGROUND : selectedLayer;
        
        if (oldTile == newTile) return;
        if (previewLayers.get(layer)[y][x] != oldTile) return;
        if (!fillEmptyTiles && oldTile == 255) return;
        
        int x1 = x;
        
        while (x1 < PK2Map13.WIDTH && previewLayers.get(layer)[y][x1] == oldTile) {
            previewLayers.get(layer)[y][x1] = newTile;
            
            x1++;
        }
        
        x1 = x - 1;
        while (x1 >= 0 && previewLayers.get(layer)[y][x1] == oldTile) {
            previewLayers.get(layer)[y][x1] = newTile;
            
            x1--;
        }
        
        // test for scanlines above
        x1 = x;
        
        while (x1 < PK2Map13.WIDTH && previewLayers.get(layer)[y][x1] == newTile) {
            if (y > 0 && previewLayers.get(layer)[y - 1][x1] == oldTile) {
                fillPreview(x1, y - 1, newTile, oldTile);
            }
            
            x1++;
        }
        
        x1 = x - 1;
        while (x1 >= 0 && previewLayers.get(layer)[y][x1] == newTile) {
            if (y > 0 && previewLayers.get(layer)[y - 1][x1] == oldTile) {
                fillPreview(x1, y - 1, newTile, oldTile);
            }
            
            x1--;
        }
        
        // test for new scanlines below
        x1 = x;
        while (x1 < PK2Map13.WIDTH && previewLayers.get(layer)[y][x1] == newTile) {
            if (y < PK2Map13.HEIGHT - 1 && previewLayers.get(layer)[y + 1][x1] == oldTile) {
                fillPreview(x1, y + 1, newTile, oldTile);
            }
            
            x1++;
        }
        
        x1 = x - 1;
        while (x1 >= 0 && previewLayers.get(layer)[y][x1] == newTile) {
            if (y < PK2Map13.HEIGHT - 1 && previewLayers.get(layer)[y + 1][x1] == oldTile) {
                fillPreview(x1, y + 1, newTile, oldTile);
            }
            
            x1--;
        }
    }
    
    private void resetPreview() {
        for (int y = 0; y < PK2Map13.HEIGHT; y++) {
            for (int x = 0; x < PK2Map13.WIDTH; x++) {
                previewLayers.get(Layer.FOREGROUND)[y][x] = map.getLayers().get(Layer.FOREGROUND)[y][x];
                previewLayers.get(Layer.BACKGROUND)[y][x] = map.getLayers().get(Layer.BACKGROUND)[y][x];
            }
        }
    }
    
    @Override
    public void onSelect() {
        if (previewLayers.isEmpty()) {
            previewLayers.add(new int[PK2Map13.HEIGHT][PK2Map13.WIDTH]);
            previewLayers.add(new int[PK2Map13.HEIGHT][PK2Map13.WIDTH]);
        }
        
        for (int y = 0; y < PK2Map13.HEIGHT; y++) {
            for (int x = 0; x < PK2Map13.WIDTH; x++) {
                previewLayers.get(Layer.FOREGROUND)[y][x] = map.getLayers().get(Layer.FOREGROUND)[y][x];
                previewLayers.get(Layer.BACKGROUND)[y][x] = map.getLayers().get(Layer.BACKGROUND)[y][x];
            }
        }
    }
    
    @Override
    public void onDeselect() {
        // Assign null to layer arrays, so that they get cleaned up?
        // OR create new arrays size [0][0]?
    }
    
    public void setFillEmptyTiles(boolean fill) {
        this.fillEmptyTiles = fill;
    }
    
    public boolean fillEmptyTiles() {
        return fillEmptyTiles;
    }
}
