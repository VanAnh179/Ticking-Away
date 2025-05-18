package main;

import entity.Entity;
import entity.Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import object.Bomb;
import object.Flame;
import object.SuperObject;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {

    // Cài đặt màn hình
    final int originalTileSize = 16;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; // Kích thước 1 ô vuông: 48 * 48 pixel
    public final int maxScreenCol = 20;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol; // 768 px
    public final int screenHeight = tileSize * maxScreenRow; // 576 px
    // world setting
    public int maxWorldCol;
    public int maxWorldRow;

    public int currentMap = 0;

    int FPS = 60;
    
    // system
    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH; // Sẽ khởi tạo sau với tham chiếu đến GamePanel
    Sound music = new Sound();
    Sound soundEffect = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    Thread gameThread;
    
    // entity and object
    public Player player = new Player(this, keyH);
    public SuperObject obj[] = new SuperObject[1000];
    public Entity enemy[] = new Entity[10];
    public ArrayList<Flame> flames = new ArrayList<>();

    public EventObject eventObj;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        
        // Khởi tạo KeyHandler sau khi GamePanel đã được tạo
        keyH = new KeyHandler(this);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.requestFocusInWindow(); // Đảm bảo GamePanel có focus

        player = new Player(this, keyH);
        
        obj = new SuperObject[1000];
        eventObj = new EventObject(this);

        // Khởi tạo lại Player với KeyHandler đã cập nhật
        player = new Player(this, keyH);

        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                System.out.println("GamePanel gained focus");
            }
        });
        setupGame();
    }
    
    public void setupGame() {
        aSetter.setObject();
        aSetter.setEnemy();
        player.wasTouchingEnemy = (cChecker.checkEntity(player, enemy) != 99);
    }

    public void startGameThread() {
        ui.resetTimer();
        if (gameThread != null && gameThread.isAlive()) {
            try {
                gameThread.join(); // Chờ thread cũ kết thúc
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            gameThread = null;
        }
        gameThread = new Thread(this);
        gameThread.start();
        System.out.println("Game thread started");
    }

    @Override
    public void run() {
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        while (gameThread != null) { // Giữ vòng lặp hoạt động để xử lý sự kiện
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

    public void update() {
        if (!ui.gameFinished) { // Chỉ update khi game chưa kết thúc
            player.update();

            // Kiểm tra game over khi health <= 0
            if (player.health <= 0) {
                gameOver();
                return; // Dừng update khi game over
            }

            // enemy
            for (int i = 0; i < enemy.length; i++) {
                if (enemy[i] != null) {
                    enemy[i].update();
                }
            }

            // Cập nhật bomb
            for (int i = 0; i < obj.length; i++) {
                if (obj[i] != null && obj[i].name.equals("Bomb")) {
                    ((Bomb) obj[i]).update();
                }
            }

            // Cập nhật flame
            for (int i = flames.size() - 1; i >= 0; i--) {
                Flame flame = flames.get(i);
                flame.update();
                
                // Kiểm tra va chạm với player
                if (flame.collision) {
                    int flameX = flame.worldX + flame.solidArea.x;
                    int flameY = flame.worldY + flame.solidArea.y;
                    int playerX = player.worldX + player.solidArea.x;
                    int playerY = player.worldY + player.solidArea.y;

                    Rectangle flameRect = new Rectangle(flameX, flameY, flame.solidArea.width, flame.solidArea.height);
                    Rectangle playerRect = new Rectangle(playerX, playerY, player.solidArea.width, player.solidArea.height);

                    if (flameRect.intersects(playerRect)) {
                        if (player.invincibleCounter == 0) {
                            player.takeDamage(1);
                            player.invincibleCounter = player.INVINCIBLE_TIME; // Đồng bộ với Player.java
                        }
                    }

                    // Kiểm tra va chạm với enemy (nếu enemy có thể bị phá hủy bởi flame)
                    for (int j = 0; j < enemy.length; j++) {
                        if (enemy[j] != null) {
                            int enemyX = enemy[j].worldX + enemy[j].solidArea.x;
                            int enemyY = enemy[j].worldY + enemy[j].solidArea.y;
                            Rectangle enemyRect = new Rectangle(enemyX, enemyY, enemy[j].solidArea.width, enemy[j].solidArea.height);

                            if (flameRect.intersects(enemyRect)) {
                                enemy[j] = null; // Xóa enemy khi bị flame chạm vào
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        
        // DEBUG
        long drawStart = 0;
        if (keyH.checkDrawTime == true) {
            drawStart = System.nanoTime();
        }

        // tile
        tileM.draw(g2); // Vẽ tile
        
        // object
        for (int i = 0; i < obj.length; i++) {
            if (obj[i] != null) {
                obj[i].draw(g2, this);
            }
        }

        // enemy
        for (int i = 0; i < enemy.length; i++) {
            if (enemy[i] != null) {
                enemy[i].draw(g2);
            }
        }
        
        // player
        player.draw(g2); // Vẽ player
        
        // UI
        ui.draw(g2);
        
        // Vẽ flame chỉ khi game chưa kết thúc
        if (!ui.gameFinished) {
            for (Flame flame : flames) {
                int screenX = flame.worldX - player.worldX + player.screenX;
                int screenY = flame.worldY - player.worldY + player.screenY;
                // Chỉ vẽ nếu flame nằm trong tầm nhìn
                if (flame.worldX + tileSize > player.worldX - player.screenX &&
                    flame.worldX - tileSize < player.worldX + player.screenX &&
                    flame.worldY + tileSize > player.worldY - player.screenY &&
                    flame.worldY - tileSize < player.worldY + player.screenY) {
                    g2.drawImage(flame.image, screenX, screenY, null);
                }
            }
        }
        
        // DEBUG
        if (keyH.checkDrawTime == true) {
            long drawEnd = System.nanoTime();
            long passed = drawEnd - drawStart;
            g2.setColor(Color.WHITE);
            g2.drawString("Draw Time: " + passed, 10, 400);
            System.out.println("Draw Time: " + passed);
        }
    }
    
    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }

    public void stopMusic() {
        music.stop();
    }

    public void playSoundEffect(int i) {
        soundEffect.setFile(i);
        soundEffect.play();
    }

    public void gameOver() {
        flames.clear();
        ui.gameFinished = true;
        ui.gameWon = false;
        stopMusic();
        keyH.reset();
        repaint(); // Đảm bảo giao diện được cập nhật
        this.requestFocusInWindow(); // Đảm bảo focus để nhận phím
    }

    public void resetGame() {
        System.out.println("ResetGame called");
        ui.gameFinished = false;
        ui.gameWon = false;
        player.resetPlayer();
        flames.clear();
        for(int i = 0; i < obj.length; i++) {
            obj[i] = null;
        }
        for(int i = 0; i < enemy.length; i++) {
            enemy[i] = null;
        }
        tileM.resetMap();
        ui.resetTimer();
        setupGame();
        if (gameThread != null && gameThread.isAlive()) {
            try {
                gameThread.join(); // Chờ thread cũ kết thúc
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            gameThread = null;
        }
        playMusic(0); // Phát lại nhạc nền
        repaint(); // Đảm bảo giao diện được làm mới
        this.requestFocusInWindow(); // Đảm bảo panel nhận lại focus
        startGameThread();
    }
}