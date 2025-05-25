package entity;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.UtilityTool;

public class Entity {
    public GamePanel gp;
	public int worldX;
    public int worldY;
    public int speed;

    public BufferedImage up1, up2, up3, up4, up5, up6, up7, up8, up9;
    public BufferedImage right1, right2, right3, right4, right5, right6, right7, right8, right9;
    public BufferedImage down1, down2, down3, down4, down5, down6, down7, down8, down9;
    public BufferedImage left1, left2, left3, left4, left5, left6, left7, left8, left9;

    public int spriteCounter = 0;
    public int spriteNum = 1;
    public String lastDirection = "down";

    public String direction;
    public Rectangle solidArea = new Rectangle();
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;

    public String name;
    public int maxHealth;
    public int health;

    public Entity(GamePanel gp) {
        this.gp = gp;
    }

    public void setAction() {

    }

    public void update() {
        setAction();
        
        collisionOn = false;
        gp.cChecker.checkTile(this);
        gp.cChecker.checkObject(this, false);
        gp.cChecker.checkPlayer(this);
        
        spriteCounter++;
        if (spriteCounter > 12) {
            spriteNum++;
            if (spriteNum > 4) {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }

        if (!collisionOn) {
            switch (direction) {
                case "up": worldY -= speed; break;
                case "down": worldY += speed; break;
                case "left": worldX -= speed; break;
                case "right": worldX += speed; break;
                case "up-left":
                    worldY -= speed;
                    worldX -= speed;
                    break;
                case "up-right":
                    worldY -= speed;
                    worldX += speed;
                    break;
                case "down-left":
                    worldY += speed;
                    worldX -= speed;
                    break;
                case "down-right":
                    worldY += speed;
                    worldX += speed;
                    break;
            }
        }
        
    }

    public void takeDamage(int damage) {}

    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Xử lý trường hợp direction rỗng
        String drawDirection = direction.isEmpty() ? lastDirection : direction;

        switch (drawDirection) {
            case "up":
                if (spriteNum == 1) image = up1;
                else if (spriteNum == 2) image = up2;
                else if (spriteNum == 3) image = up3;
                else image = up4;
                break;
            case "down":
                if (spriteNum == 1) image = down1;
                else if (spriteNum == 2) image = down2;
                else if (spriteNum == 3) image = down3;
                else image = down4;
                break;
            case "left":
                if (spriteNum == 1) image = left1;
                else if (spriteNum == 2) image = left2;
                else if (spriteNum == 3) image = left3;
                else image = left4;
                break;
            case "right":
                if (spriteNum == 1) image = right1;
                else if (spriteNum == 2) image = right2;
                else if (spriteNum == 3) image = right3;
                else image = right4;
                break;
        }

        // Vẽ hình ảnh mặc định nếu image null
        if (image == null) {
            image = down1; // Sử dụng hình ảnh mặc định
        }

        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);

        // Reset alpha
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

    public BufferedImage setup(String imagePath) {
        UtilityTool uTool = new UtilityTool();
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
            if (image == null) {
                throw new IOException("Image not found: " + imagePath + ".png");
            }
            image = uTool.scaleImage(image, gp.tileSize, gp.tileSize);
        } catch (IOException e) {
            System.err.println("Lỗi tải ảnh: " + imagePath + ".png");
            e.printStackTrace();
        }
        return image;
    }
}