package main;

import enemy.E_Bitter;
import enemy.E_Sweet;
import enemy.E_Watermelon;
import entity.Entity;
import entity.Player;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import object.Bomb;
import object.Chest1;
import object.Chest2;
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
    public boolean isPaused = false;
    MainFrame mainFrame;
    public TileManager tileM = new TileManager(this);
    public KeyHandler keyH; // Sẽ khởi tạo sau với tham chiếu đến GamePanel
    Sound music = new Sound();
    Sound soundEffect = new Sound();
    Sound deathE = new Sound();
    Sound flameSe = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    public int shakeIntensity = 0; // Cường độ rung
    public int shakeDuration = 0;  // Thời gian rung (tính bằng frame)
    public int shakeCounter = 0;   // Đếm thời gian đã rung
    int baseLightRadius = tileSize * 3;
    int flickerFrame = 0;
    final int baseRadiusMax = tileSize * 3;  // Bán kính tối đa ban đầu
    final int baseRadiusMin = tileSize;      // Bán kính tối thiểu khi thu nhỏ hết cỡ
    int timeElapsedFrames = 0;                // Đếm số frame đã chạy
    private float flickerAngle = 0;
    public boolean isStartingEffect = true;
    private long effectStartTime;
    private final int FADE_IN_DURATION = 5000; // 5 giây
    private final int FLASH_DURATION = 300; // Thời gian chớp
    private int flashCount = 0;
    private long pauseStartTime; // Thời điểm bắt đầu pause
    public long totalPausedTime = 0; // Tổng thời gian đã pause
    private Thread gameThread;
    
    // entity and object
    public Player player = new Player(this, keyH);
    public SuperObject obj[] = new SuperObject[500];
    public Entity enemy[] = new Entity[100];
    public ArrayList<Flame> flames = new ArrayList<>();

    public EventObject eventObj;
    public int hasKey = 0;

    public GamePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        effectStartTime = System.currentTimeMillis();
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        
        // Khởi tạo KeyHandler sau khi GamePanel đã được tạo
        keyH = new KeyHandler(this);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.requestFocusInWindow(); // Đảm bảo GamePanel có focus

        flameSe.setFile(8);
        flameSe.loop();

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
        this.requestFocusInWindow(); // Đảm bảo GamePanel có focus

        // Thêm MouseListener để xử lý nút "Next"
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (ui != null) {
                    ui.handleClick(e.getX(), e.getY());
                }
            }
        });
    }

    private void styleButton(JButton button) {
        Color baseColor = new Color(255, 105, 180);
        Color hoverColor = baseColor.darker();

        Dimension normalSize = new Dimension(screenWidth / 20 + 5, screenHeight / 20);
        Dimension hoverSize = new Dimension(screenWidth / 20 + 7, screenHeight / 20 + 7);
        
        button.setBackground(baseColor); // Màu hồng
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        button.setFont(new Font("Arial", Font.BOLD, 15));
        Dimension size = button.getPreferredSize();
        button.setBounds(screenWidth - size.width - 20, 48, size.width, size.height);
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

    
    public void setupGame() {
        aSetter.setObject();
        aSetter.setEnemy();
        player.wasTouchingEnemy = (cChecker.checkEntity(player, enemy) != 99);
    }

    public void startGameThread() {
        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new Thread(this); // this implements Runnable
            gameThread.start();
            System.out.println("Game thread started");
        } else {
            System.out.println("Game thread already running");
        }
    }

    public void stopGameThread() {
        if (gameThread != null) {
            gameThread.interrupt();
            gameThread = null;
        }
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

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
            if (delta >= 1 && !isPaused) { // Chỉ update khi không paused
                update();
                repaint();
                delta--;
            } 
        }
    }

    public void update() {
        // Tạm dừng mọi hoạt động nếu đang hiện tutorial hoặc thoại
        if (isPaused || ui.showTutorial || ui.showKeySequence || ui.showPortalSequence || isStartingEffect) {
            return;
        }

        if (!ui.gameFinished) { // Chỉ update khi game chưa kết thúc
            player.update();

            // Kiểm tra game over khi health <= 0
            if (player.health <= 0) {
                gameOver();
                return; // Dừng update khi game over
            }

            // Kiểm tra và xóa các object đánh dấu shouldDisappear
            for (int i = 0; i < obj.length; i++) {
                if (obj[i] != null && obj[i].shouldDisappear) {
                    obj[i] = null;
                }
            }

            for (int i = 0; i < obj.length; i++) {
                if(obj[i] != null) {
                    if(obj[i] instanceof Chest2) {
                        ((Chest2) obj[i]).update();
                    }
                }
            }

            for (int i = 0; i < obj.length; i++) {
                if(obj[i] != null) {
                    if(obj[i] instanceof Chest1) {
                        ((Chest1) obj[i]).update();
                    }
                }
            }

            //kiểm tra xóa item hết hạn
            for (int i = 0; i < obj.length; i++) {
                if (obj[i] != null && obj[i].temporary) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - obj[i].spawnTime > 15000) { // 15 giây
                        obj[i] = null;
                    }
                }
            }

            // enemy
            for (int i = 0; i < enemy.length; i++) {
                if (enemy[i] == null) continue;
                if (enemy[i].health > 0) {
                    enemy[i].update();
                    if (enemy[i].health <= 0) {
                        deathE.setFile(5);
                        deathE.play();
                        addScoreForEnemy(enemy[i]);
                        enemy[i] = null;
                    }
                }
            }

            // Cập nhật bomb
            for (int i = 0; i < obj.length; i++) {
                if (obj[i] != null) {
                    obj[i].update();
                }
                if (obj[i] != null && obj[i].name.equals("Bomb")) {
                    ((Bomb) obj[i]).update();
                }
            }

            timeElapsedFrames++;
            int durationFrames = FPS * 25; // 15 giây = 900 frame

            if (timeElapsedFrames <= durationFrames) {
                float ratio = 1.0f - ((float)timeElapsedFrames / durationFrames);
                baseLightRadius = baseRadiusMin + (int)((baseRadiusMax - baseRadiusMin) * ratio);
            } else {
                baseLightRadius = baseRadiusMin;
            }

            float flickerAmplitude = 5;
            

            flickerAngle += 0.1f;
            if (flickerAngle > 2 * Math.PI) flickerAngle -= 2 * Math.PI;
            baseLightRadius += (int)(Math.sin(flickerAngle) * flickerAmplitude);

            // Cập nhật flame
            for (int i = flames.size() - 1; i >= 0; i--) {
                if(!flames.isEmpty()) {
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

                        if(flame.justCreated) {
                            int flameCol = flame.worldX / tileSize;
                            int flameRow = flame.worldY / tileSize;
                            int playerCol = player.worldX / tileSize;
                            int playerRow = player.worldY / tileSize;
                            
                            if (flameCol == playerCol && flameRow == playerRow && player.invincibleCounter == 0) {
                                player.takeDamage(1);
                                player.invincibleCounter = player.INVINCIBLE_TIME;
                            }
                            flame.justCreated = false;
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

        // Vẽ các thành phần game
        tileM.draw(g2);
        

        player.draw(g2);
        
        // Vẽ hiệu ứng mở đầu
        if (isStartingEffect) {
            drawStartEffect(g2);
        }

        // Chỉ vẽ các thành phần game và darkness khi hiệu ứng mở đầu đã kết thúc
        if (!isStartingEffect) {
            // Xử lý hiệu ứng rung
            int offsetX = 0;
            int offsetY = 0;
            if (shakeCounter < shakeDuration) {
                offsetX = (int) (Math.random() * shakeIntensity * 2 - shakeIntensity);
                offsetY = (int) (Math.random() * shakeIntensity * 2 - shakeIntensity);
                shakeCounter++;
            }
            for (int i = 0; i < obj.length; i++) {
                if (obj[i] != null) {
                    obj[i].draw(g2, this);
                }
            }
            for (int i = 0; i < enemy.length; i++) {
                if (enemy[i] != null && enemy[i].health > 0) {
                    enemy[i].draw(g2);
                }
            }
            // Vẽ flame và darkness effect
            if (!ui.gameFinished) {
                for (Flame flame : flames) {
                    int screenX = flame.worldX - player.worldX + player.screenX + offsetX;
                    int screenY = flame.worldY - player.worldY + player.screenY + offsetY;
                    g2.drawImage(flame.image, screenX, screenY, null);
                }
                int spotlightX = player.screenX + player.solidArea.x;
                int spotlightY = player.screenY + player.solidArea.y;
                drawDarknessEffect(g2, spotlightX, spotlightY, baseLightRadius);
            }
            g2.translate(-offsetX, -offsetY);
        }
                    
        // UI
        ui.draw(g2);
    }

    private void drawStartEffect(Graphics2D g2) {
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - effectStartTime;

        // Hiệu ứng fade in từ đen
        if (elapsed < FADE_IN_DURATION) {
            float progress = (float) elapsed / FADE_IN_DURATION * 0.5f;
            float opacity = 1.0f - progress * progress;
            // Thêm một lớp phủ đen phụ với opacity cố định
            g2.setColor(new Color(0, 0, 0, 0.8f)); // Lớp phủ thêm
            g2.fillRect(0, 0, screenWidth, screenHeight);
            // Lớp phủ chính
            g2.setColor(new Color(0, 0, 0, opacity));
            g2.fillRect(0, 0, screenWidth, screenHeight);
        }

        // Hiệu ứng chớp trắng 2 lần
        else if (flashCount < 2) {
            // Tính thời gian đã trôi qua kể từ khi fade in kết thúc
            long flashElapsed = elapsed - FADE_IN_DURATION;

            // Chớp trắng trong khoảng thời gian FLASH_DURATION
            if (flashElapsed % (FLASH_DURATION * 2) < FLASH_DURATION) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, screenWidth, screenHeight);
            }

            // Tăng flashCount sau mỗi 2 lần chớp (1 chu kỳ sáng + tắt)
            if (flashElapsed >= (FLASH_DURATION * 2) * (flashCount + 1)) {
                flashCount++;
            }
        }

        // Kết thúc hiệu ứng sau 2 lần chớp
        else {
            isStartingEffect = false;
        }

        g2.setComposite(AlphaComposite.SrcOver); // Reset composite
    }

    public void drawDarknessEffect(Graphics2D g2, int centerX, int centerY, int radius) {
        BufferedImage darkness = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gDark = darkness.createGraphics();
        int totalRadius = radius + player.bonusLightRadius;

        // Tô nền tối mờ
        gDark.setColor(new Color(0, 0, 0, 250)); // Alpha cao làm tối mạnh (chỉnh từ 200 đổ lên)
        gDark.fillRect(0, 0, screenWidth + 50, screenHeight + 50);

        // Dùng AlphaComposite để đục lỗ hình tròn
        gDark.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1.0f)); // Đục vùng này
        gDark.setPaint(new RadialGradientPaint(
            new Point(centerX, centerY),
            totalRadius,
            new float[]{0f, 1f},
            new Color[]{new Color(0, 0, 0, 1f), new Color(0, 0, 0, 0f)}
        ));
        gDark.fillOval(centerX - totalRadius, centerY - totalRadius, totalRadius * 2, totalRadius * 2);

        gDark.dispose();

        // Vẽ lớp darkness đã đục lỗ lên màn hình
        g2.drawImage(darkness, 0, 0, null);
    }

    public void triggerShake(int intensity, int duration) {
        this.shakeIntensity = intensity;
        this.shakeDuration = duration;
        this.shakeCounter = 0;
    }

    public boolean isGameFinished() {
        return ui.gameFinished;
    }

    public void pauseGame() {
        isPaused = true;
        pauseStartTime = System.currentTimeMillis();
        stopMusic();
        if (gameThread != null) {
            gameThread.interrupt();
            gameThread = null;
        }
    }

    public void resumeGame() {
        if (isPaused) {
            totalPausedTime += System.currentTimeMillis() - pauseStartTime; // Cập nhật tổng thời gian pause
            isPaused = false;
        }
        if (gameThread == null) {
            startGameThread();
            playMusic(1);
        }
        requestFocusInWindow();
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
    }

    public void resetGame() {
        totalPausedTime = 0;
        stopGameThread();
        System.out.println("ResetGame called");
        ui.gameFinished = false;
        ui.gameWon = false;
        player.resetPlayer();
        hasKey = 0;
        ui.textSound.stop();
        ui.tutorialSound.stop();
        flames.clear();

        baseLightRadius = tileSize * 3; // Reset bán kính ánh sáng
        player.bonusLightRadius = tileSize; // Reset bonus light radius

        for(int i = 0; i < obj.length; i++) {
            obj[i] = null;
        }
        for(int i = 0; i < enemy.length; i++) {
            enemy[i] = null;
        }
        obj = new SuperObject[1000];
        enemy = new Entity[100];
        eventObj.firstPortalContact = true;
        tileM.resetMap();
        ui.resetTimer();
        setupGame();
        ui.startTimer();
        // Dừng thread hiện tại nếu đang chạy
        if (gameThread != null) {
            gameThread.interrupt();
            gameThread = null;
        }
        startGameThread();
        playMusic(1); // Phát lại nhạc nền
        this.requestFocusInWindow(); // Đảm bảo GamePanel có focus
        ensureFocus();
    }

    public void ensureFocus() {
        if (!this.isFocusOwner()) {
            this.requestFocusInWindow();
        }
    }

    public void addScoreForEnemy(Entity enemy) {
        double score = 0;
        if(enemy instanceof  E_Bitter) {
            score = player.BitterScore;
            ui.showMessage("+" + player.BitterScore + "points!");
        } else if(enemy instanceof  E_Sweet) {
            score = player.SweetScore;
            ui.showMessage("+" + player.SweetScore + "points!");
        } else if(enemy instanceof  E_Watermelon) {
            score = player.WatermelonScore;
            ui.showMessage("+" + player.WatermelonScore + "points!");
        }
        ui.visibleScore += score;
    }
}