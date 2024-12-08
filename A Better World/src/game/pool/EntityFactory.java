package game.pool;

import game.entity.Monster;
import game.enum_.F_Type_Sprite_Entity;

public interface EntityFactory<T,F> {
   F createEntity(T entity, F_Type_Sprite_Entity type);
}
