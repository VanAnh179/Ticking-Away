package main;

import button.StartButton;
import button.UnMuteButton;
import java.awt.*;
import javax.swing.*;

public class MenuScreen extends JFrame {
    private Sound musicPlayer;
    private StartButton startButton;
    private UnMuteButton unMuteButton;

    public MenuScreen() {
        setTitle("Ticking Away - Menu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Sử dụng BackgroundPanel để vẽ nền
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(null);

        // Tạo tiêu đề "Ticking Away"
        JLabel titleLabel = new JLabel("Ticking Away", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 50));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBounds(180, 180, 440, 60);

        // Tạo nút Start
        startButton = new StartButton();
        startButton.setBounds(325, 330, 125, 40);

        // Tạo nhạc nền
        musicPlayer = new Sound();
        musicPlayer.setFile(0); // Chọn file nhạc
        musicPlayer.loop();

        // Tạo nút UnMute
        unMuteButton = new UnMuteButton(musicPlayer);
        unMuteButton.setBounds(20, 500, 40, 40);

        // Thêm các thành phần vào panel
        backgroundPanel.add(titleLabel);
        backgroundPanel.add(startButton);
        backgroundPanel.add(unMuteButton);
        add(backgroundPanel);

        setVisible(true);
    }
    
    // Phương thức để dừng và phát nhạc
    private void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }

    private void playMusic() {
        if (musicPlayer != null) {
            musicPlayer.play();
            musicPlayer.loop();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuScreen().setVisible(true);
        });
    }

}

// Class JPanel để vẽ nền
class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel() {
        backgroundImage = Toolkit.getDefaultToolkit().getImage("assets/background/backgroundNew.png");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
