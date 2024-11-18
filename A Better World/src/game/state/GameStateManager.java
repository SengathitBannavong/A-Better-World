package game.state;



import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.enum_.Flag_GameState;

import java.awt.*;
import java.util.ArrayList;

public class GameStateManager {

    private ArrayList<GameState> states;

    public static Flag_GameState PLAY = Flag_GameState.PLAY;
    public static Flag_GameState MENU = Flag_GameState.MENU;
    public static Flag_GameState PAUSE = Flag_GameState.PAUSE;
    public static Flag_GameState GAMEOVER = Flag_GameState.GAMEOVER;

    public GameStateManager() {
        states = new ArrayList<>();
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
    }

    public void render(Graphics2D g) {
        for (GameState state : states) {
            state.render(g);
        }
    }
}
