package game.state;

import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.graphic.Font;
import game.physic.Vector2D;
import game.pool.MonsterFactory;
import game.pool.MonsterPool;

import java.awt.*;

public abstract class GameState {

    protected GameStateManager gsm;
    public static final game.graphic.Font font = new Font("font/font.png", 10, 10);
    protected static String [] Map_name;
    protected static Vector2D[] Map_origin;
    protected static MonsterPool monsterPool;
    protected static boolean OneSecond = false;

    public GameState(GameStateManager gsm) {
        this.gsm = gsm;
    }

    public abstract void update();
    public abstract void input(MouseHandler mouse, KeyHandler key);
    public abstract void render(Graphics2D g);

    public static void setOneSecond(boolean second) {
        OneSecond = second;
    }
}
