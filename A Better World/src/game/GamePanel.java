package game;

import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.state.GameStateManager;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel implements Runnable {

    public static int width;
    public static int height;

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


    public GamePanel(int width, int height) {
        GamePanel.width = width;
        GamePanel.height = height;
        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        requestFocus();
    }

    // Start the thread
    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this , "GameThread");
            thread.start();
        }
    }

    public void init() {
        // Initialize the game
        running = true;

        // Get the graphics
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics2D = (Graphics2D) image.getGraphics();

        // Initialize the input
        mouse = new MouseHandler(this);
        key = new KeyHandler(this);

        // GameStateManager
        gameStateManager = new GameStateManager();
    }

    @Override
    public void run() {
        init();

        final double GAME_HERTZ = 60.0;
        // 16.666666666666668 ms
        final double TBU = 1_000_000_000 / GAME_HERTZ; // Time before update

        final int MUBR = 5; // Must update before render

        double lastUpdateTime = System.nanoTime();
        double lastRenderTime = System.nanoTime();

        final double TARGET_FPS = 64.0;
        // 16.666666666666668 ms
        final double TTBR = 1_000_000_000 / TARGET_FPS; // Total time before render

        System.out.println("TBU: " + TBU + " TTBR: " + TTBR);

        int frameCount = 0;
        int lastSecondTime = (int) (lastUpdateTime / 1_000_000_000);
        oldFrameCount = 0;

        double deltaUpdate = 0;
        double deltaRender = 0;

        while (running) {
            double now = System.nanoTime();
            deltaUpdate += (now - lastUpdateTime) / TBU;
            deltaRender += (now - lastRenderTime) / TTBR;
            lastUpdateTime = now;

            int updateCount = 0;
            // Update the game
            while(deltaUpdate >= 1 && (updateCount < MUBR)) {
                update();
                input(mouse, key);
                deltaUpdate--;
                updateCount++;
            }

            // Render the game
            if (deltaRender >= 1) {
                // Render
                input(mouse, key);
                render();
                draw();
                deltaRender--;
                lastRenderTime = now;
                frameCount++;
            }

            // Update the frames we got
            int thisSecond = (int) (lastUpdateTime / 1_000_000_000);
            if (thisSecond > lastSecondTime) {
                if (frameCount != oldFrameCount) {
                    System.out.println("New second: " + thisSecond + " " + frameCount);
                    oldFrameCount = frameCount;
                }
                frameCount = 0;
                lastSecondTime = thisSecond;
            }


            // Yield until it has been at least the target time between renders. This saves the CPU from hogging.
            while(now - lastRenderTime < TTBR && now - lastUpdateTime < TBU) {
                Thread.yield();
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    System.out.println("Error: yielding thread");
                }
                now = System.nanoTime();
            }
        }
    }

    public void update() {
        // Update the game
        if(gameStateManager != null) {
            gameStateManager.update();
        }
    }

    public void render() {
        // Render the game
        if(graphics2D != null) {
            graphics2D.setColor(new Color(66, 134, 244));
            graphics2D.fillRect(0, 0, width, height);
            gameStateManager.render(graphics2D);
        }
    }

    public void draw() {
        // Draw the game
        Graphics g2 =(Graphics) this.getGraphics();
        g2.drawImage(image, 0, 0, width, height, null);
        g2.dispose();
    }

    public void input(MouseHandler mouse, KeyHandler key) {
        // Handle input
        if(gameStateManager != null) {
            gameStateManager.input(mouse, key);
        }
    }
}