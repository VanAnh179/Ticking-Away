package main;

import entity.Entity;
import entity.Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
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
    // world setting;
    public int maxWorldCol;
    public int maxWorldRow;

    public int currentMap = 0;
    

    int FPS = 60;
    
    //system
    public TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler();
    Sound music = new Sound();
    Sound soundEffect = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    Thread gameThread;
    
    
    //entity and object
    public Player player = new Player(this, keyH);
    public SuperObject obj[] = new SuperObject[1000];
    public Entity enemy[] = new Entity[10];
    public ArrayList<Flame> flames = new ArrayList<>();

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
        
        obj = new SuperObject[1000];

        setupGame();
    }
    
    public void setupGame() {
    	aSetter.setObject();
        aSetter.setEnemy();
        player.wasTouchingEnemy = (cChecker.checkEntity(player, enemy) != 99);
    }

    /**
     * Bắt đầu luồng game (game loop) mới.
     */
    public void startGameThread() {
        ui.resetTimer();
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

        //enemy
        for (int i = 0; i < enemy.length; i++) {
            if (enemy[i] != null) {
                System.out.println("[Debug] Updating enemy " + i);
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
                        player.invincibleCounter = player.INVINCIBLE_TIME;
                    }
                }
            }
        }
    }

    /**
     * Vẽ người chơi và các thành phần khác lên panel.
     * @param g đối tượng Graphics để vẽ
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        
        // DEBUG
        long drawStart = 0;
        if (keyH.checkDrawTime == true) {
        	drawStart = System.nanoTime();
        }
        

        //tile
        tileM.draw(g2); // Vẽ tile
        
        //object
        for (int i = 0; i < obj.length; i++) {
        	if (obj[i] != null) {
        		obj[i].draw(g2, this);
        	}
        }

        //enemy
        for (int i = 0; i < enemy.length; i++) {
            if (enemy[i] != null) {
                System.out.println("[Debug] Drawing enemy " + i + " at (" + enemy[i].worldX + ", " + enemy[i].worldY + ")");
                enemy[i].draw(g2);
            }
        }
        
        //player
        player.draw(g2); // Vẽ player
        
        //UI
        ui.draw(g2);
        
        // Vẽ flame
        for (Flame flame : flames) {
            int screenX = flame.worldX - player.worldX + player.screenX;
            int screenY = flame.worldY - player.worldY + player.screenY;
            g2.drawImage(flame.image, screenX, screenY, null);
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
}