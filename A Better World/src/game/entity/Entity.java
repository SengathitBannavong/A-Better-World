package game.entity;

import game.enum_.F_Direction;
import game.enum_.F_List_Animation_Sprite;
import game.enum_.F_Statue_Animate;
import game.object.BaseGameObject;
import game.physic.Vector2D;

import java.awt.*;

public abstract class Entity extends BaseGameObject {
    // Abilities
    protected int attackSpeed = 50;
    protected int attackDuration;
    protected int attackTimer;

    protected boolean[] movement_dir;

    // Abilities
    protected int hp;
    protected int maxHp;
    protected int damage;
    protected int maxDamage;
    protected boolean immortality = false;
    protected final int timeCountdownImmortality = 1;
    protected int countdownImmortality = 0;
    protected boolean isHurt = false;
    protected boolean stillHurt = false;

    public Entity(Vector2D origin, int size,int sizeSprite) {
        super(origin,size,sizeSprite);
        movement_dir = new boolean[F_Direction.SIZE.ordinal()];
    }

    public void animate() {
        switch (statueAnimate.ordinal()) {
            case 0:// Basic Movement
                animation_basic_movement();
                break;
            case 1: // Attack
                animation_attack();
                break;
            case 2:// Dead
                //animation_dead();
                break;
            case 3:// Hurt
                animation_hurt();
                break;
            default: // Basic Movement
                animation_basic_movement();
                break;
        }

    }

    private void animation_hurt() {
        if(!stillHurt) {
            setAnimation(currentDirection, sprite[F_List_Animation_Sprite.Hurt.ordinal()].getSpriteArray(currentDirection.ordinal()), 4);
            stillHurt = true;
        }

        if(ani.getFrame() == ani.getNumFrames() - 1){
            System.out.println("Hurt done");
            isHurt = false;
            stillHurt = false;
            statueAnimate = F_Statue_Animate.BasicMovement;
            ani.setFrames(sprite[F_List_Animation_Sprite.Idle.ordinal()].getSpriteArray(currentDirection.ordinal()));
            ani.setDelay(12);
            isMoving = false;
        }
    }

    public void animation_basic_movement(){
        // Movement
        if (movement_dir[F_Direction.UP.ordinal()]) {// UP
            if (!isMoving||currentAnimation != F_Direction.UP || ani.hasPlayedOnce()) {
                currentDirection = F_Direction.UP;
                setAnimation(F_Direction.UP, sprite[F_List_Animation_Sprite.Walking.ordinal()].getSpriteArray(F_Direction.UP.ordinal()), 5);
            }
            isMoving = true;
        }
        else if (movement_dir[F_Direction.DOWN.ordinal()]) { // DOWN
            if (!isMoving || currentAnimation != F_Direction.DOWN || ani.hasPlayedOnce()) {
                currentDirection = F_Direction.DOWN;
                setAnimation(F_Direction.DOWN, sprite[F_List_Animation_Sprite.Walking.ordinal()].getSpriteArray(F_Direction.DOWN.ordinal()), 5);
            }
            isMoving = true;
        }
        else if (movement_dir[F_Direction.LEFT.ordinal()]) { // LEFT
            if (!isMoving || currentAnimation != F_Direction.LEFT || ani.hasPlayedOnce()) {
                currentDirection = F_Direction.LEFT;
                setAnimation(F_Direction.LEFT, sprite[F_List_Animation_Sprite.Walking.ordinal()].getSpriteArray(F_Direction.LEFT.ordinal()), 5);
            }
            isMoving = true;
        }
        else if (movement_dir[F_Direction.RIGHT.ordinal()]) { // RIGHT
            if (!isMoving || currentAnimation != F_Direction.RIGHT || ani.hasPlayedOnce()) {
                currentDirection = F_Direction.RIGHT;
                setAnimation(F_Direction.RIGHT, sprite[F_List_Animation_Sprite.Walking.ordinal()].getSpriteArray(F_Direction.RIGHT.ordinal()), 5);
            }
            isMoving = true;
        }
        else {
            if(isMoving) {
                System.out.println("Idle");
                ani.setFrames(sprite[F_List_Animation_Sprite.Idle.ordinal()].getSpriteArray(currentDirection.ordinal()));
                ani.setDelay(12);
            }
            isMoving = false;
        }
    }

    private int getSpeedAttack(){
        if(attackSpeed >= 100){
            return 1;
        }else if(attackSpeed >= 90){
            return 2;
        } else if(attackSpeed >= 80){
            return 3;
        } else if(attackSpeed >= 70){
            return 4;
        } else if(attackSpeed >= 60){
            return 5;
        } else if(attackSpeed >= 50){
            return 6;
        } else if(attackSpeed >= 40){
            return 7;
        } else if(attackSpeed >= 30){
            return 8;
        } else if(attackSpeed >= 20){
            return 9;
        } else if(attackSpeed >= 10){
            return 10;
        } else {
            return 11;
        }
    }

    private void animation_attack(){
        if(!stillAttack){
            setAnimation(currentDirection, sprite[F_List_Animation_Sprite.Attack.ordinal()].getSpriteArray(currentDirection.ordinal()), getSpeedAttack());
            stillAttack = true;
            attackTimer = 0;
        }
        attackTimer++;

        if(ani.getFrame() == ani.getNumFrames() - 2){ // done fast 2 frame for handle run attack
            System.out.println("Attack done");
            stillAttack = false;
            statueAnimate = F_Statue_Animate.BasicMovement;
            ani.setFrames(sprite[F_List_Animation_Sprite.Idle.ordinal()].getSpriteArray(currentDirection.ordinal()));
            ani.setDelay(12);
            isMoving = false;
        }

    }

    public void setMovement(F_Direction fMovement, boolean b) {

    }



    public void update() {
        animate();
        ani.update();
    }

    public abstract void render(Graphics2D g);

    public int getSize() {
        return size;
    }

    public float getDx() {
        return dx;
    }

    public float getDy() {
        return dy;
    }

    public float getAcc() {
        return acc;
    }

    public float getDe_acc() {
        return de_acc;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public boolean[] getMovement_dir() {
        return movement_dir;
    }

    public boolean isMoving(){
        return isMoving && !stillAttack;
    }

    public void setDx(float dx) {
        this.dx = dx;
    }

    public void setDy(float dy) {
        this.dy = dy;
    }

    public void setAcc(float acc) {this.acc = acc;}
    public void setDe_acc(float de_acc) {this.de_acc = de_acc;}

    public Vector2D getOrigin() {return origin;}

    public void setIsMoving(boolean isMoving) {this.isMoving = isMoving;}

    public int getAttackDemage(){
        //random damge between damage and maxDamage
        return (int)(Math.random() * (maxDamage - damage + 1) + damage);
    }

    public int getHp(){
        return hp;
    }
}
