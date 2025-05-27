package main;

import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    private boolean isNewGame = true;
    private UI ui = new UI(gamePanel);

    public MainFrame() {
        setTitle("Ticking Away");
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Khởi tạo các panel
        menuPanel = new MenuPanel(this);
        gamePanel = new GamePanel(this);

        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(gamePanel, "Game");
        setSize(gamePanel.screenWidth, gamePanel.screenHeight);
        add(mainPanel);
        setVisible(true);

        // Tải trước game để giảm delay
        // new Thread(gamePanel::setupGame).start();
    }

    public void switchToGame() {
        if (isNewGame || gamePanel == null || gamePanel.isGameFinished()) {
            // Tạo game mới nếu là game mới hoặc game đã kết thúc
            gamePanel = new GamePanel(this);
            mainPanel.add(gamePanel, "Game");
            isNewGame = false;
        }
        cardLayout.show(mainPanel, "Game");
        gamePanel.resumeGame();
        menuPanel.showResumeButton(true);
        menuPanel.stopMenuMusic();
    
    }

    public void switchToMenu() {
        isNewGame = false; // Đánh dấu không phải game mới
        cardLayout.show(mainPanel, "Menu");
        menuPanel.showResumeButton(gamePanel != null && !gamePanel.isGameFinished());
        if (gamePanel != null) {
            gamePanel.pauseGame();
            gamePanel.stopMusic();
            if (ui.tutorialSound.clip.isRunning()) {
                ui.tutorialSound.stop();
                ui.textSound.stop();
            }
            
        }
        menuPanel.playMenuMusic();
    }

    // Thêm phương thức cho nút Start
    public void startNewGame() {
        isNewGame = true;
        switchToGame();
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }
}