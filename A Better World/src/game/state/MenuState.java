package game.state;

import game.Input.KeyHandler;
import game.Input.MouseHandler;

import javax.swing.*;
import java.awt.*;

public class MenuState extends GameState {

    // Singleton pattern
    private static MenuState instance = null;

    // TODO if have more time implement MenuState for the game
    private MenuState(GameStateManager gsm) {
        super(gsm);
    }

    public static synchronized MenuState getInit(GameStateManager gsm) {
        if (instance == null) {
            instance = new MenuState(gsm);
        }
        return instance;
    }

    @Override
    public void update() {
        // Any game state update logic (if necessary)
    }

    @Override
    public void input(MouseHandler mouse, KeyHandler key) {
        // Handle user input (mouse, key events) if necessary
    }

    @Override
    public void render(Graphics2D g) {
        // This will be rendered through the panel inside the GamePanel's paintComponent
    }
}
