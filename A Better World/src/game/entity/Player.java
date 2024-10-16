package game.entity;

import game.Debug;
import game.GamePanel;
import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.graphic.Camera;
import game.graphic.Sprite;
import game.physic.AABB;
import game.physic.Vector2D;
import game.state.PlayState;

import java.awt.*;
import java.util.Arrays;

public class Player extends Entity{

    private static Camera camera;

    // Movement
    private boolean dashPrssed = false;
    private final float dashSpeed = 1.5f;
    private boolean isDash = false;
    private boolean stillDash = false;
    private final float DefaultSpeed = 3f;

    // Hit box
    AABB hitBounds;

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

    public Player(Vector2D origin, int sizeRender) {
        super(origin, GamePanel.Tile_Size, sizeRender);
        this.sprite = setDefaultSpite();
        setAnimation(F_Direction.RIGHT, sprite[F_List_Animation_Sprite.Idle.ordinal()].getSpriteArray(F_Direction.RIGHT.ordinal()), 10);
        camera = new Camera(origin, ((float) GamePanel.width /2),((float) GamePanel.height /2), GamePanel.width, GamePanel.height);
        hitBounds = new AABB(origin, GamePanel.Tile_Size, GamePanel.Tile_Size);
    }

    public void update(){
        setupDirectionMovement();

        if(is_dash_update()){// Update the animation dash movement
            dash_update();
        }else{
            // Update the animation basic movement
            super.update();
        }

        camera_update();
        hitbounds_update();
    }

    public void camera_update(){
        camera.setPlayerPosition(origin);
        camera.update();
    }

    public void setupDirectionMovement(){
        movement();
        Vector2D set = new Vector2D(dx, dy);
        set = set.normalize().multiply(acc);
        origin = origin.add(set);
    }

    public boolean is_dash_update(){
        return (stillDash||(isDash && !dashPrssed && checkmovent()));
    }

    public void dash_update(){
        setDashSpeed();
        animation_dash();
        ani.update();
    }

    public void hitbounds_update(){
        hitBounds.setBox(origin, size, size);
    }

    @Override
    public void render(Graphics2D g) {
        int renderX = (int)(origin.x - Camera.getWorldX());
        int renderY = (int)(origin.y - Camera.getWorldY());

        g.drawImage(ani.getImage(),renderX, renderY, sizeSprite, sizeSprite, null);
        if(Debug.debugging) {
            renderDebug(g, renderX, renderY);
        }
    }

    public void renderDebug(Graphics2D g, int renderX, int renderY){
        g.setColor(Color.YELLOW);
        g.drawRect(renderX, renderY, sizeSprite, sizeSprite);
        hitBounds.render(g);
    }

    public void movement(){
        // Dash case
        if(stillDash){
            if(currentDirection == F_Direction.UP){
                acc_moveup();
            }
            if(currentDirection == F_Direction.DOWN){
                acc_movedown();
            }
            if(currentDirection == F_Direction.LEFT){
                acc_moveleft();
            }
            if(currentDirection == F_Direction.RIGHT){
                acc_moveright();
            }
            return;
        }
        // Attack case
        if(statueAnimate == F_Statue_Animate.Attack){
            if(!movement_dir[F_Direction.UP.ordinal()]) {
                deacc_moveup();
            }
            if(!movement_dir[F_Direction.DOWN.ordinal()]) {
                deacc_movedown();
            }
            if(!movement_dir[F_Direction.LEFT.ordinal()]) {
                deacc_moveleft();
            }
            if(!movement_dir[F_Direction.RIGHT.ordinal()]) {
                deacc_moveright();
            }
            return;
        }

        // Basic Movement case
        if(movement_dir[F_Direction.UP.ordinal()]){
            acc_moveup();
        }else{
            deacc_moveup();
        }

        if(movement_dir[F_Direction.DOWN.ordinal()]) {
            acc_movedown();
        }else{
            deacc_movedown();
        }

        if(movement_dir[F_Direction.LEFT.ordinal()]){
            acc_moveleft();
        }else{
            deacc_moveleft();
        }

        if(movement_dir[F_Direction.RIGHT.ordinal()]) {
            acc_moveright();
        }else{
            deacc_moveright();
        }

    }

    private void acc_moveup(){
        dy -= acc;
        if(dy < -maxSpeed){
            dy = -maxSpeed;
        }
    }
    private void acc_movedown(){
        dy += acc;
        if (dy > maxSpeed) {
            dy = maxSpeed;
        }
    }
    private void acc_moveleft(){
        dx -= acc;
        if(dx < -maxSpeed){
            dx = -maxSpeed;
        }
    }
    private void acc_moveright(){
        dx += acc;
        if (dx > maxSpeed) {
            dx = maxSpeed;
        }
    }

    private void deacc_moveup(){
        if(dy < 0){
            dy += deacc;
            if(dy > 0){
                dy = 0;
            }
        }
    }
    private void deacc_movedown(){
        if(dy > 0){
            dy -= deacc;
            if(dy < 0){
                dy = 0;
            }
        }
    }
    private void deacc_moveleft(){
        if(dx < 0){
            dx += deacc;
            if(dx > 0){
                dx = 0;
            }
        }
    }
    private void deacc_moveright(){
        if(dx > 0){

            dx -= deacc;
            if(dx < 0){
                dx = 0;
            }
        }
    }

    public void setDashSpeed(){
        if(!dashPrssed && !stillDash){
                System.out.println("acc add: "+acc);
               acc += dashSpeed;
        }else{
            if(ani.getFrame() == 4){
                acc -= dashSpeed;
                System.out.println("acc sub: "+acc);
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
            if(!key.up.down) {
                movement_dir[F_Direction.DOWN.ordinal()] = true;
            }
        }else{
            movement_dir[F_Direction.DOWN.ordinal()] = false;
        }

        if(key.left.down) {
            movement_dir[F_Direction.LEFT.ordinal()] = true;
        }else{
            movement_dir[F_Direction.LEFT.ordinal()] = false;
        }

        if(key.right.down) {
            if(!key.left.down) {
                movement_dir[F_Direction.RIGHT.ordinal()] = true;
            }
        }else{
            movement_dir[F_Direction.RIGHT.ordinal()] = false;
        }

        if(key.dash.down){
            isDash = true;
            if(!dashPrssed) {
                movement_dir[F_Direction.DASH.ordinal()] = true;
            }
        }else{
            isDash = false;
            movement_dir[F_Direction.DASH.ordinal()] = false;
            setDashPrssed(false);
        }

        if(MouseHandler.getButtom() == 1){
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

    public boolean isDashPrssed() {
        return dashPrssed;
    }

    private boolean checkmovent(){
        for(int i = F_Direction.UP.ordinal(); i < F_Direction.DASH.ordinal(); i++){
            if(movement_dir[i]){
                return true;
            }
        }
        return false;
    }


    public void setDashPrssed(boolean dashPrssed) {
        this.dashPrssed = dashPrssed;
    }

    public void animation_dash(){
        if(!dashPrssed){
            if(!stillDash){
                setAnimation(currentDirection, sprite[F_List_Animation_Sprite.Dash.ordinal()].getSpriteArray(currentDirection.ordinal()), 6);
                stillDash = true;
            }

            if(ani.getFrame() == 4){
                System.out.println("Dash done");
                stillDash = false;
                statueAnimate = F_Statue_Animate.BasicMoveMent;
                ani.setFrames(sprite[F_List_Animation_Sprite.Walking.ordinal()].getSpriteArray(currentDirection.ordinal()));
                ani.setDelay(5);
                setDashPrssed(true);
            }
        }
    }
}
