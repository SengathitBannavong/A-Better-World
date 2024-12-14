package game.pool;

import game.entity.Item;
import game.enum_.F_Type_Sprite_Entity;
import game.physic.Vector2D;
import game.state.PlayState;

import java.util.ArrayList;
import java.util.List;

public class ItemSpawner {

    public ItemSpawner() {
    }

    public void spawnItem(Vector2D position, F_Type_Sprite_Entity type) {
        Item item = ItemPool.instance.getItem(type);
        if(item != null) {
            item.activate();
            item.setPos(position);
            item.setHitbox(position);
            PlayState.addItem(item);
        }

    }

    public void despawnItem(List<Item> items) {
        for(Item item : new ArrayList<>(items)) {
            if(item.isActive()) {
                item.deactivate();
                ItemPool.instance.returnItem(item,item.getType());
                PlayState.removeItem(item);
                items.remove(item);
                items.remove(item);
            }
        }

    }
}
