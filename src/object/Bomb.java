package object;

import main.GamePanel;
import main.UtilityTool;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Bomb extends SuperObject {
    GamePanel gp;
    public int countdown = 120; // 3 giây
    public boolean exploded = false;
    public boolean isActive = false;
    private int activationDelay = 60; // 0.5 giây
    private BufferedImage[] animationFrames = new BufferedImage[3]; // 3 frame animation
    private int animationCounter = 0;
    private int animationDelay = 10; // Tốc độ animation
    public int indexInArray = -1;

    public Bomb(GamePanel gp) {
        this.gp = gp;
        name = "Bomb";
        collision = false; // Ban đầu không va chạm
        loadAnimationFrames();
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
        int bombCol = worldX / gp.tileSize;
        int bombRow = worldY / gp.tileSize;

        // Tạo flame trung tâm
        Flame centerFlame = new Flame(gp, worldX, worldY, "center");
        gp.flames.add(centerFlame);

        // Tạo flame 4 hướng (mỗi hướng 1 ô)
        createFlameInDirection(bombCol, bombRow, 0, -1, "vertical");    // Trên
        createFlameInDirection(bombCol, bombRow, 0, 1, "vertical");     // Dưới
        createFlameInDirection(bombCol, bombRow, -1, 0, "horizontal");  // Trái
        createFlameInDirection(bombCol, bombRow, 1, 0, "horizontal");   // Phải

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

        // Thêm flame
        String flameType = type;
        /* Điều kiện để xác định flame đầu mút */
        if ((dx == -1 && type.equals("horizontal")) || 
            (dx == 1 && type.equals("horizontal")) || 
            (dy == -1 && type.equals("vertical")) || 
            (dy == 1 && type.equals("vertical"))) {
            flameType = "left_last"; // Hoặc "right_last", "top_last", "down_last"
        }
        Flame flame = new Flame(gp, targetCol * gp.tileSize, targetRow * gp.tileSize, flameType);
        gp.flames.add(flame);

        // Chỉ thay đổi tile nếu tile hiện tại có collision
        if (gp.tileM.tile[gp.tileM.mapTileNum[targetCol][targetRow]].collision) {
            gp.tileM.changeTile(targetCol, targetRow, 41); // Đổi sang tile không chặn
        }
    }
}