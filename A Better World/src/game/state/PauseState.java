package game.state;

import game.GamePanel;
import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.graphic.Sprite;
import game.physic.Vector2D;

import java.awt.*;

public class PauseState extends GameState {
    public PauseState(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void update() {

    }

    @Override
    public void input(MouseHandler mouse, KeyHandler key) {
        if(key.pause.down) {
            gsm.addAndPop(GameStateManager.PLAY.ordinal());
            try {
                Thread.sleep(200); // 200 milliseconds delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GamePanel.width, GamePanel.height);
        Sprite.drawArray(g, font, "Pause", new Vector2D((float) GamePanel.width /2, (float) GamePanel.height /2), 32, 32, 16, 0);
    }

}
