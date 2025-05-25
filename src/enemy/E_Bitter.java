package enemy;

import main.GamePanel;
import main.Sound;
import object.Bomb;
import object.Flame;
import object.SuperObject;

import java.awt.*;
import java.util.List;

import entity.Entity;

public class E_Bitter extends Entity {

    private final int VISION_RANGE = 7; // số tile
    private final int SAFE_DISTANCE = 2; // số tile để tránh bom
    public int bombCooldown = 0;
    public final int BOMB_COOLDOWN_TIME = 300; // Mỗi 4 giây đặt 1 bomb
    public int bombRange = 1;
    public int invincibleCounter = 0;
    public final int INVINCIBLE_TIME = 60;
    private enum State { CHASE, ESCAPE, WANDER }
    private State aiState = State.WANDER; // Bắt đầu với trạng thái đi lang thang
    private int indexInEnemyArray = -1;
    private Bomb placedBomb = null;
    private Sound hurtSound = new Sound();
    List<Node> pathList = new java.util.ArrayList<>();

    public E_Bitter(GamePanel gp) {
        super(gp);
        name = "Bitter";
        speed = 2;
        maxHealth = 5;
        health = maxHealth;
        solidArea = new Rectangle(8, 8, 32, 40);
        direction = "down";
        collisionOn = false;

        // Hình ảnh
        getImage();
    }

    public void getImage() {
        up1 = setup("/enemy/chort_run_anim_l0");
        up2 = setup("/enemy/chort_run_anim_l1");
        up3 = setup("/enemy/chort_run_anim_l2");
        up4 = setup("/enemy/chort_run_anim_l3");
        down1 = setup("/enemy/chort_run_anim_l0");
        down2 = setup("/enemy/chort_run_anim_l1");
        down3 = setup("/enemy/chort_run_anim_l2");
        down4 = setup("/enemy/chort_run_anim_l3");
        left1 = setup("/enemy/chort_run_anim_l0");
        left2 = setup("/enemy/chort_run_anim_l1");
        left3 = setup("/enemy/chort_run_anim_l2");
        left4 = setup("/enemy/chort_run_anim_l3");
        right1 = setup("/enemy/chort_run_anim_r0");
        right2 = setup("/enemy/chort_run_anim_r1");
        right3 = setup("/enemy/chort_run_anim_r2");
        right4 = setup("/enemy/chort_run_anim_r3");
    }

    @Override
    public void update() {
        super.update();

        // Kiểm tra nếu bomb đã nổ
        if (placedBomb != null && placedBomb.exploded) {
            placedBomb = null;
            aiState = State.CHASE; // Quay lại đuổi sau khi bomb nổ
        }

        // Giảm cooldown bomb (nếu dùng)
        bombCooldown = Math.max(0, bombCooldown - 1);
        if (invincibleCounter > 0) invincibleCounter--;

        if (isHitByFlame()) {
            takeDamage(1);
        }

        int enemyCol = (worldX + solidArea.x + solidArea.width / 2) / gp.tileSize;
        int enemyRow = (worldY + solidArea.y + solidArea.height / 2) / gp.tileSize;
        int playerCol = (gp.player.worldX + gp.player.solidArea.x + gp.player.solidArea.width / 2) / gp.tileSize;
        int playerRow = (gp.player.worldY + gp.player.solidArea.y + gp.player.solidArea.height / 2) / gp.tileSize;

        boolean isNextToPlayer = Math.abs(enemyCol - playerCol) <= 1 && Math.abs(enemyRow - playerRow) <= 1;

        // Xử lý AI
        switch (aiState) {
            case ESCAPE:
                if (!isNearBomb() && !isNearFlame()) {
                    aiState = State.CHASE;
                    pathList.clear();
                } else {
                    escapeFromDanger();
                    moveToTarget();
                    applyMovement(); 
                    if (pathList.isEmpty()) {
                        EnemyBehavior.randomMove(this); // Di chuyển ngẫu nhiên nếu không tìm được đường
                    }
                }
                break;

            case CHASE:
                if (isNextToPlayer && bombCooldown <= 0) {
                    int bombCol = (worldX + solidArea.x + solidArea.width / 2) / gp.tileSize;
                    int bombRow = (worldY + solidArea.y + solidArea.height / 2) / gp.tileSize;
                    if (hasEscapeRouteAfterPlacingBomb(bombCol, bombRow)) {
                        placeBomb();
                        aiState = State.ESCAPE;
                    }
                } else if (calculateDistanceToPlayer() <= VISION_RANGE) {
                    calculateAStarPath();
                } else {
                    aiState = State.WANDER;
                }
                break;

            case WANDER:
                if (calculateDistanceToPlayer() <= VISION_RANGE) {
                    aiState = State.CHASE;
                } else {
                    EnemyBehavior.smartWander(this);
                    moveToTarget();  // di chuyển theo pathList nếu có
                    // fallback: nếu pathList rỗng thì random
                    if (pathList.isEmpty()) {
                        EnemyBehavior.randomMove(this);
                        applyMovement();
                    }
                }
                break;
        }

        moveToTarget();
        collisionOn = false;
        gp.cChecker.checkTile(this);
        if (!collisionOn) {
            applyMovement();
        }
        if (collisionOn && aiState == State.WANDER) {
            // lập tức chọn lại hướng hợp lệ
            EnemyBehavior.randomMove(this);
            collisionOn = false;
        }
    }

