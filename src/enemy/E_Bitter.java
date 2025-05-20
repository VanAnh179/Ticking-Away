package enemy;

import entity.Entity;
import main.GamePanel;
import object.Bomb;
import object.SuperObject;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Queue;

public class E_Bitter extends Entity {
    private final int BOMB_COOLDOWN_TIME = 180;
    private int bombCooldown = 0;
    private final int BOMB_RANGE;
    private final int VISION_RANGE;
    private Queue<Point> path = new LinkedList<>();
    private Random random = new Random();
    public int invincibleCounter = 0;
    public final int INVINCIBLE_TIME = 60;

    public E_Bitter(GamePanel gp) {
        super(gp);
        BOMB_RANGE = 3 * gp.tileSize;
        VISION_RANGE = 10 * gp.tileSize;
        name = "Bitter";
        speed = 2;
        maxHealth = 2;
        health = maxHealth;
        direction = "down";
        solidArea = new Rectangle(8, 16, 32, 32);
        getEnemyImage();
    }

    public void getEnemyImage() {
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
        bombCooldown = Math.max(0, bombCooldown - 1);
        if (calculateDistanceToPlayer() < VISION_RANGE) {
            calculateAStarPath();
        }
        if (invincibleCounter > 0) {
            invincibleCounter--;
        }
    }

    @Override
    public void setAction() {
        if (isBombNearby()) {
            avoidBomb();
        } else if (shouldPlaceBomb()) {
            placeBomb();
        } else {
            followPath();
        }
    }

    @Override
    public void takeDamage(int damage) {
    if (invincibleCounter == 0) {
        health -= damage;
        invincibleCounter = INVINCIBLE_TIME;
        if (health <= 0) {
            for (int i = 0; i < gp.enemy.length; i++) {
                if (gp.enemy[i] == this) {
                    gp.enemy[i] = null; // Xóa enemy khỏi mảng
                    break;
                }
            }
        }
    }
}

    // Triển khai A* pathfinding
    private void calculateAStarPath() {
        path.clear();
    int startCol = worldX / gp.tileSize;
    int startRow = worldY / gp.tileSize;
    int targetCol = gp.player.worldX / gp.tileSize;
    int targetRow = gp.player.worldY / gp.tileSize;

    PriorityQueue<Node> openList = new PriorityQueue<>();
    Set<Node> closedList = new HashSet<>();
    Node startNode = new Node(startCol, startRow);
    Node targetNode = new Node(targetCol, targetRow);

    startNode.gCost = 0;
    startNode.hCost = heuristic(startNode, targetNode);
    openList.add(startNode);

    while (!openList.isEmpty()) {
        Node currentNode = openList.poll();
        closedList.add(currentNode);

        if (currentNode.x == targetNode.x && currentNode.y == targetNode.y) {
            reconstructPath(currentNode);
            return;
        }

        for (Node neighbor : getNeighbors(currentNode)) {
            if (closedList.contains(neighbor) || 
                gp.tileM.mapTileNum[neighbor.x][neighbor.y] >= gp.tileM.tile.length || 
                gp.tileM.tile[gp.tileM.mapTileNum[neighbor.x][neighbor.y]].collision ||
                isTileNearBomb(neighbor.x, neighbor.y)) { // Thêm điều kiện tránh tile gần bomb
                continue;
            }

            int newGCost = currentNode.gCost + 1;
            if (newGCost < neighbor.gCost || !openList.contains(neighbor)) {
                neighbor.gCost = newGCost;
                neighbor.hCost = heuristic(neighbor, targetNode);
                neighbor.parent = currentNode;

                if (!openList.contains(neighbor)) {
                    openList.add(neighbor);
                }
            }
        }
    }
}


