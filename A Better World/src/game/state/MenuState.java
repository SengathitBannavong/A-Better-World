package game.state;

import game.GamePanel;
import game.Input.KeyHandler;
import game.Input.MouseHandler;
import game.graphic.Sprite;
import game.physic.Vector2D;

import java.awt.*;

public class MenuState extends GameState {
    // Singleton pattern
    private static MenuState instance = null;

    // Menu options
    private String[] options = {
            "Resume",
            "Play Again",
            "Quit"
    };

    private int selectedOption = 0; // Keep track of which menu item is highlighted

    // For fade-in effect and slight pause animations
    private int alpha = 0;     // For the overlay fade-in
    private int maxAlpha = 150; // Maximum alpha for the overlay

    private float titleAnim = 0; // For a subtle animation on the title text

    // Animation speed for the title text oscillation
    private float titleAnimSpeed = 0.05f;
    // The magnitude of vertical oscillation (in pixels)
    private int titleAnimAmplitude = 10;

    private MenuState(GameStateManager gsm) {
        super(gsm);
    }

    public static synchronized MenuState getInit(GameStateManager gsm) {
        if (instance == null) {
            instance = new MenuState(gsm);
        }
        return instance;
    }

    @Override
    public void update() {
        // Update the fade-in of the background overlay
        if (alpha < maxAlpha) {
            alpha += 3;
            if (alpha > maxAlpha) alpha = maxAlpha;
        }

        // Update the title animation
        titleAnim += titleAnimSpeed;
        if (titleAnim > 2 * Math.PI) {
            titleAnim = 0;
        }
    }

    @Override
    public void input(MouseHandler mouse, KeyHandler key) {
        // Navigation with up/down keys to select menu options
        if (key.up.down) {
            selectedOption--;
            if (selectedOption < 0) {
                selectedOption = options.length - 1;
            }
            key.up.down = false;
        }

        if (key.down.down) {
            selectedOption++;
            if (selectedOption >= options.length) {
                selectedOption = 0;
            }
            key.down.down = false;
        }

        // Confirm selection with enter or 'M'
        if (key.menu.down) {
            key.enter.down = false;
            key.menu.down = false;
            selectOption(selectedOption);
        }
    }

    private void selectOption(int index) {
        switch (index) {
            case 0:
                // Resume
                gsm.pop();
                break;
            case 1:
                // Play again - Implement your logic to open an options menu
                PlayState.getInit(gsm).resetState();
                gsm.pop();
                break;
            case 2:
                // Quit - Implement logic to quit game or return to launcher
                System.exit(0);
                break;
            default:
                break;
        }
    }

    @Override
    public void render(Graphics2D g) {
        // Draw a semi-transparent overlay on top of the current game
        Color overlayColor = new Color(161, 96, 191);
        g.fillRect(0, 0, GamePanel.width, GamePanel.height);

        // Compute the vertical offset for the title animation (a subtle bounce)
        float titleOffsetY = (float) (Math.sin(titleAnim) * titleAnimAmplitude);

        // Draw the "Pause" title, centered, with oscillation
        Sprite.drawArray(g, font, "MENU",
                new Vector2D(
                        (float) (GamePanel.width / 2) - 64,
                        (float) (GamePanel.height / 2) - 128 + titleOffsetY
                ),
                64, 64, 36, 0);

        // Draw instructions or a subtitle
        Sprite.drawArray(g, font, "Game is paused. For Menu",
                new Vector2D(
                        (float) (GamePanel.width / 2) - 200,
                        (float) (GamePanel.height / 2) - 32
                ),
                32, 32, 20, 0);

        // Draw the menu options
        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            float x = (float) (GamePanel.width / 2) - 128;
            float y = (float) (GamePanel.height / 2) + (i * 48);

            if (i == selectedOption) {
                // Highlight the selected option
                // Change color or size to highlight
                g.setColor(new Color(255, 215, 0)); // gold-ish
                Sprite.drawArray(g, font, ">> " + option + " <<",
                        new Vector2D(x, y),
                        32, 32, 25, 0);
            } else {
                g.setColor(Color.WHITE);
                Sprite.drawArray(g, font, option,
                        new Vector2D(x, y),
                        32, 32, 25, 0);
            }
        }

        // Draw a small prompt to resume quickly
        Sprite.drawArray(g, font, "Press M to select",
                new Vector2D(
                        (float) (GamePanel.width / 2) - 150,
                        (float) (GamePanel.height / 2) + (options.length * 48) + 32
                ),
                20, 20, 16, 0);
    }
}
