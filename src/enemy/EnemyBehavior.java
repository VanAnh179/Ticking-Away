package enemy;

import entity.Entity;
import java.util.Random;

public class EnemyBehavior {
    // Logic đuổi theo player
    public static void chasePlayer(Entity enemy, int chaseRange, int stopRange) {
        int xDistance = (int) (enemy.gp.player.worldX - enemy.worldX);
        int yDistance = (int) (enemy.gp.player.worldY - enemy.worldY);
        int totalDistance = Math.abs(xDistance) + Math.abs(yDistance);

        int chaseRangePixel = chaseRange * enemy.gp.tileSize;
        int stopRangePixel = stopRange * enemy.gp.tileSize;

        if (totalDistance <= chaseRangePixel) {
            StringBuilder dir = new StringBuilder();
            // Thêm độ trễ khi đổi hướng để tạo cảm giác ma mị
            if (new Random().nextInt(100) < 30) {
                enemy.direction = dir.toString();
            }
            if (yDistance < 0) dir.append("up");
            else if (yDistance > 0) dir.append("down");

            if (xDistance < 0) dir.append((dir.length() > 0) ? "-left" : "left");
            else if (xDistance > 0) dir.append((dir.length() > 0) ? "-right" : "right");

            enemy.direction = dir.toString();
        } else if (totalDistance > stopRangePixel) {
            randomMove(enemy); // Dừng đuổi
        } else {
            randomMove(enemy); // Di chuyển ngẫu nhiên
        }
    }

    // Di chuyển ngẫu nhiên
    private static int actionLockCounter = 0;

    public static void randomMove(Entity enemy) {
        actionLockCounter++;

        if (actionLockCounter == 70) {
            Random random = new Random();
            int i = random.nextInt(100) + 1;

            if (i <= 12) enemy.direction = "up";
            else if (i <= 24) enemy.direction = "down";
            else if (i <= 36) enemy.direction = "left";
            else if (i <= 48) enemy.direction = "right";
            else if (i <= 60) enemy.direction = "up-left";
            else if (i <= 72) enemy.direction = "up-right";
            else if (i <= 84) enemy.direction = "down-left";
            else if (i <= 96) enemy.direction = "down-right";
            else enemy.direction = ""; // đứng yên

            actionLockCounter = 0;
        }
    }
}