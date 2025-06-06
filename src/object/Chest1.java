package object;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import main.GamePanel;
import object.debuffitems.*;
import object.buffitems.*;

public class Chest1 extends SuperObject {
    
    private BufferedImage[] animationFrames;
    private int currentFrame = 0;
    private int animationCounter = 0;
    private final int animationSpeed = 10; // Tốc độ animation (càng nhỏ càng nhanh)
    private boolean isOpening = false;
    private boolean isOpened = false;
    private long openedTime = 0; // Thời điểm rương được mở
    private final int DISAPPEAR_DELAY = 5000; // 5 giây = 5000ms
    private boolean hasSpawnItem = false;
    GamePanel gp;

    public Chest1(GamePanel gp) {
        this.gp = gp;
        this.name = "Chest1";
        this.collision = true;
        
        // Khởi tạo mảng animation
        animationFrames = new BufferedImage[3];
        
        try {
            // Load các frame animation
            animationFrames[0] = ImageIO.read(getClass().getResourceAsStream("/objects/chest1-1.png"));
            animationFrames[1] = ImageIO.read(getClass().getResourceAsStream("/objects/chest1-2.png"));
            animationFrames[2] = ImageIO.read(getClass().getResourceAsStream("/objects/chest1-3.png"));
            
            // Đặt hình ảnh ban đầu là frame đóng
            image = animationFrames[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (isOpening && !isOpened) {
            animationCounter++;
            
            // Chuyển frame khi đủ thời gian
            if (animationCounter >= animationSpeed) {
                currentFrame++;
                animationCounter = 0;
                
                // Nếu đã đến frame cuối
                if (currentFrame >= animationFrames.length) {
                    currentFrame = animationFrames.length - 1; // Giữ ở frame cuối
                    isOpened = true;
                    spawnItem();
                    this.shouldDisappear = true;
                }
                
                image = animationFrames[currentFrame];
            }
        }
        
        // Kiểm tra nếu đã mở và đủ thời gian thì biến mất
        if (isOpened) {
            // Đánh dấu để xóa khỏi game
            this.shouldDisappear = true;
        }
    }

    @Override
    public void draw(Graphics2D g2, GamePanel gp) {
        // Không vẽ nếu đã biến mất
        if (shouldDisappear) return;
        
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        
        // Chỉ vẽ khi trong tầm nhìn của player
        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
            worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
            worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
            worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
            
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }

    public void startOpening() {
        if (!isOpening && !isOpened) {
            isOpening = true;
            currentFrame = 0;
            animationCounter = 0;
            hasSpawnItem = false;
        }
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void spawnItem() {
        if(isOpened && !hasSpawnItem) {
            Random rand = new Random();
            int randomItem = rand.nextInt(6); // 5 loại item với tỉ lệ bằng nhau
            
            SuperObject item = switch (randomItem) {
                case 0 -> new DecreaseSpeed(gp);
                case 1 -> new IncreaseBombTime(gp);
                case 2 -> new IncreaseDamage(gp);
                case 3 -> new IncreaseHealth(gp);
                case 4 -> new IncreaseLight(gp);
                case 5 -> new Teleport(gp);
                default -> null;
            };

            if (item != null) {
                item.worldX = this.worldX;
                item.worldY = this.worldY;
                item.spawnTime = System.currentTimeMillis();
                item.temporary = true;
                item.collision = false;
                
                for (int i = 0; i < gp.obj.length; i++) {
                    if (gp.obj[i] == null) {
                        gp.obj[i] = item;
                        hasSpawnItem = true;
                        break;
                    }
                }
            }
        }
    }

    public void reset() {
        isOpened = false;
        isOpening = false;
        hasSpawnItem = false;
        shouldDisappear = false;
    }
}