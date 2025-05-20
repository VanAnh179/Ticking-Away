package enemy;

import entity.Entity;
import java.awt.Rectangle;
import main.GamePanel;
import object.Flame;

public class E_Sweet extends Entity {
    private int indexInEnemyArray = -1;
    private int invincibleCounter = 0;


    public E_Sweet(GamePanel gp) {
        super(gp);
        name = "Sweet";
        speed = 1;
        maxHealth = 1;
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

        // Kiểm tra va chạm với flame
        for (int i = 0; i < gp.flames.size(); i++) {
            Flame flame = gp.flames.get(i);
            Rectangle flameRect = new Rectangle(flame.worldX + flame.solidArea.x, flame.worldY + flame.solidArea.y, flame.solidArea.width, flame.solidArea.height);
            Rectangle enemyRect = new Rectangle((int)(worldX + solidArea.x), (int)(worldY + solidArea.y), solidArea.width, solidArea.height);

            if (flameRect.intersects(enemyRect) && invincibleCounter == 0) {
                takeDamage(1);
                invincibleCounter = 60;
                break; // chỉ trừ máu 1 lần mỗi frame
            }
        }

        if (invincibleCounter > 0) invincibleCounter--;
    }

    public void setIndexInEnemyArray(int index) {
        this.indexInEnemyArray = index;
    }

    @Override
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0 && indexInEnemyArray != -1) {
            gp.enemy[indexInEnemyArray] = null;
            // Xóa enemy khỏi danh sách
        }
    }

    @Override
    public void setAction() {
        EnemyBehavior.chasePlayer(this, 5, 7);
    }
}