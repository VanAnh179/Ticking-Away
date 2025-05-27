package object;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;
import main.Sound;
import main.UtilityTool;
import tile.Tile;

public class Bomb extends SuperObject {
    public static final int EXPLOSION_RADIUS = 3;
    GamePanel gp;
    public int countdown = 90; // 1.5 giây
    public boolean exploded = false;
    public boolean isActive = false;
    private int activationDelay = 60; // 0.5 giây
    private BufferedImage[] animationFrames = new BufferedImage[3]; // 3 frame animation
    private int animationCounter = 0;
    private int animationDelay = 10; // Tốc độ animation
    public int indexInArray = -1;
    public int explosionRange = 1; // Phạm vi nổ
    private Sound bombSound = new Sound();

    public Bomb(GamePanel gp) {
        this.gp = gp;
        name = "Bomb";
        collision = false; // Ban đầu không va chạm
        loadAnimationFrames();
    }

    public Bomb(int col, int row, GamePanel gp) {
        this(gp);
        this.worldX = col * gp.tileSize;
        this.worldY = row * gp.tileSize;
    }

    private void loadAnimationFrames() {
        try {
            animationFrames[0] = ImageIO.read(getClass().getResourceAsStream("/objects/boom/bomb.png"));
            animationFrames[1] = ImageIO.read(getClass().getResourceAsStream("/objects/boom/bomb_1.png"));
            animationFrames[2] = ImageIO.read(getClass().getResourceAsStream("/objects/boom/bomb_2.png"));
            // Scale ảnh
            UtilityTool uTool = new UtilityTool();
            for (int i = 0; i < animationFrames.length; i++) {
                animationFrames[i] = uTool.scaleImage(animationFrames[i], gp.tileSize, gp.tileSize);
            }
            image = animationFrames[0]; // Frame đầu tiên
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        // Xử lý delay trước khi kích hoạt
        if (activationDelay > 0) {
            activationDelay--;
            if (activationDelay == 0) {
                collision = true;
                isActive = true;
            }
            return;
        }

        if (isActive) {
            if (animationDelay > 0) {
                animationDelay--;
            } else {
                animationDelay = 10; // Reset tốc độ animation
                animationCounter = (animationCounter + 1) % animationFrames.length;
                image = animationFrames[animationCounter];
            }
        }

        // Countdown nổ
        if (isActive && countdown > 0) {
            countdown--;
        } else if (isActive) {
            explode(getIndexInObjArray());
        }
    }

    // Helper method to find this bomb's index in the gp.obj array
    private int getIndexInObjArray() {
        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] == this) {
                return i;
            }
        }
        return -1;
    }

    private void explode(int indexInArray) {
        exploded = true;

        bombSound.setFile(2);
        bombSound.play();

        int bombCol = worldX / gp.tileSize;
        int bombRow = worldY / gp.tileSize;

        checkPlayerDamage(bombCol, bombRow);

        // Tạo flame trung tâm
        Flame centerFlame = new Flame(gp, worldX, worldY, "center");
        gp.flames.add(centerFlame);

        // Tạo flame 4 hướng (mỗi hướng 1 ô)
        for(int r = 1; r <= explosionRange; r++) {
            createFlameInDirection(bombCol, bombRow, 0, -r, "vertical");    // Trên
            createFlameInDirection(bombCol, bombRow, 0, r, "vertical");     // Dưới
            createFlameInDirection(bombCol, bombRow, -r, 0, "horizontal");  // Trái
            createFlameInDirection(bombCol, bombRow, r, 0, "horizontal");   // Phải
        }

        // Kích hoạt rung màn hình
        gp.triggerShake(8, 30); // Cường độ 5 pixel, thời gian 10 frames

        // Xóa bomb
        gp.obj[indexInArray] = null;
    }

    private void createFlameInDirection(int startCol, int startRow, int dx, int dy, String type) {
        int targetCol = startCol + dx;
        int targetRow = startRow + dy;

        // Kiểm tra giới hạn map
        if (targetCol < 0 || targetCol >= gp.maxWorldCol || targetRow < 0 || targetRow >= gp.maxWorldRow) {
            return;
        }
        // Tính toán vị trí flame và player theo pixel
        int flameX = targetCol * gp.tileSize;
        int flameY = targetRow * gp.tileSize;
        int playerX = gp.player.worldX + gp.player.solidArea.x;
        int playerY = gp.player.worldY + gp.player.solidArea.y;

        // Kiểm tra va chạm giữa flame và player
        Rectangle flameRect = new Rectangle(flameX + solidArea.x, flameY + solidArea.y, solidArea.width, solidArea.height);
        Rectangle playerRect = new Rectangle(playerX, playerY, gp.player.solidArea.width, gp.player.solidArea.height);

        if (flameRect.intersects(playerRect)) {
            if (gp.player.invincibleCounter == 0) {
                gp.player.takeDamage(1);
                gp.player.invincibleCounter = gp.player.INVINCIBLE_TIME;
            }
        }

        // Thêm flame
        String flameType = type;
        /* Điều kiện để xác định flame đầu mút */
        if (type.equals("horizontal")) {
            if (dx < 0) {
                flameType = "left_last";
            } else if (dx > 0) {
                flameType = "right_last";
            }
        } else if (type.equals("vertical")) {
            if (dy < 0) {
                flameType = "top_last";
            } else if (dy > 0) {
                flameType = "down_last";
            }
        }
        Flame flame = new Flame(gp, targetCol * gp.tileSize, targetRow * gp.tileSize, flameType);
        gp.flames.add(flame);

        // Chỉ thay đổi tile nếu tile có ID là 1 và đang có collision
        int tileIndex = gp.tileM.mapTileNum[targetCol][targetRow]; // Lấy index của tile trong mảng tile[]
        Tile currentTile = gp.tileM.tile[tileIndex]; // Lấy đối tượng tile

        // Kiểm tra ID và collision
        if (currentTile.id == 1 && currentTile.collision) {
            gp.tileM.changeTile(targetCol, targetRow, 41); // Đổi sang tile không chặn (ID 41)
            gp.ui.addScoreForRock(70); // Cộng 70 điểm khi phá rock
        }
    }

    private void checkPlayerDamage(int col, int row) {
        // Tính toán vị trí bom và người chơi theo pixel, bao gồm vùng va chạm
        int bombX = worldX + solidArea.x;
        int bombY = worldY + solidArea.y;
        int playerX = gp.player.worldX + gp.player.solidArea.x;
        int playerY = gp.player.worldY + gp.player.solidArea.y;

        // Tạo vùng va chạm cho bom và người chơi
        Rectangle bombRect = new Rectangle(bombX, bombY, solidArea.width, solidArea.height);
        Rectangle playerRect = new Rectangle(playerX, playerY, gp.player.solidArea.width, gp.player.solidArea.height);

        // Kiểm tra giao nhau
        if (bombRect.intersects(playerRect) && gp.player.invincibleCounter == 0) {
            gp.player.takeDamage(1);
            gp.player.invincibleCounter = 60; // Đặt thời gian bất tử
        }
    }
}