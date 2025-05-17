package enemy;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import entity.Entity;
import main.GamePanel;

public class E_Watermelon extends Entity {
    
    public E_Watermelon(GamePanel gp) {
        super(gp);

        name = "Sweet";
        speed = 1;
        maxHealth = 2;
        health = maxHealth;
        direction = "down";

        this.solidArea = new Rectangle();
        solidArea.x = 3;
        solidArea.y = 18;
        solidArea.width = 42;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();

    }
    public void getImage() {
        
        // Use left images for up and down directions
        up1 = setup("/enemy/angel_l0");
        up2 = setup("/enemy/angel_l1");
        up3 = setup("/enemy/angel_l2");
        up4 = setup("/enemy/angel_l3");

        down1 = setup("/enemy/angel_l0");
        down2 = setup("/enemy/angel_l1");
        down3 = setup("/enemy/angel_l2");
        down4 = setup("/enemy/angel_l3");

        left1 = setup("/enemy/angel_l0");  // Thêm "/enemy/" vào đầu
        left2 = setup("/enemy/angel_l1");
        left3 = setup("/enemy/angel_l2");
        left4 = setup("/enemy/angel_l3");

        right1 = setup("/enemy/angel_r0");  // Sửa tên file cho đúng (nếu có)
        right2 = setup("/enemy/angel_r1");
        right3 = setup("/enemy/angel_r2");
        right4 = setup("/enemy/angel_r3");

    }


    public void setAction() {
        
        
    } 
}
