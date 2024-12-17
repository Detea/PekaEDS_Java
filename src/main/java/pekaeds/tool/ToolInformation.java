package pekaeds.tool;

/**
 * This class contains the following information:
 *      - The current x and y position of the mouse.
 *      - The tile id at the current mouse position on the foreground layer
 *      - The tile id at the current mouse position on the background layer
 *      - The sprite id and sprite filename at the current mouse position.
 */
public class ToolInformation {
    private int x;
    private int y;
    private int tileX;
    private int tileY;
    
    private int foregroundTile;
    private int backgroundTile;
    
    private int spriteId;
    private String spriteFilename;
    
    public ToolInformation() {
    }
    
    public int getX() {
        return this.x;
    }

    public int getTileX(){
        return this.tileX;
    }
    
    void setX(int x) {
        this.tileX = x / 32;
        this.x = x;
    }
    
    public int getY() {
        return this.y;
    }

    public int getTileY(){
        return this.tileY;
    }
    
    void setY(int y) {
        this.tileY = y / 32;
        this.y = y;
    }
    
    public int getForegroundTile() {
        return foregroundTile;
    }
    
    void setForegroundTile(int foregroundTile) {
        this.foregroundTile = foregroundTile;
    }
    
    public int getBackgroundTile() {
        return backgroundTile;
    }
    
    void setBackgroundTile(int backgroundTile) {
        this.backgroundTile = backgroundTile;
    }
    
    public int getSpriteId() {
        return spriteId;
    }
    
    void setSpriteId(int spriteId) {
        this.spriteId = spriteId;
    }
    
    public String getSpriteFilename() {
        return spriteFilename;
    }
    
    void setSpriteFilename(String spriteFilename) {
        this.spriteFilename = spriteFilename;
    }
}
