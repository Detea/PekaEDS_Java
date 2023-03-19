package pk.pekaeds.tool.undomanager;

import pk.pekaeds.tool.Tool;

import java.util.Arrays;

public class UndoAction {
    private Tool tool;
    private int x, y;
    private int[][] newTiles;
    private int[][] oldTiles;
    private int layer;
    private ActionType type;
    private ActionType undoType;
    
    public UndoAction(Tool tool, ActionType undoType, ActionType actionType, int x, int y, int[][] newTile, int[][] oldTile, int layer) {
        this.tool = tool;
        
        this.undoType = undoType;
        this.type = actionType;
        
        this.x = x;
        this.y = y;
        
        this.newTiles = newTile;
        this.oldTiles = oldTile;
        
        this.layer = layer;
    }
    
    // Constructor for sprites
    public UndoAction(Tool tool, ActionType undoType, ActionType actionType, int x, int y, int[][] newTile, int[][] oldTile) {
        this.tool = tool;
        
        this.undoType = undoType;
        this.type = actionType;
        
        this.x = x;
        this.y = y;
        
        this.newTiles = newTile;
        this.oldTiles = oldTile;
    }
    
    public void changeIntoRedo() {
        undoType = ActionType.REDO;
    }
    
    public void changeIntoUndo() {
        undoType = ActionType.UNDO;
    }
    
    @Override
    public String toString() {
        return "UndoAction{" +
                "tool=" + tool +
                ", x=" + x +
                ", y=" + y +
                ", newTiles=" + Arrays.deepToString(newTiles) +
                ", oldTiles=" + Arrays.deepToString(oldTiles) +
                ", layer=" + layer +
                ", type=" + type +
                '}';
    }
    
    public Tool getTool() {
        return tool;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int[][] getNewTiles() {
        return newTiles;
    }
    
    public int[][] getOldTiles() {
        return oldTiles;
    }
    
    public int getLayer() {
        return layer;
    }
    
    public ActionType getType() {
        return type;
    }
    
    public ActionType getUndoType() { return undoType; }
}
