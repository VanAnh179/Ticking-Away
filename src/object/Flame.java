package object;

import main.GamePanel;
import main.UtilityTool;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Flame extends SuperObject {
    GamePanel gp;
    private int duration = 60; // 1 giây
    private BufferedImage[][] flameFrames; // [Loại flame][Frame]
    private int animationIndex = 0;
    private String flameType; // "horizontal", "vertical", "left_last", "right_last", "top_last", "down_last"
    private int activationDelay = 10;

    public Flame(GamePanel gp, int x, int y, String flameType) {
        this.gp = gp;
        this.worldX = x;
        this.worldY = y;
        this.flameType = flameType;
        this.name = "Flame";
        this.collision = true;
        solidArea = new Rectangle(16, 16, 16, 16); // Điều chỉnh vùng va chạm nhỏ hơn
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        loadFlameFrames();
    }

    private void loadFlameFrames() {
        flameFrames = new BufferedImage[7][3]; // 7 loại flame, mỗi loại 3 frame
        try {
            // Flame trung tâm
            flameFrames[0][0] = loadAndScale("/objects/flame/bomb_exploded.png");
            flameFrames[0][1] = loadAndScale("/objects/flame/bomb_exploded1.png");
            flameFrames[0][2] = loadAndScale("/objects/flame/bomb_exploded2.png");

            // Flame ngang
            flameFrames[1][0] = loadAndScale("/objects/flame/explosion_horizontal.png");
            flameFrames[1][1] = loadAndScale("/objects/flame/explosion_horizontal1.png");
            flameFrames[1][2] = loadAndScale("/objects/flame/explosion_horizontal2.png");

            // Flame dọc
            flameFrames[2][0] = loadAndScale("/objects/flame/explosion_vertical.png");
            flameFrames[2][1] = loadAndScale("/objects/flame/explosion_vertical1.png");
            flameFrames[2][2] = loadAndScale("/objects/flame/explosion_vertical2.png");

            // Flame đầu trái
            flameFrames[3][0] = loadAndScale("/objects/flame/explosion_horizontal_left_last.png");
            flameFrames[3][1] = loadAndScale("/objects/flame/explosion_horizontal_left_last1.png");
            flameFrames[3][2] = loadAndScale("/objects/flame/explosion_horizontal_left_last2.png");

            // Flame đầu phải
            flameFrames[4][0] = loadAndScale("/objects/flame/explosion_horizontal_right_last.png");
            flameFrames[4][1] = loadAndScale("/objects/flame/explosion_horizontal_right_last1.png");
            flameFrames[4][2] = loadAndScale("/objects/flame/explosion_horizontal_right_last2.png");

            // Flame đầu trên
            flameFrames[5][0] = loadAndScale("/objects/flame/explosion_vertical_top_last.png");
            flameFrames[5][1] = loadAndScale("/objects/flame/explosion_vertical_top_last1.png");
            flameFrames[5][2] = loadAndScale("/objects/flame/explosion_vertical_top_last2.png");

            // Flame đầu dưới
            flameFrames[6][0] = loadAndScale("/objects/flame/explosion_vertical_down_last.png");
            flameFrames[6][1] = loadAndScale("/objects/flame/explosion_vertical_down_last1.png");
            flameFrames[6][2] = loadAndScale("/objects/flame/explosion_vertical_down_last2.png");

            // Set frame đầu tiên
            switch (flameType) {
                case "center":
                    image = flameFrames[0][0];
                    break;
                case "horizontal":
                    image = flameFrames[1][0];
                    break;
                case "vertical":
                    image = flameFrames[2][0];
                    break;
                case "left_last":
                    image = flameFrames[3][0];
                    break;
                case "right_last":
                    image = flameFrames[4][0];
                    break;
                case "top_last":
                    image = flameFrames[5][0];
                    break;
                case "down_last":
                    image = flameFrames[6][0];
                    break;
                default:
                    throw new IllegalArgumentException("Invalid flame type: " + flameType);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage loadAndScale(String path) throws IOException {
        BufferedImage image = ImageIO.read(getClass().getResourceAsStream(path));
        UtilityTool uTool = new UtilityTool();
        return uTool.scaleImage(image, gp.tileSize, gp.tileSize);
    }

    public void update() {
        if (activationDelay > 0) {
            activationDelay--;
            collision = false;
        } else {
            collision = true; // Bật va chạm sau delay
        }

        duration--;
        if (duration <= 0) {
            gp.flames.remove(this);
        }

        // Cập nhật animation
        animationIndex = (animationIndex + 1) % 3;
        switch (flameType) {
            case "horizontal": image = flameFrames[0][animationIndex]; break;
            case "vertical": image = flameFrames[1][animationIndex]; break;
            case "left_last": image = flameFrames[2][animationIndex]; break;
            case "right_last": image = flameFrames[3][animationIndex]; break;
            case "top_last": image = flameFrames[4][animationIndex]; break;
            case "down_last": image = flameFrames[5][animationIndex]; break;
        }
    }
}