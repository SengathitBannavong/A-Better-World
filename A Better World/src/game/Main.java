package game;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame menuFrame = new JFrame("Menu");
            menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            menuFrame.setSize(Window.getWIDTH(), Window.getHEIGHT());
            menuFrame.setLocationRelativeTo(null);

            MenuPanel menuPanel = new MenuPanel(menuFrame);
            menuFrame.setContentPane(menuPanel);
            menuFrame.setVisible(true);
        });
    }
}