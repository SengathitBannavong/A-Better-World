package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuPanel extends JPanel {

    public static int valueMusic = 100;
    private final ImageIcon backgroundImage;
    private JButton playButton, optionsButton, guidesButton, quitButton;
    private Color buttonBaseColor = new Color(0, 0, 0, 187);  // orange-like
    private Color buttonHoverColor = new Color(174, 127, 127); // lighter orange
    private Color titleColor = new Color(255, 255, 255); // white
    private Color overlayTopColor = new Color(0, 0, 0, 180);
    private Color overlayBottomColor = new Color(0, 0, 0, 80);

    private Font titleFont = new Font("Segoe UI", Font.BOLD, 48);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 24);
    private Font labelFont = new Font("Segoe UI", Font.PLAIN, 30);

    public MenuPanel(JFrame menuFrame) {
        setLayout(new GridBagLayout());
        backgroundImage = new ImageIcon("res/photo/titile.jpg");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;

        // Title Label
        JLabel titleLabel = new JLabel("A BETTER WORLD");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(titleColor);

        add(titleLabel, gbc);

        // Create buttons
        playButton = createStyledButton("PLAY");
        playButton.addActionListener(e -> {
            menuFrame.dispose();
            // Launch the game window
            new Window();
        });
        add(playButton, gbc);

        optionsButton = createStyledButton("OPTIONS");
        optionsButton.addActionListener(e -> showOptionsMenu());
        add(optionsButton, gbc);

        guidesButton = createStyledButton("GUIDES");
        guidesButton.addActionListener(e -> showGuidesMenu());
        add(guidesButton, gbc);

        quitButton = createStyledButton("QUIT");
        quitButton.addActionListener(e -> System.exit(0));
        add(quitButton, gbc);

        // Mouse listener to handle hover effects
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                JButton[] buttons = {playButton, optionsButton, guidesButton, quitButton};
                for (JButton btn : buttons) {
                    if (btn.getBounds().contains(e.getPoint())) {
                        btn.setBackground(buttonHoverColor);
                    } else {
                        btn.setBackground(buttonBaseColor);
                    }
                }
                repaint();
            }
        };
        addMouseMotionListener(mouseAdapter);
        setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image first
        Graphics2D g2 = (Graphics2D) g.create();
        g2.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);

        // Apply a vertical gradient overlay
        GradientPaint overlayPaint = new GradientPaint(0, 0, overlayTopColor, 0, getHeight(), overlayBottomColor);
        g2.setPaint(overlayPaint);
        g2.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 50, 50);

        // White border with subtle transparency
        g2.setColor(new Color(122, 88, 182, 255));
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 50, 50);

        g2.dispose();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                int width = getWidth();
                int height = getHeight();

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background: simple gradient or flat color
                Color bg = getBackground();
                GradientPaint gp = new GradientPaint(0, 0, bg.brighter(), 0, height, bg.darker());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, width, height, 35, 35);

                // Border
                g2.setColor(new Color(122, 88, 182));
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, 0, width, height, 35, 35);

                // Text
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getAscent();
                g2.setColor(new Color(161, 96, 191));
                g2.drawString(getText(), (width - textWidth) / 2, (height + textHeight) / 2 - 2);

                g2.dispose();
            }
        };

        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setFont(buttonFont);
        button.setBackground(buttonBaseColor);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(250, 60));

        return button;
    }

    private void showOptionsMenu() {
        removeAll();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;

        JLabel optionsTitle = new JLabel("OPTIONS", SwingConstants.CENTER);
        optionsTitle.setFont(titleFont);
        optionsTitle.setForeground(new Color(255, 255, 255, 255));

        JLabel volumeLabel = new JLabel("Volume:");
        volumeLabel.setFont(labelFont);
        volumeLabel.setForeground(new Color(161, 96, 191, 255));

        JSlider volumeSlider = new JSlider(0, 100, 50);
        volumeSlider.setMajorTickSpacing(20);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setOpaque(false);
        volumeSlider.setForeground(new Color(161, 96, 191, 255));
        volumeSlider.addChangeListener(event -> {
            int volume = volumeSlider.getValue();
            valueMusic = volume;
            System.out.println("Volume: " + volume);
        });

        JButton backButton = createStyledButton("BACK");
        backButton.addActionListener(e -> resetMenu());

        add(optionsTitle, gbc);
        add(volumeLabel, gbc);
        add(volumeSlider, gbc);
        add(backButton, gbc);

        revalidate();
        repaint();
    }

    private void showGuidesMenu() {
        removeAll();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(15, 30, 15, 30);
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;

        JLabel guideLabel = new JLabel("<html>"
                + "<div style='text-align:center; color:white; font-family:Segoe UI,sans-serif;"
                + "border: 3px solid #A168C7; border-radius: 15px; padding: 20px; background-color: rgba(0,0,0,0.5);'>"
                + "<h1 style='color:#A168C7; text-shadow: 2px 2px 4px #000; margin-bottom:20px;'>Game Guides</h1>"
                + "<ul style='list-style-type:none; padding:0; margin:0; font-size:24px; line-height:1.6;'>"
                + "<li><strong>1.</strong> Press <span style='color:#FFAAAA;'>P </span> to pause.</li>"
                + "<li><strong>2.</strong> Press <span style='color:#FFAAAA;'>'M'</span> to show menu.</li>"
                + "<li><strong>3.</strong> Click <span style='color:#FFAAAA;'>'Left Mouse'</span> to Attack.</li>"
                + "<li><strong>4.</strong> Press <span style='color:#FFAAAA;'>'A S D W'</span> to move.</li>"
                + "<li><strong>5.</strong> Press <span style='color:#FFAAAA;'>'A S D W + Shift'</span> to dash.</li>"
                +" <li><strong>6.</strong> Press <span style='color:#FFAAAA;'>'Q'</span> to goto next map.</li>"
                +" <li><strong>7.</strong> Press <span style='color:#FFAAAA;'>'C'</span> to interact with NPC.</li>"
                +" <li><strong>8.</strong> Press <span style='color:#FFAAAA;'>'Space Bar'</span> to Skip Speaking.</li>"
                + "</ul>"
                + "</div>"
                + "</html>");

        guideLabel.setFont(labelFont);
        guideLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton backButton = createStyledButton("BACK");
        backButton.addActionListener(e -> resetMenu());

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
