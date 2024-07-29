package pekaeds.pk2.sprite.io;

import java.io.File;
import pekaeds.pk2.sprite.ISpritePrototype;

public interface ISpriteReader {
    public ISpritePrototype readSpriteFile(File file) throws Exception;
}
