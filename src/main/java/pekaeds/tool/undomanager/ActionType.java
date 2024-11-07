package pekaeds.tool.undomanager;

public enum ActionType {
    // TODO These two aren't necessary, remove them
    UNDO,
    REDO,
    
    PLACE_TILE,
    PLACE_SPRITE,
    
    CUT_TOOL_CUT_FOREGROUND,
    CUT_TOOL_CUT_BACKGROUND,
    CUT_TOOL_CUT_SPRITES,
    
    CUT_TOOL_PLACE_FOREGROUND,
    CUT_TOOL_PLACE_BACKGROUND,
    CUT_TOOL_PLACE_SPRITES,
}
