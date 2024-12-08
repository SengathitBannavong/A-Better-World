package game.object;

import game.physic.AABB;
import game.physic.Vector2D;

import java.awt.*;

public abstract class Rectangle {
    protected AABB aabb;
    protected Vector2D position;
    protected int width;
    protected int height;

    public Rectangle(Vector2D position, int width, int height) {
        this.position = position;
        aabb = new AABB(position, width, height);
    }

    public abstract void render(Graphics2D g);

    public AABB getAABB() {
        return aabb;
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public void setWidth(float width) {
        aabb.setWidth(width);
    }
}
