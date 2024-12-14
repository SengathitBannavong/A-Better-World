package game.entity;

import game.Debug;
import game.GamePanel;
import game.enum_.F_Direction;
import game.enum_.F_Item_Name;
import game.enum_.F_Type_Sprite_Entity;
import game.graphic.Camera;
import game.graphic.Sprite;
import game.physic.AABB;
import game.physic.Vector2D;
import game.pool.IPoolable;
import game.state.GameState;

import java.awt.*;

public class Item extends Entity implements IPoolable {

    private boolean active;
    private F_Type_Sprite_Entity type;

    private Sprite[] setSpriteType(F_Type_Sprite_Entity type){
        Sprite[] sprite = new Sprite[F_Item_Name.SIZE.ordinal()];
        sprite[F_Item_Name.SHIELD.ordinal()] = new Sprite ("item/item_"+type.ordinal()+".png", 32, 32);
        sprite[F_Item_Name.POWER.ordinal()] = new Sprite ("item/item_"+type.ordinal()+".png", 32, 32);
        sprite[F_Item_Name.BLOOD.ordinal()] = new Sprite ("item/item_"+type.ordinal()+".png", 32, 32);
        return sprite;
    }


    public Item(Vector2D origin, int size, int sizeSprite, F_Type_Sprite_Entity type) {
        super(origin, size, sizeSprite);
        this.sprite = setSpriteType(type);
        active = false;
        this.type = type;
        super.setHitbox(origin, 32, 32);
        setAnimation(F_Direction.RIGHT, sprite[F_Item_Name.SHIELD.ordinal()].getSpriteArray(0), 10);
        setAnimation(F_Direction.RIGHT, sprite[F_Item_Name.POWER.ordinal()].getSpriteArray(0), 10);
    }

    @Override
    public void animate() {

    }


    @Override
    public void update() {
        //hitbound_update();
        super.update();
        setHitbox(origin, 32, 32);
    }

    @Override
    public void render(Graphics2D g) {
       // if(collected) return;

        int renderX = (int)(origin.x - Camera.getWorldX());
        int renderY = (int)(origin.y - Camera.getWorldY());
        int rendersize = size * GamePanel.Zoom;
        g.drawImage(ani.getImage(), renderX, renderY, rendersize, rendersize, null);
        if(Debug.debugging){
            renderDebug(g, renderX, renderY);
        }
    }

    public void renderDebug(Graphics2D g, int renderX, int renderY){
        g.setColor(Color.BLACK);
        int rendersize = size * GamePanel.Zoom;
        g.drawRect(renderX, renderY, rendersize, rendersize);
        hitbox.render(g);
        Sprite.drawArray(g, GameState.font, "X: " + (int)origin.x + " Y: " + (int)origin.y, new Vector2D(renderX, renderY - 10), 32, 32, 16, 0);
    }


    @Override
    public void activate() { active = true; }

    @Override
    public void deactivate() { active = false; }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setPos(Vector2D position) {
        this.origin = position;
    }

    public void setHitbox(Vector2D position) {
        setHitbox(position, 32, 32);
    }

    public AABB getHitbox() { return hitbox;}

    public void setType(F_Type_Sprite_Entity type) {
        this.sprite = setSpriteType(type);
        this.type = type;
    }

    public F_Type_Sprite_Entity getType() {
        return type;
    }
}
