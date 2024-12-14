package game.state;

import game.Debug;
import game.GamePanel;
import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.design.Observarable;
import game.design.Observer;
import game.entity.Item;
import game.entity.Monster;
import game.entity.NPC;
import game.entity.Player;
import game.enum_.F_Statue_Animate;
import game.enum_.F_Type_Sprite_Entity;
import game.enum_.Flag_GameState;
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
import java.util.Arrays;
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

    // Item
    private static List<Item> activeItems;
    private static final ItemSpawner itemSpawner = new ItemSpawner();

    // CountDown
    private static int countdown_spawn = 0;

    // Event
    Event bufferEvent = null;
    private static boolean[] Event_Ending;

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
        monsterPool = MonsterPool.getInit(15, new MonsterFactory(), player, F_Type_Sprite_Entity.Type3.ordinal());
        npcPool = NPCPool.getInit(5, new NPCFactory(),F_Type_Sprite_Entity.Type4.ordinal());
        itemPool = ItemPool.getInit(5, new ItemFactory(), F_Type_Sprite_Entity.Type3.ordinal());
        maps = cachedMap;
        Event_Ending = new boolean[Map_Index_Teleport.Dungeon.ordinal() + 2];
        resetEventEnding();
        GridCellWrite.parseGrid("maps/"+ Map_name[index]+".txt");
        GridCellWrite.setPath(Map_name[index]);
        showMapTeleport();
        activeMonsters = new ArrayList<>();
        activeNPCs = new ArrayList<>();
        activeItems = new ArrayList<>();
        LoadDataFromEvent(Map_name[index]);
        renderQueue = new RenderQueue();
        EventManager.addListener(this);
        SaveStatusInPlaystate.getInstance().load(player,index);
    }

    public void resetState(){
        clear();
        index = 0;
        bufferMap = mapParse.parsing("maps/"+ Map_name[0] +"_1_Layer.xml");
        setMap();
        player.setPlayerPosition(Map_origin[index]);
        player.setStatusAnimation(F_Statue_Animate.BasicMovement);
        player.Init();
        GridCellWrite.parseGrid("maps/"+ Map_name[index]+".txt");
        GridCellWrite.setPath(Map_name[index]);
        resetEventEnding();
        LoadDataFromEvent(Map_name[index]);
        SaveStatusInPlaystate.getInstance().load(player,index);
    }

    public void loadCurrentSave(){
        SaveStatusInPlaystate save = SaveStatusInPlaystate.getInstance();
        clear();
        index = save.getMapIndex();
        setMap();
        player.setPlayerPosition(save.getPlayerPosition());
        player.setHp(save.getHealthPlayer());
        player.setStatusAnimation(F_Statue_Animate.BasicMovement);
        GridCellWrite.parseGrid("maps/"+ Map_name[index]+".txt");
        GridCellWrite.setPath(Map_name[index]);
        setEventEnding(index,false);
        LoadDataFromEvent(Map_name[index]);
        if(index == Map_Index_Teleport.Dungeon.ordinal()){
            GamePanel.playMusic(4);
        }else if(index == Map_Index_Teleport.Middle.ordinal() || index == Map_Index_Teleport.Level1.ordinal()) {
            GamePanel.playMusic(9);
        }else{
            GamePanel.playMusic(10);
        }
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
        itemUpdate();
        npcUpdate();
        checkItemPickupCollision();
        sentCoundown();
    }

    private void LoadDataFromEvent(String event){
        bufferEvent = GameStateManager.getEvent(event);
        if(bufferEvent == null) {
            System.out.println("Event not found");
            return;
        }

        int size_monster = bufferEvent.getCapacityMonster();
        if(size_monster > 0 && !Event_Ending[index]) {
            List<Vector2D> monsterPosition = bufferEvent.getMonsterSpawnPoints();
            List<F_Type_Sprite_Entity> monsterTypes = bufferEvent.getMonsterTypes();
            for (int i = 0; i < size_monster; i++) {
                MonsterSpawner.spawnMonster(monsterPosition.get(i), monsterTypes.get(i));
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

        int size_item = bufferEvent.getCapacityItem();
        if(size_item > 0 && !Event_Ending[index]){
            List<Vector2D> itemPosition = bufferEvent.getItemSpawnPoints();
            for (int i = 0; i < size_item; i++){
                F_Type_Sprite_Entity temp = bufferEvent.getItemTypes().get(i);
                itemSpawner.spawnItem(itemPosition.get(i), temp);
            }
        }
    }

    private void monsterUpdate(){
        for(Monster monster : activeMonsters){
            monster.update();
        }
        if(activeMonsters.isEmpty()){
            addEventEnding(index);
        }
    }

    private void npcUpdate(){
        for(NPC npc : activeNPCs){
            npc.update();
        }
    }

    private void itemUpdate(){
        if (activeItems == null || activeItems.isEmpty()) return;
        for(Item item : activeItems){
            item.update();
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
        if(key.test.down  && Debug.admin){
            EventManager.triggerEvent("PlayerGetHit", 1);
        }
        if(key.dead.down  && Debug.admin){
            EventManager.triggerEvent("PlayerDead");
        }
    }

    private void GoNextMapByAdmin(KeyHandler key){
        if(key.passMapAD.down  && Debug.admin) {
            int i = ++index % 5;
            index = i;
            bufferMap = mapParse.parsing("maps/"+ Map_name[i] +"_1_Layer.xml");
            GridCellWrite.parseGrid("maps/"+ Map_name[i]+".txt");
            GridCellWrite.setPath(Map_name[i]);
            player.setPlayerPosition(Map_origin[i]);
            setMap();
            clear();
            LoadDataFromEvent(Map_name[i]);
            SaveStatusInPlaystate.getInstance().load(player,i);
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
                SaveStatusInPlaystate.getInstance().load(player, i);
                GamePanel.playSE(8);
                System.gc();
                try {
                    Thread.sleep(200); // 200 milliseconds delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void clear(){
        despawnMonster(activeMonsters);
        despawnNPC(activeNPCs);
        despawnItem(activeItems);
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
        for (Item item : activeItems) {
            renderQueue.addToQueue(item);
        }
        renderQueue.render(g);
        player.render(g);
        renderUI(g);

        drawTeleport(g);
    }

    public void renderUI(Graphics2D g){
        player.renderUI(g);
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
        }else{
            maps = cachedMap;
        }
    }

    public static List<Monster> getActiveMonsters(){
        return activeMonsters;
    }
    public static List<NPC> getActiveNPCs(){return activeNPCs;}

    public static void spawnMonster(Vector2D position){
        if(countdown_spawn == 0){
            countdown_spawn = 2;
            monsterSpawner.spawnMonster(position, F_Type_Sprite_Entity.Type1);
            System.out.println("Spawn");
        }
    }

    public static void despawnMonster(List<Monster> monsters){
        monsterSpawner.despawnMonster(monsters);
    }
    public static void despawnNPC(List<NPC> npcs){npcSpawner.despawnNPC(npcs);}
    public static void despawnItem(List<Item> items){itemSpawner.despawnItem(items);}

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

    public static void addEventEnding(int index){
        Event_Ending[index] = true;
    }

    public static boolean[] getEventEnding(){
        return Event_Ending;
    }

    public static void setEventEnding(int index, boolean value){
        Event_Ending[index] = value;
    }

    public static void resetEventEnding(){
        Arrays.fill(Event_Ending, false);
    }

    public static void addItem(Item item){
        activeItems.add(item);
    }

    public static void removeItem(Item item){
        Iterator<Item> iterator = activeItems.iterator();
        while(iterator.hasNext()){
            Item temp = iterator.next();
            if(temp.equals(item)){
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


    private void checkItemPickupCollision() {
        Iterator<Item> itemIterator = activeItems.iterator();
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();

            // Check collision between Player and Item
            if (player.getHitbox().collides(item.getHitbox())) {
                player.pickUpItem(item);

                // Remove this item
                itemIterator.remove();

                System.out.println("Player picked up item: " + item.getType());
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
                        if(monster.getImmortality())continue;
                        monster.takeDamage(player.getAttackDemage());
                        if(monster.getHp() <= 0){
                            if(monster.getType() == F_Type_Sprite_Entity.Type3){
                                GameStateManager.GameEnding = true;
                                player.CantMove();
                                GamePanel.playMusic(0);
                            }else {
                                System.out.println("Monster dead");
                                monster.deactivate();
                                deadMonsters.add(monster);
                            }
                        }
                    }
                }
                despawnMonster(deadMonsters);
                break;
            case "PlayerGetHit":
                player.takeDamage((int) args[0]);
                break;
            case "PlayerDead":
                GamePanel.playMusic(2);
                gsm.addState(Flag_GameState.GAMEOVER);
                break;
            case "MonsterAttack":
                AABB monsterBox = (AABB) args[0];
                if(monsterBox.collides(player.getHitbox())){
                    player.takeDamage(1);
                }
                break;
            case "LastWordOfBoss":
                GameStateManager.setBufferState("LastWordOfBoss");
                break;
            default:
                break;
        }
    }
}