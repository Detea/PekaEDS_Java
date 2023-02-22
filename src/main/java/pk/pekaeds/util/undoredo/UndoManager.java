package pk.pekaeds.util.undoredo;

import pk.pekaeds.pk2.map.PK2Map;

import java.util.Stack;

/**
 * This is the undo manager for the MapPanel. It registers the placed tiles and sprites and makes their placing reversible.
 *
 * It works by keeping track of "undo blocks".
 * You start an undo block by calling startUndoBlock() and end it by calling endUndoBlock().
 * This is best done, like in the MapPanelMouseHandler implementation, by putting each in the mousePressed() and mouseReleased() method respectively.
 * Then you only have to call addUndoAction once, like in the Tool classes placeTile() method, and you don't have to worry about multiple tiles that were placed while dragging.
 *
 * When the undo action happens it removes all the tiles/sprites placed during the undo block.
 *
 * An undo block consists of the following two elements:
 * - The start
 * - The end
 *
 * The start is the size of the undo stack when the block is started.
 * The end is the size of the undo stack when the block is ended.
 *
 * So if the undo action stack's size is 10 at the start and 20 at the end and that block is undone, the UndoActions between position 10 and 20 in the undo action stack are gonna be undone.
 * The size of the undo action stack increases when an undo action is pushed onto it. When a single tile or sprite has been placed or when a selection of multiple tiles have been placed at once.
 */
public final class UndoManager {
    private static final Stack<UndoAction> undoActionStack = new Stack<>();
    private static final Stack<UndoAction> redoActionStack = new Stack<>();
    
    private static final Stack<UndoBlock> undoBlockStack = new Stack<>();
    private static final Stack<UndoBlock> redoBlockStack = new Stack<>();
    
    private static PK2Map map;
    
    private UndoManager() {};
    
    public static void setMap(PK2Map m) {
        map = m;
    }
    
    // TODO Actions get added multiple times?
    public static void addUndoAction(UndoAction action) {
        if (!undoActionStack.contains(action)) {
            undoActionStack.push(action);
        }
    }
    
    public static void undoLastAction() {
        if (!undoActionStack.empty()) {
            var lastUndoBlock = undoBlockStack.pop();
            
            int redoStart = redoActionStack.size();
            
            for (int start = lastUndoBlock.start; start < lastUndoBlock.end; start++) {
                var lastUndo = undoActionStack.pop();
                
                performAction(lastUndo);
                
                lastUndo.changeIntoRedo();
                redoActionStack.push(lastUndo);
            }
            
            redoBlockStack.push(new UndoBlock(redoStart, redoActionStack.size()));
        }
    }
    
    public static void redoLastAction() {
        if (!redoActionStack.empty()) {
            var lastRedoBlock = redoBlockStack.pop();
    
            int undoStart = undoActionStack.size();
    
            for (int start = lastRedoBlock.start; start < lastRedoBlock.end; start++) {
                var lastRedo = redoActionStack.pop();
        
                performAction(lastRedo);
    
                lastRedo.changeIntoUndo();
                undoActionStack.push(lastRedo);
            }
    
            undoBlockStack.push(new UndoBlock(undoStart, undoActionStack.size()));
        }
    }
    
    private static void performAction(UndoAction action) {
        int[][] mapLayer = null;
    
        switch (action.getType()) {
            case UNDO_PLACE_TILE, REDO_PLACE_TILE -> mapLayer = map.getLayers().get(action.getAffectedLayer());
            case UNDO_PLACE_SPRITE, REDO_PLACE_SPRITE -> mapLayer = map.getSpritesLayer();
        }
        
        if (mapLayer != null) {
            for (int x = 0; x < action.getNewData()[0].length; x++) {
                for (int y = 0; y < action.getNewData().length; y++) {
                    int startX = action.getStartX();
                    int startY = action.getStartY();
                    
                    switch (action.getType()) {
                        case UNDO_PLACE_TILE, UNDO_PLACE_SPRITE -> mapLayer[startY + y][startX + x] = action.getOldData()[y][x];
                        case REDO_PLACE_TILE, REDO_PLACE_SPRITE -> mapLayer[startY + y][startX + x] = action.getNewData()[y][x];
                    }
                }
            }
        }
    }
    
    public static void clearRedoStack() {
        redoActionStack.clear();
        redoBlockStack.clear();
    }
    
    /*
        UndoBlock
     */
    private static int blockStart;
    public static void startUndoBlock() {
        blockStart = undoActionStack.size();
    }
    
    public static void endUndoBlock() {
        undoBlockStack.push(new UndoBlock(blockStart, undoActionStack.size()));
    }
    
    private static class UndoBlock {
        int start;
        int end;
        
        public UndoBlock(int s, int e) {
            start = s;
            end = e;
        }
    }
}