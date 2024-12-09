package game.state;

import game.GamePanel;
import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.graphic.Sprite;
import game.physic.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DialogConversationState extends GameState {

    private final List<String> dialogQueue;
    int boxWidth = 1350;
    int boxHeight = 150;
    int boxX = 50;
    int boxY = 600;

    private int currentSentenceIndex = 0; // Tracks the current sentence
    private int displayedChars = 0; // Tracks how many characters are displayed for the current sentence

    private boolean isFinishOneSentence = false;
    private long lastUpdateTime = 0; // Tracks the last update time
    private long typingDelay = 50; // Delay between each character in milliseconds
    private long sentenceDelay = 3000; // Delay between sentences in milliseconds
    private boolean isSentenceDelayActive = false;

    public DialogConversationState(GameStateManager gsm, String path) {
        super(gsm);
        dialogQueue = new ArrayList<>();
        loadSentencesFromFile(path);
        lastUpdateTime = System.currentTimeMillis();
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
//        if (key.skip) { // Replace "skip" with the actual key check
//            skipAnimation();
//        }
    }

    @Override
    public void render(Graphics2D g) {
        drawSubWindow(g);
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
            Sprite.drawArray(
                    g,
                    GameState.font,
                    dialogQueue.get(currentSentenceIndex).substring(0, displayedChars), // Display partial text
                    new Vector2D(boxX + 10, boxY + 10),
                    GamePanel.Tile_Size * 2,
                    GamePanel.Tile_Size * 2,
                    20,
                    0
            );
        }
    }

    private void loadSentencesFromFile(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader("res/conversation/" + path + ".txt"))) {
            String line = br.readLine();
            String[] data = line.split(":");
            int size = Integer.parseInt(data[1]);
            for (int i = 0; i < size; i++) {
                dialogQueue.add(br.readLine());
                System.out.println("Dialog: " + dialogQueue.get(i));
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
