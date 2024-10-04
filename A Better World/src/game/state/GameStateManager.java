package game.state;



import game.Input.KeyHandler;
import game.Input.MouseHandler;

import java.awt.*;
import java.util.ArrayList;

public class GameStateManager {

    private ArrayList<GameState> states;

    public static final int PLAY = 0;
    public static final int MENU = 1;
    public static final int PAUSE = 2;
    public static final int GAMEOVER = 3;

    public GameStateManager() {
        states = new ArrayList<>();
        states.add(new PlayState(this));
    }

    public void pop(int state) {
        states.remove(state);
    }

    public void add(int state) {
        if(state == PLAY) {
            states.add(new PlayState(this));
        }
        if(state == MENU) {
            states.add(new MenuState(this));
        }
        if(state == PAUSE) {
            states.add(new PauseState(this));
        }
        if(state == GAMEOVER) {
            states.add(new GameOverState(this));
        }
    }

    public void addAndPop(int state) {
        pop(0);
        add(state);
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
