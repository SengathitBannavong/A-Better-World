package game.state;

import game.GamePanel;
import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.entity.Monster;
import game.entity.Player;
import game.enum_.Map_Index_Teleport;
import game.graphic.Sprite;
import game.object.BoxTP;
import game.object.Map_teleport;
import game.physic.Vector2D;
import game.pool.MonsterFactory;
import game.pool.MonsterPool;
import game.pool.MonsterSpawner;
import game.tile.GridCellWrite;
import game.tile.Map;
import game.tile.MapParse;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlayState extends GameState {

    private final Player player;
    Map maps;
    private static List<Monster> activeMonsters;
    private static final MonsterSpawner monsterSpawner = new MonsterSpawner();

    private static final MapParse mapParse = new MapParse();
    private static Map cachedMap = mapParse.parsing("maps/"+ Map_name[0] +"_1_Layer.xml");
    private Map bufferMap = null;
    private int index = 0;
    private static Map_teleport[] map_teleport = parseMapTeleport();

    private static int countdown_spawn = 0;

    public PlayState(GameStateManager gsm) {
        super(gsm);
        player = new Player(Map_origin[index], GamePanel.Tile_Size * GamePanel.Scale, 64); // scale the player
        monsterPool = MonsterPool.getInit(15, new MonsterFactory(), player);
        maps = cachedMap;
        GridCellWrite.parseGrid("maps/"+ Map_name[index]+".txt");
        GridCellWrite.setPath(Map_name[index]);
        showMapTeleport();
        activeMonsters = new ArrayList<>();
    }

    @Override
    public void update() {
        player.update();
        monsterUpdate();
        countdownSpawn();
    }

    private void monsterUpdate(){
        for(Monster monster : activeMonsters){
            monster.update();
        }
    }

    public static void countdownSpawn(){
        if(countdown_spawn > 0 && OneSecond){
            countdown_spawn--;
            OneSecond = false;
        }
    }

    @Override
    public void input(MouseHandler mouse, KeyHandler key) {
        if(key.pause.down) {
            int i = ++index % 5;
            bufferMap = mapParse.parsing("maps/"+ Map_name[i] +"_1_Layer.xml");
            GridCellWrite.parseGrid("maps/"+ Map_name[i]+".txt");
            GridCellWrite.setPath(Map_name[i]);
            player.setPlayerPosition(Map_origin[i]);
            setMap();
            try {
                Thread.sleep(200); // 200 milliseconds delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        BoxTP temp;
        if(key.nextMap.down){
            temp = PlayState.map_teleport[index % 5].checkCollision(player.getHitbox());
            System.out.println(temp == null);
            if(temp != null) {
                System.out.println("Teleport to " + temp.getIndex_tp_to_map());
                int i = temp.getIndex_tp_to_map();
                index = i;
                bufferMap = mapParse.parsing("maps/" + Map_name[i] + "_1_Layer.xml");
                GridCellWrite.parseGrid("maps/" + Map_name[i] + ".txt");
                GridCellWrite.setPath(Map_name[i]);
                player.setPlayerPosition(temp.getTp_to_position());
                setMap();
                try {
                    Thread.sleep(200); // 200 milliseconds delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        player.input(key, mouse);
    }

    @Override
    public void render(Graphics2D g) {
        maps.drawMap(g, Player.getCamera());
        Sprite.drawArray(g, font, GamePanel.oldFrameCount + " FPS", new Vector2D(GamePanel.width - 130, GamePanel.height - 50), 32, 32, 16, 0);
        player.render(g);
        monsterRender(g);
        drawTeleport(g);
    }

    public void monsterRender(Graphics2D g){
        for(Monster monster : activeMonsters){
            monster.render(g);
        }
    }

    public void drawTeleport(Graphics2D g){
        PlayState.map_teleport[index % 5].render(g);
        BoxTP temp = PlayState.map_teleport[index % 5].checkCollision(player.getHitbox());
        if(temp != null){
            Sprite.drawArray(g, font, "Press Q To Next Map", new Vector2D((float) 80,30), 32, 32, 24, 0);
        }
    }

    private void setMap(){
        if(bufferMap != null) {
            maps = bufferMap;
            cachedMap = bufferMap;
            bufferMap = null;
        }
    }

    public static List<Monster> getActiveMonsters(){
        return activeMonsters;
    }

    public static void spawnMonster(Vector2D position){
        if(countdown_spawn == 0){
            countdown_spawn = 2;
            monsterSpawner.spawnMonster(position);
            System.out.println("Spawn");
        }
    }

    public static void despawnMonster(List<Monster> monsters){
        monsterSpawner.despawnMonster(monsters);
    }

    public static void addMonster(Monster monster){
        activeMonsters.add(monster);
    }

    public static Monster removeMonster(Monster monster){
        Iterator<Monster> iterator = activeMonsters.iterator();
        while(iterator.hasNext()){
            Monster temp = iterator.next();
            if(temp.equals(monster)){
                iterator.remove();
                return temp;
            }
        }
        return null;
    }

    private static Map_teleport[] parseMapTeleport() {
        Map_teleport[] map_teleports = new Map_teleport[Map_Index_Teleport.Dungeon.ordinal() + 1];
        List<BoxTP> boxTPs;
        Vector2D buffer = null,buffer2 = null;
        int[] offset_x = {100,-30, 22,-30, 22,0};
        int[] offset_y = { 30, 30,-120, 30,-60,100};
        int index = 0;
        for(int i = 0; i < Map_Index_Teleport.Dungeon.ordinal(); i++){
            buffer = Map_origin[i + Map_Index_Teleport.Village_Forest.ordinal()];
            boxTPs = new ArrayList<>();
            if(i == Map_Index_Teleport.Forest.ordinal()) {
                buffer2 = Map_origin[Map_Index_Teleport.Village.ordinal()];
                boxTPs.add(new BoxTP(new Vector2D(buffer.x, buffer.y - 128), 264, 264, i, Map_Index_Teleport.Village.ordinal(), new Vector2D(buffer2.x, buffer2.y)));
            }else{
                buffer2 = Map_origin[Map_Index_Teleport.Village.ordinal() + i];
                boxTPs.add(new BoxTP(new Vector2D(buffer.x + offset_x[index], buffer.y + offset_y[index]), 128, 128, i, i + Map_Index_Teleport.Village.ordinal(), new Vector2D(buffer2.x, buffer2.y)));
                index++;
                buffer = Map_origin[i];
                buffer2 = Map_origin[Map_Index_Teleport.Dungeon.ordinal() + i];
                boxTPs.add(new BoxTP(new Vector2D(buffer.x + offset_x[index] , buffer.y+ offset_y[index]), 128, 128, i, i - Map_Index_Teleport.Village.ordinal() , new Vector2D(buffer2.x, buffer2.y)));
                index++;
            }
            map_teleports[i] = new Map_teleport(boxTPs);
        }
        boxTPs = new ArrayList<>();
        buffer = new Vector2D(495,170);
        buffer2 = Map_origin[Map_Index_Teleport.Dungeon_Level1.ordinal()];
        boxTPs.add(new BoxTP(new Vector2D(buffer.x, buffer.y), 64, 64, Map_Index_Teleport.Dungeon.ordinal(), Map_Index_Teleport.Level1.ordinal(), new Vector2D(buffer2.x, buffer2.y)));
        buffer = new Vector2D(1023,698);
        buffer2 = Map_origin[Map_Index_Teleport.Middle_Village.ordinal()];
        boxTPs.add(new BoxTP(new Vector2D(buffer.x, buffer.y), 44, 44, Map_Index_Teleport.Dungeon.ordinal(), Map_Index_Teleport.Village.ordinal(), new Vector2D(buffer2.x, buffer2.y)));
        map_teleports[Map_Index_Teleport.Dungeon.ordinal()] = new Map_teleport(boxTPs);
        return  map_teleports;
    }

    private static void showMapTeleport(){
        for(int i = 0; i <= Map_Index_Teleport.Dungeon.ordinal(); i++){
            System.out.println("Map teleport " + i);
            for(BoxTP boxTP : map_teleport[i].getBoxTPs()){
                System.out.print("Map teleport " + i + " " + boxTP.getIndex_map() + " " + boxTP.getIndex_tp_to_map());
                System.out.println(" tp to " + boxTP.getTp_to_position().x + " " + boxTP.getTp_to_position().y);
            }
        }
    }
}