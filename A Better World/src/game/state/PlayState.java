package game.state;

import game.GamePanel;
import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.design.Observarable;
import game.design.Observer;
import game.entity.Monster;
import game.entity.NPC;
import game.entity.Player;
import game.enum_.F_Type_Sprite_Entity;
import game.enum_.Map_Index_Teleport;
import game.event.Event;
import game.event.EventManager;
import game.graphic.RenderQueue;
import game.graphic.Sprite;
import game.object.BoxTP;
import game.object.Map_teleport;
import game.physic.AABB;
import game.physic.Vector2D;
import game.pool.*;
import game.tile.GridCellWrite;
import game.tile.Map;
import game.tile.MapParse;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlayState extends GameState implements Observarable, EventManager.EventListener {

    // Entity
    private final Player player;
    private static List<Monster> activeMonsters;
    private static final MonsterSpawner monsterSpawner = new MonsterSpawner();
    private static List<NPC> activeNPCs;
    private static final NPCSpawner npcSpawner = new NPCSpawner();

    // Map
    private Map maps;
    private static final MapParse mapParse = new MapParse();
    private static Map cachedMap = mapParse.parsing("maps/"+ Map_name[0] +"_1_Layer.xml");
    private Map bufferMap = null;
    private int index = 0;
    private static final Map_teleport[] map_teleport = parseMapTeleport();

    // CountDown
    private static int countdown_spawn = 0;

    // Event
    Event bufferEvent = null;

    // Conversation

    // Render
    private final RenderQueue renderQueue;

    //observer pattern
    private static List<Observer> observers = new ArrayList<>();

    // Singleton pattern
    private static PlayState instance = null;

    private PlayState(GameStateManager gsm) {
        super(gsm);
        instance = this;
        player = Player.getInstance(Map_origin[index], GamePanel.Tile_Size * GamePanel.Scale, 64,this); // scale the player
        monsterPool = MonsterPool.getInit(25, new MonsterFactory(), player, F_Type_Sprite_Entity.Type1.ordinal());
        npcPool = NPCPool.getInit(5, new NPCFactory(),F_Type_Sprite_Entity.Type4.ordinal());
        maps = cachedMap;
        GridCellWrite.parseGrid("maps/"+ Map_name[index]+".txt");
        GridCellWrite.setPath(Map_name[index]);
        showMapTeleport();
        activeMonsters = new ArrayList<>();
        activeNPCs = new ArrayList<>();
        LoadDataFromEvent(Map_name[index]);
        renderQueue = new RenderQueue();
        EventManager.addListener(this);
    }

    public static synchronized PlayState getInit(GameStateManager gsm) {
        if(instance == null) {
            instance = new PlayState(gsm);
        }
        return instance;
    }

    public static synchronized PlayState getInstance() {
        return instance;
    }

    @Override
    public void update() {
        player.update();
        monsterUpdate();
        npcUpdate();
        sentCoundown();
    }

    private void LoadDataFromEvent(String event){
        bufferEvent = GameStateManager.getEvent(event);
        if(bufferEvent == null) {
            System.out.println("Event not found");
            return;
        }

        int size_monster = bufferEvent.getCapacityMonster();
        if(size_monster > 0) {
            List<Vector2D> monsterPosition = bufferEvent.getMonsterSpawnPoints();
            for (int i = 0; i < size_monster; i++) {
                monsterSpawner.spawnMonster(monsterPosition.get(i));
            }
        }
        int size_npc = bufferEvent.getCapacityNPC();
        if(size_npc > 0) {
            List<Vector2D> npcPosition = bufferEvent.getNpcSpawnPoints();
            List<Boolean> npcCanTalk = bufferEvent.getIsCanTalk_NPC();
            List<String> npcPath = bufferEvent.getPath_NPC();
            for (int i = 0; i < size_npc; i++) {
                F_Type_Sprite_Entity temp = bufferEvent.getNpcTypes().get(i);
                boolean canTalk = npcCanTalk.get(i);
                String path = npcPath.get(i);
                npcSpawner.spawnNPC(npcPosition.get(i), temp);
                activeNPCs.get(i).setCanTalk(canTalk);
                activeNPCs.get(i).setPathConversation(path);
            }
        }
    }

    private void monsterUpdate(){
        for(Monster monster : activeMonsters){
            monster.update();
        }
    }

    private void npcUpdate(){
        for(NPC npc : activeNPCs){
            npc.update();
        }
    }

    public void sentCoundown(){
        if(OneSecond) {
            notifyObservers();
            countdownSpawn();
            OneSecond = false;
        }
    }

    public void countdownSpawn(){
        if(countdown_spawn > 0 ){
            countdown_spawn--;
            System.out.println("Countdown spawn " + countdown_spawn);

        }

    }

    @Override
    public void input(MouseHandler mouse, KeyHandler key) {
        GoNextMapByAdmin(key);
        GoNextMapByBox(key);
        player.input(key, mouse);
        if(key.test.down){
            EventManager.triggerEvent("PlayerGetHit", 1);
        }
    }

    private void GoNextMapByAdmin(KeyHandler key){
        if(key.pause.down) {
            int i = ++index % 5;
            bufferMap = mapParse.parsing("maps/"+ Map_name[i] +"_1_Layer.xml");
            GridCellWrite.parseGrid("maps/"+ Map_name[i]+".txt");
            GridCellWrite.setPath(Map_name[i]);
            player.setPlayerPosition(Map_origin[i]);
            setMap();
            clear();
            LoadDataFromEvent(Map_name[i]);
            try {
                Thread.sleep(200); // 200 milliseconds delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void GoNextMapByBox(KeyHandler key){
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
                clear();
                LoadDataFromEvent(Map_name[i]);
                try {
                    Thread.sleep(200); // 200 milliseconds delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void clear(){
        despawnMonster(activeMonsters);
        despawnNPC(activeNPCs);
    }

    @Override
    public void render(Graphics2D g) {
        maps.drawMap(g, Player.getCamera());
        Sprite.drawArray(g, font, GamePanel.oldFrameCount + " FPS", new Vector2D(GamePanel.width - 130, GamePanel.height - 50), 32, 32, 16, 0);
        for (Monster monster : activeMonsters) {
            renderQueue.addToQueue(monster);
        }
        for (NPC npc : activeNPCs) {
            renderQueue.addToQueue(npc);
        }
        renderQueue.render(g);
        player.render(g);
        drawTeleport(g);
    }

    public void monsterRender(Graphics2D g){
        if(activeMonsters == null || activeMonsters.isEmpty()) return;
        for(Monster monster : activeMonsters){
            monster.render(g);
        }
    }

    public void npcRender(Graphics2D g){
        if(activeNPCs == null || activeNPCs.isEmpty()) return;
        for(NPC npc : activeNPCs){
            npc.render(g);
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
    public static List<NPC> getActiveNPCs(){return activeNPCs;}

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
    public static void despawnNPC(List<NPC> npcs){npcSpawner.despawnNPC(npcs);}

    public static void addMonster(Monster monster){
        activeMonsters.add(monster);
    }

    public static void removeMonster(Monster monster){
        activeMonsters.removeIf(temp -> temp.equals(monster));
    }

    public static void addNPC(NPC npc){
        activeNPCs.add(npc);
    }

    public static void removeNPC(NPC npc){
        Iterator<NPC> iterator = activeNPCs.iterator();
        while(iterator.hasNext()){
            NPC temp = iterator.next();
            if(temp.equals(npc)){
                iterator.remove();
            }
        }
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

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for(Observer observer : observers){
            observer.updateListener(this);
        }
    }

    @Override
    public void onEvent(String eventName, Object... args) {
        switch (eventName){
            case "PlayerAttack":
                AABB attackBox = (AABB) args[0];
                List<Monster> deadMonsters = new ArrayList<>();
                for(Monster monster : activeMonsters){
                    if(monster.getHitbox().collides(attackBox)){
                        monster.takeDamage(player.getAttackDemage());
                        if(monster.getHp() <= 0){
                           monster.deactivate();
                            deadMonsters.add(monster);
                        }
                    }
                }
                despawnMonster(deadMonsters);
                break;
            case "PlayerGetHit":
                player.takeDamage((int) args[0]);
                break;
            default:
                break;
        }
    }
}