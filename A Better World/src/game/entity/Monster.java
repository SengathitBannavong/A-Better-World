package game.entity;

import game.GamePanel;
import game.enum_.F_Direction;
import game.enum_.F_List_Animation_Sprite;
import game.graphic.Camera;
import game.graphic.Sprite;
import game.physic.Vector2D;

import java.awt.*;

public class Monster extends Entity {

    private Sprite[] setDefaultSpite(){
        Sprite[] sprite = new Sprite[F_List_Animation_Sprite.SIZE.ordinal()];
        sprite[F_List_Animation_Sprite.Idle.ordinal()] = new Sprite("entity/Boss_idle_128_128_sprite.png", 128, 128);
        sprite[F_List_Animation_Sprite.Walking.ordinal()] = new Sprite("entity/Monster_idle_64_64_sprite.png", 64, 64);
        sprite[F_List_Animation_Sprite.Attack.ordinal()] = new Sprite("entity/Monster_idle_64_64_sprite.png", 64, 64);
        sprite[F_List_Animation_Sprite.Dead.ordinal()] = new Sprite("entity/Monster_idle_64_64_sprite.png", 64, 64);
        sprite[F_List_Animation_Sprite.Hurt.ordinal()] = new Sprite("entity/Monster_idle_64_64_sprite.png", 64, 64);
        sprite[F_List_Animation_Sprite.Dash.ordinal()] = new Sprite("entity/Monster_idle_64_64_sprite.png", 64, 64);
        return sprite;
    }

    public Monster(Vector2D origin, int size, int sizeSprite) {
        super(origin, size, sizeSprite);
        this.sprite = setDefaultSpite();
        setAnimation(F_Direction.RIGHT, sprite[F_List_Animation_Sprite.Idle.ordinal()].getSpriteArray(F_Direction.UP.ordinal()), 10);
    }

    public void update(){
       super.update();
    }

    @Override
    public void render(Graphics2D g) {
//        int renderX = (int)(origin.x - Camera.getWorldX());
//        int renderY = (int)(origin.y - Camera.getWorldY());
//        int rendersize = size * GamePanel.Zoom;
//        g.drawImage(ani.getImage(),renderX, renderY,rendersize,rendersize, null);
//        g.setColor(Color.YELLOW);
//        g.drawRect(renderX, renderY, rendersize, rendersize);
    }
}
