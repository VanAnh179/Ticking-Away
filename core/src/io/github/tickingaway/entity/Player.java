package io.github.tickingaway.entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.github.tickingaway.GamePanel;
import io.github.tickingaway.KeyHandler;

public class Player extends Entity {
    
    GamePanel gp;
    KeyHandler keyH;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        x = 100;
        y = 100;
        speed = 4;
        direction = "down";
    }

    public void getPlayerImage() {

        try {
            up1 = ImageIO.read(new File("assets/player/up (1).png"));
            up2 = ImageIO.read(new File("assets/player/up (2).png"));
            up3 = ImageIO.read(new File("assets/player/up (3).png"));
            up4 = ImageIO.read(new File("assets/player/up (4).png"));
            up5 = ImageIO.read(new File("assets/player/up (5).png"));
            up6 = ImageIO.read(new File("assets/player/up (6).png"));
            up7 = ImageIO.read(new File("assets/player/up (7).png"));
            up8 = ImageIO.read(new File("assets/player/up (8).png"));
            up9 = ImageIO.read(new File("assets/player/up (9).png"));

            down1 = ImageIO.read(new File("assets/player/down (1).png"));
            down2 = ImageIO.read(new File("assets/player/down (2).png"));
            down3 = ImageIO.read(new File("assets/player/down (3).png"));
            down4 = ImageIO.read(new File("assets/player/down (4).png"));
            down5 = ImageIO.read(new File("assets/player/down (5).png"));
            down6 = ImageIO.read(new File("assets/player/down (6).png"));
            down7 = ImageIO.read(new File("assets/player/down (7).png"));
            down8 = ImageIO.read(new File("assets/player/down (8).png"));
            down9 = ImageIO.read(new File("assets/player/down (9).png"));

            left1 = ImageIO.read(new File("assets/player/left (9).png"));
            left2 = ImageIO.read(new File("assets/player/left (1).png"));
            left3 = ImageIO.read(new File("assets/player/left (2).png"));
            left4 = ImageIO.read(new File("assets/player/left (3).png"));
            left5 = ImageIO.read(new File("assets/player/left (4).png"));
            left6 = ImageIO.read(new File("assets/player/left (5).png"));
            left7 = ImageIO.read(new File("assets/player/left (6).png"));
            left8 = ImageIO.read(new File("assets/player/left (7).png"));
            left9 = ImageIO.read(new File("assets/player/left (8).png"));

            right1 = ImageIO.read(new File("assets/player/right (9).png"));
            right2 = ImageIO.read(new File("assets/player/right (1).png"));
            right3 = ImageIO.read(new File("assets/player/right (2).png"));
            right4 = ImageIO.read(new File("assets/player/right (3).png"));
            right5 = ImageIO.read(new File("assets/player/right (4).png"));
            right6 = ImageIO.read(new File("assets/player/right (5).png"));
            right7 = ImageIO.read(new File("assets/player/right (6).png"));
            right8 = ImageIO.read(new File("assets/player/right (7).png"));
            right9 = ImageIO.read(new File("assets/player/right (8).png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void update() {
        if (keyH.upPressed == true) {
            direction = "up";
            y -= speed;
        } else if (keyH.downPressed == true) {
            direction = "down";
            y += speed;
        } else if (keyH.leftPressed == true) {
            direction = "left";
            x -= speed;
        } else if (keyH.rightPressed == true) {
            direction = "right";
            x += speed;
        }
    }
    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        // Animation only updates when moving
        int frame = 1;
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            frame = (int)((System.currentTimeMillis() / 100) % 9) + 1;
        }
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
        g2.drawImage(image, x, y, gp.tileSize, gp.tileSize, null);
    }
}
