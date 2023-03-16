package pk.pekaeds.util.undoredo;

public enum ActionType {
    UNDO_PLACE_TILE,
    REDO_PLACE_TILE,
    
    UNDO_PLACE_SPRITE,
    REDO_PLACE_SPRITE,
    
    UNDO_CUT_TILES,
    REDO_CUT_TILES,
    
    UNDO_CUT_SPRITES,
    REDO_CUT_SPRITES,
    
    UNDO_CUT_TOOL_CUT,
    UNDO_CUT_TOOL_PLACEMENT,
    
    UNDO_MOVE_CUT_SELECTION,
    REDO_MOVE_CUT_SELECTION
}
