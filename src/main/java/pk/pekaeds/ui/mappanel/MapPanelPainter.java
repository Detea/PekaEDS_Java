package pk.pekaeds.ui.mappanel;

import pk.pekaeds.data.Layer;
import pk.pekaeds.pk2.map.PK2Map13;
import pk.pekaeds.pk2.sprite.PK2Sprite;
import pk.pekaeds.settings.Settings;

import java.awt.*;
import java.awt.image.BufferedImage;

// TODO Optimize drawing
public class MapPanelPainter {
    private MapPanel mapPanel;
    
    private final Composite compAlphaHalf = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
    private final Composite compAlphaFull = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
    
    public void setMapPanelModel(MapPanel mp) {
        this.mapPanel = mp;
    }

    public void drawBackground(Graphics2D g) {
        if (mapPanel.getModel().getBackgroundImage() != null) {
            for (int x = 0; x < mapPanel.getModel().getBgRepeatX(); x++) {
                for (int y = 0; y < mapPanel.getModel().getBgRepeatY(); y++) {
                    // Using the viewport's viewRect makes for jittery movement, need to use scrollbar values
                    g.drawImage(mapPanel.getModel().getMap().getBackgroundImage(),
                            mapPanel.getScrollPane().getHorizontalScrollBar().getValue() + (x * mapPanel.getModel().getMap().getBackgroundImage().getWidth()),
                            mapPanel.getScrollPane().getVerticalScrollBar().getValue() + (y * mapPanel.getModel().getMap().getBackgroundImage().getHeight()),
                            null);
                }
            }
        }
    }

    public void drawLayers(Graphics2D g) {
        if (mapPanel.getModel().getMap() != null) {
            int currentLayer = mapPanel.getModel().getSelectedLayer();
            
            switch (currentLayer) {
                case Layer.FOREGROUND -> {
                    g.setComposite(compAlphaHalf);
                    drawBackgroundLayer(g);
    
                    g.setComposite(compAlphaFull);
                    drawLayer(g, Layer.FOREGROUND, mapPanel.getModel().getMap().getTilesetImage());
                }
                
                case Layer.BACKGROUND -> {
                    g.setComposite(compAlphaHalf);
                    drawLayer(g, Layer.FOREGROUND, mapPanel.getModel().getMap().getTilesetImage());
    
                    g.setComposite(compAlphaFull);
                    drawBackgroundLayer(g);
                }
                
                case Layer.BOTH -> {
                    g.setComposite(compAlphaFull);
                    drawBackgroundLayer(g);
    
                    g.setComposite(compAlphaFull);
                    drawLayer(g, Layer.FOREGROUND, mapPanel.getModel().getMap().getTilesetImage());
                }
            }
        }
    }
    
    private void drawBackgroundLayer(Graphics2D g) {
        if (Settings.useBGTileset() && mapPanel.getModel().getMap().getBackgroundTilesetImage() != null) {
            drawLayer(g, Layer.BACKGROUND, mapPanel.getModel().getMap().getBackgroundTilesetImage());
        } else {
            drawLayer(g, Layer.BACKGROUND, mapPanel.getModel().getMap().getTilesetImage());
        }
    }
    
    public void drawLayer(Graphics2D g, int layerIndex, BufferedImage tileset) {
        // TODO Optimization: Should make these values available to the whole class, so only the sprites within the viewport can be drawn.
        int viewX = mapPanel.getViewport().getViewRect().x / 32;
        int viewY = mapPanel.getViewport().getViewRect().y / 32;
        int viewWidth = mapPanel.getViewport().getViewRect().width / 32;
        int viewHeight = mapPanel.getViewport().getViewRect().height / 32;
        
        for (int x = viewX; x <= viewX + viewWidth + 1; x++) {
            for (int y = viewY; y <= viewY + viewHeight + 1; y++) {
                drawTile(g, x * 32, y * 32, mapPanel.getModel().getMap().getTileAt(layerIndex, x, y), tileset);
            }
        }
    }
    
