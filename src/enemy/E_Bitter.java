package enemy;

import main.GamePanel;
import main.Sound;
import object.Bomb;
import object.Flame;


import java.awt.*;
import java.util.List;

import entity.Entity;

public class E_Bitter extends Entity {

    private final int VISION_RANGE = 7; // số tile
    private final int SAFE_DISTANCE = 2; // số tile để tránh bom
    public int bombCooldown = 0;
    public final int BOMB_COOLDOWN_TIME = 240; // Mỗi 4 giây đặt 1 bomb
    public int bombRange = 1;
    public int invincibleCounter = 0;
    public final int INVINCIBLE_TIME = 60;
    private enum State { CHASE, ESCAPE }
    private State aiState = State.CHASE;
    private int indexInEnemyArray = -1;
    private Sound hurtSound = new Sound();
    private List<Node> pathList = new java.util.ArrayList<>();

    public E_Bitter(GamePanel gp) {
        super(gp);
        name = "E_Bitter";
        speed = 2;
        maxHealth = 5;
        health = maxHealth;
        solidArea = new Rectangle(8, 8, 32, 32);
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

        // Giảm cooldown bomb (nếu dùng)
        bombCooldown = Math.max(0, bombCooldown - 1);

        if (bombCooldown > 0) {
            bombCooldown--;
        }

        // Bất tử tạm thời
        if (invincibleCounter > 0) invincibleCounter--;

        // Nếu bị trúng flame
        if (isHitByFlame()) {
            takeDamage(1);
        }

        // Xử lý theo trạng thái AI
        switch (aiState) {
            case ESCAPE:
                if (isNearFlame()) {
                    escapeFromBomb();
                } else {
                    aiState = State.CHASE;
                    pathList.clear();
                }
                break;

            case CHASE:
                if (calculateDistanceToPlayer() < VISION_RANGE) {
                    calculateAStarPath();
                }
                break;
        }

        // Di chuyển theo path
        moveToTarget();

        // Kiểm tra nếu đủ gần để đặt bomb
        double distanceToPlayer = Math.hypot(
            (gp.player.worldX - this.worldX),
            (gp.player.worldY - this.worldY)
        );

        if (aiState == State.CHASE && distanceToPlayer < gp.tileSize * 2) {
            placeBomb();
            aiState = State.ESCAPE;
            pathList.clear();
        }

        // Va chạm tile
        collisionOn = false;
        gp.cChecker.checkTile(this);

        // Nếu không bị kẹt thì di chuyển
        if (!collisionOn) {
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

        // Di chuyển từng pixel
        if (dx > 0) { worldX += speed; direction = "right"; }
        else if (dx < 0) { worldX -= speed; direction = "left"; }

        if (dy > 0) { worldY += speed; direction = "down"; }
        else if (dy < 0) { worldY -= speed; direction = "up"; }

        // Nếu đã đến nơi
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
        List<Node> path = pf.findPath(startCol, startRow, goalCol, goalRow, true); // true = cho phép đi chéo
        if (path != null) pathList = path;
    }

    private void escapeFromBomb() {
        int startCol = worldX / gp.tileSize;
        int startRow = worldY / gp.tileSize;

        PathFinder pf = new PathFinder(gp);
        int radius = 5; // phạm vi tìm tile an toàn
        List<Node> safestPath = null;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int testCol = startCol + dx;
                int testRow = startRow + dy;

                // Kiểm tra nằm trong giới hạn bản đồ
                if (testCol < 0 || testRow < 0 || testCol >= gp.maxWorldCol || testRow >= gp.maxWorldRow) {
                    continue;
                }

                if (isSafeTile(testCol, testRow)) {
                    List<Node> path = pf.findPath(startCol, startRow, testCol, testRow, true);
                    if (path != null && (safestPath == null || path.size() < safestPath.size())) {
                        safestPath = path;
                    }
                }
            }
        }

        if (safestPath != null) {
            pathList = safestPath;
        }
    }

    private boolean isSafeTile(int col, int row) {
        // Kiểm tra xem tile này có bị bomb hoặc flame đe dọa không
        for (Flame flame : gp.flames) {
            if (flame != null && flame.collision) {
                int flameCol = flame.worldX / gp.tileSize;
                int flameRow = flame.worldY / gp.tileSize;
                if (flameCol == col && flameRow == row) {
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
