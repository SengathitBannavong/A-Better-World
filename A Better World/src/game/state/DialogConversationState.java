package game.state;

import game.GamePanel;
import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.graphic.Sprite;
import game.physic.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DialogConversationState extends GameState {

    private final List<String> dialogQueue;
    int boxWidth = 1350;
    int boxHeight = 150;
    int boxX = 50;
    int boxY = 600;

    private Font font;

    private int currentSentenceIndex = 0; // Tracks the current sentence
    private int displayedChars = 0; // Tracks how many characters are displayed for the current sentence

    private boolean isFinishOneSentence = false;
    private long lastUpdateTime = 0; // Tracks the last update time
    private long typingDelay = 50; // Delay between each character in milliseconds
    private long sentenceDelay = 3000; // Delay between sentences in milliseconds
    private boolean isSentenceDelayActive = false;
    private boolean skipDelay = false;

    private float alpha = 1.0f;
    private static BufferedImage image = Sprite.loadSprite_("conversation/nah_id_win.jpg");
    private boolean isSpecialShow = false;

    private float fadeOutAlpha = 1.0f; // Initial alpha value (fully opaque)
    private boolean isFadingOut = false; // Tracks if fading out is active
    private long fadeStartTime = 0; // Tracks the start time of the fade-out
    private long fadeDuration = 2000; // Duration of fade-out in milliseconds
    private boolean isEndOnetime = false; // Tracks if the end event is triggered

    public DialogConversationState(GameStateManager gsm, String path) {
        super(gsm);
        dialogQueue = new ArrayList<>();
        loadSentencesFromFile(path);
        lastUpdateTime = System.currentTimeMillis();
        getFont();
    }

    private void getFont()  {
        InputStream is = getClass().getResourceAsStream("/font/ByteBounce.ttf");
        if(is == null) {
            throw new RuntimeException("Font not found");
        }
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        font = font.deriveFont(Font.PLAIN, 35);
    }

    @Override
    public void update() {
        long currentTime = System.currentTimeMillis();

        if (isSentenceDelayActive) {
            // Handle sentence delay
            if (currentTime - lastUpdateTime >= sentenceDelay) {
                moveToNextSentence();
                isSentenceDelayActive = false;
            }
        } else {
            // Handle typing animation
            if (currentSentenceIndex < dialogQueue.size()
                    && displayedChars < dialogQueue.get(currentSentenceIndex).length()) {
                if (currentTime - lastUpdateTime >= typingDelay) {
                    displayedChars++;
                    lastUpdateTime = currentTime;
                }

                // If the sentence is fully displayed, start the sentence delay
                if (displayedChars == dialogQueue.get(currentSentenceIndex).length()) {
                    isFinishOneSentence = true;
                    isSentenceDelayActive = true;
                    lastUpdateTime = currentTime;
                }
            }
        }
    }

    @Override
    public void input(MouseHandler mouse, KeyHandler key) {
        // Handle skipping by user input (e.g., pressing a key)
        if (key.skip.down && !skipDelay) { // Replace "skip" with the actual key check
            skipDelay = true;
            skipAnimation();
        }
    }

    @Override
    public void render(Graphics2D g) {
        drawSubWindow(g);
        if (isSpecialShow) {;
            SpecialShow(g);
        }
    }

    private void drawSubWindow(Graphics2D g) {
        Color color = new Color(0, 0, 0, 200);
        g.setColor(color);
        g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 35, 35);

        color = new Color(255, 255, 255);
        g.setColor(color);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(boxX + 5, boxY + 5, boxWidth - 10, boxHeight - 10, 25, 25);

        g.setColor(Color.WHITE);
        if (currentSentenceIndex < dialogQueue.size()) {
//            Sprite.drawArray(
//                    g,
//                    GameState.font,
//                    dialogQueue.get(currentSentenceIndex).substring(0, displayedChars), // Display partial text
//                    new Vector2D(boxX + 10, boxY + 10),
//                    GamePanel.Tile_Size * 2,
//                    GamePanel.Tile_Size * 2,
//                    20,
//                    0
//            );
            String currentText = dialogQueue.get(currentSentenceIndex).substring(0, displayedChars);
            g.setFont(font);
            g.drawString(currentText, boxX + 20, boxY + 40); // Adjust text position for better alignment
            String temp = dialogQueue.get(currentSentenceIndex).substring(0, displayedChars);
            if (temp.equalsIgnoreCase("Player: Nah I'd win") && !isSpecialShow && !isEndOnetime) {
                isSpecialShow = true;
                isFadingOut = true;
                fadeStartTime = System.currentTimeMillis(); // Start the fade timer
            }
        }
    }

    private void SpecialShow(Graphics2D g) {
        if (image == null) {
            System.out.println("Image is null");
            return;
        }

        if (isFadingOut) {
            // Calculate the elapsed time since fade started
            long currentTime = System.currentTimeMillis();
            float elapsedTime = (currentTime - fadeStartTime) / (float) fadeDuration;

            // Update alpha value
            fadeOutAlpha = Math.max(0, 1.0f - elapsedTime);

            // If fade-out is complete, stop rendering the image
            if (fadeOutAlpha <= 0) {
                isSpecialShow = false;
                isFadingOut = false;
                isEndOnetime = true;
                return;
            }
        }

        // Set transparency
        Composite originalComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeOutAlpha));

        // Draw the image
        g.drawImage(image, 500, 100, 500, 500, null);

        // Restore original composite
        g.setComposite(originalComposite);
    }

    private void loadSentencesFromFile(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader("res/conversation/" + path + ".txt"))) {
            String line = br.readLine();
            String[] data = line.split(":");
            int size = Integer.parseInt(data[1]);
            for (int i = 0; i < size; i++) {
                dialogQueue.add(br.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Dialog loaded successfully with size: " + dialogQueue.size());
    }

    private void moveToNextSentence() {
        currentSentenceIndex++;
        if (currentSentenceIndex >= dialogQueue.size()) {
            gsm.clearBufferState();
            System.out.println("Dialog conversation finished");
            return;
        }
        displayedChars = 0;
        isFinishOneSentence = false;
        skipDelay = false;
    }

    private void skipAnimation() {
        if (isSentenceDelayActive) {
            moveToNextSentence();
            isSentenceDelayActive = false;
        } else {
            displayedChars = dialogQueue.get(currentSentenceIndex).length(); // Show full sentence
            isFinishOneSentence = true;
            isSentenceDelayActive = true;
            lastUpdateTime = System.currentTimeMillis();
        }
    }
}
