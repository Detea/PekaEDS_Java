package pekaeds.pk2.sprite;

import java.awt.image.BufferedImage;

import pk2.sprite.IPrototype;

public interface ISpritePrototypeEDS extends IPrototype{
    public static final int TYPE_BACKGROUND = 5;
    public static final int TYPE_CHARACTER = 1;

    public void setPlayerSprite(boolean is);
    public boolean isPlayerSprite();
    public BufferedImage getImage();
    public void setImage(BufferedImage img);

    public int getPlacedAmount();
    public void increasePlacedAmount();
    public void decreasePlacedAmount();
}
