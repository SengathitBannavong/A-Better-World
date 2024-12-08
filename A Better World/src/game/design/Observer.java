package game.design;

import game.entity.Player;

public interface Observer<T> {
    void update(T t);
}
