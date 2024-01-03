package pekaeds.pk2.map;

import java.io.File;
import java.io.IOException;

public interface PK2MapWriter {
    void write(PK2Map map, File filename) throws IOException;
}
