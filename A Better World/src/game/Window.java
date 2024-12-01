package game;

import javax.swing.*;

public class Window extends JFrame {
    private static final int WIDTH = 1540;
    private static final int HEIGHT = 800;

    public Window() {
        this(WIDTH, HEIGHT);
    }

    public Window(int width, int height) {
        setTitle("A BETTER WORLD");
        setSize(width, height);
        setContentPane(new GamePanel(width, height));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
