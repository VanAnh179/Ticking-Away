package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import main.GamePanel;
import main.KeyHandler;
import object.Bomb;

public class Player extends Entity {
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;

    public int maxHealth = 4;
    public int health = maxHealth;
    public int invincibleCounter = 0;
    public final int INVINCIBLE_TIME = 60;
    public boolean wasTouchingEnemy = false;

    public BufferedImage idleDown, idleUp, idleLeft, idleRight;
    
    public int hasKey = 0;

    public String lastDirection = "down";

    // Tính thời gian đặt bomb để tránh spam
    public int bombCooldown = 0; // biến cooldown cho đặt bomb
    public final int BOMB_COOLDOWN_TIME = 30; // 1 giây (60 frames)

    public int bombRange = 1; // Số ô bomb có thể nổ

    public int baseSpeed = 4; // Tốc độ di chuyển cơ bản
    public int speed = baseSpeed; // Tốc độ di chuyển hiện tại

    public boolean isTeleporting = false; // Biến kiểm tra xem có đang teleport hay không
    public int teleportCounter = 0; // Biến đếm thời gian teleport

    public Player(GamePanel gp, KeyHandler keyH) {
        super(gp);
        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle();
        solidArea.x = 16;
        solidArea.y = 35;
        solidArea.width = 16;
        solidArea.height = 13;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 31;
        worldY = gp.tileSize * 4;
        speed = 4;
        direction = "";
        maxHealth = 4;
        health = maxHealth;
        speed = baseSpeed;
    }

    public void getPlayerImage() {
        up1 = setup("/player/up (1)");
        up2 = setup("/player/up (2)");
        up3 = setup("/player/up (3)");
        up4 = setup("/player/up (4)");
        up5 = setup("/player/up (5)");
        up6 = setup("/player/up (6)");
        up7 = setup("/player/up (7)");
        up8 = setup("/player/up (8)");
        up9 = setup("/player/up (9)");

        down1 = setup("/player/down (1)");
        down2 = setup("/player/down (2)");
        down3 = setup("/player/down (3)");
        down4 = setup("/player/down (4)");
        down5 = setup("/player/down (5)");
        down6 = setup("/player/down (6)");
        down7 = setup("/player/down (7)");
        down8 = setup("/player/down (8)");
        down9 = setup("/player/down (9)");

        left1 = setup("/player/left (9)");
        left2 = setup("/player/left (1)");
        left3 = setup("/player/left (2)");
        left4 = setup("/player/left (3)");
        left5 = setup("/player/left (4)");
        left6 = setup("/player/left (5)");
        left7 = setup("/player/left (6)");
        left8 = setup("/player/left (7)");
        left9 = setup("/player/left (8)");

        right1 = setup("/player/right (9)");
        right2 = setup("/player/right (1)");
        right3 = setup("/player/right (2)");
        right4 = setup("/player/right (3)");
        right5 = setup("/player/right (4)");
        right6 = setup("/player/right (5)");
        right7 = setup("/player/right (6)");
        right8 = setup("/player/right (7)");
        right9 = setup("/player/right (8)");

        idleDown = setup("/player/down (1)");
        idleUp = setup("/player/up (1)");
        idleLeft = setup("/player/left (9)");
        idleRight = setup("/player/right (9)");
    }
    
    public void update() {
        // Giảm cooldown mỗi frame nếu > 0
        if (bombCooldown > 0) {
            bombCooldown--;
        }

        // Cập nhật hướng di chuyển dựa trên phím nhấn
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            String newDirection = direction;
            // Cập nhật direction và lastDirection khi di chuyển
            if (keyH.upPressed) {
                direction = "up";
                lastDirection = "up";
            } else if (keyH.downPressed) {
                direction = "down";
                lastDirection = "down";
            } else if (keyH.leftPressed) {
                direction = "left";
                lastDirection = "left";
            } else if (keyH.rightPressed) {
                direction = "right";
                lastDirection = "right";
            }
            if (keyH.spacePressed) {
                placeBomb();
                keyH.spacePressed = false;
            }

            // Nếu hướng thay đổi, kiểm tra collision ngay
            if (!newDirection.equals(direction)) {
                solidArea.x = solidAreaDefaultX;
                solidArea.y = solidAreaDefaultY;
                collisionOn = false;
                gp.cChecker.checkTile(this);
                if (collisionOn) {
                    // Điều chỉnh vị trí nếu cần
                    switch (newDirection) {
                        case "up": worldY += speed; break;
                        case "down": worldY -= speed; break;
                        case "left": worldX += speed; break;
                        case "right": worldX -= speed; break;
                    }
                }
            }
            solidArea.x = solidAreaDefaultX;
            solidArea.y = solidAreaDefaultY;

            // Kiểm tra va chạm
            collisionOn = false;
            gp.cChecker.checkTile(this);

            //check obj collision
            int objIndex = gp.cChecker.checkObject(this, true);
            pickUpObject(objIndex);

            //check enemy collision
            int enemyIndex = gp.cChecker.checkEntity(this, gp.enemy);
            // Chỉ trừ máu khi vừa mới va chạm (trước đó chưa va chạm)
            if (enemyIndex != 999 && invincibleCounter == 0) {
                interactEnemy(enemyIndex);
                wasTouchingEnemy = true;
            } else {
                wasTouchingEnemy = false;
            }
            // Giảm thời gian bất tử
            if (invincibleCounter > 0) {
                invincibleCounter--;
            }

            // Di chuyển nếu không có va chạm
            if (!collisionOn) {
                switch (direction) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }
        } else {
            // Khi không di chuyển, vẫn reset solidArea để tránh chồng lấn
            solidArea.x = solidAreaDefaultX;
            solidArea.y = solidAreaDefaultY;
            direction = lastDirection;
        }

