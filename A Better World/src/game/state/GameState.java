package game.state;

import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.graphic.Font;

import java.awt.*;

public abstract class GameState {

    protected GameStateManager gsm;
    protected static final game.graphic.Font font = new Font("font/font.png", 10, 10);

    public GameState(GameStateManager gsm) {
        this.gsm = gsm;
    }

    public abstract void update();
    public abstract void input(MouseHandler mouse, KeyHandler key);
    public abstract void render(Graphics2D g);
}
