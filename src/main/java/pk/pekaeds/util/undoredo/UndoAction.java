package pk.pekaeds.util.undoredo;

import pk.pekaeds.pk2.map.PK2Map;

import java.util.Arrays;
import java.util.Objects;

public class UndoAction {
    private ActionType actionType;
    private int[][] oldData;
    private int[][] newData;
    
    private int affectedLayer;
    
    private int startX;
    private int startY;
    
    private int oldX, oldY;
    private int newX, newY;
    
    public UndoAction(ActionType type, int[][] oldData, int[][] newData, int layer, int startX, int startY) {
        this.actionType = type;
        this.oldData = oldData;
        this.newData = newData;
        
        this.affectedLayer = layer;
        
        this.startX = startX;
        this.startY = startY;
    }
    
    /**
     * Constructor for the cut tiles/sprites action type.
     *
     * startX and startY are in the scale of the tiles. That means that they should be between MAP_WIDTH (256) and MAP_HEIGHT (224).
     * @param type
     */
    public UndoAction(ActionType type, int[][] oldData, int layer, int startX, int startY) {
        this.actionType = type;
        
        this.oldData = oldData;
        this.affectedLayer = layer;
        this.startX = startX;
        this.startY = startY;
    }
    
    public void changeIntoRedo() {
        switch (actionType) {
            case UNDO_PLACE_TILE -> actionType = ActionType.REDO_PLACE_TILE;
            case UNDO_PLACE_SPRITE -> actionType = ActionType.REDO_PLACE_SPRITE;
            
            case UNDO_CUT_TILES -> actionType = ActionType.REDO_CUT_TILES;
            case UNDO_CUT_SPRITES -> actionType = ActionType.REDO_CUT_SPRITES;
        }
    }
    
    public void changeIntoUndo() {
        switch (actionType) {
            case REDO_PLACE_TILE -> actionType = ActionType.UNDO_PLACE_TILE;
            case REDO_PLACE_SPRITE -> actionType = ActionType.UNDO_PLACE_SPRITE;
            
            case REDO_CUT_TILES -> actionType = ActionType.UNDO_CUT_TILES;
            case REDO_CUT_SPRITES -> actionType = ActionType.UNDO_CUT_SPRITES;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        
        var other = (UndoAction) obj;
        if (actionType != other.actionType) {
            return false;
        }
        
        if (!Arrays.deepEquals(oldData, other.oldData)) {
            return false;
        }
        
        if (!Arrays.deepEquals(newData, other.newData)) {
            return false;
        }
        
        for (int i = 0; i < oldData.length; i++) {
            for (int j = 0; j < oldData[0].length; j++) {
                if (oldData[i][j] != other.oldData[i][j]) {
                    return false;
                }
            }
        }
        
        for (int i = 0; i < newData.length; i++) {
            for (int j = 0; j < newData[0].length; j++) {
                if (newData[i][j] != other.newData[i][j]) {
                    return false;
                }
            }
        }
    
        for (int i = 0; i < newData.length; i++) {
            for (int j = 0; j < newData[0].length; j++) {
                if (newData[i][j] != other.oldData[i][j]) {
                    return false;
                }
            }
        }
    
        for (int i = 0; i < oldData.length; i++) {
            for (int j = 0; j < oldData[0].length; j++) {
                if (oldData[i][j] != other.newData[i][j]) {
                    return false;
                }
            }
        }
        
        if (affectedLayer != other.affectedLayer) {
            return false;
        }
        
        if (startX != other.startX) {
            return false;
        }
        
        if (startY != other.startY) {
            return false;
        }
    
        if (!Arrays.deepEquals(oldData, other.newData)) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(actionType, affectedLayer, startX, startY);
        result = 31 * result + Arrays.deepHashCode(oldData);
        result = 31 * result + Arrays.deepHashCode(newData);
        return result;
    }
    
    @Override
    public String toString() {
        return "UndoAction{" +
                "actionType=" + actionType +
                ", oldData=" + Arrays.deepToString(oldData) +
                ", newData=" + Arrays.deepToString(newData) +
                ", affectedLayer=" + affectedLayer +
                ", startX=" + startX +
                ", startY=" + startY +
                '}';
    }
    
    public ActionType getType() {
        return actionType;
    }
    
    public int[][] getOldData() {
        return oldData;
    }
    
    public int[][] getNewData() {
        return newData;
    }
    
    public int getAffectedLayer() {
        return affectedLayer;
    }
    
    public int getStartX() {
        return startX;
    }
    
    public int getStartY() {
        return startY;
    }
    
    public int getAreaWidth() {
        return oldData[0].length;
    }
    
    public int getAreaHeight() {
        return oldData.length;
    }
}
