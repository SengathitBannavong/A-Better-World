package game;

import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.event.EventManager;
import game.graphic.Sprite;
import game.physic.Vector2D;
import game.state.GameState;
import game.state.GameStateManager;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel implements Runnable , EventManager.EventListener {
    // GamePanel
    public static int width;
    public static int height;
    public static float ratio = 0;

    public static final int Tile_Size = 16;
    public static final int Scale = 8;
    public static final int Zoom = 2;

    // Frames
    public static int oldFrameCount;

    // Thread
    private Thread thread = null;
    private volatile boolean running = false;

    // Graphics
    private Graphics2D graphics2D;
    private BufferedImage image;

    // game.Input
    private MouseHandler mouse;
    private KeyHandler key;

    // GameStateManager
    private GameStateManager gameStateManager;

    private static Sound sound = new Sound();
    private static Sound background = new Sound();

    public GamePanel(int width, int height) {
        GamePanel.width = width;
        GamePanel.height = height;
        gameStateManager = null;
        ratio = 0.5f; // height / width =  0.5194805194805194
        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        requestFocus();
        EventManager.addListener(this);
    }

    // Start the thread
    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this , "GameThread");
            thread.start();
        }
        playMusic(10);
    }

    public void init() {
        // Initialize the game
        running = true;

        // Get the graphics
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics2D = (Graphics2D) image.getGraphics();

        // render background first
        graphics2D.drawImage(Window.background.getImage(), 0, 0, width, height, null);
        draw();
        // Initialize the input
        mouse = new MouseHandler(this);
        key = new KeyHandler(this);

        // GameStateManager
        new Thread(() -> gameStateManager = new GameStateManager()).start();
    }

    @Override
    public void run() {
        init();
        System.out.println("GamePanel running...");
        final double GAME_HERTZ = 60.0;
        // 16.666666666666668 ms
        final double TBU = 1_000_000_000 / GAME_HERTZ; // Time before update

        final int MUBR = 1; // Must update before render
        final int MUBU = 5; // Must update before update

        double lastUpdateTime = System.nanoTime();
        double lastRenderTime = System.nanoTime();

        final double TARGET_FPS = 60.0;
        // 16.666666666666668 ms
        final double TBR = 1_000_000_000 / TARGET_FPS; // Total time before render
        System.out.println("TBU: " + TBU + " TBR: " + TBR);

        int frameCount = 0;
        int lastSecondTime = (int) (lastUpdateTime / 1_000_000_000);
        oldFrameCount = 0;

        double deltaUpdate = 0;
        double deltaRender = 0;
        boolean tick_state = false;
        int tickcount = 0;
        double now;
        int updateCount, renderCount, thisSecond;
        AnimationStory story = new AnimationStory();
        while (running) {
            // Wait for GameStateManager to be ready
            if (gameStateManager == null) {
                Thread.onSpinWait();
                graphics2D.drawImage(Window.background.getImage(), 0, 0, width, height, null);
                Vector2D vector2D = new Vector2D(((float) width / 2)+150, (float) height / 2);
                Sprite.drawArray(graphics2D,Window.font ,"Loading...",vector2D, 32, 32, 32, 0);
//                story.update();
//                story.input(mouse, key);
//                story.render(graphics2D);
                draw();
                lastSecondTime = (int) (lastUpdateTime / 1_000_000_000);
                lastUpdateTime = System.nanoTime();
                lastRenderTime = System.nanoTime();
                continue; // Skip the loop until gameStateManager is ready
            }
            now = System.nanoTime();
            deltaUpdate += (now - lastUpdateTime) / TBU;
            deltaRender += (now - lastRenderTime) / TBR;
            lastUpdateTime = now;

            updateCount = 0;
            // Update the game
            while(deltaUpdate >= 1 && (updateCount < MUBU)) {
                input(mouse, key);
                if(!tick_state && key.escape.down  && Debug.admin){
                    tick_state = true;
                    Debug.debugging = !Debug.debugging;
                }
                update();
                deltaUpdate--;
                updateCount++;
            }
            tickcount++;
            renderCount = 0;
            // Render the game
            while (deltaRender >= 1 && (renderCount < MUBR)) {
                // Render
                input(mouse, key);
                render();
                draw();
                deltaRender--;
                lastRenderTime = now;
                frameCount++;
                renderCount++;
            }
            if(tickcount > 25){
                tick_state = false;
                tickcount = 0;
            }
            // Update the frames we got
            thisSecond = (int) (lastUpdateTime / 1_000_000_000);
            if (thisSecond > lastSecondTime) {
                if (frameCount != oldFrameCount) {
                    System.out.println("New second: " + thisSecond + " " + frameCount);
                    oldFrameCount = frameCount;
                }
                frameCount = 0;
                lastSecondTime = thisSecond;
                GameState.setOneSecond(true);
            }


            // Yield until it has been at least the target time between renders. This saves the CPU from hogging.
            while(now - lastRenderTime < TBR && now - lastUpdateTime < TBU) {
                Thread.onSpinWait();
                now = System.nanoTime();
            }
        }
    }

    // GamePanel update
    public void update() {
        // Update the game
        if(gameStateManager != null) {
            gameStateManager.update();
        }
    }

    // GamePanel render
    public void render() {
        // Render the game
        if(graphics2D != null) {
            graphics2D.setColor(new Color(129, 62, 177, 100));
            graphics2D.fillRect(0, 0, width, height);
           gameStateManager.render(graphics2D);
        }
    }

    public void draw() {
        // Draw the game
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, width, height, null);
        g2.dispose();
    }

    public void input(MouseHandler mouse, KeyHandler key) {
        // Handle input
        if(gameStateManager != null) {
            gameStateManager.input(mouse, key);
        }
    }

    public static void playMusic(int i) {
        System.out.println("------------Playing music: " + i);
        background.setFile(i);
        background.play();
        background.loop();
    }

    public static void stopMusic() {
         background.stop();
    }

    public static void playSE(int i) {
        sound.setFile(i);
        sound.play();
    }

    public static void playerAttack() {
        sound.setFile(5);
        sound.play();
    }

    public Sound getSound() {
        return sound;
    }

    @Override
    public void onEvent(String eventName, Object... args) {
        if(eventName.equals("ENDPROGRAM")) {
            running = false;
            System.out.println("++++++++++++++++++++++GamePanel stopping...++++++++++++++++++++++");
            System.exit(0);
        }
    }
}
