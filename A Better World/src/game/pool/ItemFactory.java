package game.pool;

import game.entity.Item;
import game.enum_.F_Type_Sprite_Entity;
import game.physic.Vector2D;

public class ItemFactory implements EntityFactory< Vector2D, Item>{

    @Override
    public Item createEntity(Vector2D position, F_Type_Sprite_Entity type) {
        //return new Item(position, 64, 64, ItemPool.getNameByType(type));
        return new Item(position, 32, 32, type);
    }
}
