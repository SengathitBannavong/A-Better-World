package game.pool;

import game.entity.Monster;
import game.entity.Player;

public class MonsterFactory implements EntityFactory<Player,Monster>{

    @Override
    public Monster crateEntity(Player entity) {
        return new Monster(entity);
    }
}
