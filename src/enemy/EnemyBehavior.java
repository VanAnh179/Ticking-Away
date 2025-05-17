package enemy;

import entity.Entity;
import java.util.Random;

public class EnemyBehavior {
    // Logic đuổi theo player
    public static void chasePlayer(Entity enemy, int chaseRange, int stopRange) {
        int xDistance = enemy.gp.player.worldX - enemy.worldX;
        int yDistance = enemy.gp.player.worldY - enemy.worldY;
        // int totalDistance = xDistance + yDistance;

        // // Chuyển chaseRange và stopRange thành pixel
        // int chaseRangePixel = chaseRange * enemy.gp.tileSize;
        // int stopRangePixel = stopRange * enemy.gp.tileSize;

        // if (totalDistance <= chaseRangePixel) {
        //     // Đuổi theo
        //     if (enemy.gp.player.worldX > enemy.worldX) enemy.direction = "right";
        //     else if (enemy.gp.player.worldX < enemy.worldX) enemy.direction = "left";
        //     if (enemy.gp.player.worldY > enemy.worldY) enemy.direction = "down";
        //     else if (enemy.gp.player.worldY < enemy.worldY) enemy.direction = "up";
        // } else if (totalDistance > stopRangePixel) {
        //     enemy.direction = ""; // Dừng đuổi
        // } else {
        //     randomMove(enemy); // Di chuyển ngẫu nhiên
        // }
        
        // Xác định hướng di chuyển ưu tiên trục X hoặc Y
        if (Math.abs(xDistance) > Math.abs(yDistance)) {
            enemy.direction = (xDistance > 0) ? "right" : "left";
        } else {
            enemy.direction = (yDistance > 0) ? "down" : "up";
        }
    }

    // Di chuyển ngẫu nhiên
    private static int actionLockCounter = 0;

    public static void randomMove(Entity enemy) {
        actionLockCounter++;

        if (actionLockCounter == 70) {
            Random random = new Random();
            int i = random.nextInt(100) + 1;

            if (i <= 25) {
                enemy.direction = "up";
            }
            if (i > 25 && i <= 50) {
                enemy.direction = "down";
            }
            if (i > 50 && i <= 75) {
                enemy.direction = "left";
            }
            if (i > 75 && i <= 100) {
                enemy.direction = "right";
            }
            actionLockCounter = 0;
        }
    }
}