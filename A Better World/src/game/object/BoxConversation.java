package game.object;

import game.design.Observarable;
import game.design.Observer;
import game.entity.NPC;
import game.physic.Vector2D;

import java.awt.*;

public class BoxConversation extends Rectangle implements Observer<NPC> {


    private boolean isShow = false;
    private boolean isEnd = false;

    public BoxConversation(Vector2D position, int width, int height) {
        super(position, width, height);
    }

    @Override
    public void render(Graphics2D g) {
        if(isShow && !isEnd){


        }
    }

    @Override
    public void update(NPC npc) {

    }
}
