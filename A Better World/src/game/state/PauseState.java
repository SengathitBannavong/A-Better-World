package game.state;

import game.GamePanel;
import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.enum_.Flag_GameState;
import game.graphic.Sprite;
import game.physic.Vector2D;

import java.awt.*;

public class PauseState extends GameState {
    // Singleton pattern
    private static PauseState instance = null;

    private PauseState(GameStateManager gsm) {
        super(gsm);
    }

    public static synchronized PauseState getInit(GameStateManager gsm) {
        if(instance == null) {
            instance = new PauseState(gsm);
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
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GamePanel.width, GamePanel.height);
        Sprite.drawArray(g, font, "Pause", new Vector2D((float) (GamePanel.width /2) - 64, (float) (GamePanel.height /2) - 64 ) , 64, 64, 36, 0);
        Sprite.drawArray(g,font,"My Mom was Call",new Vector2D((float) (GamePanel.width /2) - 128, (float) GamePanel.height /2 ), 32, 32, 25, 0);
        Sprite.drawArray(g,font,"Press P to continue",new Vector2D((float) (GamePanel.width /2) - 128, (float) GamePanel.height /2 + 64), 32, 32, 25, 0);
    }

}