        if(isTeleporting) {
            teleportCounter++;
            if(teleportCounter > 30) {
                isTeleporting = false;
                teleportCounter = 0;
            }
        }
    }

    public void interactEnemy(int i) {
        if (i != 99) {
            System.out.println("[Debug] Player collided with enemy at index: " + i);
            takeDamage(1);
            invincibleCounter = INVINCIBLE_TIME;
        }
    }

    private void placeBomb() {
        if (bombCooldown > 0) return;

        // Tính tọa độ trung tâm của player
        int centerX = worldX + solidArea.x - 1 + ((solidArea.width - 1) / 2);
        int centerY = worldY + solidArea.y - 1 + ((solidArea.height - 1) / 2);

        // Xác định ô dựa trên trung tâm
        int bombCol = centerX / gp.tileSize;
        int bombRow = centerY / gp.tileSize;
        int bombWorldX = bombCol * gp.tileSize;
        int bombWorldY = bombRow * gp.tileSize;

        // Kiểm tra giới hạn map và vật cản
        if (bombCol < 0 || bombCol >= gp.maxWorldCol || bombRow < 0 || bombRow >= gp.maxWorldRow) return;
        if (gp.tileM.tile[gp.tileM.mapTileNum[bombCol][bombRow]].collision) return;

        // Đặt bomb vào ô trống đầu tiên trong mảng obj[]
        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] == null) {
                Bomb bomb = new Bomb(gp);
                bomb.worldX = bombWorldX;
                bomb.worldY = bombWorldY;
                bomb.explosionRange = this.bombRange;
                gp.obj[i] = bomb;
                bombCooldown = BOMB_COOLDOWN_TIME; // Kích hoạt cooldown
                break;
            }
        }
    }

    public void pickUpObject(int i) {
        gp.eventObj.handleItemPickup(i);
    }
    
    @Override
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
            switch (lastDirection) {
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
            }
        }
        g2.drawImage(image, screenX, screenY, null);
        // DEBUG check solidArea
        if (gp.keyH.debugMode) {
            g2.setColor(new Color(255, 0, 0, 128)); // Màu đỏ trong suốt
            g2.fillRect(
                screenX + solidArea.x, // Vị trí X trên màn hình
                screenY + solidArea.y, // Vị trí Y trên màn hình
                solidArea.width,      // Chiều rộng
                solidArea.height      // Chiều cao
            );
        }
    }

    public void takeDamage(int damage) {
        if (invincibleCounter == 0) { // Chỉ nhận sát thương khi không bất tử
            health -= damage;
            if (health < 0) {
                health = 0;
            }

            gp.ui.showMessage("-1 life");
            if (health <= 0) {
                gp.gameOver();
            } else {
                invincibleCounter = INVINCIBLE_TIME; // Bật trạng thái bất tử
            }
        }
    }

    // Thêm phương thức resetPlayer
    public void resetPlayer() {
        setDefaultValues(); // Đặt lại vị trí, health, speed, v.v.
        hasKey = 0; // Đặt lại số lượng key
        invincibleCounter = 0; // Đặt lại trạng thái bất tử
        bombCooldown = 0; // Đặt lại cooldown bomb
        bombRange = 1; // Đặt lại phạm vi bomb
        speed = baseSpeed; // Đặt lại tốc độ
        isTeleporting = false; // Đặt lại trạng thái teleport
        teleportCounter = 0; // Đặt lại đếm thời gian teleport
        lastDirection = "down"; // Đặt lại hướng cuối cùng
    }
}