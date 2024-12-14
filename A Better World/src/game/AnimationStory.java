package game;

import game.Input.KeyHandler;
import game.Input.MouseHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class AnimationStory {

    // Scenes: Each scene has one image and multiple lines of text
    private BufferedImage[] sceneImages;
    private String[][] sceneLines;

    private int currentSceneIndex = 0;
    private int currentLineIndex = 0;

    // Typewriter effect variables (optional)
    private String currentDisplayedText = "";
    private int charIndex = 0;
    private long lastCharTime = 0;
    private long charDelay = 50; // ms between characters
    private boolean finishedAllScenes = false;

    private Font storyFont;

    public AnimationStory() {
        init();
    }

    private void init() {
        // Load multiple images
        // Adjust paths and count as needed
        try {
            sceneImages = new BufferedImage[] {
                    ImageIO.read(getClass().getResourceAsStream("/background/Chill_Guy.png")),
                    ImageIO.read(getClass().getResourceAsStream("/conversation/BackGround.png")),
                    ImageIO.read(getClass().getResourceAsStream("/conversation/nah_id_win.jpg"))
            };
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            // Fallback handling
            sceneImages = new BufferedImage[0];
        }

        // Set up multiple scenes, each with its own lines
        // For example, scene 0 has 2 lines, scene 1 has 2 lines, etc.
        sceneLines = new String[][] {
                { "In the quiet morning, a hero awakens...", "With courage in heart, a journey begins." },
                { "Across vast fields, the hero marches on.", "Dangers loom, yet hope shines ahead." },
                { "At the final mountain peak, destiny awaits.", "A final challenge, a final triumph." }
        };

        storyFont = new Font("Serif", Font.BOLD, 24);

        // Initialize the first line for the first scene
        if (sceneLines.length > 0 && sceneLines[0].length > 0) {
            currentDisplayedText = "";
            charIndex = 0;
        } else {
            finishedAllScenes = true;
        }
    }

    public void update() {
        if (!finishedAllScenes) {
            long currentTime = System.currentTimeMillis();
            String currentFullLine = getCurrentLine();
            if (charIndex < currentFullLine.length() && currentTime - lastCharTime > charDelay) {
                currentDisplayedText += currentFullLine.charAt(charIndex);
                charIndex++;
                lastCharTime = currentTime;
            }
        }
    }

    public void input(MouseHandler mouse, KeyHandler key) {
        if (key.skip.down) {
            key.skip.down = false;

            if (!finishedAllScenes) {
                String currentFullLine = getCurrentLine();
                // If we're still typing out the line, jump to the full line
                if (charIndex < currentFullLine.length()) {
                    currentDisplayedText = currentFullLine;
                    charIndex = currentFullLine.length();
                } else {
                    // Move to the next line/scene
                    currentLineIndex++;
                    if (currentLineIndex >= sceneLines[currentSceneIndex].length) {
                        // Move to next scene
                        currentSceneIndex++;
                        currentLineIndex = 0;
                        if (currentSceneIndex >= sceneLines.length) {
                            // No more scenes
                            finishedAllScenes = true;
                            // gsm.setState(GameStateManager.NEXT_STATE); // if needed
                        } else {
                            // Prepare for the next scene's first line
                            resetTypewriter();
                        }
                    } else {
                        // Just next line in the same scene
                        resetTypewriter();
                    }
                }
            }
        }
    }

    private void resetTypewriter() {
        currentDisplayedText = "";
        charIndex = 0;
        lastCharTime = 0;
    }

    private String getCurrentLine() {
        if (finishedAllScenes) return "";
        return sceneLines[currentSceneIndex][currentLineIndex];
    }

    public void render(Graphics2D g) {
        // Draw the current scene image if available
        if (currentSceneIndex < sceneImages.length) {
            BufferedImage currentImage = sceneImages[currentSceneIndex];
            if (currentImage != null) {
                g.drawImage(currentImage, 0, 0, null);
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, 800, 600);
            }
        } else {
            // No scene image available, just clear
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 800, 600);
        }

        // Draw the text
        g.setFont(storyFont);
        g.setColor(Color.WHITE);
        int textY = 400;
        g.drawString(currentDisplayedText, 50, textY);

        if (!finishedAllScenes) {
            String currentFullLine = getCurrentLine();
            // If current line fully displayed, show prompt to continue
            if (charIndex == currentFullLine.length()) {
                g.setFont(new Font("SansSerif", Font.PLAIN, 16));
                g.setColor(Color.YELLOW);
                g.drawString("Press SPACE to continue...", 50, textY + 40);
            }
        } else {
            // If everything is finished
            g.setFont(new Font("SansSerif", Font.ITALIC, 20));
            g.setColor(Color.CYAN);
            g.drawString("The End", 50, textY + 80);
        }
    }
}
