package enemy;

import entity.Entity;
import java.awt.Rectangle;
import main.GamePanel;
import object.Flame;

public class E_Watermelon extends Entity {
    public boolean isInvisible = false; // Trạng thái tàng hình
    private int invisibilityCounter = 0; // Bộ đếm thời gian tàng hình
    private final int INVISIBILITY_DURATION = 120; // Thời gian tàng hình (2 giây)
    private final int VISIBILITY_DURATION = 180; // Thời gian hiện hình (3 giây)
    private int indexInEnemyArray = -1;
    private int invincibleCounter = 0;
    private boolean isStunned = false;
    private int stunCounter = 0;
    private final int STUN_DURATION = 60; // Thời gian bất động (1 giây)


    public E_Watermelon(GamePanel gp) {
        super(gp);
        name = "Sweet";
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

    public boolean isInvisible() {
        return isInvisible;
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
        // Kiểm tra trạng thái bất động
        if (isStunned) {
            stunCounter--;
            if (stunCounter <= 0) {
                isStunned = false;
            }
            return; // Không di chuyển nếu đang bất động
        }

        super.update();

         // Logic tàng hình/hiện hình và di chuyển
        boolean isPlayerMoving = gp.player.keyH.upPressed || 
                                gp.player.keyH.downPressed || 
                                gp.player.keyH.leftPressed || 
                                gp.player.keyH.rightPressed;

        if (isPlayerMoving) {
            isInvisible = true;
            invisibilityCounter++;
            if (invisibilityCounter >= INVISIBILITY_DURATION) {
                isInvisible = false;
                invisibilityCounter = 0;
            }
            setAction(); // Di chuyển khi player di chuyển
        } else {
            isInvisible = false;
            invisibilityCounter = 0;
            direction = ""; // Đứng yên
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
    if (isStunned) {
    System.out.println("STUNNED - countdown: " + stunCounter); // Debug
    stunCounter--;
    if (stunCounter <= 0) {
        isStunned = false;
        System.out.println("Recovered from stun.");
    }
    return;
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
            if (health <= 0) {
                // Xử lý khi enemy chết
            } else {
                invincibleCounter = 60; 
                isStunned = true;
                stunCounter = STUN_DURATION;
            }
        }
    }

    

    @Override
    public void setAction() {
        EnemyBehavior.chasePlayer(this, 15, 17); // Điều chỉnh phạm vi đuổi
        if (direction.isEmpty()) {
            direction = lastDirection; // Giữ hướng cuối cùng
        } else {
            lastDirection = direction; // Cập nhật hướng mới
        }
    }
}