    // TODO Code reuse, clean this up
    // Create a method drawSprites(boolean backgroundOnly)
    public void drawBackgroundSprites(Graphics2D g) {
        if (mapPanel.getModel().getMap() != null) {
            int[][] layerData = mapPanel.getModel().getMap().getSpritesLayer();
    
            // TODO Don't use hard coded values
            for (int x = 0; x < 256; x++) {
                for (int y = 0; y < 224; y++) {
                    if (layerData[y][x] != 255 && layerData[y][x] < mapPanel.getModel().getMap().getSpriteList().size()) {
                        var spr = mapPanel.getModel().getMap().getSprite(layerData[y][x]);
                
                        if (spr.getType() == PK2Sprite.TYPE_BACKGROUND) {
                            drawSprite(g, spr, x * 32, y * 32);
                        }
                    }
                }
            }
        }
    }
    
    public void drawForegroundSprites(Graphics2D g) {
        if (mapPanel.getModel().getMap() != null) {
            int[][] layerData = mapPanel.getModel().getMap().getSpritesLayer();
    
            for (int x = 0; x < 256; x++) {
                for (int y = 0; y < 224; y++) {
                    if (layerData[y][x] != 255 && layerData[y][x] < mapPanel.getModel().getMap().getSpriteList().size()) {
                        var spr = mapPanel.getModel().getMap().getSprite(layerData[y][x]);
                
                        if (spr.getType() != PK2Sprite.TYPE_BACKGROUND) {
                            drawSprite(g, spr, x * 32, y * 32);
                        }
                    }
                }
            }
        }
    }
    
    public void drawSprite(Graphics2D g, PK2Sprite spr, int x, int y) {
        if (Settings.showSprites()) {
            g.drawImage(spr.getImage(), x - (spr.getFrameWidth() / 2) + 16, y - (spr.getFrameHeight() - 32), null);
    
            // TODO Fix background sprites rectangle being painted over by foreground tiles
            if (Settings.highlightSprites()) {
                g.setColor(Color.WHITE);
                g.drawRect(x, y, 32, 32);
            }
        }
    }
    
    /*
    Javadoc:
        https://docs.oracle.com/javase/7/docs/api/java/awt/Graphics.html#drawImage(java.awt.Image,%20int,%20int,%20int,%20int,%20int,%20int,%20int,%20int,%20java.awt.image.ImageObserver)
        
        img - the specified image to be drawn. This method does nothing if img is null.
        dx1 - the x coordinate of the first corner of the destination rectangle.
        dy1 - the y coordinate of the first corner of the destination rectangle.
        dx2 - the x coordinate of the second corner of the destination rectangle.
        dy2 - the y coordinate of the second corner of the destination rectangle.
        sx1 - the x coordinate of the first corner of the source rectangle.
        sy1 - the y coordinate of the first corner of the source rectangle.
        sx2 - the x coordinate of the second corner of the source rectangle.
        sy2 - the y coordinate of the second corner of the source rectangle.
        observer - object to be notified as more of the image is scaled and converted.
     */
    public void drawTile(Graphics2D g, int x, int y, int tile, BufferedImage tileset) {
        if (mapPanel.getModel().getMap() != null) {
            if (tile != 255) { // 255 is the empty tile
                if (x >= 0 && x <= PK2Map13.WIDTH * 32 && y >= 0 && y <= PK2Map13.HEIGHT * 32) { // TODO Delete this check
                    // Position of the tile in the tileset image
                    int tileX = (tile % 10) * 32;
                    int tileY = (tile / 10) * 32;

                    if (tileset != null) {
                        g.drawImage(tileset, x, y, x + 32, y + 32, tileX, tileY, tileX + 32, tileY + 32, null);
                    }
                }
            }
        }
    }
    
    // Method used in the Tool subclasses, they don't care which tileset is supposed to be used.
    public void drawTile(Graphics2D g, int x, int y, int tile) {
        if (Settings.useBGTileset() && mapPanel.getModel().getMap().getBackgroundTilesetImage() != null && mapPanel.getModel().getSelectedLayer() == Layer.BACKGROUND) {
            drawTile(g, x, y, tile, mapPanel.getModel().getMap().getBackgroundTilesetImage());
        } else {
            drawTile(g, x, y, tile, mapPanel.getModel().getMap().getTilesetImage());
        }
    }
}
