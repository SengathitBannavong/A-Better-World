package game.state;

import game.GamePanel;
import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.entity.F_List_Animation_Sprite;
import game.entity.Player;
import game.graphic.Font;
import game.graphic.Sprite;
import game.physic.Vector2D;
import game.tile.TileManager;

import java.awt.*;

public class PlayState extends GameState {

    Font font;
    Player player;
    TileManager tileManager;

    public PlayState(GameStateManager gsm) {
        super(gsm);

        font = new Font("font/font.png", 10, 10);
        player = new Player(new Vector2D(300, 300), 64);
        tileManager = new TileManager("maps/world_map.txt");
    }

    @Override
    public void update(){
        player.update();
    }

    @Override
    public void input(MouseHandler mouse, KeyHandler key) {
        player.input(key, mouse);
    }

    @Override
    public void render(Graphics2D g) {
        tileManager.render(g);
        Sprite.drawArray(g, font, GamePanel.oldFrameCount + " FPS", new Vector2D(GamePanel.width - 130, 0), 32, 32, 16, 0);
        player.render(g);

    }

}
