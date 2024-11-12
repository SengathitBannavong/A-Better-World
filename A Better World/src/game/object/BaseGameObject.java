package game.object;

import game.entity.F_Direction;
import game.entity.F_Statue_Animate;
import game.graphic.Animation;
import game.graphic.Sprite;
import game.physic.AABB;
import game.physic.Vector2D;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class BaseGameObject {
    // Animation
    protected Animation ani;
    protected F_Direction currentAnimation;
    protected F_Direction currentDirection;
    protected boolean isMoving;
    protected F_Statue_Animate statueAnimate;
    protected boolean stillAttack;

    // Sprite
    protected Sprite[] sprite;
    protected int size;
    protected int sizeSprite;

    // Vector2D (Position and Velocity)
    protected Vector2D origin;
    protected float dx;
    protected float dy;
    protected float acc = 3f; // Acceleration
    protected float de_acc = 3f; // Deceleration
    protected int maxSpeed = 20;

    // Hitbox
    protected AABB hitbox;

    // Constructor
    public BaseGameObject(Vector2D origin,int size,int sizeSprite){
        this.origin = origin;
        this.size = size;
        this.sizeSprite = sizeSprite;
        ani = new Animation();
        statueAnimate = F_Statue_Animate.BasicMovement;

        isMoving = false;
    }

    // Set the animation
    public void setAnimation(F_Direction fMovement, BufferedImage[] spriteArray, int delay){
        currentAnimation = fMovement;
        currentDirection = fMovement;
        ani.setFrames(spriteArray);
        ani.setDelay(delay);
    }

    // Set the hitbox
    public void setHitbox(Vector2D position,int width,int height){
        hitbox = new AABB(position,width,height);
    }

    public abstract void animate();
    public abstract void update();
    public abstract void render(Graphics2D g);
}
