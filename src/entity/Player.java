package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;
import main.UtilityTool;

public class Player extends Entity {
	GamePanel gp;
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;

    public BufferedImage idleDown, idleUp, idleLeft, idleRight;
    
    public int hasChest = 0;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle();
        solidArea.x = 12;
        solidArea.y = 12;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 24;
        solidArea.height = 24;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 31;
        worldY = gp.tileSize * 4;
        speed = 4;
        direction = "";
    }

    public void getPlayerImage() {
        
        up1 = setup("up (1)");
        up2 = setup("up (2)");
        up3 = setup("up (3)");
        up4 = setup("up (4)");
        up5 = setup("up (5)");
        up6 = setup("up (6)");
        up7 = setup("up (7)");
        up8 = setup("up (8)");
        up9 = setup("up (9)");

        down1 = setup("down (1)");
        down2 = setup("down (2)");
        down3 = setup("down (3)");
        down4 = setup("down (4)");
        down5 = setup("down (5)");
        down6 = setup("down (6)");
        down7 = setup("down (7)");
        down8 = setup("down (8)");
        down9 = setup("down (9)");

        left1 = setup("left (9)");
        left2 = setup("left (1)");
        left3 = setup("left (2)");
        left4 = setup("left (3)");
        left5 = setup("left (4)");
        left6 = setup("left (5)");
        left7 = setup("left (6)");
        left8 = setup("left (7)");
        left9 = setup("left (8)");

        right1 = setup("right (9)");
        right2 = setup("right (1)");
        right3 = setup("right (2)");
        right4 = setup("right (3)");
        right5 = setup("right (4)");
        right6 = setup("right (5)");
        right7 = setup("right (6)");
        right8 = setup("right (7)");
        right9 = setup("right (8)");
        
        idleDown = setup("down (1)"); // Sử dụng frame đầu tiên của để tạo hình ảnh riêng cho đứng yên
        idleUp = setup("up (1)");
        idleLeft = setup("left (9)");
        idleRight = setup("right (9)");
    }

    public BufferedImage setup(String imageName) {
    	UtilityTool uTool = new UtilityTool();
    	BufferedImage image = null;
    	
    	try {
    		image = ImageIO.read(getClass().getResourceAsStream("/player/" + imageName + ".png"));
    		image = uTool.scaleImage(image, gp.tileSize, gp.tileSize);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return image;
    }
    
    public void update() {
        // Cập nhật hướng di chuyển dựa trên phím nhấn
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            if (keyH.upPressed) {
                direction = "up";
            } else if (keyH.downPressed) {
                direction = "down";
            } else if (keyH.leftPressed) {
                direction = "left";
            } else if (keyH.rightPressed) {
                direction = "right";
            }

            // Kiểm tra va chạm
            collisionOn = false;
            gp.cChecker.checkTile(this);
            int objIndex = gp.cChecker.checkObject(this, true);
            pickUpObject(objIndex);

            // Di chuyển nếu không có va chạm
            if (!collisionOn) {
                switch (direction) {
                    case "up":
                        worldY -= speed;
                        break;
                    case "down":
                        worldY += speed;
                        break;
                    case "left":
                        worldX -= speed;
                        break;
                    case "right":
                        worldX += speed;
                        break;
                }
            }
        } else {
            // Dừng di chuyển khi không có phím nào được nhấn
            direction = "";
        }

    }
    
    public void pickUpObject(int i) {
    	if (i != 999) {
    		String objectName = gp.obj[i].name;
    		
    		switch(objectName) {
    		case "Chest":
    			//gp.playSoundEffect(null);
    			gp.obj[i] = null;
    			gp.ui.showMessage("you got a chest");
    			gp.ui.gameFinished = true;
    			gp.stopMusic();
    			break;
    		
    			
    			
    		}
    		
    	}
    }
    
    public void draw(Graphics2D g2) {
        BufferedImage image = idleDown;

        // Animation only updates when moving
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            int frame = (int)((System.currentTimeMillis() / 100) % 9) + 1;
            switch (direction) {
                case "up":
                    switch (frame) {
                        case 1: image = up1; break;
                        case 2: image = up2; break;
                        case 3: image = up3; break;
                        case 4: image = up4; break;
                        case 5: image = up5; break;
                        case 6: image = up6; break;
                        case 7: image = up7; break;
                        case 8: image = up8; break;
                        case 9: image = up9; break;
                    }
                    break;
                case "down":
                    switch (frame) {
                        case 1: image = down1; break;
                        case 2: image = down2; break;
                        case 3: image = down3; break;
                        case 4: image = down4; break;
                        case 5: image = down5; break;
                        case 6: image = down6; break;
                        case 7: image = down7; break;
                        case 8: image = down8; break;
                        case 9: image = down9; break;
                    }
                    break;
                case "left":
                    switch (frame) {
                        case 1: image = left1; break;
                        case 2: image = left2; break;
                        case 3: image = left3; break;
                        case 4: image = left4; break;
                        case 5: image = left5; break;
                        case 6: image = left6; break;
                        case 7: image = left7; break;
                        case 8: image = left8; break;
                        case 9: image = left9; break;
                    }
                    break;
                case "right":
                    switch (frame) {
                        case 1: image = right1; break;
                        case 2: image = right2; break;
                        case 3: image = right3; break;
                        case 4: image = right4; break;
                        case 5: image = right5; break;
                        case 6: image = right6; break;
                        case 7: image = right7; break;
                        case 8: image = right8; break;
                        case 9: image = right9; break;
                    }
                    break;
            }
        } else {
            // Xử lý hình ảnh khi đứng yên dựa trên hướng cuối cùng
            switch (direction) {
                case "up":
                    image = idleUp;
                    break;
                case "down":
                    image = idleDown;
                    break;
                case "left":
                    image = idleLeft;
                    break;
                case "right":
                    image = idleRight;
                    break;
                default:
                    image = idleDown; // Mặc định nếu không có hướng
            }
        }
        if (image != null) {
            g2.drawImage(image, screenX, screenY, null);
        }
    }
}
