package button;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.awt.Font;
import java.awt.Color;
import main.GamePanel;
import main.MenuScreen;

public class StartButton extends JButton {
    private ImageIcon normalIcon, hoverIcon, clickIcon;

    public StartButton() {
        super("");

        // Load hình ảnh
        normalIcon = new ImageIcon("assets/button/normal_start.jpg");
        hoverIcon = new ImageIcon("assets/button/hover_start.jpg");
        clickIcon = new ImageIcon("assets/button/start_click.jpg");

        // Đặt hình ảnh mặc định
        setIcon(normalIcon);
        setFont(new Font("Serif", Font.BOLD, 30));
        setBackground(Color.DARK_GRAY);
        setForeground(Color.WHITE);
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);

        // Xử lý khi di chuột vào nút
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setIcon(hoverIcon);
                setBackground(Color.BLACK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setIcon(normalIcon);
                setBackground(Color.DARK_GRAY);
            }
        });

        addActionListener(e -> {
            JFrame menuFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (menuFrame instanceof MenuScreen menuScreen) {
                menuScreen.stopMenuMusic(); // Dừng nhạc menu
                menuScreen.setVisible(false); // Ẩn menu thay vì dispose
            } else {
                menuFrame.setVisible(false); // fallback nếu không phải MenuScreen
            }

            JFrame gameFrame = new JFrame();
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.setResizable(false);
            gameFrame.setTitle("Ticking Away");

            GamePanel gamePanel = new GamePanel(); // setupGame() đã được gọi trong constructor
            gamePanel.playMusic(1);
            gameFrame.add(gamePanel);
            gameFrame.pack();
            gameFrame.setLocationRelativeTo(null);
            gameFrame.setVisible(true);

            gamePanel.startGameThread();
        });
    }
}
