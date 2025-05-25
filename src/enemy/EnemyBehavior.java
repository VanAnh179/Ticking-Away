package enemy;

import entity.Entity;

import java.util.List;
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

    public static void smartWander(Entity enemy) {
        // chỉ chạy khi pathList đã hết hoặc mới bắt đầu
        if (!(enemy instanceof E_Bitter)) return;
        E_Bitter b = (E_Bitter) enemy;
        if (!b.pathList.isEmpty()) return;

        // 1. Chọn ngẫu nhiên một tile đích
        int radius = 5;
        Random rnd = new Random();
        int startCol = enemy.worldX / enemy.gp.tileSize;
        int startRow = enemy.worldY / enemy.gp.tileSize;
        int targetCol, targetRow;
        for (int i = 0; i < 10; i++) {
            int dx = rnd.nextInt(radius*2+1) - radius;
            int dy = rnd.nextInt(radius*2+1) - radius;
            targetCol = startCol + dx;
            targetRow = startRow + dy;
            // kiểm tra trong bản đồ và không va chạm
            if (targetCol < 0 || targetCol >= enemy.gp.maxWorldCol ||
                targetRow < 0 || targetRow >= enemy.gp.maxWorldRow) continue;
            if (enemy.gp.tileM.tile[enemy.gp.tileM.mapTileNum[targetCol][targetRow]].collision)
                continue;
            // tìm được đích an toàn
            PathFinder pf = new PathFinder(enemy.gp);
            List<Node> path = pf.findPath(startCol, startRow, targetCol, targetRow, false);
            if (path != null) {
                b.pathList = path;
                return;
            }
        }
        // nếu không tìm được ô an toàn, fallback về randomMove
        randomMove(enemy);
    }

    public static void randomMove(Entity enemy) {
        actionLockCounter++;

        if (actionLockCounter < 70) return;

        String[] directions;

        if (enemy instanceof E_Sweet) {
            // đi 8 hướng
            directions = new String[] {
                "up", "down", "left", "right",
                "up-left", "up-right", "down-left", "down-right"
            };
        } else {
            // Chỉ cho phép 4 hướng
            directions = new String[] { "up", "down", "left", "right" };
        }

        Random rnd = new Random();

        for (int i = 0; i < directions.length * 2; i++) {
            String dir = directions[rnd.nextInt(directions.length)];

            int newX = enemy.worldX;
            int newY = enemy.worldY;
            int tileSize = enemy.gp.tileSize;

            switch (dir) {
                case "up": newY -= tileSize; break;
                case "down": newY += tileSize; break;
                case "left": newX -= tileSize; break;
                case "right": newX += tileSize; break;
                case "up-left": newY -= tileSize; newX -= tileSize; break;
                case "up-right": newY -= tileSize; newX += tileSize; break;
                case "down-left": newY += tileSize; newX -= tileSize; break;
                case "down-right": newY += tileSize; newX += tileSize; break;
            }

            int col = newX / tileSize;
            int row = newY / tileSize;

            if (col >= 0 && col < enemy.gp.maxWorldCol && row >= 0 && row < enemy.gp.maxWorldRow) {
                if (canMove(enemy, dir)) {
                    enemy.direction = dir;
                    actionLockCounter = 0;
                    return;
                }
            }
        }

        enemy.direction = findAlternativeDirection(enemy);
        actionLockCounter = 0;
    }

    // Hàm kiểm tra hướng có đi được không (trong bản đồ và không va chạm)
    private static boolean canMove(Entity enemy, String direction) {
        int tileSize = enemy.gp.tileSize;
        int newX = enemy.worldX;
        int newY = enemy.worldY;

        // Di chuyển tạm thời để kiểm tra
        if (direction.contains("up")) newY -= tileSize;
        if (direction.contains("down")) newY += tileSize;
        if (direction.contains("left")) newX -= tileSize;
        if (direction.contains("right")) newX += tileSize;

        int col = newX / tileSize;
        int row = newY / tileSize;

        // Kiểm tra ra ngoài map
        if (col < 0 || col >= enemy.gp.maxWorldCol || row < 0 || row >= enemy.gp.maxWorldRow) {
            return false;
        }

        // Nếu không phải Sweet thì kiểm tra va chạm
        if (!enemy.name.equals("Sweet")) {
            // Giữ vị trí cũ
            int oldX = enemy.worldX;
            int oldY = enemy.worldY;
            enemy.worldX = newX;
            enemy.worldY = newY;

            enemy.collisionOn = false;
            enemy.gp.cChecker.checkTile(enemy);

            enemy.worldX = oldX;
            enemy.worldY = oldY;

            return !enemy.collisionOn;
        }
        // Nếu là Sweet => chỉ cần kiểm tra giới hạn bản đồ, không cần collision
        return true;
    }

    // Tìm hướng thay thế hợp lệ nếu bị chặn
    static String findAlternativeDirection(Entity enemy) {
        String[] directions;
        if (enemy instanceof E_Sweet) {
            directions = new String[] {
                "up", "down", "left", "right",
                "up-left", "up-right", "down-left", "down-right"
            };
        } else {
            directions = new String[] {"up", "down", "left", "right"};
        }
        Random random = new Random();

        // Lặp cho đến khi tìm được hướng di chuyển được
        for (int i = 0; i < directions.length * 2; i++) {
            String dir = directions[random.nextInt(directions.length)];
            if (canMove(enemy, dir)) {
                return dir;
            }
        }

        // Nếu không tìm được hướng, thử duyệt tất cả hướng một lần cuối
        for (String dir : directions) {
            if (canMove(enemy, dir)) {
                return dir;
            }
        }

        return enemy.direction; // giữ nguyên hướng cũ thay vì đứng yên
    }
}