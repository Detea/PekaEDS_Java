package pekaeds.pk2.sprite.old;

import java.awt.image.BufferedImage;

import pk2.sprite.Prototype;

public class SpritePrototypeEDS implements ISpritePrototypeEDS{

    protected boolean is_player_srite = false;
    protected int placedAmount = 0;
    protected BufferedImage image;

    private final Prototype prototype;

    public SpritePrototypeEDS(Prototype spritePrototype){
        this.prototype = spritePrototype;
    }

    public int getType(){
        return this.prototype.getType();
    }

    public String getFilename(){
        return this.prototype.getFilename();
    }
    public String getName(){
        return this.prototype.getName();
    }

    public int getWidth(){
        return this.prototype.getWidth();
    }

    public int getHeight(){
        return this.prototype.getHeight();
    }

    public String getTextureName(){
        return this.prototype.getTextureName();
    }

    public int getColor(){
        return this.prototype.getColor();
    }
    public int getFrameX(){
        return this.prototype.getFrameX();
    }

    public int getFrameY(){
        return this.prototype.getFrameY();
    }
    public int getFrameWidth(){
        return this.prototype.getFrameWidth();
    }
    public int getFrameHeight(){
        return this.prototype.getFrameHeight();
    }


    public void setPlayerSprite(boolean is){
        this.is_player_srite = is;
    }

    public boolean isPlayerSprite(){
        return this.is_player_srite;
    }


    public int getPlacedAmount() {
        return placedAmount;
    }
    
    public void increasePlacedAmount() {
        placedAmount++;
    }
    
    public void decreasePlacedAmount() {
        if (placedAmount - 1 >= 0) placedAmount--;
    }

    public void setImage(BufferedImage img) {
        this.image = img;
    }
    
    public BufferedImage getImage() {
        return image;
    }
    
}
