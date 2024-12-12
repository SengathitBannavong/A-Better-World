package game.state;

import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.graphic.Font;
import game.physic.Vector2D;
import game.pool.MonsterPool;
import game.pool.NPCPool;

import java.awt.*;

public abstract class GameState {

    // GameStateManager
    protected GameStateManager gsm;

    // Font
    public static final Font font = new Font("font/font.png", 10, 10);

    // Map data
    protected static String [] Map_name;
    protected static Vector2D[] Map_origin;

    //pool
    // TODO - Implement item Hang's item pool
    protected static MonsterPool monsterPool;
    protected static NPCPool npcPool;

    // Timer
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
