package game.pool;

import game.entity.Monster;
import game.entity.NPC;
import game.enum_.F_Type_Sprite_Entity;
import game.physic.Vector2D;

public class NPCFactory implements EntityFactory< Vector2D, NPC>{

    @Override
    public NPC createEntity(Vector2D position, F_Type_Sprite_Entity type) {
        return new NPC(position,64,64,type);
    }
}
