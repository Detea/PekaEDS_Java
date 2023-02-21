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

    // TODO I feel like this code could be cleaned up a bit, make it more readable
    public void drawLayers(Graphics2D g) {
        if (mapPanel.getModel().getMap() != null) {
            // Set alpha for background layer
            if (mapPanel.getModel().getSelectedLayer() == Layer.BACKGROUND || mapPanel.getModel().getSelectedLayer() == Layer.BOTH) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
            } else {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            }
            
            // Check whether to use background tileset or the foreground one
            if (Settings.useBGTileset() && mapPanel.getModel().getMap().getBackgroundTilesetImage() != null) {
                drawLayer(g, Layer.BACKGROUND, mapPanel.getModel().getMap().getBackgroundTilesetImage());
            } else {
                drawLayer(g, Layer.BACKGROUND, mapPanel.getModel().getMap().getTilesetImage());
            }
            
            if (mapPanel.getModel().getSelectedLayer() == Layer.FOREGROUND || mapPanel.getModel().getSelectedLayer() == Layer.BOTH) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
            } else {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            }
            
            drawLayer(g, Layer.FOREGROUND, mapPanel.getModel().getMap().getTilesetImage());
    
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        }
    }
    
    public void drawLayer(Graphics2D g, int layerIndex, BufferedImage tileset) {
        for (int x = mapPanel.getScrollPane().getHorizontalScrollBar().getValue() / 32; x < ((mapPanel.getScrollPane().getHorizontalScrollBar().getValue() + mapPanel.getModel().getViewRect().width) / 32) + 1; x++) {
            for (int y = mapPanel.getScrollPane().getVerticalScrollBar().getValue() / 32; y < ((mapPanel.getScrollPane().getVerticalScrollBar().getValue() + mapPanel.getModel().getViewRect().height) / 32) + 1; y++) {
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
        if (mapPanel.getModel().shouldShowSprites()) {
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
                if (x >= 0 && x < PK2Map13.WIDTH * 32 && y >= 0 && y < PK2Map13.HEIGHT * 32) {
                    // Position of the tile in the tileset image
                    int tileX = (tile % 10) * 32;
                    int tileY = (tile / 10) * 32;
    
                    g.drawImage(tileset, x, y, x + 32, y + 32, tileX, tileY, tileX + 32, tileY + 32, null);
                }
            }
        }
    }
    
    public void drawTile(Graphics2D g, int x, int y, int tile) {
        if (mapPanel.getModel().getMap() != null) {
            if (tile != 255) { // 255 is the empty tile
                if (x >= 0 && x <= PK2Map13.WIDTH * 32 && y >= 0 && y <= PK2Map13.HEIGHT * 32) {
                    // Position of the tile in the tileset image
                    int tileX = (tile % 10) * 32;
                    int tileY = (tile / 10) * 32;
                    
                    if (Settings.useBGTileset() && mapPanel.getModel().getMap().getBackgroundTilesetImage() != null && mapPanel.getModel().getSelectedLayer() == Layer.BACKGROUND) {
                        g.drawImage(mapPanel.getModel().getMap().getBackgroundTilesetImage(), x, y, x + 32, y + 32, tileX, tileY, tileX + 32, tileY + 32, null);
                    } else {
                        g.drawImage(mapPanel.getModel().getMap().getTilesetImage(), x, y, x + 32, y + 32, tileX, tileY, tileX + 32, tileY + 32, null);
                    }
                }
            }
        }
    }
}