    private void applyMovement() {
        // Lưu vị trí cũ để rollback nếu cần
        int oldWorldX = worldX;
        int oldWorldY = worldY;

        switch (direction) {
            case "up": worldY -= speed; break;
            case "down": worldY += speed; break;
            case "left": worldX -= speed; break;
            case "right": worldX += speed; break;
            case "upleft": worldX -= speed; worldY -= speed; break;
            case "upright": worldX += speed; worldY -= speed; break;
            case "downleft": worldX -= speed; worldY += speed; break;
            case "downright": worldX += speed; worldY += speed; break;
        }
        // Kiểm tra collision sau khi di chuyển
        collisionOn = false;
        gp.cChecker.checkTile(this);
        if (collisionOn) {
            // Rollback vị trí nếu va chạm
            worldX = oldWorldX;
            worldY = oldWorldY;
            pathList.clear(); // Hủy đường đi hiện tại
        }
    }

    public void setIndexInEnemyArray(int index) {
        this.indexInEnemyArray = index;
    }

    private void placeBomb() {
        if (bombCooldown > 0) return;

        int centerX = worldX + solidArea.x + solidArea.width / 2;
        int centerY = worldY + solidArea.y + solidArea.height / 2;

        int bombCol = centerX / gp.tileSize;
        int bombRow = centerY / gp.tileSize;

        int bombWorldX = bombCol * gp.tileSize;
        int bombWorldY = bombRow * gp.tileSize;

        if (!hasEscapeRouteAfterPlacingBomb(bombCol, bombRow)) return;

        if (bombCol < 0 || bombCol >= gp.maxWorldCol || bombRow < 0 || bombRow >= gp.maxWorldRow) return;
        if (gp.tileM.tile[gp.tileM.mapTileNum[bombCol][bombRow]].collision) return;

        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] == null) {
                Bomb bomb = new Bomb(gp);
                bomb.worldX = bombWorldX;
                bomb.worldY = bombWorldY;
                bomb.explosionRange = this.bombRange;
                gp.obj[i] = bomb;
                bombCooldown = BOMB_COOLDOWN_TIME;
                placedBomb = (Bomb) gp.obj[i];
                break;
            }
        }
    }

    private int targetX, targetY;

    private void moveToTarget() {
        if (pathList.isEmpty()) return;

        if (targetX == 0 && targetY == 0) {
            Node next = pathList.get(0);
            targetX = next.col * gp.tileSize;
            targetY = next.row * gp.tileSize;
        }

        int dx = targetX - worldX;
        int dy = targetY - worldY;

        if (Math.abs(dx) > Math.abs(dy)) {
            // Ưu tiên di chuyển theo trục X trước
            if (dx > 0) {
                direction = "right";
                worldX += speed;
            } else if (dx < 0) {
                direction = "left";
                worldX -= speed;
            }
        } else {
            // Nếu trục Y lớn hơn thì di chuyển theo trục Y
            if (dy > 0) {
                direction = "down";
                worldY += speed;
            } else if (dy < 0) {
                direction = "up";
                worldY -= speed;
            }
        }

        // Nếu đến nơi
        if (Math.abs(dx) <= speed && Math.abs(dy) <= speed) {
            worldX = targetX;
            worldY = targetY;
            pathList.remove(0);
            targetX = 0;
            targetY = 0;
        }
    }

    private int calculateDistanceToPlayer() {
        int col = worldX / gp.tileSize;
        int row = worldY / gp.tileSize;
        int playerCol = gp.player.worldX / gp.tileSize;
        int playerRow = gp.player.worldY / gp.tileSize;

        return Math.abs(col - playerCol) + Math.abs(row - playerRow);
    }

    private void calculateAStarPath() {
        int startCol = worldX / gp.tileSize;
        int startRow = worldY / gp.tileSize;
        int goalCol = gp.player.worldX / gp.tileSize;
        int goalRow = gp.player.worldY / gp.tileSize;

        PathFinder pf = new PathFinder(gp);
        List<Node> path = pf.findPath(startCol, startRow, goalCol, goalRow, false); // true = cho phép đi chéo
        if (path != null) pathList = path;
    }

    private boolean hasEscapeRouteAfterPlacingBomb(int bombCol, int bombRow) {
        PathFinder pf = new PathFinder(gp);
        int startCol = bombCol;
        int startRow = bombRow;

        int radius = 5;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int testCol = startCol + dx;
                int testRow = startRow + dy;

                if (testCol < 0 || testRow < 0 || testCol >= gp.maxWorldCol || testRow >= gp.maxWorldRow) continue;
                if (!isSafeFromBomb(testCol, testRow, bombCol, bombRow)) continue;

                List<Node> path = pf.findPath(startCol, startRow, testCol, testRow, false);
                if (path != null) return true;
            }
        }
        return false;
    }

    private boolean isSafeFromBomb(int tileCol, int tileRow, int bombCol, int bombRow) {
        if (tileCol == bombCol && Math.abs(tileRow - bombRow) <= bombRange) return false;
        if (tileRow == bombRow && Math.abs(tileCol - bombCol) <= bombRange) return false;
        if (tileCol == bombCol && tileRow == bombRow) return false; // Tránh tile bom đặt
        return true;
    }

    private int countSafeNeighbors(int col, int row, int radius) {
        int count = 0;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int nCol = col + dx;
                int nRow = row + dy;
                if (isInBounds(nCol, nRow) && isSafeTile(nCol, nRow)) {
                    count++;
                }
            }
        }
        return count;
    }

    private void escapeFromDanger() {
        int startCol = worldX / gp.tileSize;
        int startRow = worldY / gp.tileSize;
        PathFinder pf = new PathFinder(gp);
        List<Node> safestPath = null;
        int maxSafeNeighbors = -1;
        int searchRadius = 5;
        int radius = 3; // radius cho neighbors count

        for (int dx = -searchRadius; dx <= searchRadius; dx++) {
            for (int dy = -searchRadius; dy <= searchRadius; dy++) {
                int testCol = startCol + dx;
                int testRow = startRow + dy;
                if (isInBounds(testCol, testRow) && isSafeTile(testCol, testRow)) {
                    List<Node> path = pf.findPath(startCol, startRow, testCol, testRow, false);
                    if (path != null) {
                        int safeNeighbors = countSafeNeighbors(testCol, testRow, radius);
                        if (safeNeighbors > maxSafeNeighbors || safestPath == null) {
                            maxSafeNeighbors = safeNeighbors;
                            safestPath = path;
                        }
                    }
                }
            }
        }

        if (safestPath != null) {
            pathList = safestPath;
        } else {
            EnemyBehavior.smartWander(this);
        }
    }

    private boolean isNearBomb() {
        int enemyCol = worldX / gp.tileSize;
        int enemyRow = worldY / gp.tileSize;
        for (SuperObject obj : gp.obj) {
            if (obj instanceof Bomb) {
                int bombCol = obj.worldX / gp.tileSize;
                int bombRow = obj.worldY / gp.tileSize;
                int dist = Math.abs(bombCol - enemyCol) + Math.abs(bombRow - enemyRow);
                if (dist <= SAFE_DISTANCE) return true;
            }
        }
        return false;
    }

    private boolean isInBounds(int col, int row) {
        return col >= 0 && col < gp.maxWorldCol && row >= 0 && row < gp.maxWorldRow;
    }

    private boolean isSafeTile(int col, int row) {
        // Kiểm tra tile collision
        if (gp.tileM.tile[gp.tileM.mapTileNum[col][row]].collision) return false;

        // Kiểm tra bomb và flame
        for (Flame flame : gp.flames) {
            if (flame != null && flame.collision && 
                flame.worldX / gp.tileSize == col && 
                flame.worldY / gp.tileSize == row) {
                return false;
            }
        }
        // Kiểm tra bomb và các ô xung quanh bomb
        for (SuperObject obj : gp.obj) {
            if (obj instanceof Bomb) {
                int bombCol = obj.worldX / gp.tileSize;
                int bombRow = obj.worldY / gp.tileSize;

                if (
                    (col == bombCol && row == bombRow) || // chính ô bomb
                    (col == bombCol && Math.abs(row - bombRow) == 1) || // trên/dưới
                    (row == bombRow && Math.abs(col - bombCol) == 1)    // trái/phải
                ) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isNearFlame() {
        int enemyCol = worldX / gp.tileSize;
        int enemyRow = worldY / gp.tileSize;

        for (Flame flame : gp.flames) {
            if (flame != null && flame.collision) {
                int flameCol = flame.worldX / gp.tileSize;
                int flameRow = flame.worldY / gp.tileSize;

                int dist = Math.abs(flameCol - enemyCol) + Math.abs(flameRow - enemyRow);
                if (dist <= SAFE_DISTANCE) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isHitByFlame() {
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
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void takeDamage(int damage) {
        if (invincibleCounter == 0) {
            health -= damage;
            hurtSound.setFile(6);
            hurtSound.play();
            if (health <= 0) {
                // Có thể thêm hiệu ứng chết ở đây
            } else {
                invincibleCounter = 60;
            }
        }
    }
}
