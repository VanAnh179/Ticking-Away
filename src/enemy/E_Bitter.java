package enemy;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entity.Entity;
import main.GamePanel;
import object.Bomb;
import object.Flame;
import object.SuperObject;

public class E_Bitter extends Entity {
    // Thuộc tính bomb
    public int bombCooldown = 0;
    public final int BOMB_COOLDOWN_TIME = 120;
    private final int CHASE_RANGE = 9 * gp.tileSize;
    private final int BOMB_RANGE = 2 * gp.tileSize;
    private final int SAFE_DISTANCE = 2 * gp.tileSize;
    private Bomb lastPlacedBomb = null;
    private int avoidCounter = 0;

    // Invincibility counter for damage cooldown
    private int invincibleCounter = 0;

    // Index in the enemy array
    private int indexInEnemyArray;

    public E_Bitter(GamePanel gp) {
        super(gp);
        name = "Bitter";
        speed = 2;
        maxHealth = 2;
        health = maxHealth;
        direction = "down";

        solidArea = new Rectangle(10, 20, 22, 28);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    public void getImage() {
        up1 = setup("/enemy/angel_l0");
        up2 = setup("/enemy/angel_l1");
        up3 = setup("/enemy/angel_l2");
        up4 = setup("/enemy/angel_l3");

        down1 = setup("/enemy/angel_l0");
        down2 = setup("/enemy/angel_l1");
        down3 = setup("/enemy/angel_l2");
        down4 = setup("/enemy/angel_l3");

        left1 = setup("/enemy/angel_l0");
        left2 = setup("/enemy/angel_l1");
        left3 = setup("/enemy/angel_l2");
        left4 = setup("/enemy/angel_l3");

        right1 = setup("/enemy/angel_r0");
        right2 = setup("/enemy/angel_r1");
        right3 = setup("/enemy/angel_r2");
        right4 = setup("/enemy/angel_r3");
    }

    private void placeBomb() {
        // Tính vị trí trung tâm
        int centerX = worldX + solidArea.x + (solidArea.width/2);
        int centerY = worldY + solidArea.y + (solidArea.height/2);
        
        // Chuyển sang tọa độ tile
        int bombCol = centerX/gp.tileSize;
        int bombRow = centerY/gp.tileSize;

        // Kiểm tra vị trí hợp lệ
        if(gp.tileM.tile[gp.tileM.mapTileNum[bombCol][bombRow]].collision) return;

        // Đặt bomb vào obj array
        for(int i = 0; i < gp.obj.length; i++) {
            if(gp.obj[i] == null) {
                Bomb bomb = new Bomb(gp);
                bomb.worldX = bombCol * gp.tileSize;
                bomb.worldY = bombRow * gp.tileSize;
                gp.obj[i] = bomb;
                break;
            }
        }
    }

    @Override
    public void update() {
        super.update();
        
        // Kiểm tra va chạm với flame
        int flameIndex = gp.cChecker.checkEntity(this, new Entity[0]);
        if (flameIndex != 999 && invincibleCounter == 0) {
            takeDamage(1);
            invincibleCounter = 60; // Invincible for 1 second
        }
        
        if (bombCooldown > 0) bombCooldown--;
        if (invincibleCounter > 0) invincibleCounter--;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            // Find and remove this enemy from the gp.enemy array
            for (int i = 0; i < gp.enemy.length; i++) {
                if (gp.enemy[i] == this) {
                    gp.enemy[i] = null;
                    break;
                }
            }
        }
    }

    @Override
    public void setAction() {
        handleBombAvoidance();
        if (avoidCounter <= 0) {
            chaseWithDistance();
            placeBombLogic();
        }
    }

    private void handleBombAvoidance() {
        // Kiểm tra và tránh bomb
        List<Bomb> nearbyBombs = getNearbyBombs();
        if (!nearbyBombs.isEmpty()) {
            Bomb closestBomb = nearbyBombs.get(0);
            int bombX = closestBomb.worldX + closestBomb.solidArea.x;
            int bombY = closestBomb.worldY + closestBomb.solidArea.y;
            
            // Di chuyển ngược hướng bomb
            if (Math.abs(worldX - bombX) > Math.abs(worldY - bombY)) {
                direction = (worldX < bombX) ? "left" : "right";
            } else {
                direction = (worldY < bombY) ? "up" : "down";
            }
            avoidCounter = 60; // Tránh trong 1 giây
            return;
        }
        
        if (avoidCounter > 0) {
            avoidCounter--;
        }
    }

    private List<Bomb> getNearbyBombs() {
        List<Bomb> bombs = new ArrayList<>();
        for (SuperObject obj : gp.obj) {
            if (obj instanceof Bomb) {
                int distance = calculateDistanceToObject(obj);
                if (distance < SAFE_DISTANCE) {
                    bombs.add((Bomb) obj);
                }
            }
        }
        return bombs;
    }

    // Phương thức để tính khoảng cách tới SuperObject
    private int calculateDistanceToObject(SuperObject obj) {
        return Math.abs(obj.worldX - this.worldX) + Math.abs(obj.worldY - this.worldY);
    }

    private void chaseWithDistance() {
        int playerX = gp.player.worldX;
        int playerY = gp.player.worldY;
        int distance = calculateDistancePlayer(gp.player);

        // Giữ khoảng cách tối thiểu 1 tile
        if (distance < gp.tileSize) {
            // Di chuyển ngược hướng player
            if (Math.abs(worldX - playerX) > Math.abs(worldY - playerY)) {
                direction = (worldX < playerX) ? "left" : "right";
            } else {
                direction = (worldY < playerY) ? "up" : "down";
            }
            return;
        }

        // Đuổi bình thường khi ở xa
        if (distance <= CHASE_RANGE) {
            if (playerX > worldX + gp.tileSize) direction = "right";
            else if (playerX < worldX - gp.tileSize) direction = "left";
            if (playerY > worldY + gp.tileSize) direction = "down";
            else if (playerY < worldY - gp.tileSize) direction = "up";
        }
    }

    private void placeBombLogic() {
        if (calculateDistancePlayer(gp.player) <= BOMB_RANGE 
            && bombCooldown <= 0 
            && isPositionSafe()) {
            placeBomb();
            bombCooldown = BOMB_COOLDOWN_TIME;
        }
    }

    private boolean isPositionSafe() {
        // Kiểm tra ít nhất 2 hướng trống để thoát
        int freeDirections = 0;
        String[] directions = {"up", "down", "left", "right"};
        for (String dir : directions) {
            if (canMoveInDirection(dir)) freeDirections++;
        }
        return freeDirections >= 2;
    }

    private boolean canMoveInDirection(String direction) {
        // Kiểm tra va chạm tạm thời
        int originalX = worldX;
        int originalY = worldY;
        
        switch (direction) {
            case "up": worldY -= speed; break;
            case "down": worldY += speed; break;
            case "left": worldX -= speed; break;
            case "right": worldX += speed; break;
        }
        
        boolean canMove = !checkCollision();
        worldX = originalX;
        worldY = originalY;
        return canMove;
    }

    private boolean checkCollision() {
        collisionOn = false;
        gp.cChecker.checkTile(this);
        gp.cChecker.checkObject(this, false);
        gp.cChecker.checkPlayer(this);
        return collisionOn;
    }

    private int calculateDistancePlayer(Entity target) {
        return Math.abs(target.worldX - worldX) + Math.abs(target.worldY - worldY);
    }

    public void setIndexInEnemyArray(int i) {
        this.indexInEnemyArray = i;
    }
}