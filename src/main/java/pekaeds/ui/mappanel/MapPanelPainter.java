package pekaeds.ui.mappanel;

import java.awt.*;
import java.awt.image.BufferedImage;

import pekaeds.data.Layer;
import pekaeds.pk2.level.PK2Level;
import pekaeds.pk2.level.PK2LevelSector;
import pekaeds.pk2.level.PK2TileArray;
import pekaeds.pk2.sprite.ISpritePrototype;
import pekaeds.settings.Settings;

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



                    g.drawImage(mapPanel.getModel().getSector().getBackgroundImage(),
                            mapPanel.getScrollPane().getHorizontalScrollBar().getValue() + (x * mapPanel.getModel().getSector().getBackgroundImage().getWidth()),
                            mapPanel.getScrollPane().getVerticalScrollBar().getValue() + (y * mapPanel.getModel().getSector().getBackgroundImage().getHeight()),
                            null);
                }
            }
        }
    }

    public void drawLayers(Graphics2D g) {
        if (mapPanel.getModel().getSector() != null) {
            int currentLayer = mapPanel.getModel().getSelectedLayer();

            PK2TileArray fgLayer = mapPanel.getModel().getSector().getFGLayer();
            BufferedImage fgImage = mapPanel.getModel().getSector().getTilesetImage();
            
            switch (currentLayer) {
                case Layer.FOREGROUND -> {
                    g.setComposite(compAlphaHalf);
                    drawBackgroundLayer(g);
    
                    g.setComposite(compAlphaFull);
                    drawLayer(g, fgLayer, fgImage);
                }
                
                case Layer.BACKGROUND -> {
                    g.setComposite(compAlphaHalf);
                    drawLayer(g, fgLayer, fgImage);
    
                    g.setComposite(compAlphaFull);
                    drawBackgroundLayer(g);
                }
                
                case Layer.BOTH -> {
                    g.setComposite(compAlphaFull);
                    drawBackgroundLayer(g);
    
                    g.setComposite(compAlphaFull);
                    drawLayer(g, fgLayer, fgImage);
                }
            }
        }
    }
    
    private void drawBackgroundLayer(Graphics2D g) {

        PK2TileArray layer = mapPanel.getModel().getSector().getBGLayer();

        if (mapPanel.getModel().getSector().getBackgroundTilesetImage() != null) {
            drawLayer(g, layer, mapPanel.getModel().getSector().getBackgroundTilesetImage());
        } else {
            drawLayer(g, layer, mapPanel.getModel().getSector().getTilesetImage());
        }
    }
    
    public void drawLayer(Graphics2D g, PK2TileArray layer, BufferedImage tileset) {
        // TODO Optimization: Should make these values available to the whole class, so only the sprites within the viewport can be drawn.
        int viewX = mapPanel.getViewport().getViewRect().x / 32;
        int viewY = mapPanel.getViewport().getViewRect().y / 32;
        int viewWidth = mapPanel.getViewport().getViewRect().width / 32;
        int viewHeight = mapPanel.getViewport().getViewRect().height / 32;
        
        for (int x = viewX; x <= viewX + viewWidth + 1; x++) {
            for (int y = viewY; y <= viewY + viewHeight + 1; y++) {
                if(x>=0 && y>=0 && x <layer.getWidth() && y<layer.getHeight()){
                    drawTile(g, x * 32, y * 32, layer.get(x, y), tileset);
                }               
            }
        }
    }
    
    // TODO Code reuse, clean this up
    // Create a method drawSprites(boolean backgroundOnly)
    public void drawBackgroundSprites(Graphics2D g) {
        if (mapPanel.getModel().getSector() != null) {

            PK2LevelSector sector = mapPanel.getModel().getSector();
            PK2Level level = mapPanel.getModel().getLevel();
    
            // TODO Don't use hard coded values
            for (int x = 0; x < sector.getWidth(); x++) {
                for (int y = 0; y < sector.getHeight(); y++) {
                    int spriteTile = sector.getSpriteTile(x, y);

                    if (spriteTile != 255 && spriteTile < level.getSpriteList().size()) {
                        var spr = level.getSprite(spriteTile);
                
                        if (spr.getType() == ISpritePrototype.TYPE_BACKGROUND) {
                            drawSprite(g, spr, x * 32, y * 32);
                        }
                    }
                }
            }
        }
    }
    
    public void drawForegroundSprites(Graphics2D g) {
        PK2LevelSector sector = mapPanel.getModel().getSector();
        PK2Level level = mapPanel.getModel().getLevel();
        if (sector != null) {
            //int[][] layerData = mapPanel.getModel().getSector().getSpritesLayer();
            
            // TODO Optimize: Only loop through viewportX + width && viewportY + height
            for (int x = 0; x < 256; x++) {
                for (int y = 0; y < 224; y++) {
                    int tile = sector.getSpriteTile(x, y);

                    if (tile != 255 && tile < level.getSpriteList().size()) {
                        var spr = level.getSprite(tile);
                
                        if (spr.getType() != ISpritePrototype.TYPE_BACKGROUND) {
                            drawSprite(g, spr, x * 32, y * 32);
                        }
                    }
                }
            }
        }
    }
    
    public void drawSprite(Graphics2D g, ISpritePrototype spr, int x, int y) {
        if (Settings.showSprites()) {
            g.drawImage(spr.getImage(), x - (spr.getFrameWidth() / 2) + 16, y - (spr.getFrameHeight() - 32), null);
        }
    }
    
    public void drawSpriteHighlights(Graphics2D g, Rectangle viewRect) {

        PK2LevelSector sector = mapPanel.getModel().getSector();

        for (int y = viewRect.y / 32; y < viewRect.height / 32; y++) {
            for (int x = viewRect.x / 32; x < viewRect.width / 32; x++) {
                if (sector.getSpriteTile(x, y) != 255) {
                    g.setColor(Color.WHITE);
                    g.drawRect(x * 32, y * 32, 32, 32);
                }
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

        PK2LevelSector sector = mapPanel.getModel().getSector();
        if (sector != null) {
            if (tile != 255) { // 255 is the empty tile
                if (x >= 0 && x <= sector.getWidth() * 32 && y >= 0 && y <= sector.getHeight() * 32) { // TODO Delete this check
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
        if (mapPanel.getModel().getSector().getBackgroundTilesetImage() != null && mapPanel.getModel().getSelectedLayer() == Layer.BACKGROUND) {
            drawTile(g, x, y, tile, mapPanel.getModel().getSector().getBackgroundTilesetImage());
        } else {
            drawTile(g, x, y, tile, mapPanel.getModel().getSector().getTilesetImage());
        }
    }

    public void setCursor(Cursor cursor) {
        mapPanel.setCursor(cursor);
    }
    
    public void repaint() {
        mapPanel.repaint();
    }
}
