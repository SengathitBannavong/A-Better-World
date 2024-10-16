package game.object;

import game.physic.Vector2D;

import java.awt.Graphics2D;
import java.awt.Color;

public class Box extends Rectangle {
    public Box(Vector2D position, int width, int height) {
        super(position, width, height);
        aabb.setxOffset(-16);
        aabb.setyOffset(-16);
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.BLUE);
        g.fillRect((int) position.getWorldVar().x , (int) position.getWorldVar().y, aabb.getSize(), aabb.getSize());
        g.setColor(Color.BLACK);
        aabb.render(g);
    }
}
