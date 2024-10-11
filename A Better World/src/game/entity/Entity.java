package game.entity;

import game.graphic.Animation;
import game.graphic.Sprite;
import game.physic.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Entity {
    // Animation
    protected Animation ani;
    protected F_Direction currentAnimation;
    protected F_Direction currentDirection;
    protected boolean isMoving;
    protected F_Statue_Animate statueAnimate;
    protected boolean stillAttack;

    // Vector2D
    protected Vector2D origin;
    // Sprite Basic movement
    protected Sprite[] sprite;
    // Size
    protected int size;
    // Movement Direction
    protected boolean[] movement_dir;
    // Abilities
    protected static int attackSpeed = 50;
    protected int attackDuration;
    protected int attackTimer;
    // Speed
    protected float dx;
    protected float dy;
    protected int maxSpeed = 20;
    protected float acc = 3f; // Acceleration
    protected float deacc = 3f; // Deceleration

    public Entity(Vector2D origin, int size) {
        this.origin = origin;
        this.size = size;

        movement_dir = new boolean[F_Direction.SIZE.ordinal()];
        ani = new Animation();
        statueAnimate = F_Statue_Animate.BasicMoveMent;

        isMoving = false;
    }

    public void setAnimation(F_Direction fMovement, BufferedImage[] spriteArray, int delay){
        currentAnimation = fMovement;
        currentDirection = fMovement;
        ani.setFrames(spriteArray);
        ani.setDelay(delay);
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
                //animation_attack();
                break;
            case 3:// Hurt
                //animation_dead();
                break;
            case 4:// Dead
                //animation_hurt();
                break;
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

        if(ani.getFrame() == 6){
            System.out.println("Attack done");
            stillAttack = false;
            statueAnimate = F_Statue_Animate.BasicMoveMent;
            ani.setFrames(sprite[F_List_Animation_Sprite.Idle.ordinal()].getSpriteArray(currentDirection.ordinal()));
            ani.setDelay(12);
            isMoving = false;
        }

    }

    public void setMovement(F_Direction fMovement, boolean b) {
        movement_dir[fMovement.ordinal()] = b;
    }



    public void update() {
        animate();
        ani.update();
    }

    public abstract void render(Graphics2D g);
}
