package game.pool;

import game.entity.Monster;
import game.entity.Player;
import game.enum_.F_Type_Sprite_Entity;

public class MonsterFactory implements EntityFactory<Player,Monster>{

    @Override
    public Monster createEntity(Player entity, F_Type_Sprite_Entity type) {
        return new Monster(entity,type);
    }
}