    private java.util.List<Node> getNeighbors(Node node) {
        java.util.List<Node> neighbors = new java.util.ArrayList<>();
        int[][] dirs = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}}; // Up, Down, Left, Right
        for (int[] dir : dirs) {
            int newX = node.x + dir[0];
            int newY = node.y + dir[1];
            if (newX >= 0 && newX < gp.maxWorldCol && newY >= 0 && newY < gp.maxWorldRow) {
                neighbors.add(new Node(newX, newY));
            }
        }
        return neighbors;
    }

    private int heuristic(Node a, Node b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y); // Manhattan distance
    }

    private void reconstructPath(Node node) {
        LinkedList<Point> tempPath = new LinkedList<>();
        while (node.parent != null) {
            tempPath.addFirst(new Point(node.x * gp.tileSize, node.y * gp.tileSize));
            node = node.parent;
        }
        path = new LinkedList<>(tempPath);
    }

    private void followPath() {
        if (!path.isEmpty()) {
            Point next = path.peek();
            int targetX = next.x;
            int targetY = next.y;

            if (worldX < targetX) direction = "right";
            else if (worldX > targetX) direction = "left";
            else if (worldY < targetY) direction = "down";
            else if (worldY > targetY) direction = "up";

            if (Math.abs(worldX - targetX) < speed && Math.abs(worldY - targetY) < speed) {
                path.poll();
            }
        }
    }

    private boolean isBombNearby() {
        for (SuperObject obj : gp.obj) {
            if (obj instanceof Bomb) {
                int dx = Math.abs(obj.worldX - worldX);
                int dy = Math.abs(obj.worldY - worldY);
                if (dx < 5 * gp.tileSize && dy < 5 * gp.tileSize) return true;
            }
        }
        return false;
    }

    private boolean shouldPlaceBomb() {
        return calculateDistanceToPlayer() <= BOMB_RANGE && 
               bombCooldown == 0 && 
               hasEscapeRoute();
    }

    private void placeBomb() {
        int bombCol = (worldX + solidArea.x) / gp.tileSize;
        int bombRow = (worldY + solidArea.y) / gp.tileSize;

        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] == null) {
                Bomb bomb = new Bomb(gp);
                bomb.worldX = bombCol * gp.tileSize;
                bomb.worldY = bombRow * gp.tileSize;
                gp.obj[i] = bomb;
                bombCooldown = BOMB_COOLDOWN_TIME;
                break;
            }
        }
    }

    private void avoidBomb() {
        calculateAStarPathToSafeZone(); // Tính toán đường đến vùng an toàn
    followPath();
}

private void calculateAStarPathToSafeZone() {
    path.clear();
    Point safePoint = findNearestSafeTile();
    if (safePoint == null) return;

    int startCol = worldX / gp.tileSize;
    int startRow = worldY / gp.tileSize;
    int targetCol = safePoint.x / gp.tileSize;
    int targetRow = safePoint.y / gp.tileSize;

    PriorityQueue<Node> openList = new PriorityQueue<>();
    Set<Node> closedList = new HashSet<>();
    Node startNode = new Node(startCol, startRow);
    Node targetNode = new Node(targetCol, targetRow);

    startNode.gCost = 0;
    startNode.hCost = heuristic(startNode, targetNode);
    openList.add(startNode);

    while (!openList.isEmpty()) {
        Node currentNode = openList.poll();
        closedList.add(currentNode);

        if (currentNode.x == targetNode.x && currentNode.y == targetNode.y) {
            reconstructPath(currentNode);
            return;
        }

        for (Node neighbor : getNeighbors(currentNode)) {
            if (closedList.contains(neighbor) ||
                gp.tileM.mapTileNum[neighbor.x][neighbor.y] >= gp.tileM.tile.length ||
                gp.tileM.tile[gp.tileM.mapTileNum[neighbor.x][neighbor.y]].collision ||
                isTileNearBomb(neighbor.x, neighbor.y)) {
                continue;
            }

            int newGCost = currentNode.gCost + 1;
            if (newGCost < neighbor.gCost || !openList.contains(neighbor)) {
                neighbor.gCost = newGCost;
                neighbor.hCost = heuristic(neighbor, targetNode);
                neighbor.parent = currentNode;

                if (!openList.contains(neighbor)) {
                    openList.add(neighbor);
                }
            }
        }
    }
}

