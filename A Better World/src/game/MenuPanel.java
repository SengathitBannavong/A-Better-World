package game;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {
    private final ImageIcon backgroundImage;

    public MenuPanel(JFrame menuFrame) {
        setLayout(new GridBagLayout());
        backgroundImage = new ImageIcon("res/conversation/BackGround.png");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;

        // Title Label
        JLabel titleLabel = new JLabel("A BETTER WORLD");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, gbc);

        // Play Button
        JButton playButton = createRoundedButton("PLAY");
        playButton.addActionListener(e -> {
            menuFrame.dispose();
            new Window();
        });
        add(playButton, gbc);

        // Options Button
        JButton optionsButton = createRoundedButton("OPTIONS");
        optionsButton.addActionListener(e -> showOptionsMenu());
        add(optionsButton, gbc);

        // Guides Button
        JButton guidesButton = createRoundedButton("GUIDES");
        guidesButton.addActionListener(e -> showGuidesMenu());
        add(guidesButton, gbc);

        // Quit Button
        JButton quitButton = createRoundedButton("QUIT");
        quitButton.addActionListener(e -> System.exit(0));
        add(quitButton, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
        g2.dispose();
    }

    private JButton createRoundedButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(getForeground());
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getAscent();
                g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 4);
                g2.dispose();
            }
        };
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setBackground(Color.ORANGE);
        button.setForeground(Color.BLACK);
        button.setPreferredSize(new Dimension(200, 50));
        return button;
    }

    private void showOptionsMenu() {
        removeAll();

        JLabel optionsTitle = new JLabel("OPTIONS", SwingConstants.CENTER);
        optionsTitle.setFont(new Font("Arial", Font.BOLD, 40));
        optionsTitle.setForeground(Color.BLUE);

        JLabel volumeLabel = new JLabel("Volume:");
        volumeLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        volumeLabel.setForeground(Color.BLACK);

        JSlider volumeSlider = new JSlider(0, 100, 50);
        volumeSlider.setMajorTickSpacing(20);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.addChangeListener(event -> {
            int volume = volumeSlider.getValue();
            System.out.println("Volume: " + volume);
        });

        JButton backButton = createRoundedButton("BACK");
        backButton.addActionListener(e -> resetMenu());

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;

        add(optionsTitle, gbc);
        add(volumeLabel, gbc);
        add(volumeSlider, gbc);
        add(backButton, gbc);

        revalidate();
        repaint();
    }

    private void showGuidesMenu() {
        removeAll();

        JLabel guideLabel = new JLabel("<html><h1>Game Guides</h1>"
                + "<p>1. Use arrow keys to move.<br>"
                + "2. Press 'Space' to jump.<br>"
                + "3. Click 'Left Mouse' to play.<br>"
                + "4. Press 'A S D W' to move.<br>"
                + "5. Press 'A S D W + Shift' to speed up.</p></html>");
        guideLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        guideLabel.setForeground(Color.RED);
        guideLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton backButton = createRoundedButton("BACK");
        backButton.addActionListener(e -> resetMenu());

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;

        add(guideLabel, gbc);
        add(backButton, gbc);

        revalidate();
        repaint();
    }

    private void resetMenu() {
        removeAll();
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        MenuPanel newMenu = new MenuPanel(frame);
        frame.setContentPane(newMenu);
        frame.revalidate();
    }
}
