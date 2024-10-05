package game.entity;

import game.graphic.Animation;
import game.graphic.Sprite;
import game.physic.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Entity {
    // Animation
    protected Animation ani;
    protected int currentAnimation;
    protected int currentDirection;
    protected boolean isMoving;

    // Vector2D
    protected Vector2D origin;
    // Sprite Basic movement
    protected Sprite[] sprite;
    // Size
    protected int size;
    // Movement Direction
    protected boolean[] movement_dir;
    // Abilities
    protected int attackSpeed;
    protected int attackDuration;
    protected int attackTimer;
    // Speed
    protected float dx;
    protected float dy;
    protected float maxSpeed = 10f;
    protected float acc = 3f; // Acceleration
    protected float deacc = 3f; // Deceleration

    public Entity(Vector2D origin, int size) {
        this.origin = origin;
        this.size = size;

        movement_dir = new boolean[F_Movement.SIZE.ordinal()];
        ani = new Animation();

        isMoving = false;
    }

    public void setAnimation(F_Movement fMovement, BufferedImage[] spriteArray, int delay){
        currentAnimation = fMovement.ordinal();
        currentDirection = fMovement.ordinal();
        ani.setFrames(spriteArray);
        ani.setDelay(delay);
    }

    public void animate() {
        if (movement_dir[F_Movement.UP.ordinal()]) {// UP

            if (!isMoving||currentAnimation != F_Movement.UP.ordinal() || ani.hasPlayedOnce()) {
                currentDirection = F_Movement.UP.ordinal();
                setAnimation(F_Movement.UP, sprite[F_List_Animation_Sprite.Walking.ordinal()].getSpriteArray(F_Movement.UP.ordinal()), 5);
            }
            isMoving = true;
        }
        else if (movement_dir[F_Movement.DOWN.ordinal()]) { // DOWN

            if (!isMoving||currentAnimation != F_Movement.DOWN.ordinal() || ani.hasPlayedOnce()) {
                currentDirection = F_Movement.DOWN.ordinal();
                setAnimation(F_Movement.DOWN, sprite[F_List_Animation_Sprite.Walking.ordinal()].getSpriteArray(F_Movement.DOWN.ordinal()), 5);
            }
            isMoving = true;
        }
        else if (movement_dir[F_Movement.LEFT.ordinal()]) { // LEFT

            if (!isMoving||currentAnimation != F_Movement.LEFT.ordinal() || ani.hasPlayedOnce()) {
                currentDirection = F_Movement.LEFT.ordinal();
                setAnimation(F_Movement.LEFT, sprite[F_List_Animation_Sprite.Walking.ordinal()].getSpriteArray(F_Movement.LEFT.ordinal()), 5);
            }
            isMoving = true;
        }
        else if (movement_dir[F_Movement.RIGHT.ordinal()]) { // RIGHT

            if (!isMoving||currentAnimation != F_Movement.RIGHT.ordinal() || ani.hasPlayedOnce()) {
                currentDirection = F_Movement.RIGHT.ordinal();
                setAnimation(F_Movement.RIGHT, sprite[F_List_Animation_Sprite.Walking.ordinal()].getSpriteArray(F_Movement.RIGHT.ordinal()), 5);
            }
            isMoving = true;
        }
        else {
            if(isMoving) {
                ani.setFrames(sprite[F_List_Animation_Sprite.Idle.ordinal()].getSpriteArray(currentDirection));
                ani.setDelay(12);
            }

            isMoving = false;
        }
    }


    public void update() {
        animate();
        ani.update();
    }

    public abstract void render(Graphics2D g);
}
