package game.state;

import game.Input.KeyHandler;
import game.Input.MouseHandler;

import java.awt.*;

public class MenuState extends GameState {

    // Singleton pattern
    private static MenuState instance = null;

    private MenuState(GameStateManager gsm) {
        super(gsm);
    }

    public static synchronized MenuState getInit(GameStateManager gsm) {
        if(instance == null) {
            instance = new MenuState(gsm);
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
