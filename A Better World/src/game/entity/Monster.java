package game.entity;

import game.Debug;
import game.GamePanel;
import game.design.DemageFrame;
import game.design.Observer;
import game.enum_.F_Direction;
import game.enum_.F_List_Animation_Sprite;
import game.enum_.F_Statue_Animate;
import game.enum_.F_Type_Sprite_Entity;
import game.event.EventManager;
import game.graphic.Camera;
import game.graphic.Sprite;
import game.physic.AABB;
import game.physic.Vector2D;
import game.pool.IPoolable;
import game.state.GameState;
import game.state.GameStateManager;
import game.state.PlayState;

import java.awt.*;
import java.util.Objects;

import static game.enum_.F_Statue_Animate.Attack;

public class Monster extends Entity implements IPoolable, Observer<PlayState>, DemageFrame {

    protected boolean active;
    private static int count  = 0;
    private int id;

    private MonsterAI ai;
    private int damageTaken;
    private boolean damageTakenGate = false;
    // y offset for animation falling down
    private int yOffset = 0;
    // sword hitbox
    private AABB monsterSwordHitbox;

    // sight of light
    private AABB monsterSightofLight;
    // type
    private F_Type_Sprite_Entity type ;

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
        sprite[F_List_Animation_Sprite.Idle.ordinal()] = new Sprite("entity/Monster"+type.ordinal()+"_idle_264_264_sprite.png", 264, 264);
        sprite[F_List_Animation_Sprite.Walking.ordinal()] = new Sprite("entity/Monster"+type.ordinal()+"_walking_264_264_sprite.png", 264, 264);
        sprite[F_List_Animation_Sprite.Attack.ordinal()] = new Sprite("entity/Monster"+type.ordinal()+"_attack_264_264_sprite.png", 264, 264);
        sprite[F_List_Animation_Sprite.Dead.ordinal()] = new Sprite("entity/Monster"+type.ordinal()+"_idle_264_264_sprite.png", 264, 264);
        sprite[F_List_Animation_Sprite.Hurt.ordinal()] = new Sprite("entity/Monster"+type.ordinal()+"_idle_264_264_sprite.png", 264, 264);
        sprite[F_List_Animation_Sprite.Dash.ordinal()] = new Sprite("entity/Monster"+type.ordinal()+"_idle_264_264_sprite.png", 264, 264);
        return sprite;
    }

    private Sprite[] setBossSprite(){
        Sprite[] sprite = new Sprite[F_List_Animation_Sprite.SIZE.ordinal()];
        sprite[F_List_Animation_Sprite.Idle.ordinal()] = new Sprite("entity/Boss_idle_128_128_sprite.png", 128, 128);
        sprite[F_List_Animation_Sprite.Walking.ordinal()] = new Sprite("entity/Boss_idle_128_128_sprite.png", 128, 128);
        sprite[F_List_Animation_Sprite.Attack.ordinal()] = new Sprite("entity/Boss_attack_128_128_sprite.png", 128, 128);
        sprite[F_List_Animation_Sprite.Dead.ordinal()] = new Sprite("entity/Boss_dead_128_128_sprite.png", 128, 128);
        sprite[F_List_Animation_Sprite.Hurt.ordinal()] = new Sprite("entity/Boss_hurt_128_128_sprite.png", 128, 128);
        return sprite;
    }

    public Monster(Player player, F_Type_Sprite_Entity type){
        System.out.println("Monster created");
        int size = 64;
        if(type == F_Type_Sprite_Entity.Type3){
            size = 192;
        }
        super(new Vector2D(0,0), size, size);
        this.type = type;
        setAcc(1.5f);
        setDe_acc(1.5f);
        if(type == F_Type_Sprite_Entity.Type3) {
            this.sprite = setBossSprite();
            BossInit();
        }else{
            this.sprite = setSpriteType(type);
            Init();
        }
        active = false;
        setHitbox(origin, size, size);
        setAnimation(F_Direction.RIGHT, sprite[F_List_Animation_Sprite.Idle.ordinal()].getSpriteArray(F_Direction.LEFT.ordinal()), 10);
        id = count++;
        ai = new MonsterAI(this, player);
        PlayState.getInstance().addObserver(this);
        monsterSwordHitbox = new AABB(origin.add(new Vector2D(4,4)),(sizeSprite /2) * GamePanel.Zoom  , (sizeSprite / 2) * GamePanel.Zoom );
        monsterSightofLight = new AABB(origin.add(new Vector2D(4,4)),(sizeSprite) * GamePanel.Zoom  , (sizeSprite) * GamePanel.Zoom );
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
        setAttackSpeed(2);
    }

    private void BossInit(){
        maxHp = 30;
        hp = maxHp;
        maxDamage = 5;
        damage = 3;
        setAttackSpeed(2);
    }

    public void takeDamage(int damage){
        if(immortality) return;
        hp -= damage;
        damageTaken = damage;
        damageTakenGate = true;
        System.out.println("Monster taken damage:"+damage+"current hp is:" + hp);
        if(hp <= 0){
            if(type == F_Type_Sprite_Entity.Type3) {
                setAnimation(F_Direction.UP, sprite[F_List_Animation_Sprite.Dead.ordinal()].getSpriteArray(F_Direction.UP.ordinal()), 20);
                return;
            }
            deactivate();
        }
        immortality = true;
        countdownImmortality = timeCountdownImmortality;
    }

    public void InitHp(){
        hp = maxHp;
    }

    public void update(){
        if(GameStateManager.GameEnding){
            super.update();
            if(ani.getFrame() == 5){
                EventManager.triggerEvent("LastWordOfBoss");
            }
            if(ani.getFrame() == 6){
                deactivate();
            }
            return;
        }
       ai.update();
       setDirectionMovement();
       updateDelay();
       super.update();
       onframeDamage(4,5);
       hitbounds_update();
       updateMonsterSwordHitboxPosition();
       updateSightOfLightPosition();
    }

    private void updateDelay(){
        if (Objects.requireNonNull(statueAnimate) == Attack) {
            if (type == F_Type_Sprite_Entity.Type3 && ani.getFrame() == 1) {
                ani.setDelay(20);
            } else if (type == F_Type_Sprite_Entity.Type3 && ani.getFrame() == 2) {
                ani.setDelay(5);
            } else if (type != F_Type_Sprite_Entity.Type3 && ani.getFrame() == 5) {
                ani.setDelay(30);
            }
        }
    }

    private void updateSightOfLightPosition(){
        float offsetX = 0, offsetY = 0;
        if(type == F_Type_Sprite_Entity.Type3){

            switch (currentDirection) {
                case UP:
                    offsetX = 288 * GamePanel.Zoom;
                    offsetY = 384 * GamePanel.Zoom;
                    break;
                case DOWN:
                    offsetX = 288 * GamePanel.Zoom;
                    offsetY = 192 * GamePanel.Zoom;
                    break;
                case LEFT:
                    offsetX = 384 * GamePanel.Zoom;
                    offsetY = 288 * GamePanel.Zoom;
                    break;
                case RIGHT:
                    offsetX = 192 * GamePanel.Zoom;
                    offsetY = 288 * GamePanel.Zoom;
                    break;
                default:
                    // No offset changes for other directions (if any)
                    break;
            }
        }else{
            switch (currentDirection) {
                case UP:
                    offsetX = 192;
                    offsetY = 256;
                    break;
                case DOWN:
                    offsetX = 192;
                    offsetY = 128;
                    break;
                case LEFT:
                    offsetX = 256;
                    offsetY = 192;
                    break;
                case RIGHT:
                    offsetX = 128;
                    offsetY = 192;
                    break;
                default:
                    // No offset changes for other directions (if any)
                    break;
            }
        }
        monsterSightofLight.setBox(origin.subtract(new Vector2D(offsetX,offsetY)), size*2, size*2);
    }

    private void updateMonsterSwordHitboxPosition() {
        int offsetX = 0, offsetY = 0;
        int hitboxSize = (sizeSprite/2)  * GamePanel.Zoom ;

        if(type == F_Type_Sprite_Entity.Type3){
            hitboxSize = (sizeSprite/3)  * GamePanel.Zoom + 10;
            monsterSwordHitbox.setyOffset((float) -sizeSprite );
            monsterSwordHitbox.setxOffset((float) -sizeSprite );
            switch (currentDirection) {
                case UP:
                    offsetX = 108;
                    offsetY = 108;
                    break;
                case DOWN:
                    offsetX = 108;
                    offsetY = 220;
                    break;
                case LEFT:
                    offsetX = 63 ;
                    offsetY = 160 ;
                    break;
                case RIGHT:
                    offsetX = 193 ;
                    offsetY = 160 ;
                    break;
                default:
                    // No offset changes for other directions (if any)
                    break;
            }
            monsterSwordHitbox.setBox(origin.add(new Vector2D(offsetX, offsetY)), hitboxSize, hitboxSize);
        }else {
            switch (currentDirection) {
                case UP:
                    offsetX = -32 * GamePanel.Zoom;
                    offsetY = -60 * GamePanel.Zoom;
                    break;
                case DOWN:
                    offsetX = -32 * GamePanel.Zoom;
                    offsetY = -15 * GamePanel.Zoom;
                    break;
                case LEFT:
                    offsetX = -55 * GamePanel.Zoom;
                    offsetY = -32 * GamePanel.Zoom;
                    break;
                case RIGHT:
                    offsetX = -15 * GamePanel.Zoom;
                    offsetY = -32 * GamePanel.Zoom;
                    break;
                default:
                    // No offset changes for other directions (if any)
                    break;
            }
            monsterSwordHitbox.setBox(origin.add(new Vector2D(offsetX, offsetY)), hitboxSize, hitboxSize);
        }

    }

    public void hitbounds_update(){
        int width = (sizeSprite / 2);
        int height = (sizeSprite / 2);
        if(type == F_Type_Sprite_Entity.Type3){
            height = (sizeSprite / 2);
            hitbox.setyOffset(40);
        }

        hitbox.setBox(origin, width, height);
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
        // set Opacity
        if(immortality){
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }
        g.drawImage(ani.getImage(),renderX, renderY,rendersize,rendersize, null);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        if(stillAttack &&ani.getFrame() == 4 || ani.getFrame() == 5){
            renderMonsterSwortHitbox(g);
        }

        Sprite.drawArray(g, GameState.font, "HP: " + hp, new Vector2D(renderX, renderY - 10), 16, 16, 16, 0);

        if(Debug.debugging){
            renderDebug(g, renderX, renderY);
            renderSightOfLight(g);
        }

    }
    private void renderMonsterSwortHitbox(Graphics2D g){
        monsterSwordHitbox.render(g);
    }

    public void renderSightOfLight(Graphics2D g){
        monsterSightofLight.render(g);
    }

    public void renderDebug(Graphics2D g, int renderX, int renderY){
        g.setColor(Color.YELLOW);
        int rendersize = size * GamePanel.Zoom;
        g.drawRect(renderX, renderY, rendersize, rendersize);
        hitbox.render(g);
        ai.render(g);
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

    public void SetstateAnimation(F_Statue_Animate flag){
        statueAnimate = flag;
    }

    public int getId(){
        return id;
    }

    public F_Type_Sprite_Entity getType(){
        return type;
    }

    public AABB getMonsterSightofLight(){
        return monsterSightofLight;
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

    public boolean isStillAttack(){
        return stillAttack;
    }

    @Override
    public void onframeDamage(int startFrame, int endFrame) {
        if(stillAttack && ani.getFrame() >= startFrame && ani.getFrame() <= endFrame){
            if(ani.getFrame() == startFrame){
                GamePanel.playerAttack();
            }
            EventManager.triggerEvent("MonsterAttack", monsterSwordHitbox);
        }
    }

    public void setDelay(int i) {
        ani.setDelay(i);
    }
}
