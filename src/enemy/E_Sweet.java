package enemy;

import entity.Entity;
import java.awt.Rectangle;
import main.GamePanel;
import main.Sound;
import object.Flame;

public class E_Sweet extends Entity {
    private int indexInEnemyArray = -1;
    private int invincibleCounter = 0;
    private Sound hurtSound = new Sound();

    public E_Sweet(GamePanel gp) {
        super(gp);
        name = "Sweet";
        speed = 1;
        maxHealth = 1;
        health = maxHealth;
        direction = "down";

        solidArea.x = 5;
        solidArea.y = 10;
        solidArea.width = 38;
        solidArea.height = 38;
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

    @Override
    public void update() {
        super.update();

        int col = worldX / gp.tileSize;
        int row = worldY / gp.tileSize;
        boolean outOfBounds = col < 0 || col >= gp.maxWorldCol || row < 0 || row >= gp.maxWorldRow;

        if (outOfBounds) {
            switch (direction) {
                case "up": direction = "down"; break;
                case "down": direction = "up"; break;
                case "left": direction = "right"; break;
                case "right": direction = "left"; break;
                case "up-left": direction = "down-right"; break;
                case "up-right": direction = "down-left"; break;
                case "down-left": direction = "up-right"; break;
                case "down-right": direction = "up-left"; break;
            }
        }

        // Kiểm tra va chạm với flame
        for (Flame flame : gp.flames) {
        if (flame != null && flame.collision && invincibleCounter == 0) {
            Rectangle flameRect = new Rectangle(
                flame.worldX + flame.solidArea.x,
                flame.worldY + flame.solidArea.y,
                flame.solidArea.width,
                flame.solidArea.height
            );
            Rectangle enemyRect = new Rectangle(
                worldX + solidArea.x,
                worldY + solidArea.y,
                solidArea.width,
                solidArea.height
            );

            if (flameRect.intersects(enemyRect)) {
                takeDamage(1); // Chỉ gọi takeDamage() một lần
                break;
            }
        }
    }

    if (invincibleCounter > 0) invincibleCounter--;
    }

    public void setIndexInEnemyArray(int index) {
        this.indexInEnemyArray = index;
    }

    @Override
    public void takeDamage(int damage) {
        if (invincibleCounter == 0) { // Chỉ nhận sát thương khi không bất tử
            health -= damage;
            hurtSound.setFile(4);
            hurtSound.play();
            if (health <= 0) {
            } else {
                invincibleCounter = 60; // Kích hoạt thời gian bất tử
            }
        }
    }

    @Override
    public void setAction() {
        EnemyBehavior.chasePlayer(this, 8, 13);
    }
}