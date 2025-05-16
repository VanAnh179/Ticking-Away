package button;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import main.GamePanel;

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
        setBackground(Color.DARK_GRAY); // Màu nền bình thường
        setForeground(Color.WHITE); // Màu chữ bình thường
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);

        // Xử lý khi di chuột vào nút
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setIcon(hoverIcon);
                setBackground(Color.BLACK); // Màu tối hơn khi di chuột vào
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setIcon(normalIcon);
                setBackground(Color.DARK_GRAY); // Quay lại màu bình thường
            }
        });

        addActionListener(e -> {
            setIcon(clickIcon);
            setBackground(Color.BLACK);
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                JFrame frame = (JFrame) window;
                frame.getContentPane().removeAll(); // Xóa toàn bộ nội dung MenuScreen
                GamePanel gamePanel = new GamePanel(); // Tạo một GamePanel mới
                frame.add(gamePanel); // Thêm GamePanel vào cửa sổ hiện tại
                frame.revalidate(); // Cập nhật lại bố cục
                frame.repaint(); // Vẽ lại màn hình

                gamePanel.requestFocusInWindow();
                gamePanel.startGameThread();
            }
        });
    }
}
