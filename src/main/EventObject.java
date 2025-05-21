package main;

import entity.Player;
import object.SuperObject;
import object.buffitems.IncreaseDamage;
import object.buffitems.IncreaseHealth;
import object.debuffitems.DecreaseSpeed;
import object.debuffitems.Teleport;
import object.Chest;
import java.util.Random;

public class EventObject {
    GamePanel gp;

    public EventObject(GamePanel gp) {
        this.gp = gp;
    }

    public void handleItemPickup(int index) {
        if (index == 999) return; // Không có item để nhặt

        SuperObject item = gp.obj[index];
        
        if (item instanceof IncreaseDamage) {
            applyBombRangeEffect(index);
        }
        // Thêm các loại item khác ở đây (nếu cần)
        else if (item instanceof IncreaseHealth) {
            applyHealthEffect(index);
        }
        else if(item instanceof DecreaseSpeed) {
            applySpeedDebuff(index);
        }
        else if(item instanceof Teleport) {
            applyTeleportEffect(index);
        }
    }

    private void applyBombRangeEffect(int itemIndex) {
        // Tăng phạm vi bom nổ (không cộng dồn)
        Player player = gp.player;
        player.bombRange = Math.max(player.bombRange, 2); // Mặc định là 1, tăng lên 2

        // Xóa item khỏi map
        gp.obj[itemIndex] = null;

    }

    private void applyHealthEffect(int itemIndex) {
        Player player = gp.player;
        if(player.maxHealth < 4) {
            player.maxHealth++;
            player.health = player.maxHealth;
            gp.obj[itemIndex] = null; // Xóa item khỏi map
        }
    }

    private void applySpeedDebuff(int itemIndex) {
        Player player = gp.player;
        int newSpeed = Math.max(1, (int)(player.speed * 0.7));

        if(newSpeed < player.speed) {
            player.speed = newSpeed;
            gp.obj[itemIndex] = null; // Xóa item khỏi map

            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    player.speed = 4; // Khôi phục tốc độ ban đầu
                }
            }, 10000); // Khôi phục sau 5 giây
        }
    }

    private void applyTeleportEffect(int itemIndex) {
        gp.obj[itemIndex] = null;
    
        // 2. Tìm vị trí ngẫu nhiên hợp lệ
        int attempts = 0;
        while (attempts < 100) { // Thử tối đa 100 lần
            int randomCol = (int)(Math.random() * gp.maxWorldCol);
            int randomRow = (int)(Math.random() * gp.maxWorldRow);
            
            // Kiểm tra tile có thể đứng được
            if (!gp.tileM.tile[gp.tileM.mapTileNum[randomCol][randomRow]].collision) {
                // 3. Di chuyển player
                gp.player.worldX = randomCol * gp.tileSize;
                gp.player.worldY = randomRow * gp.tileSize;
                
                return;
            }
            attempts++;
        }
    }

    private void opendChest(int itemIndex) {
        if(gp.obj[itemIndex] instanceof Chest) {
            Chest chest = (Chest) gp.obj[itemIndex];
            
            spawnRandomItem(chest.worldX, chest.worldY);

            gp.obj[itemIndex] = null; // Xóa chest khỏi map
        }
    }

    private void spawnRandomItem(int x, int y) {
        int randomItem = new Random().nextInt(4);
        SuperObject item = switch (randomItem) {
            case 0 -> new IncreaseDamage(gp);
            case 1 -> new IncreaseHealth(gp);
            case 2 -> new DecreaseSpeed(gp);
            case 3 -> new Teleport(gp);
            default -> null;
        };
        if (item != null) {
            item.worldX = x;
            item.worldY = y;
            for (int i = 0; i < gp.obj.length; i++) {
                if (gp.obj[i] == null) {
                    gp.obj[i] = item;
                    break;
                }
            }
        }
    }
}