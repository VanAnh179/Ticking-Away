package enemy;

import entity.Entity;
import java.util.Random;

public class EnemyBehavior {
    private static int actionLockCounter = 0;

    public static void chasePlayer(Entity enemy, int chaseRange, int stopRange) {
        int xDistance = enemy.gp.player.worldX - enemy.worldX;
        int yDistance = enemy.gp.player.worldY - enemy.worldY;
        int totalDistance = Math.abs(xDistance) + Math.abs(yDistance);

        int chaseRangePixel = chaseRange * enemy.gp.tileSize;
        int stopRangePixel = stopRange * enemy.gp.tileSize;

        if (totalDistance <= chaseRangePixel) {
            String dir = "";

            if (Math.abs(xDistance) > Math.abs(yDistance)) {
                dir = (xDistance < 0) ? "left" : "right";
            } else {
                dir = (yDistance < 0) ? "up" : "down";
            }

            // Nếu hướng bị chặn, chọn hướng khác
            if (!canMove(enemy, dir)) {
                dir = findAlternativeDirection(enemy);
            }

            enemy.direction = dir;
        } else if (totalDistance > stopRangePixel) {
            randomMove(enemy);
        } else {
            randomMove(enemy);
        }
    }

    public static void randomMove(Entity enemy) {
        actionLockCounter++;

        if (actionLockCounter == 70) {
            String[] directions = {
                "up", "down", "left", "right",
                "up-left", "up-right", "down-left", "down-right"
            };
            Random random = new Random();

            for (int i = 0; i < 10; i++) {
                String dir = directions[random.nextInt(directions.length)];
                if (canMove(enemy, dir)) {
                    enemy.direction = dir;
                    break;
                }
            }

            actionLockCounter = 0;
        }
    }

    // Hàm kiểm tra hướng có đi được không (trong bản đồ và không va chạm)
    private static boolean canMove(Entity enemy, String direction) {
        int tileSize = enemy.gp.tileSize;
        int worldX = enemy.worldX;
        int worldY = enemy.worldY;

        // Di chuyển tạm thời để kiểm tra
        if (direction.contains("up")) worldY -= tileSize;
        if (direction.contains("down")) worldY += tileSize;
        if (direction.contains("left")) worldX -= tileSize;
        if (direction.contains("right")) worldX += tileSize;

        int col = worldX / tileSize;
        int row = worldY / tileSize;

        // Kiểm tra ra ngoài map
        if (col < 0 || col >= enemy.gp.maxWorldCol || row < 0 || row >= enemy.gp.maxWorldRow) {
            return false;
        }

        // Lưu vị trí cũ
        int oldX = enemy.worldX;
        int oldY = enemy.worldY;

        // Tạm dịch để kiểm tra va chạm
        enemy.worldX = worldX;
        enemy.worldY = worldY;

        enemy.collisionOn = false;
        enemy.gp.cChecker.checkTile(enemy);

        // Trả lại vị trí ban đầu
        enemy.worldX = oldX;
        enemy.worldY = oldY;

        return !enemy.collisionOn;
    }

    // Tìm hướng thay thế hợp lệ nếu bị chặn
    private static String findAlternativeDirection(Entity enemy) {
        String[] directions = {"up", "down", "left", "right"};
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            String dir = directions[random.nextInt(directions.length)];
            if (canMove(enemy, dir)) return dir;
        }

        return ""; // Đứng yên nếu không có hướng nào đi được
    }
}