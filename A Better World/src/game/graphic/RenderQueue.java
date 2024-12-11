
package game.graphic;

import game.entity.Entity;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RenderQueue {
    private final List<Entity> renderQueue;

    public RenderQueue() {
        renderQueue = new ArrayList<>();
    }

    public void addToQueue(Entity entity) {
        renderQueue.add(entity);
    }

    public void render(Graphics2D g) {
        // Sort entities by their y-coordinate to handle overlapping
        renderQueue.sort(Comparator.comparingDouble(e -> e.getOrigin().y));
        for (Entity entity : renderQueue) {
            entity.render(g);
        }
        renderQueue.clear();
    }
}