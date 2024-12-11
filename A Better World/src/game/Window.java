package game;

import game.graphic.Font;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {
    private static final int WIDTH = 1540;
    private static final int HEIGHT = 800;
    public static  ImageIcon background;
    public static Font font;
    public Window() {
        this(WIDTH, HEIGHT);
    }

    public Window(int width, int height) {
        //input image
        background = new ImageIcon("res/background/Chill_Guy.png");
        font = new Font("font/font.png", 10, 10);
        setTitle("A BETTER WORLD");
        setSize(width, height);
        setContentPane(new GamePanel(WIDTH, HEIGHT));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static int getWIDTH() {
        return WIDTH;
    }

    public static int getHEIGHT() {
        return HEIGHT;
    }
}
