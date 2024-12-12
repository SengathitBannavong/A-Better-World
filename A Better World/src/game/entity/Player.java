package game.entity;

import game.Debug;
import game.GamePanel;
import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.design.DemageFrame;
import game.design.Observarable;
import game.design.Observer;
import game.enum_.F_Direction;
import game.enum_.F_List_Animation_Sprite;
import game.enum_.F_Statue_Animate;
import game.event.Event;
import game.event.EventManager;
import game.graphic.Camera;
import game.graphic.Sprite;
import game.movement.BasicMovement;
import game.movement.MovementStrategy;
import game.physic.AABB;
import game.physic.Vector2D;
import game.state.GameState;
import game.state.GameStateManager;
import game.state.PlayState;
import game.tile.GridCellWrite;
import game.tile.Map;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Player extends Entity implements Observer<PlayState>, Observarable , DemageFrame {

    private MovementStrategy movementStrategy;
    private final List<Observer> observers = new ArrayList<>(); // List of observers to notify player position
    private static Camera camera;

    // Dash Movement
    private boolean dashPrssed = false;
    private final float dashSpeed = 1.5f;
    private boolean isDash = false;
    private boolean stillDash = false;
    private final float DefaultSpeed = 3f;
    private final int dashcountdown = 2;
    private int count_dash_countdown = 0;

    // Hitbox Sword
    private AABB playerSwordHitbox;

    // HP UI
    Sprite hpUI = new Sprite("player/hp_ui.png", 128, 128);

    GridCellWrite gridCellWrite = new GridCellWrite(GameStateManager.getMapName(1));

    // Optimize this to parse source in file txt
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

    // make Player class singleton
    private static Player instance = null;

    private Player(Vector2D origin,int size ,int sizeSprite,PlayState playState) {
        super(origin, size, sizeSprite);
        playState.addObserver(this); // Add observer to PlayState for update dash countdown
        this.sprite = setDefaultSpite();
        setAnimation(F_Direction.RIGHT, sprite[F_List_Animation_Sprite.Idle.ordinal()].getSpriteArray(F_Direction.RIGHT.ordinal()), 10);
        camera = new Camera(origin, ((float) GamePanel.width/2 - sizeSprite - GamePanel.Tile_Size) ,((float) GamePanel.height/2 - sizeSprite - GamePanel.Tile_Size), GamePanel.width, GamePanel.height);
        super.setHitbox(origin,(sizeSprite / 2) * GamePanel.Zoom , (sizeSprite / 2) * GamePanel.Zoom);
        movementStrategy = new BasicMovement();
        playerSwordHitbox = new AABB(origin.add(new Vector2D(4,4)),(sizeSprite / 2) * GamePanel.Zoom , (sizeSprite / 2) * GamePanel.Zoom);
        Init();
    }

    public static Player getInstance(Vector2D origin,int size ,int sizeSprite,PlayState playState){
        if(instance == null){
            instance = new Player(origin, size, sizeSprite, playState);
        }
        return instance;
    }

    public void Init(){
        maxHp = 10;
        hp = maxHp;
        maxDamage = 2;
        damage = 1;
    }

    public void update(){
        if(stillDead){
            super.update();
            return;
        }
        if(isDied){
            isDied = false;
            EventManager.triggerEvent("PlayerDead");
        }
        setupDirectionMovement();

        if(is_dash_update() && !IsdashCountDown()){// Update the animation dash movement
            dash_update();
        }else{
            // Update the animation basic movement
            super.update();
        }

        camera_update();
        updatePlayerSwordHitboxPosition();
        onframeDamage(2,3);
        hitbounds_update();
        checkMapBoundaries();
        notifyObservers();
    }

    private void updatePlayerSwordHitboxPosition() {
        int offsetX = 0, offsetY = 0;
        int hitboxSize = sizeSprite * GamePanel.Zoom;

        if (currentDirection == F_Direction.UP) {
            offsetX = -64 * GamePanel.Zoom;
            offsetY = -76 * GamePanel.Zoom;
        } else if (currentDirection == F_Direction.DOWN) {
            offsetX = -64 * GamePanel.Zoom;
            offsetY = -48 * GamePanel.Zoom;
        } else if (currentDirection == F_Direction.LEFT) {
            offsetX = -90 * GamePanel.Zoom;
            offsetY = -64 * GamePanel.Zoom;
        } else if (currentDirection == F_Direction.RIGHT) {
            offsetX = -36 * GamePanel.Zoom;
            offsetY = -64 * GamePanel.Zoom;
        }
        playerSwordHitbox.setBox(origin.add(new Vector2D(offsetX, offsetY)), hitboxSize, hitboxSize);
    }


    public void takeDamage(int damage){
        if(immortality) return;
        hp -= damage;
        isHurt = true;
        System.out.println("Player taken damage:"+damage+"current hp is:" + hp);
        if(hp <= 0){
            Dead();
            System.out.println("Player died\n");
        }
        immortality = true;
        countdownImmortality = timeCountdownImmortality;
    }

    public void Dead() {
        isDead = true;
    }


    private void checkMapBoundaries() {
        // check the map boundaries
        int bound = ((GamePanel.Tile_Size * GamePanel.Zoom * GamePanel.Scale) / 2);
        int sizeHitbox = (sizeSprite / 2) * GamePanel.Zoom;
        int mapWidth = (Map.getWidth() * GamePanel.Tile_Size * Map.getScale() * GamePanel.Zoom) - (bound + (sizeHitbox/2));
        int mapHeight = (Map.getHeight() * GamePanel.Tile_Size * Map.getScale() * GamePanel.Zoom) - (bound + (sizeHitbox/2));
        // Ensure the player does not go out of the map boundaries
        if (origin.x < -99) {
            origin.x = -99;
        } else if (origin.x > mapWidth) {
            origin.x = mapWidth;
        }

        if (origin.y < -99) {
            origin.y = -99;
        } else if (origin.y > mapHeight) {
            origin.y = mapHeight;
        }
    }

    private boolean checkHitWall(Vector2D origin_future){
        int[][] grid_wall = getGridNearPlayer(origin_future);
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(grid_wall[i][j] == 1){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean IsdashCountDown(){
        return count_dash_countdown > 0;
    }

    public void camera_update(){
        camera.setPlayerPosition(origin);
        camera.update();
    }

    public void setupDirectionMovement(){
        movementStrategy.move(this);
        Vector2D set;
        set = new Vector2D(dx, dy);
        set = set.normalize().multiply(acc);
        Vector2D temp = origin.add(set);
        if(checkHitWall(temp) && Debug.collision){
            set = new Vector2D(0, 0);
        }
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
        int width = (sizeSprite / 2) * GamePanel.Zoom;
        int height = (sizeSprite / 2)* GamePanel.Zoom;
        hitbox.setBox(origin, width, height);
    }

    @Override
    public void render(Graphics2D g) {
        int renderX = (int)(origin.x - Camera.getWorldX());
        int renderY = (int)(origin.y - Camera.getWorldY());
        int rendersize = size * GamePanel.Zoom;
        if(immortality){
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }
        g.drawImage(ani.getImage(),renderX, renderY,rendersize,rendersize, null);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        renderNpcSpeak(g);
        if(Debug.debugging) {
            renderDebug(g, renderX, renderY);
            drawGridAroundPlayer(g);
            renderPlayerSwortHitbox(g);
        }
    }

    private int getIndexHpUi(int index){
        return 10 - index;
    }

    public void renderUI(Graphics2D g){
        int index = getIndexHpUi(hp);
        if(index < 0) index = 0;
        if(index > 10) index = 10;
        g.drawImage(hpUI.getSprite(index,0), -30, -140, 384, 384, null);
    }

    private void renderPlayerSwortHitbox(Graphics2D g){
        if(stillAttack && ani.getFrame() >= 2) {
            playerSwordHitbox.render(g);
        }
    }

    private void renderNpcSpeak(Graphics2D g){
        List<NPC> npcs = getNPCsAroundPlayer();
        if(!npcs.isEmpty()){
            for(NPC npc : npcs){
                if(!npc.getCanTalk())continue;
                int renderX = (int)(npc.getOrigin().x - Camera.getWorldX());
                int renderY = (int)(npc.getOrigin().y - Camera.getWorldY());
                Sprite.drawArray(g, GameState.font, "Press C To Talk", new Vector2D(renderX, renderY - 10), 32, 32, 20, 0);
            }
        }
    }

    public void renderDebug(Graphics2D g, int renderX, int renderY){
        g.setColor(Color.YELLOW);
        int rendersize = size * GamePanel.Zoom;
        g.drawRect(renderX, renderY, rendersize, rendersize);
        hitbox.render(g);
        Sprite.drawArray(g, GameState.font, "X: " + (int)origin.x + " Y: " + (int)origin.y, new Vector2D(renderX, renderY - 10), 32, 32, 16, 0);
    }

    public void setDashSpeed(){
        if(!dashPrssed && !stillDash){
                System.out.println("acc add: "+acc);
               acc += dashSpeed;
        }else{
            if(ani.getFrame() == 4){
                acc -= dashSpeed;
                count_dash_countdown = dashcountdown;
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
        }else{
            isDash = false;
            setDashPrssed(false);
        }

        if(MouseHandler.getButtom() == 1){
            statueAnimate = F_Statue_Animate.Attack;
            setFlase();
        }else{
            if(!stillAttack) {
                statueAnimate = F_Statue_Animate.BasicMovement;
            }
        }

        if(isHurt){
            statueAnimate = F_Statue_Animate.Hurt;
            setFlase();
        }

        if(isDead){
            statueAnimate = F_Statue_Animate.Dead;
            setFlase();
        }

        if(key.write.down){
            System.out.println("Write");
            GridCellWrite.writeGrid();
            //delay
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(key.cell.down){
            int pos_x = ((int)origin.x)  / (GamePanel.Tile_Size * GamePanel.Zoom);
            int pos_y = ((int)origin.y) / (GamePanel.Tile_Size * GamePanel.Zoom);
            gridCellWrite.setGrid(pos_x, pos_y);
            System.out.println("Cell : "+pos_x+" "+pos_y);
        }

        if(key.deletecell.down){
            int pos_x = ((int)origin.x)  / (GamePanel.Tile_Size * GamePanel.Zoom);
            int pos_y = ((int)origin.y) / (GamePanel.Tile_Size * GamePanel.Zoom);
            gridCellWrite.deleteGrid(pos_x, pos_y);
            System.out.println("Cell : "+pos_x+" "+pos_y);
        }

        if(key.spawn.down){
            PlayState.spawnMonster(origin);
        }

        if (key.despawn.down) {
            PlayState.despawnMonster(getMonstersAroundPlayer());
        }

        if(key.talking.down){
            List<NPC> npcs = getNPCsAroundPlayer();
            if(!npcs.isEmpty()){
                for(NPC npc : npcs){
                    if(!npc.getCanTalk())continue;
                    npc.Talking();
                }
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

    public AABB getHitbox(){return hitbox;}

    private boolean checkmovent(){
        for(int i = F_Direction.UP.ordinal(); i < F_Direction.SIZE.ordinal(); i++){
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
                immortality = true;
            }

            if(ani.getFrame() == 4){
                System.out.println("Dash done");
                stillDash = false;
                immortality = false;
                statueAnimate = F_Statue_Animate.BasicMovement;
                ani.setFrames(sprite[F_List_Animation_Sprite.Walking.ordinal()].getSpriteArray(currentDirection.ordinal()));
                ani.setDelay(5);
                setDashPrssed(true);
            }
        }
    }

    public void setPlayerPosition(Vector2D origin){
        this.origin = origin;
    }

    public void setStatusAnimation(F_Statue_Animate flag){
        statueAnimate = flag;
    }

    // get grid in screen
    public int[][] getGridAroundPlayer(){
        int[][] grid = new int[10][10];
        int x = (int)origin.x / (GamePanel.Tile_Size * GamePanel.Zoom);
        int y = (int)origin.y / (GamePanel.Tile_Size * GamePanel.Zoom);
        int mapWidth = Map.getWidth();
        int mapHeight = Map.getHeight();
        x = Math.max(Math.min(x,(mapWidth * Map.getScale())- 10), 0);
        y = Math.max(Math.min(y,(mapHeight * Map.getScale())- 10), 0);
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                grid[i][j] = gridCellWrite.getGrid(x + i , y + j );
            }
        }
        return grid;
    }

    public int[][] getGridNearPlayer(Vector2D origin_future){
        int[][] n = new int[3][3];
        int x = (int)origin_future.x / (GamePanel.Tile_Size * GamePanel.Zoom);
        int y = (int)origin_future.y / (GamePanel.Tile_Size * GamePanel.Zoom);
        int mapWidth = Map.getWidth();
        int mapHeight = Map.getHeight();
        int hitbox_x = x +3;
        int hitbox_y = y +3;
        hitbox_x = Math.max(Math.min(hitbox_x,(mapWidth * Map.getScale()) - 3), 0);
        hitbox_y = Math.max(Math.min(hitbox_y,(mapHeight * Map.getScale()) - 3), 0);
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                n[i][j] = gridCellWrite.getGrid(hitbox_x + i , hitbox_y + j );
            }
        }
        return n;
    }

    private LinkedList<Monster> getMonstersAroundPlayer(){
        LinkedList<Monster> monsters = new LinkedList<>();
        for(Monster monster : PlayState.getActiveMonsters()){
            if(monster.isActive()){
                if(hitbox.collides(monster.getHitbox())){
                    monsters.add(monster);
                }
            }
        }
        return monsters;
    }

    private LinkedList<NPC> getNPCsAroundPlayer(){
        LinkedList<NPC> npcs = new LinkedList<>();
        for(NPC npc : PlayState.getActiveNPCs()){
            if(npc.isActive()){
                if(hitbox.collides(npc.getHitbox())){
                    npcs.add(npc);
                }
            }
        }
        return npcs;
    }

    @Override
    public void addObserver(Observer observer){
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer){
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(){
        for(Observer observer : observers){
            observer.updateListener(this);
        }
    }

    // draw grid around player
    public void drawGridAroundPlayer(Graphics2D g){
        int[][] grid = getGridAroundPlayer();
        int x = (int)origin.x / (GamePanel.Tile_Size * GamePanel.Zoom);
        int y = (int)origin.y / (GamePanel.Tile_Size * GamePanel.Zoom);
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                if(grid[i][j] == 1){
                    g.setColor(Color.RED);
                    g.fillRect((x  + i) * GamePanel.Tile_Size * GamePanel.Zoom - (int)Camera.getWorldX(), (y  + j) * GamePanel.Tile_Size * GamePanel.Zoom - (int)Camera.getWorldY(), GamePanel.Tile_Size * GamePanel.Zoom , GamePanel.Tile_Size * GamePanel.Zoom );
                }
            }
        }
    }

    @Override
    public void updateListener(PlayState playState) {
        if(count_dash_countdown != 0){
            System.out.println("Dash countdown: "+count_dash_countdown);
            count_dash_countdown--;
        }
        if(countdownImmortality > 0) {
            countdownImmortality--;
            if(countdownImmortality <= 0){
                immortality = false;
            }
        }
    }

    @Override
    public void onframeDamage(int startFrame, int endFrame) {
        if(stillAttack && ani.getFrame() >= startFrame && ani.getFrame() <= endFrame){
            EventManager.triggerEvent("PlayerAttack",playerSwordHitbox);
        }
    }
}
