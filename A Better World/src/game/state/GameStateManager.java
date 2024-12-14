package game.state;
import game.Debug;
import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.enum_.Flag_GameState;
import game.enum_.Map_Index_Teleport;
import game.event.Event;
import game.event.EventManager;
import game.physic.Vector2D;


import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class GameStateManager implements EventManager.EventListener  {

    private final Stack<GameState> states;
    private static GameState bufferState = null;

    private static Map<String, Event> events;

    // Singleton pattern
    private static GameStateManager instance = null;

    // GameEnding
    public static boolean GameEnding = false;

    public GameStateManager() {
        states = new Stack<>();
        events = new HashMap<>();
        instance = this;
        EventManager.addListener(this);
        Init();
    }

    public static synchronized GameStateManager getInstance() {
        if(instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }

    private void Init(){
        parseMapName();
        showMapName();
        LoadEvent(Map_Index_Teleport.Dungeon.ordinal() + 1);
        CachesState.setState(PlayState.getInit(this));
        states.push(CachesState.getState());
    }

    public void addState(Flag_GameState state) {
        switch (state) {
            case PLAY:
                states.push(PlayState.getInit(this));
                break;
            case MENU:
                states.push(MenuState.getInit(this));
                break;
            case PAUSE:
                states.push(PauseState.getInit(this));
                break;
            case GAMEOVER:
                states.push(GameOverState.getInit(this));
                break;
            case ENDCREDIT:
                states.push(GameEndingState.getInit(this));
                break;
            default:
                break;
        }
    }

    public void addAndPop(Flag_GameState state) {
        states.pop();
        addState(state);
    }

    public void pop() {
        if(!states.isEmpty()){
            states.pop();
        }
    }

    private void LoadEvent(int size){
        for(int i = 0; i < size; i++){
            System.out.println("Load Event name: "+GameState.Map_name[i]);
           Event event = new Event(GameState.Map_name[i]);
           events.put(GameState.Map_name[i], event);
        }
    }

    public static Event getEvent(String name){
        return events.get(name);
    }

    public void update() {
        if(bufferState!= null){
            bufferState.update();
            return;
        }

        if(!states.isEmpty()){
            states.peek().update();
        }
    }

    public void input(MouseHandler mouse, KeyHandler key) {

       if(key.pause.down) {
           if(states.peek() instanceof PlayState) {
               System.out.println("Pause");
               addState(Flag_GameState.PAUSE);
               delay(200);
           }else{
                System.out.println("Resume");
                pop();
                delay(200);
           }
       }

       if(key.menu.down) {
           if (states.peek() instanceof PlayState) {
               System.out.println("Menu");
               addState(Flag_GameState.MENU);
               delay(200);
           }
       }
        if(bufferState!= null){
            bufferState.input(mouse, key);
            return;
        }

        if(!states.isEmpty()){
            if(states.peek() instanceof GameEndingState){
                states.peek().input(mouse, key);
            }
        }
        if(GameEnding)return;
        if(!states.isEmpty()){
            states.peek().input(mouse, key);
        }
        CollisionHandleInput(key);

    }

    public void render(Graphics2D g) {
       if(!states.isEmpty()){
           states.peek().render(g);
       }

       if(bufferState!= null){
           bufferState.render(g);
       }
    }

    private void CollisionHandleInput(KeyHandler key) {
        if(key.collition.down  && Debug.admin) {
            Debug.collision = !Debug.collision;
            //delay
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseMapName() {
        try (BufferedReader br = new BufferedReader(new FileReader("res/maps/dataMap.txt"))) {
            String line;
            int size = 0;
            if ((line = br.readLine()) != null && line.contains("=")) {
                size = Integer.parseInt(line.split("=")[1].trim().replace("\"", ""));
            }
            String[] mapNames = new String[size];
            Vector2D[] mapOrigins = new Vector2D[size];
            int index = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\.");
                if (parts.length == 2) {
                    String[] parts2 = parts[1].split(" ");
                    mapNames[index] = parts2[0];
                    Vector2D vector2D = new Vector2D(Integer.parseInt(parts2[1]), Integer.parseInt(parts2[2]));
                    mapOrigins[index++] = vector2D;
                }
            }
            GameState.Map_name = mapNames;
            GameState.Map_origin = mapOrigins;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMapName() {
        for (String name : GameState.Map_name) {
            System.out.println(name);
        }
    }

    public static String getMapName(int index) {
        return GameState.Map_name[index];
    }

    public static void setBufferState(String path){
        bufferState = new DialogConversationState(instance ,path);
    }

    public static GameState getBufferState(){
        return bufferState;
    }

    public void clearBufferState(){
        bufferState = null;
    }

    @Override
    public void onEvent(String eventName, Object... args) {
        if(eventName.equals("EndGame")){
            GameEnding = true;
            addState(Flag_GameState.ENDCREDIT);
        }
    }

    public void delay(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
