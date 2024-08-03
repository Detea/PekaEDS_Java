package pekaeds.pk2.level;

import java.awt.image.BufferedImage;

public class PK2LevelSector {

    public static final int CLASSIC_WIDTH = 256;
    public static final int CLASSIC_HEIGHT = 224;

    public BufferedImage tilesetImage;
    public BufferedImage tilesetBgImage;
    public BufferedImage backgroundImage;

    public String name;
    public String tilesetName;
    public String tilesetBgName;
    public String backgroundName;
    public String musicName;

    public int weather = 0;
    public int splash_color = -1; //based on the tileset
    public int fire_color_1 = 64;  //red
    public int fire_color_2 = 128; //orange
    
    public int background_scrolling = 0;

    protected PK2TileArray bgTiles;
    protected PK2TileArray fgTiles;
    protected PK2TileArray spriteTiles;

    private final int mWidth;
    private final int mHeight;


    public PK2LevelSector(int width, int height){
        this.mWidth = width;
        this.mHeight = height;

        this.bgTiles = new PK2TileArray(width, height);
        this.fgTiles = new PK2TileArray(width, height);
        this.spriteTiles = new PK2TileArray(width, height);
    }


    public int getWidth(){
        return this.mWidth;
    }

    public int getHeight(){
        return this.mHeight;
    }

    public int getBGTile(int posX, int posY){
        return this.bgTiles.get(posX, posY);
    };
    public int getFGTile(int posX, int posY){
        return this.fgTiles.get(posX, posY);
    }
    public int getSpriteTile(int posX, int posY){
        return this.spriteTiles.get(posX, posY);
    }

    public void setBGTile(int posX, int posY, int value){
        this.bgTiles.set(posX, posY, value);
    }
    public void setFGTile(int posX, int posY, int value){
        this.fgTiles.set(posX, posY, value);
        
    }
    public void setSpriteTile(int posX, int posY, int value){
        this.spriteTiles.set(posX, posY, value);
    }

    protected void removeSprite(int id){
        this.spriteTiles.removeID(id); 
    }
    
    public boolean isCorrectTilePos(int pos_x, int pos_y){
        return pos_x>=0 && pos_y>=0 &&
        pos_x<this.mWidth && pos_y<this.mHeight;
    }


    public String getTilesetName(){
        return this.tilesetName;
    }

    public String getBgTilesetName(){
        return this.tilesetBgName;
    }

    public String getBackgroundName(){
        return this.backgroundName;
    }
}
