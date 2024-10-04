package game.entity;

import game.GamePanel;
import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.graphic.Camera;
import game.graphic.Sprite;
import game.physic.Vector2D;

import java.awt.Graphics2D;

public class Player extends Entity{

    private static Camera camera;

    private Sprite[] setDefaultSpite(){
        Sprite[] sprite = new Sprite[F_List_Animation_Sprite.SIZE.ordinal()];
        sprite[F_List_Animation_Sprite.Idle.ordinal()] = new Sprite("player/Player_Idle_sprite.png", 32, 32);
        sprite[F_List_Animation_Sprite.Walking.ordinal()] = new Sprite("player/Player_walking_sprite.png", 32, 32);
        sprite[F_List_Animation_Sprite.Attack.ordinal()] = new Sprite("player/Player_walking_sprite.png", 32, 32);
        sprite[F_List_Animation_Sprite.Dead.ordinal()] = new Sprite("player/Player_walking_sprite.png", 32, 32);
        sprite[F_List_Animation_Sprite.Hurt.ordinal()] = new Sprite("player/Player_walking_sprite.png", 32, 32);
        return sprite;
    }

    public Player(Vector2D origin, int size) {
        super(origin, size);
        this.sprite = setDefaultSpite();
        setAnimation(F_Movement.RIGHT, sprite[F_List_Animation_Sprite.Idle.ordinal()].getSpriteArray(F_Movement.RIGHT.ordinal()), 10);
        camera = new Camera(origin, ((float) GamePanel.width /2),((float) GamePanel.height /2), GamePanel.width, GamePanel.height);
    }

    public void update(){
        super.update();
        movement();
        Vector2D set = new Vector2D(dx, dy);
        set = set.normalize().multiply(acc);
        origin = origin.add(set);
        camera.setPlayerPosition(origin);
        camera.update();
    }

    @Override
    public void render(Graphics2D g) {
        int renderX = (int)(origin.x - Camera.getWorldX());
        int renderY = (int)(origin.y - Camera.getWorldY());

        g.drawImage(ani.getImage(),renderX, renderY, size, size, null);
    }

    public void movement(){
        if(movement_dir[F_Movement.UP.ordinal()]){
            dy -= acc;
            if(dy < -maxSpeed){
                dy = -maxSpeed;
            }
        }else{
            if(dy < 0){
                dy += deacc;
                if(dy > 0){
                    dy = 0;
                }
            }
        }

        if(movement_dir[F_Movement.DOWN.ordinal()]) {
            dy += acc;
            if (dy > maxSpeed) {
                dy = maxSpeed;
            }
        }else{
            if(dy > 0){
                dy -= deacc;
                if(dy < 0){
                    dy = 0;
                }
            }
        }

        if(movement_dir[F_Movement.LEFT.ordinal()]){
            dx -= acc;
            if(dx < -maxSpeed){
                dx = -maxSpeed;
            }
        }else{
            if(dx < 0){
                dx += deacc;
                if(dx > 0){
                    dx = 0;
                }
            }
        }

        if(movement_dir[F_Movement.RIGHT.ordinal()]) {
            dx += acc;
            if (dx > maxSpeed) {
                dx = maxSpeed;
            }
        }else{
            if(dx > 0){
                dx -= deacc;
                if(dx < 0){
                    dx = 0;
                }
            }
        }
    }

    public void input(KeyHandler key, MouseHandler mouse) {

        if(mouse.getButtom() == 1){
            System.out.println("Player:" + origin.x + " " + origin.y);
        }

        if(key.up.down){
            movement_dir[F_Movement.UP.ordinal()] = true;
        }else{
            movement_dir[F_Movement.UP.ordinal()] = false;
        }

        if(key.down.down){
            movement_dir[F_Movement.DOWN.ordinal()] = true;
        }else{
            movement_dir[F_Movement.DOWN.ordinal()] = false;
        }

        if(key.left.down) {
            movement_dir[F_Movement.LEFT.ordinal()] = true;
        }else{
            movement_dir[F_Movement.LEFT.ordinal()] = false;
        }

        if(key.right.down) {
            movement_dir[F_Movement.RIGHT.ordinal()] = true;
        }else{
            movement_dir[F_Movement.RIGHT.ordinal()] = false;
        }

        if(key.attack.down){
            movement_dir[F_Movement.ATTACK.ordinal()] = true;
        }else{
            movement_dir[F_Movement.ATTACK.ordinal()] = false;
        }
    }

    public static Camera getCamera() {
        return camera;
    }
}
