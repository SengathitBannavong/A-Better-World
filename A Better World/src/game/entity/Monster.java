package game.entity;

import game.Debug;
import game.GamePanel;
import game.design.Observer;
import game.enum_.F_Direction;
import game.enum_.F_List_Animation_Sprite;
import game.enum_.F_Type_Sprite_Entity;
import game.graphic.Camera;
import game.graphic.Sprite;
import game.physic.Vector2D;
import game.pool.IPoolable;
import game.state.GameState;
import game.state.PlayState;

import java.awt.*;

public class Monster extends Entity implements IPoolable, Observer<PlayState> {

    protected boolean active;
    private static int count  = 0;
    private int id;

    private MonsterAI ai;
    private int damageTaken;
    private boolean damageTakenGate = false;
    // y offset for animation falling down
    private int yOffset = 0;

    private Sprite[] setDefaultSpite(){
        Sprite[] sprite = new Sprite[F_List_Animation_Sprite.SIZE.ordinal()];
        sprite[F_List_Animation_Sprite.Idle.ordinal()] = new Sprite("entity/Monster1_idle_264_264_sprite.png", 264, 264);
        sprite[F_List_Animation_Sprite.Walking.ordinal()] = new Sprite("entity/Monster1_walking_264_264_sprite.png", 264, 264);
        sprite[F_List_Animation_Sprite.Attack.ordinal()] = new Sprite("entity/Monster1_attack_264_264_sprite.png", 264, 264);
        sprite[F_List_Animation_Sprite.Dead.ordinal()] = new Sprite("entity/Monster_idle_64_64_sprite.png", 64, 64);
        sprite[F_List_Animation_Sprite.Hurt.ordinal()] = new Sprite("entity/Monster_idle_64_64_sprite.png", 64, 64);
        sprite[F_List_Animation_Sprite.Dash.ordinal()] = new Sprite("entity/Monster_idle_64_64_sprite.png", 64, 64);
        return sprite;
    }

    private Sprite[] setSpriteType(F_Type_Sprite_Entity type){
        Sprite[] sprite = new Sprite[F_List_Animation_Sprite.SIZE.ordinal()];
        sprite[F_List_Animation_Sprite.Idle.ordinal()] = new Sprite("entity/Monster_idle"+type.ordinal()+"_64_64_sprite.png", 64, 64);
        sprite[F_List_Animation_Sprite.Walking.ordinal()] = new Sprite("entity/Monster_walking"+type.ordinal()+"_64_64_sprite.png", 64, 64);
        sprite[F_List_Animation_Sprite.Attack.ordinal()] = new Sprite("entity/Monster_attack"+type.ordinal()+"_64_64_sprite.png", 64, 64);
        sprite[F_List_Animation_Sprite.Dead.ordinal()] = new Sprite("entity/Monster_death"+type.ordinal()+"_64_64_sprite.png", 64, 64);
        sprite[F_List_Animation_Sprite.Hurt.ordinal()] = new Sprite("entity/Monster_hurt"+type.ordinal()+"_64_64_sprite.png", 64, 64);
        return sprite;
    }

    public Monster(Player player, F_Type_Sprite_Entity type){
        System.out.println("Monster created");
        super(new Vector2D(0,0), 64, 64);
        setAcc(1.5f);
        setDe_acc(1.5f);
        this.sprite = setDefaultSpite();
        active = false;
        setHitbox(origin, 64, 64);
        setAnimation(F_Direction.RIGHT, sprite[F_List_Animation_Sprite.Idle.ordinal()].getSpriteArray(F_Direction.LEFT.ordinal()), 10);
        id = count++;
        ai = new MonsterAI(this, player);
        PlayState.getInstance().addObserver(this);
        Init();
    }

    public Monster(Vector2D origin, int size, int sizeSprite) {
        super(origin, size, sizeSprite);
        this.sprite = setDefaultSpite();
        active = false;
        setAnimation(F_Direction.RIGHT, sprite[F_List_Animation_Sprite.Idle.ordinal()].getSpriteArray(F_Direction.UP.ordinal()), 10);
    }

    private void Init(){
        maxHp = 3;
        hp = maxHp;
        maxDamage = 2;
        damage = 1;
    }

    public void takeDamage(int damage){
        if(immortality) return;
        hp -= damage;
        damageTaken = damage;
        damageTakenGate = true;
        System.out.println("Monster taken damage:"+damage+"current hp is:" + hp);
        if(hp <= 0){
            deactivate();
        }
        immortality = true;
        countdownImmortality = timeCountdownImmortality;
    }

    public void InitHp(){
        hp = maxHp;
    }

    public void update(){
       ai.update();
       setDirectionMovement();
       super.update();
       hitbounds_update();
    }

    public void hitbounds_update(){
       setHitbox(origin, 64, 64);
    }

    public void setDirectionMovement(){
        Vector2D set;
        set = new Vector2D(dx, dy);
        set = set.normalize().multiply(acc);
        Vector2D temp = origin.add(set);
        origin = temp;
    }

    @Override
    public void render(Graphics2D g) {
        int renderX = (int)(origin.x - Camera.getWorldX());
        int renderY = (int)(origin.y - Camera.getWorldY());
        if(damageTakenGate){
            Sprite.drawArray(g, GameState.font, "-"+damageTaken, new Vector2D(renderX + 10, renderY +5 + yOffset++), 32, 32, 16, 0);
            if(yOffset > 10){
                yOffset = 0;
                damageTakenGate = false;
            }
        }
        if(!active) return;

        int rendersize = size * GamePanel.Zoom;
        g.drawImage(ani.getImage(),renderX, renderY,rendersize,rendersize, null);
        // render damage taken


        if(immortality){
            Sprite.drawArray(g, GameState.font, "Important", new Vector2D(renderX, renderY - 10), 16, 16, 16, 0);
        }
        if(Debug.debugging){
            renderDebug(g, renderX, renderY);
            ai.render(g);
        }

    }

    public void renderDebug(Graphics2D g, int renderX, int renderY){
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

    public void setPos(Vector2D pos){
        origin = pos;
    }

    public void setHitbox(Vector2D position){
       hitbox.setPosition(position);
    }

    public int getId(){
        return id;
    }

    @Override
    public void updateListener(PlayState playState) {
        if(countdownImmortality > 0) {
            countdownImmortality--;
            if(countdownImmortality <= 0){
                immortality = false;
            }
        }
    }
}
