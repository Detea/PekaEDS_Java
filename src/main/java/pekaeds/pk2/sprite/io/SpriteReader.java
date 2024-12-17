package pekaeds.pk2.sprite.io;

import java.io.File;
import pekaeds.pk2.sprite.SpritePrototype;

public interface SpriteReader {
    public SpritePrototype readSpriteFile(File file) throws Exception;
}
