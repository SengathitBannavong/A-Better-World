package game.entity;

import game.Debug;
import game.GamePanel;
import game.enum_.F_Direction;
import game.enum_.F_List_Animation_Sprite;
import game.enum_.F_Type_Sprite_Entity;
import game.graphic.Camera;
import game.graphic.Sprite;
import game.physic.Vector2D;
import game.pool.IPoolable;
import game.state.GameState;
import game.state.GameStateManager;

import java.awt.*;

public class NPC extends Entity implements IPoolable {

    private boolean active;
    private F_Type_Sprite_Entity type;
    private boolean CanTalk = false;
    private String path_conversation = null;

    private Sprite[] setDefaultSpite(){
        Sprite[] sprite = new Sprite[F_List_Animation_Sprite.SIZE.ordinal()];
        sprite[F_List_Animation_Sprite.Idle.ordinal()] = new Sprite("player/Player_idle_64_64_sprite.png", 64, 64);
        sprite[F_List_Animation_Sprite.Walking.ordinal()] = new Sprite("player/Player_walking_64_64_sprite.png", 64, 64);
        sprite[F_List_Animation_Sprite.Attack.ordinal()] = new Sprite("player/Player_attack_64_64_sprite.png", 64, 64);
        sprite[F_List_Animation_Sprite.Dead.ordinal()] = new Sprite("player/Player_death_64_64_sprite.png", 64, 64);
        sprite[F_List_Animation_Sprite.Hurt.ordinal()] = new Sprite("player/Player_hurt_64_64_sprite.png", 64, 64);
        sprite[F_List_Animation_Sprite.Dash.ordinal()] = new Sprite("player/Player_dash_64_64_sprite.png", 64, 64);
        return sprite;
    }

    private Sprite[] setSpriteType(F_Type_Sprite_Entity type){
        Sprite[] sprite = new Sprite[F_List_Animation_Sprite.SIZE.ordinal()];
        sprite[F_List_Animation_Sprite.Idle.ordinal()] = new Sprite("entity/NPC"+type.ordinal()+"_idle_48_48_sprite.png", 48, 48);
        sprite[F_List_Animation_Sprite.Walking.ordinal()] = new Sprite("entity/NPC"+type.ordinal()+"_walking_48_48_sprite.png", 48, 48);
        sprite[F_List_Animation_Sprite.Attack.ordinal()] = new Sprite("entity/NPC"+type.ordinal()+"_idle_48_48_sprite.png", 48, 48);
        sprite[F_List_Animation_Sprite.Dead.ordinal()] = new Sprite("entity/NPC"+type.ordinal()+"_idle_48_48_sprite.png", 48, 48);
        sprite[F_List_Animation_Sprite.Hurt.ordinal()] = new Sprite("entity/NPC"+type.ordinal()+"_idle_48_48_sprite.png", 48, 48);
        return sprite;
    }

    public NPC(Vector2D origin, int width, int height,F_Type_Sprite_Entity type) {
        super(origin, width, height);
        this.sprite = setSpriteType(type);
        setAnimation(F_Direction.RIGHT, sprite[F_List_Animation_Sprite.Idle.ordinal()].getSpriteArray(F_Direction.DOWN.ordinal()), 10);
        super.setHitbox(origin, width, height);
        this.type = type;
        active = false;
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void render(Graphics2D g) {
        int renderX = (int)(origin.x - Camera.getWorldX());
        int renderY = (int)(origin.y - Camera.getWorldY());
        int rendersize = size * GamePanel.Zoom;
        g.drawImage(ani.getImage(),renderX, renderY,rendersize,rendersize, null);
        if(Debug.debugging){
            renderDebug(g, renderX, renderY);
        }
    }

    public void renderDebug(Graphics2D g, int renderX, int renderY) {
        g.setColor(Color.YELLOW);
        int rendersize = size * GamePanel.Zoom;
        g.drawRect(renderX, renderY, rendersize, rendersize);
        hitbox.render(g);
        Sprite.drawArray(g, GameState.font, "X: " + (int)origin.x + " Y: " + (int)origin.y, new Vector2D(renderX, renderY - 10), 32, 32, 16, 0);
    }

    @Override
    public void activate() {
        active = true;
    }

    @Override
    public void deactivate() {
        active = false;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setPos(Vector2D position) {
        this.origin = position;
    }

    public void setHitbox(Vector2D position) {
        setHitbox(position, 64, 64);
    }

    public void setType(F_Type_Sprite_Entity type) {
        this.sprite = setSpriteType(type);
        this.type = type;
    }

    public F_Type_Sprite_Entity getType() {
        return type;
    }

    public void setCanTalk(boolean CanTalk) {
        this.CanTalk = CanTalk;
        if(CanTalk) {
            System.out.println("NPC Can talk: " + CanTalk);
        }
    }

    public boolean getCanTalk() {
        return CanTalk;
    }

    public void setPathConversation(String path_conversation) {
        this.path_conversation = path_conversation;
        if(!path_conversation.isEmpty()) {
            System.out.println("NPC Path conversation: " + path_conversation);
        }
    }

    public String getPathConversation() {
        return path_conversation;
    }

    public void Talking() {
        if(path_conversation == null || path_conversation.isEmpty()) {
            System.err.println("Error: NPC conversation path is not set. + " + path_conversation);
            return;
        }
        GameStateManager.setBufferState(path_conversation);
    }
}
