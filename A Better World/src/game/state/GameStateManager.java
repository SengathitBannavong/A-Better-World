package game.state;
import game.Debug;
import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.enum_.Flag_GameState;
import game.enum_.Map_Index_Teleport;
import game.physic.Vector2D;


import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class GameStateManager {

    private ArrayList<GameState> states;

    public static Flag_GameState PLAY = Flag_GameState.PLAY;
    public static Flag_GameState MENU = Flag_GameState.MENU;
    public static Flag_GameState PAUSE = Flag_GameState.PAUSE;
    public static Flag_GameState GAMEOVER = Flag_GameState.GAMEOVER;

    public GameStateManager() {
        states = new ArrayList<>();
        parseMapName();
        showMapName();
        CachesState.setState(new PlayState(this));
        states.add(CachesState.getState());
    }

    public void pop(int state) {
        CachesState.setState(states.get(state));
        states.remove(state);
    }

    public void add(int state) {
        if(state == PLAY.ordinal()) {
            if(CachesState.isStateNull()) {
                states.add(new PlayState(this));
            } else {
                states.add(CachesState.getState());
            }
        }
        if(state == MENU.ordinal()) {
            states.add(new MenuState(this));
        }
        if(state == PAUSE.ordinal()) {
            states.add(new PauseState(this));
        }
        if(state == GAMEOVER.ordinal()) {
            states.add(new GameOverState(this));
        }
    }

    public void addAndPop(int state) {
        System.out.println("Swift state");
        add(state);
        pop(0);
    }

    public void update() {
        for (GameState state : states) {
            state.update();
        }
    }

    public void input(MouseHandler mouse, KeyHandler key) {
        for (GameState state : states) {
            state.input(mouse, key);
        }
        if(key.collition.down) {
            Debug.collision = !Debug.collision;
            //delay
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void render(Graphics2D g) {
        for (GameState state : states) {
            state.render(g);
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
}
