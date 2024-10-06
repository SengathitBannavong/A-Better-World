package game.entity;

import game.GamePanel;
import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.graphic.Camera;
import game.graphic.Sprite;
import game.physic.Vector2D;

import java.awt.Graphics2D;
import java.util.Arrays;

public class Player extends Entity{

    private static Camera camera;

    private Sprite[] setDefaultSpite(){
        Sprite[] sprite = new Sprite[F_List_Animation_Sprite.SIZE.ordinal()];
        sprite[F_List_Animation_Sprite.Idle.ordinal()] = new Sprite("player/Player_idle_64_64_sprite.png", 64, 64);
        sprite[F_List_Animation_Sprite.Walking.ordinal()] = new Sprite("player/Player_walking_64_64_sprite.png", 64, 64);
        sprite[F_List_Animation_Sprite.Attack.ordinal()] = new Sprite("player/Player_attack_64_64_sprite.png", 64, 64);
        sprite[F_List_Animation_Sprite.Dead.ordinal()] = new Sprite("player/Player_attack_64_64_sprite.png", 64, 64);
        sprite[F_List_Animation_Sprite.Hurt.ordinal()] = new Sprite("player/Player_attack_64_64_sprite.png", 64, 64);
        return sprite;
    }

    public Player(Vector2D origin, int size) {
        super(origin, size);
        this.sprite = setDefaultSpite();
        setAnimation(F_Direction.RIGHT, sprite[F_List_Animation_Sprite.Idle.ordinal()].getSpriteArray(F_Direction.RIGHT.ordinal()), 10);
        camera = new Camera(origin, ((float) GamePanel.width /2),((float) GamePanel.height /2), GamePanel.width, GamePanel.height);
    }

    public void update(){
        movement();
        super.update();
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
        if(statueAnimate == F_Statue_Animate.Attack){
            if(!movement_dir[F_Direction.UP.ordinal()]) {
                if(dy < 0){
                    dy += deacc;
                    if(dy > 0){
                        dy = 0;
                    }
                }
            }
            if(!movement_dir[F_Direction.DOWN.ordinal()]) {
                if(dy > 0){
                    dy -= deacc;
                    if(dy < 0){
                        dy = 0;
                    }
                }
            }
            if(!movement_dir[F_Direction.LEFT.ordinal()]) {
                if(dx < 0){
                    dx += deacc;
                    if(dx > 0){
                        dx = 0;
                    }
                }
            }
            if(!movement_dir[F_Direction.RIGHT.ordinal()]) {
                if(dx > 0){
                    dx -= deacc;
                    if(dx < 0){
                        dx = 0;
                    }
                }
            }
            return;
        }

        if(movement_dir[F_Direction.UP.ordinal()]){
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

        if(movement_dir[F_Direction.DOWN.ordinal()]) {
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

        if(movement_dir[F_Direction.LEFT.ordinal()]){
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

        if(movement_dir[F_Direction.RIGHT.ordinal()]) {
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

        if(key.up.down){
            movement_dir[F_Direction.UP.ordinal()] = true;
        }else{
            movement_dir[F_Direction.UP.ordinal()] = false;
        }

        if(key.down.down){
            movement_dir[F_Direction.DOWN.ordinal()] = true;
        }else{
            movement_dir[F_Direction.DOWN.ordinal()] = false;
        }

        if(key.left.down) {
            movement_dir[F_Direction.LEFT.ordinal()] = true;
        }else{
            movement_dir[F_Direction.LEFT.ordinal()] = false;
        }

        if(key.right.down) {
            movement_dir[F_Direction.RIGHT.ordinal()] = true;
        }else{
            movement_dir[F_Direction.RIGHT.ordinal()] = false;
        }

        if(mouse.getButtom() == 1){
            statueAnimate = F_Statue_Animate.Attack;
            setFlase();
        }else{
            if(!stillAttack) {
                statueAnimate = F_Statue_Animate.BasicMoveMent;
            }
        }
    }

    private void setFlase(){
        Arrays.fill(movement_dir, false);
    }

    public static Camera getCamera() {
        return camera;
    }
}
