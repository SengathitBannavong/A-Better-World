package game.state;


import game.Input.KeyHandler;
import game.Input.MouseHandler;

import java.awt.*;

public class GameOverState extends GameState {

    // Singleton pattern
    private static GameOverState instance = null;

    private GameOverState(GameStateManager gsm) {
        super(gsm);
    }

    public static synchronized GameOverState getInit(GameStateManager gsm) {
        if(instance == null) {
            instance = new GameOverState(gsm);
        }
        return instance;
    }

    @Override
    public void update() {

    }

    @Override
    public void input(MouseHandler mouse, KeyHandler key) {

    }


    @Override
    public void render(Graphics2D g) {

    }
}
