package game.pool;

public interface IPoolable {
    void activate();
    void deactivate();
    boolean isActive();
}
