package game.pool;

import game.entity.Monster;

public interface EntityFactory<T,F> {
   F crateEntity(T entity);
}
