package game.object;

import game.design.Observer;
import game.entity.NPC;
import game.physic.Vector2D;


import java.awt.*;

import java.util.ArrayList;
import java.util.List;

public class BoxConversation extends Rectangle implements Observer<NPC> {

    private final List<String> dialogQueue;
    private boolean isShow = false;
    private boolean isEnd = false;

    public BoxConversation(Vector2D position, int width, int height) {
        super(position, width, height);
        dialogQueue = new ArrayList<>();
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
