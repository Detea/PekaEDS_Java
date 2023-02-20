package pk.pekaeds.ui.mappanel;

import pk.pekaeds.data.Layer;
import pk.pekaeds.data.MapData;
import pk.pekaeds.pk2.map.PK2Map13;
import pk.pekaeds.pk2.sprite.PK2Sprite;
import pk.pekaeds.pk2.sprite.PK2Sprite12;
import pk.pekaeds.settings.Settings;

import java.awt.*;
import java.awt.image.BufferedImage;

// TODO Optimize drawing
public class MapPanelPainter {
    private MapPanelModel model;
    
    public void setMapPanelModel(MapPanelModel mp) {
        this.model = mp;
    }

    public void drawBackground(Graphics2D g) {
        // TODO Optimization, don't redraw this every on repaint. Only repaint on resize.
        // TODO Use viewport position to only draw the background as many times as is necessary.
        if (model.getBackgroundImage() != null) {
            for (int x = 0; x < model.getBgRepeatX(); x++) {
                for (int y = 0; y < model.getBgRepeatY(); y++) {
                    g.drawImage(model.getMap().getBackgroundImage(), model.getViewRect().x + (x * model.getMap().getBackgroundImage().getWidth()), model.getViewRect().y + (y * model.getMap().getBackgroundImage().getHeight()), null);
                }
            }
        }
    }

    // TODO I feel like this code could be cleaned up a bit, make it more readable
    public void drawLayers(Graphics2D g) {
        if (model.getMap() != null) {
            if (model.getSelectedLayer() == Layer.BACKGROUND || model.getSelectedLayer() == Layer.BOTH) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
            } else {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            }
    
            if (Settings.useBGTileset() && model.getMap().getBackgroundTilesetImage() != null) {
                drawLayer(g, model.getMap().getLayers().get(Layer.BACKGROUND), model.getMap().getBackgroundTilesetImage());
            } else {
                drawLayer(g, model.getMap().getLayers().get(Layer.BACKGROUND), model.getMap().getTilesetImage());
            }
            
            if (model.getSelectedLayer() == Layer.FOREGROUND || model.getSelectedLayer() == Layer.BOTH) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
            } else {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            }
            
            drawLayer(g, model.getMap().getLayers().get(Layer.FOREGROUND), model.getMap().getTilesetImage());
        }
    }
    
    public void drawLayer(Graphics2D g, int[][] layer, BufferedImage tileset) {
        // TODO Only draw visible tiles
        for (int x = model.getViewRect().x / 32; x < (model.getViewRect().x + model.getViewRect().width) / 32; x++) {
            for (int y = model.getViewRect().y / 32; y < (model.getViewRect().y + model.getViewRect().height) / 32; y++) {
                drawTile(g, x * 32, y * 32, layer[y][x], tileset);
            }
        }
    }
    
    // TODO Code reuse, clean this up
    // Create a method drawSprites(boolean backgroundOnly)
    public void drawBackgroundSprites(Graphics2D g) {
        if (model.getMap() != null) {
            int[][] layerData = model.getMap().getSpritesLayer();
    
            // TODO Don't use hard coded values
            for (int x = 0; x < 256; x++) {
                for (int y = 0; y < 224; y++) {
                    if (layerData[y][x] != 255 && layerData[y][x] < model.getMap().getSpriteList().size()) {
                        var spr = model.getMap().getSprite(layerData[y][x]);
                
                        if (spr.getType() == PK2Sprite.TYPE_BACKGROUND) {
                            drawSprite(g, spr, x * 32, y * 32);
                        }
                    }
                }
            }
        }
    }
    
    public void drawForegroundSprites(Graphics2D g) {
        if (model.getMap() != null) {
            int[][] layerData = model.getMap().getSpritesLayer();
    
            for (int x = 0; x < 256; x++) {
                for (int y = 0; y < 224; y++) {
                    if (layerData[y][x] != 255 && layerData[y][x] < model.getMap().getSpriteList().size()) {
                        var spr = model.getMap().getSprite(layerData[y][x]);
                
                        if (spr.getType() != PK2Sprite.TYPE_BACKGROUND) {
                            drawSprite(g, spr, x * 32, y * 32);
                        }
                    }
                }
            }
        }
    }
    
    public void drawSprite(Graphics2D g, PK2Sprite spr, int x, int y) {
        g.drawImage(spr.getImage(), x - (spr.getFrameWidth() / 2) + 16, y - (spr.getFrameHeight() - 32), null);
        
        // TODO Fix background sprites rectangle being painted over by foreground tiles
        if (Settings.highlightSprites()) {
            g.setColor(Color.WHITE);
            g.drawRect(x, y, 32, 32);
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
        if (model.getMap() != null) {
            if (tile != 255) { // 255 is the empty tile
                if (x >= 0 && x <= PK2Map13.WIDTH * 32 && y >= 0 && y <= PK2Map13.HEIGHT * 32) {
                    // Position of the tile in the tileset image
                    int tileX = (tile % 10) * 32;
                    int tileY = (tile / 10) * 32;
    
                    g.drawImage(tileset, x, y, x + 32, y + 32, tileX, tileY, tileX + 32, tileY + 32, null);
                }
            }
        }
    }
    
    public void drawTile(Graphics2D g, int x, int y, int tile) {
        if (model.getMap() != null) {
            if (tile != 255) { // 255 is the empty tile
                if (x >= 0 && x <= PK2Map13.WIDTH * 32 && y >= 0 && y <= PK2Map13.HEIGHT * 32) {
                    // Position of the tile in the tileset image
                    int tileX = (tile % 10) * 32;
                    int tileY = (tile / 10) * 32;
                    
                    if (Settings.useBGTileset() && model.getMap().getBackgroundTilesetImage() != null && model.getSelectedLayer() == Layer.BACKGROUND) {
                        g.drawImage(model.getMap().getBackgroundTilesetImage(), x, y, x + 32, y + 32, tileX, tileY, tileX + 32, tileY + 32, null);
                    } else {
                        g.drawImage(model.getMap().getTilesetImage(), x, y, x + 32, y + 32, tileX, tileY, tileX + 32, tileY + 32, null);
                    }
                }
            }
        }
    }
}
