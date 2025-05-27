package main;

import entity.Player;
import java.awt.Rectangle;
import object.Chest1;
import object.Chest2;
import object.Key;
import object.Portal;
import object.SuperObject;
import object.buffitems.IncreaseDamage;
import object.buffitems.IncreaseHealth;
import object.buffitems.IncreaseLight;
import object.debuffitems.DecreaseSpeed;
import object.debuffitems.IncreaseBombTime;
import object.debuffitems.Teleport;

public class EventObject {
    GamePanel gp;
    public boolean firstPortalContact = true;

    public EventObject(GamePanel gp) {
        this.gp = gp;
    }

    public void handleItemPickup(int index) {
        if (index == 999) return; // Không có item để nhặt

        SuperObject item = gp.obj[index];

        if (item instanceof Key) {
            checkKeyPickup(index);
        }
        if (item instanceof Portal) {
            checkPortalEvent(index);
        }
        if (item instanceof Chest1) {
            Chest1 chest = (Chest1) item;
            if(!chest.isOpened()) {
                chest.startOpening();
            }
        }
        if (item instanceof IncreaseDamage) {
            applyBombRangeEffect(index);
        }
        // Thêm các loại item khác ở đây (nếu cần)
        else if (item instanceof IncreaseHealth) {
            applyHealthEffect(index);
        }
        else if (item instanceof IncreaseBombTime) {
            applyBombTimeDebuff(index);
        }
        else if (item instanceof DecreaseSpeed) {
            applySpeedDebuff(index);
        }
        else if (item instanceof Teleport) {
            applyTeleportEffect(index);
        } 
        else if (item instanceof Chest2) {
            Chest2 chest2 = (Chest2) item;
            if(!chest2.isOpened()) {
                chest2.startOpening();
            }
        } 
        else if (item instanceof IncreaseLight) {
            applyLightBuff(index);
        }
    }

    private void applyBombRangeEffect(int itemIndex) {
        // Tăng phạm vi bom nổ (không cộng dồn)
        Player player = gp.player;
        player.tempBombRange = player.bombRange + 1; // Tăng phạm vi bom nổ
        player.bombRangeExpireTime = System.currentTimeMillis() + 30000; // 30 giây 
        player.bombRange = Math.max(player.originalBombRange, player.tempBombRange); // Cập nhật phạm vi bom nổ

        gp.ui.showMessage("Bomb range +1");

        // Xóa item khỏi map
        gp.obj[itemIndex] = null;

        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                player.tempBombRange = 0;
                player.bombRange = player.originalBombRange;
                gp.ui.showMessage("Bomb range returned to normal");
            }
        }, 30000); //tăng phạm vi bomb nổ trong 30s 

    }

    private void applyHealthEffect(int itemIndex) {
        Player player = gp.player;
        if(player.health < 4) {
            player.health++;
            gp.ui.showMessage("Health +1");
        } else {
            player.tempHealth += 1; // Tăng máu tạm thời
            player.tempHealthExpireTime = System.currentTimeMillis() + 30000; // 30 giây
            player.health = player.getTotalHealth();
            gp.ui.showMessage("Temp Health +1");
        }
        gp.obj[itemIndex] = null; // Xóa item khỏi map
    }

    private void applySpeedDebuff(int itemIndex) {
        Player player = gp.player;
        int newSpeed = player.speed - 1;
        if(newSpeed > 0) {
            player.speed = newSpeed;
            gp.obj[itemIndex] = null;
            gp.ui.showMessage("Speed - 1");
            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    player.speed = player.baseSpeed;
                    gp.ui.showMessage("Speed back to normal");
                }
            }, 10000);
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

    private void applyLightBuff(int itemIndex) {
        Player player = gp.player;
        player.bonusLightRadius += gp.tileSize;
        gp.ui.showMessage("Light Radius Increased!");
        gp.obj[itemIndex] = null;
    }

    private void applyBombTimeDebuff(int itemIndex) {
        Player player = gp.player;
        // Lưu giá trị cooldown gốc và tăng cooldown lên gấp đôi (hoặc giá trị mong muốn)
        player.originalBombCooldown = player.BOMB_COOLDOWN_TIME;
        player.tempBombCooldown = player.BOMB_COOLDOWN_TIME * 5; // Ví dụ: tăng gấp đôi
        player.bombCooldownExpireTime = System.currentTimeMillis() + 15000; // 15 giây

        gp.ui.showMessage("Bomb cooldown increased to 5ss!");
        gp.obj[itemIndex] = null; // Xóa item khỏi map

        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                // Khôi phục giá trị cooldown ban đầu
                player.tempBombCooldown = 0;
                gp.ui.showMessage("Bomb cooldown back to normal");
            }
        }, 15000); // Hiệu ứng kéo dài 1515 giây
    }

    public void checkPortalEvent(int i) {
        Rectangle playerRect = new Rectangle(gp.player.worldX, gp.player.worldY, gp.tileSize, gp.tileSize);
        Rectangle portalRect = new Rectangle(gp.obj[i].worldX, gp.obj[i].worldY, gp.tileSize, gp.tileSize);
        
        if (playerRect.intersects(portalRect)) {
            if (firstPortalContact) {
                gp.ui.triggerPortalSequence();
                firstPortalContact = false;
            } else {
                checkPortalCondition();
            }
        }
    }

    public void checkPortalAfterDialog() {
        checkPortalCondition();
    }

    private void checkPortalCondition() {
        if (gp.hasKey >= 1) {
            gp.ui.gameFinished = true;
            gp.ui.gameWon = true;
            gp.stopMusic();
        } else {
            gp.ui.showMessage("Bạn cần 3 chìa khóa để mở cổng!");
        }
    }

    public void checkKeyPickup(int i) {
        if (gp.obj[i] == null || gp.obj[i].collision) return;

        Rectangle playerRect = new Rectangle(
            gp.player.worldX + gp.player.solidArea.x,
            gp.player.worldY + gp.player.solidArea.y,
            gp.player.solidArea.width,
            gp.player.solidArea.height
        );
        Rectangle keyRect = new Rectangle(gp.obj[i].worldX + 10, gp.obj[i].worldY + 10, gp.tileSize - 20, gp.tileSize - 20);

        if (playerRect.intersects(keyRect)) {
            gp.hasKey++;
            gp.obj[i] = null; // Xóa key ngay lập tức
            gp.playSoundEffect(9);
            gp.player.isJumping = true;
            gp.player.initialY = gp.player.worldY;
            gp.player.verticalVelocity = gp.player.jumpForce;
            gp.ui.showMessage("Key collected! " + gp.hasKey);
            System.out.println("Key picked! Total keys: " + gp.hasKey);

            // Kích hoạt hiệu ứng sequence chỉ một lần
            if (!gp.ui.hasShownKeyMessage) {
                gp.ui.triggerKeySequence();
                gp.ui.hasShownKeyMessage = true;
            }
        }
    }
}