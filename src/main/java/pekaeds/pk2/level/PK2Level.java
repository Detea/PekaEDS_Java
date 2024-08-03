package pekaeds.pk2.level;

import java.util.ArrayList;
import java.util.List;

import pekaeds.pk2.sprite.ISpritePrototype;

public class PK2Level {
    public List<PK2LevelSector> sectors = new ArrayList<>();

    public List<String> spriteNames = new ArrayList<>();

    protected List<ISpritePrototype> sprites = new ArrayList<>();

    public String name;
    public String author;

    public int      level_number          = 0;                            // level of the episode
    public int      time           = 0;                            // time (in (dec)conds)
    public int      extra          = 0;                            // extra config - not used

    public int      player_sprite_index = 0;                            // player prototype

    public int      icon_x = 0;                                         // icon x pos
	public int      icon_y = 0;                                         // icon x pos
    public int      icon_id = 0;                                        // icon id

    public String lua_script = "main.lua";                        // lua script
    public int game_mode = 0;                                          // game mode

    public List<ISpritePrototype> getSpriteList(){
        return this.sprites;
    }
}
