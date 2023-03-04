package pk.pekaeds.pk2.map;

import pk.pekaeds.data.Layer;
import pk.pekaeds.util.file.PK2FileUtils;
import pk.pekaeds.util.TileUtils;

import java.io.*;

public class PK2MapWriter13 implements PK2MapWriter {
    @Override
    public void write(PK2Map map, File filename) throws IOException {
        if (filename != null) {
            try (var out = new DataOutputStream(new FileOutputStream(filename))) {
                for (var id : PK2Map13.ID) {
                    out.writeByte(id);
                }
        
                PK2FileUtils.writeString(out, map.getTileset(), 13);
                PK2FileUtils.writeString(out, map.getBackground(), 13);
                PK2FileUtils.writeString(out, map.getMusic(), 13);
        
                PK2FileUtils.writeString(out, map.getName(), 40);
                PK2FileUtils.writeString(out, map.getAuthor(), 40);
        
                PK2FileUtils.writeInt(out, map.getLevelNumber());
                PK2FileUtils.writeInt(out, map.getWeatherType());
        
                // Not used
                byte[] switchTimes = {'2', '0', '0', '0', 0, 0, 0, 0};
                out.write(switchTimes); // Switch value 1
                out.write(switchTimes); // 2
                out.write(switchTimes); // 3
        
                PK2FileUtils.writeInt(out, map.getTime());
        
                // "Extra", this value is not used.
                byte[] emptyPK2Int = {'0', 0, 0, 0, 0, 0, 0, 0};
                out.write(emptyPK2Int);
        
                PK2FileUtils.writeInt(out, map.getScrollType());
        
                PK2FileUtils.writeInt(out, map.getPlayerSpriteId());
        
                PK2FileUtils.writeInt(out, map.getMapX());
                PK2FileUtils.writeInt(out, map.getMapY());
                PK2FileUtils.writeInt(out, map.getIcon());
        
                PK2FileUtils.writeInt(out, map.getSpriteFilenames().size());
                for (int i = 0; i < map.getSpriteFilenames().size(); i++) {
                    PK2FileUtils.writeString(out, map.getSpriteFilenames().get(i), 13);
                }
        
                var usedLayerSpace = TileUtils.calculateUsedLayerSpace(map.getLayers().get(Layer.BACKGROUND));
                writeLayer(out, map.getLayers().get(Layer.BACKGROUND), usedLayerSpace.x, usedLayerSpace.y, usedLayerSpace.width, usedLayerSpace.height);

                usedLayerSpace = TileUtils.calculateUsedLayerSpace(map.getLayers().get(Layer.FOREGROUND));
                writeLayer(out, map.getLayers().get(Layer.FOREGROUND), usedLayerSpace.x, usedLayerSpace.y, usedLayerSpace.width, usedLayerSpace.height);
                
                usedLayerSpace = TileUtils.calculateUsedLayerSpace(map.getSpritesLayer());
                writeLayer(out, map.getSpritesLayer(), usedLayerSpace.x, usedLayerSpace.y, usedLayerSpace.width, usedLayerSpace.height);
                
                out.flush();
            }
        }
    }
    
    private void writeLayer(DataOutputStream out, int[][] layer, int startX, int startY, int width, int height) throws IOException {
        PK2FileUtils.writeInt(out, startX);
        PK2FileUtils.writeInt(out, startY);
        PK2FileUtils.writeInt(out, width);
        PK2FileUtils.writeInt(out, height);
    
        for (int y = startY; y <= startY + height; y++) {
            for (int x = startX; x <= startX + width; x++) {
                if (x < PK2Map13.WIDTH && y < PK2Map13.HEIGHT) {
                    out.writeByte(layer[y][x]);
                }
            }
        }
    }
}
