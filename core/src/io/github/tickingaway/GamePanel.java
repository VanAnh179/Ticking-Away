package io.github.tickingaway;

import javax.swing.JPanel;

import io.github.tickingaway.entity.Player;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GamePanel extends JPanel implements Runnable {
    
    // Cài đặt màn hình
    final int originalTileSize = 16;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; // Kích thước 1 ô vuông: 48 * 48 pixel
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol; // 768 px
    final int screenHeight = tileSize * maxScreenRow; // 576 px

    int FPS = 60;

    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    Player player = new Player(this, keyH);

    int playerX = 0;
    int playerY = 0;
    int playerSpeed = 4;

    /**
     * Hàm khởi tạo GamePanel.
     * Thiết lập kích thước panel, màu nền, bật double buffering, thêm key listener và cho phép focus.
     */
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    /**
     * Bắt đầu luồng game (game loop) mới.
     */
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Vòng lặp chính của game.
     * Xử lý thời gian, cập nhật trạng thái game và vẽ lại panel.
     */
    @Override
    public void run() {
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                update();
                repaint();
                delta--;
            } 
        }
    }

    /**
     * Cập nhật vị trí của người chơi dựa trên phím bấm.
     */
    public void update() {
        player.update();
    }

    /**
     * Vẽ người chơi và các thành phần khác lên panel.
     * @param g đối tượng Graphics để vẽ
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        player.draw(g2);
        g2.dispose();
    }
}
