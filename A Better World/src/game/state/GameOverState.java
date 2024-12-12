package game.state;


import game.GamePanel;
import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.entity.Player;
import game.graphic.Sprite;
import game.physic.Vector2D;

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
        if(key.talking.down) {// Key c for continue
            PlayState.getInit(gsm).loadCurrentSave();
            gsm.pop();
        }

        if(key.restart.down) {// Key r for restart
            PlayState.getInit(gsm).resetState();
            gsm.pop();
        }
    }


    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GamePanel.width, GamePanel.height);
        Sprite.drawArray(g, font, "Game Over", new Vector2D((float) (GamePanel.width /2) - 64, (float) (GamePanel.height /2) - 64 ) , 64, 64, 36, 0);
        Sprite.drawArray(g,font,"Press C to continue",new Vector2D((float) (GamePanel.width /2) - 128, (float) GamePanel.height /2 ), 32, 32, 25, 0);
        Sprite.drawArray(g,font,"Press R to restart",new Vector2D((float) (GamePanel.width /2) - 128, (float) GamePanel.height /2 + 64), 32, 32, 25, 0);
    }
}
