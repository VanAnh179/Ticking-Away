package enemy;

import entity.Entity;
import java.awt.Rectangle;
import java.util.Random;

import main.GamePanel;
import main.Sound;
import object.Flame;

public class E_Watermelon extends Entity {
    private int indexInEnemyArray = -1;
    private int invincibleCounter = 0;
    private Sound hurtSound = new Sound();
    private int stuckTimer = 0;
    private final int MAX_STUCK_TIME = 5 * 60; // 5 giây (60 FPS)

    public E_Watermelon(GamePanel gp) {
        super(gp);
        name = "Watermelon";
        speed = 1;
        maxHealth = 3;
        health = maxHealth;
        direction = "down";

        solidArea.x = 10;
        solidArea.y = 20;
        solidArea.width = 22;
        solidArea.height = 28;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    public void getImage() {
        up1 = setup("/enemy/goblin_run_anim_l0");
        up2 = setup("/enemy/goblin_run_anim_l1");
        up3 = setup("/enemy/goblin_run_anim_l2");
        up4 = setup("/enemy/goblin_run_anim_l3");

        down1 = setup("/enemy/goblin_run_anim_l0");
        down2 = setup("/enemy/goblin_run_anim_l1");
        down3 = setup("/enemy/goblin_run_anim_l2");
        down4 = setup("/enemy/goblin_run_anim_l3");

        left1 = setup("/enemy/goblin_run_anim_l0");
        left2 = setup("/enemy/goblin_run_anim_l1");
        left3 = setup("/enemy/goblin_run_anim_l2");
        left4 = setup("/enemy/goblin_run_anim_l3");

        right1 = setup("/enemy/goblin_run_anim_r0");
        right2 = setup("/enemy/goblin_run_anim_r1");
        right3 = setup("/enemy/goblin_run_anim_r2");
        right4 = setup("/enemy/goblin_run_anim_r3");
    }

    @Override
    public void update() {
        super.update();
        // Kiểm tra collision trước khi đuổi
        gp.cChecker.checkTile(this);

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
        if (invincibleCounter == 0) { 
            health -= damage;
            hurtSound.setFile(7);
            hurtSound.play();
            if (health <= 0) {
                // Xử lý khi enemy chết
            } else {
                invincibleCounter = 60;
            }
        }
    }

    @Override
    public void setAction() {
        gp.cChecker.checkTile(this);
        // Chỉ đuổi nếu player đang di chuyển
        if (!collisionOn && gp.player.speed > 0) {
            EnemyBehavior.chasePlayer(this, 13, 15);
        } else {
            // Dừng đuổi và di chuyển ngẫu nhiên
            EnemyBehavior.randomMove(this);
        }
    }
}