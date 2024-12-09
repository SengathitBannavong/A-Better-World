package game.object;

import game.GamePanel;
import game.graphic.Sprite;
import game.physic.Vector2D;
import game.state.GameState;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TabDialogBox {
    private final List<String> dialogQueue;
    private int currentSentenceIndex = 0;
    private int displayedChars = 0;
    private Timer typingTimer;
    private Timer sentenceTimer;

    private final int tileSize = GamePanel.Tile_Size;
    private final int xOffset;
    private final int yOffset;

    private Runnable onDialogEnd; // Callback when dialog ends

    public TabDialogBox(String path, int xOffset, int yOffset) {
        this.dialogQueue = new ArrayList<>();
        loadSentencesFromFile(path);
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public void setOnDialogEnd(Runnable onDialogEnd) {
        this.onDialogEnd = onDialogEnd;
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

    public void startTypingAnimation() {
        if (currentSentenceIndex < dialogQueue.size()) {
            typingTimer = new Timer(50, e -> {
                if (displayedChars < dialogQueue.get(currentSentenceIndex).length()) {
                    displayedChars++;
                } else {
                    typingTimer.stop();
                    startSentenceDelay();
                }
            });
            typingTimer.start();
        } else {
            endDialog();
        }
    }

    private void startSentenceDelay() {
        sentenceTimer = new Timer(2000, e -> {
            sentenceTimer.stop();
            currentSentenceIndex++;
            displayedChars = 0;
            startTypingAnimation();
        });
        sentenceTimer.setRepeats(false);
        sentenceTimer.start();
    }

    public void drawDialog(Graphics2D g) {
        int boxWidth = 1350;
        int boxHeight = 100;
        int boxX = 50;
        int boxY = 600;

        g.setColor(Color.BLACK);
        g.fillRect(boxX, boxY, boxWidth, boxHeight);

        g.setColor(Color.WHITE);
        if (currentSentenceIndex < dialogQueue.size()) {
            Sprite.drawArray(
                    g,
                    GameState.font, // Replace with your font object
                    dialogQueue.get(currentSentenceIndex).substring(0, displayedChars),
                    new Vector2D(boxX + 10, boxY + 10),
                    tileSize,
                    tileSize,
                    xOffset,
                    yOffset
            );
        }

    }

    public void skipAnimation() {
        if (typingTimer != null && typingTimer.isRunning()) {
            typingTimer.stop();
            displayedChars = dialogQueue.get(currentSentenceIndex).length();
            startSentenceDelay();
        } else if (sentenceTimer != null && sentenceTimer.isRunning()) {
            sentenceTimer.stop();
            currentSentenceIndex++;
            displayedChars = 0;
            startTypingAnimation();
        }
    }

    private void endDialog() {
        if (onDialogEnd != null) {
            onDialogEnd.run();
        }
    }
}
