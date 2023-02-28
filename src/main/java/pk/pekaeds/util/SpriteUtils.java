package pk.pekaeds.util;

import pk.pekaeds.pk2.sprite.PK2Sprite;

import java.util.List;

public final class SpriteUtils {
    private SpriteUtils() {}
    
    /**
     * Calculates the amount of times each sprite in spriteList has been placed on the map.
     * @param layer The sprites layer
     * @param spriteList The list of PK2Sprites, used in the map
     */
    public static void calcutePlacementAmountForSprites(int[][] layer, List<PK2Sprite> spriteList) {
        for (int x = 0; x < layer[0].length; x++) {
            for (int y = 0; y < layer.length; y++) {
                int sprite = layer[y][x];
                if (sprite != 255) { // If the id in the layer is 255 that means there is no sprite
                    // Check if the sprite is contained in the list, just to be safe
                    if (sprite >= 0 && sprite < spriteList.size()) {
                        spriteList.get(sprite).increasePlacedAmount();
                    }
                }
            }
        }
    }
}
