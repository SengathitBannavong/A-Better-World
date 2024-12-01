package game.object;

import game.Debug;
import game.physic.Vector2D;

import java.awt.*;

public class BoxTP extends Rectangle{

    private Vector2D tp_to_position;
    private int index_tp_to_map;
    private int index_map;


    public BoxTP(Vector2D position, int width, int height,int index_map,int index_tp_to_map,Vector2D tp_to_position) {
        super(position, width, height);
        this.tp_to_position = tp_to_position;
        this.index_tp_to_map = index_tp_to_map;
        this.index_map = index_map;
        aabb.setxOffset(-16);
        aabb.setyOffset(-16);
    }

    @Override
    public void render(Graphics2D g) {
        if(Debug.debugging) {
            aabb.render(g);
        }
    }

    public Vector2D getTp_to_position() {
        return tp_to_position;
    }

    public int getIndex_tp_to_map() {
        return index_tp_to_map;
    }

    public int getIndex_map() {
        return index_map;
    }
}
