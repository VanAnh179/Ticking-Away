package main;

import javax.swing.*;


import java.awt.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import java.io.IOException;

public class MenuPanel extends JPanel {
    private MainFrame mainFrame;
    private GamePanel gp = new GamePanel(mainFrame);
    private Sound menuMusic;
    private BufferedImage backgroundImage;
    private JToggleButton muteButton;
    private JButton resumeButton;
    private JButton startButton;

    public MenuPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        // Load hình nền
        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/background/menu_bg.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Panel chứa các thành phần
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Tiêu đề
        GradientTitleLabel titleLabel = new GradientTitleLabel("Ticking Away", new Font("Serif", Font.BOLD, 100));
        // JLabel titleLabel = new GradientTitleLabel("Ticking Away");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Nút Resume (Khởi tạo trước)
        resumeButton = new JButton("RESUME");
        styleButton(resumeButton);
       resumeButton.addActionListener(e -> {
            mainFrame.switchToGame();
            // Đảm bảo game được resume đúng cách
            if (mainFrame.getGamePanel() != null) {
                mainFrame.getGamePanel().resumeGame();
            }
        });
        resumeButton.setVisible(false);

        // Nút Start
        startButton = new JButton("START");
        styleButton(startButton);
        startButton.addActionListener(e -> mainFrame.startNewGame());

        // Nút Mute/Unmute
        muteButton = new JToggleButton("MUTE");
        styleToggleButton(muteButton);
        muteButton.addActionListener(e -> toggleMusic());

        // Thêm các thành phần vào contentPanel
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(startButton);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(muteButton);
        contentPanel.add(Box.createVerticalStrut(30));
        contentPanel.add(resumeButton);
        contentPanel.add(Box.createVerticalStrut(100));

        add(contentPanel, BorderLayout.CENTER);

        // Nhạc nền
        menuMusic = new Sound();
        menuMusic.setFile(0);
        menuMusic.loop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public void showResumeButton(boolean visible) {
        resumeButton.setVisible(visible);
    }

    private void styleButton(JButton button) {
        Color baseColor = new Color(255, 105, 180);
        Color hoverColor = baseColor.darker();

        Dimension normalSize = new Dimension(gp.screenWidth / 8, gp.screenHeight / 16);
        Dimension hoverSize = new Dimension(gp.screenWidth / 8 + 5, gp.screenHeight / 16 + 5);
        
        button.setBackground(baseColor); // Màu hồng
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        button.setFont(new Font("Arial", Font.BOLD, 25));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(normalSize);
        button.setMaximumSize(normalSize);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
                button.setPreferredSize(hoverSize);
                button.setMaximumSize(hoverSize);
                button.revalidate();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
                button.setPreferredSize(normalSize);
                button.setMaximumSize(normalSize);
                button.revalidate();
            }
        });
    }

    private void styleToggleButton(JToggleButton button) {
        Color baseColor = new Color(100, 149, 237);
        Color hoverColor = baseColor.darker();

        Dimension normalSize = new Dimension(gp.screenWidth / 8, gp.screenHeight / 16);
        Dimension hoverSize = new Dimension(gp.screenWidth / 8 + 5, gp.screenHeight / 16 + 5);

        button.setBackground(baseColor); // Màu xanh lam nhẹ
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        button.setFont(new Font("Arial", Font.BOLD, 25));
        button.setPreferredSize(normalSize);
        button.setMaximumSize(normalSize);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!button.isSelected()) {
                    button.setBackground(hoverColor);
                }
                button.setPreferredSize(hoverSize);
                button.setMaximumSize(hoverSize);
                button.revalidate();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!button.isSelected()) {
                    button.setBackground(baseColor);
                }
                button.setPreferredSize(normalSize);
                button.setMaximumSize(normalSize);
                button.revalidate();
            }
        });
    }

    private void toggleMusic() {
        if (muteButton.isSelected()) {
            menuMusic.pause(); // Thay stop() bằng pause()
            muteButton.setText("UNMUTE");
        } else {
            menuMusic.resume(); // Thay loop() bằng resume()
            muteButton.setText("MUTE");
        }
    }

    public void playMenuMusic() {
        if (!menuMusic.clip.isRunning()) menuMusic.loop();
    }

    public void stopMenuMusic() {
            menuMusic.stop(); 
    }
}