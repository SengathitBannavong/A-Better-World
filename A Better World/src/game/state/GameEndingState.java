package game.state;

import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.Window;
import game.design.Observer;
import game.event.EventManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameEndingState extends GameState{

    private List<String> credits;
    private int yOffset;       // vertical start position of the credits off-screen
    private int scrollSpeed;   // how quickly credits scroll upward;
    private int displayDuration; // duration in ms before returning to another state

    private boolean skipAllowed = false;

    // Use window dimensions from Window class
    private int windowWidth = Window.getWIDTH();
    private int windowHeight = Window.getHEIGHT();

    // image
    private Image image;
    private float alpha = 1.0f;

    // Singleton pattern
    private static GameEndingState instance = null;

    private GameEndingState(GameStateManager gsm) {
        super(gsm);
        init();
    }

    public static synchronized GameEndingState getInit(GameStateManager gsm) {
        if (instance == null) {
            instance = new GameEndingState(gsm);
        }
        return instance;
    }

    private void init() {
        // Initialize credits text
        credits = new ArrayList<>();
        credits.add("Thank you for playing!");
        credits.add("A Game by Group 4");
        credits.add("Programming: Bannvong,Anh,Hang,Ly");
        credits.add("Art: KimHeng");
        credits.add("Music: Anh");
        credits.add("Special Thanks:");
        credits.add(" Every One");
        credits.add("-- Project OOP --");
        credits.add("");
        credits.add("Press any key to skip...");

        // Load image
        image = new ImageIcon(Objects.requireNonNull(getClass().getResource("/background/ending_photo.jpg"))).getImage();

        // Start credits off-screen below the window
        yOffset = windowHeight + 50;
        scrollSpeed = 1; // Adjust as needed

        // Show the credits for 20 seconds before auto-returning (adjust as desired)
        displayDuration = 25;

    }

    @Override
    public void update() {
        // Move the credits upwards
        yOffset -= scrollSpeed;
        if(OneSecond){
            displayDuration--;
            OneSecond = false;
            System.out.println("displayDuration: " + displayDuration);
        }
        if(displayDuration <=15){
            skipAllowed = true;
        }
        // Check if time is up
        if (displayDuration<=0) {
            // exit program
            EventManager.triggerEvent("ENDPROGRAM");
            gsm.pop();
        }
    }

    @Override
    public void input(MouseHandler mouse, KeyHandler key) {
        // If player presses any key, skip credits immediately
        if (skipAllowed&&key.anyKeyPress()) {
            EventManager.triggerEvent("ENDPROGRAM");
        }
    }

    @Override
    public void render(Graphics2D g) {
        // Clear the screen
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, windowWidth, windowHeight);

        // Draw the image
        // set opacity
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.drawImage(image, 0, 0, windowWidth, windowHeight, null);
        alpha -= 0.002f;
        if(alpha < 0){
            alpha = 0;
        }
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // Set font and color for credits
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));

        // Line spacing and initial drawing position for credits
        int lineHeight = 30;
        int currentY = yOffset;

        // Draw credits centered horizontally
        for (String line : credits) {
            int textWidth = g.getFontMetrics().stringWidth(line);
            int x = (windowWidth - textWidth) / 2;
            g.drawString(line, x, currentY);
            currentY += lineHeight;
        }
    }

}
