package game.state;

import game.GamePanel;
import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.entity.Player;
import game.graphic.Sprite;
import game.object.Box;
import game.physic.Vector2D;
import game.tile.Map;
import game.tile.MapParse;

import java.awt.*;

public class PlayState extends GameState {

    Player player;
    Map maps;

    private static final MapParse mapParse = new MapParse();
    private static Map cachedMap = mapParse.parsing("maps/Map_Forest_1_Layer.xml");

    public static Box box;

    public PlayState(GameStateManager gsm) {
        super(gsm);
        player = new Player(new Vector2D(300, 2800), GamePanel.Tile_Size * GamePanel.Scale, 64); // scale the player
        maps = cachedMap;
        box = new Box(new Vector2D(100, 100), 32, 32);
    }

    @Override
    public void update() {
        player.update();
    }

    @Override
    public void input(MouseHandler mouse, KeyHandler key) {
        if(key.pause.down) {
            gsm.addAndPop(GameStateManager.PAUSE.ordinal());
            try {
                Thread.sleep(200); // 200 milliseconds delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        player.input(key, mouse);
    }

    @Override
    public void render(Graphics2D g) {
        maps.drawMap(g, Player.getCamera());
        Sprite.drawArray(g, font, GamePanel.oldFrameCount + " FPS", new Vector2D(GamePanel.width - 130, GamePanel.height - 50), 32, 32, 16, 0);
        box.render(g);
        player.render(g);
    }

}