// Thêm phương thức tìm tile an toàn gần nhất
private Point findNearestSafeTile() {
    int currentCol = worldX / gp.tileSize;
    int currentRow = worldY / gp.tileSize;
    int[][] dirs = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}}; // Lên, Xuống, Trái, Phải
    Point nearest = null;
    int minDistance = Integer.MAX_VALUE;

    for (int[] dir : dirs) {
        int newCol = currentCol + dir[0];
        int newRow = currentRow + dir[1];
        
        if (isTileSafe(newCol, newRow)) {
            int distance = Math.abs(newCol - currentCol) + Math.abs(newRow - currentRow);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = new Point(newCol * gp.tileSize, newRow * gp.tileSize);
            }
        }
    }
    return nearest;
}

// Kiểm tra tile có an toàn (không collision và không gần bomb)
private boolean isTileSafe(int col, int row) {
    // Kiểm tra collision
    if (col < 0 || col >= gp.maxWorldCol || row < 0 || row >= gp.maxWorldRow) return false;
    if (gp.tileM.tile[gp.tileM.mapTileNum[col][row]].collision) return false;
    
    // Kiểm tra phạm vi bomb
    return !isTileNearBomb(col, row);
}

// Cập nhật phương thức isTileNearBomb() để kiểm tra 4 hướng
private boolean isTileNearBomb(int col, int row) {
    for (SuperObject obj : gp.obj) {
        if (obj instanceof Bomb) {
            int bombCol = obj.worldX / gp.tileSize;
            int bombRow = obj.worldY / gp.tileSize;
            
            // Kiểm tra 4 hướng xung quanh bomb (trong phạm vi 1 tile)
            if ((col >= bombCol - 1 && col <= bombCol + 1) && 
                (row >= bombRow - 1 && row <= bombRow + 1)) {
                return true;
            }
        }
    }
    return false;
}
    

    private boolean canMoveSafely(String direction) {
        int tempX = worldX;
        int tempY = worldY;
        switch (direction) {
            case "up": tempY -= speed; break;
            case "down": tempY += speed; break;
            case "left": tempX -= speed; break;
            case "right": tempX += speed; break;
        }
        // Kiểm tra collision và phạm vi bomb
        return !checkCollision(tempX, tempY) && !isInBombRange(tempX, tempY);
    }

    private boolean checkCollision(int x, int y) {
        int col = x / gp.tileSize;
        int row = y / gp.tileSize;
        if (col < 0 || col >= gp.maxWorldCol || row < 0 || row >= gp.maxWorldRow) {
            return true; // Ngoài map coi như collision
        }
        int tileNum = gp.tileM.mapTileNum[col][row];
        return gp.tileM.tile[tileNum].collision;
    }

    private boolean isInBombRange(int x, int y) {
        for (SuperObject obj : gp.obj) {
            if (obj instanceof Bomb) {
                int bombCol = obj.worldX / gp.tileSize;
                int bombRow = obj.worldY / gp.tileSize;
                int targetCol = x / gp.tileSize;
                int targetRow = y / gp.tileSize;
                
                // Kiểm tra phạm vi bomb (ví dụ: 3 tile xung quanh)
                if (Math.abs(bombCol - targetCol) <= 3 && Math.abs(bombRow - targetRow) <= 3) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasEscapeRoute() {
        int safeDirections = 0;
        for (String dir : new String[]{"up", "down", "left", "right"}) {
            if (canMoveSafely(dir)) safeDirections++;
        }
        return safeDirections >= 2;
    }

    private int calculateDistanceToPlayer() {
        return Math.abs(gp.player.worldX - worldX) + Math.abs(gp.player.worldY - worldY);
    }

    // Helper class for A*
    private static class Node implements Comparable<Node> {
        int x, y;
        int gCost = Integer.MAX_VALUE;
        int hCost;
        Node parent;

        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int fCost() { return gCost + hCost; }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.fCost(), other.fCost());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Node node = (Node) obj;
            return x == node.x && y == node.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}