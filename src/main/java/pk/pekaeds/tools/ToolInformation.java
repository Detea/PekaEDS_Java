package pk.pekaeds.tools;

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
    
    private int foregroundTile;
    private int backgroundTile;
    
    private int spriteId;
    private String spriteFilename;
    
    public ToolInformation() {
    }
    
    public int getX() {
        return x;
    }
    
    void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    void setY(int y) {
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
