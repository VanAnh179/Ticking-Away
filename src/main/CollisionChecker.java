package main;

import java.awt.Rectangle;

import entity.Entity;
import object.Bomb;
import object.Flame;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;

    }

    public void checkTile(Entity entity) {

        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        int tileNum1, tileNum2;

		if (entity.getClass().getSimpleName().equals("E_Sweet")) {
			entity.collisionOn = false;
			return;
		}

		switch (entity.direction) {
			case "up":
				entityTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
				tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
				tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];

				boolean tileCollisionUp1 = gp.tileM.tile[tileNum1].collision;
				boolean tileCollisionUp2 = gp.tileM.tile[tileNum2].collision;
				entity.collisionOn = tileCollisionUp1 || tileCollisionUp2;

				if (!entity.collisionOn) {
					if (gp.obj[tileNum1] instanceof Bomb && !((Bomb) gp.obj[tileNum1]).isActive) {
					entity.collisionOn = false;
					}
					if (gp.obj[tileNum2] instanceof Bomb && !((Bomb) gp.obj[tileNum2]).isActive) {
					entity.collisionOn = false;
					}
				}
				break;
			case "down":
				entityBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
				tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
				tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];

				boolean tileCollisionDown1 = gp.tileM.tile[tileNum1].collision;
				boolean tileCollisionDown2 = gp.tileM.tile[tileNum2].collision;
				entity.collisionOn = tileCollisionDown1 || tileCollisionDown2;

				if (!entity.collisionOn) {
					if (gp.obj[tileNum1] instanceof Bomb && !((Bomb) gp.obj[tileNum1]).isActive) {
					entity.collisionOn = false;
					}
					if (gp.obj[tileNum2] instanceof Bomb && !((Bomb) gp.obj[tileNum2]).isActive) {
					entity.collisionOn = false;
					}
				}
				break;
			case "left":
				entityLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
				tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
				tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];

				boolean tileCollisionLeft1 = gp.tileM.tile[tileNum1].collision;
				boolean tileCollisionLeft2 = gp.tileM.tile[tileNum2].collision;
				entity.collisionOn = tileCollisionLeft1 || tileCollisionLeft2;

				if (!entity.collisionOn) {
					if (gp.obj[tileNum1] instanceof Bomb && !((Bomb) gp.obj[tileNum1]).isActive) {
					entity.collisionOn = false;
					}
					if (gp.obj[tileNum2] instanceof Bomb && !((Bomb) gp.obj[tileNum2]).isActive) {
					entity.collisionOn = false;
					}
				}
				break;
			case "right":
				entityRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
				tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
				tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];

				boolean tileCollisionRight1 = gp.tileM.tile[tileNum1].collision;
				boolean tileCollisionRight2 = gp.tileM.tile[tileNum2].collision;
				entity.collisionOn = tileCollisionRight1 || tileCollisionRight2;

				if (!entity.collisionOn) {
					if (gp.obj[tileNum1] instanceof Bomb && !((Bomb) gp.obj[tileNum1]).isActive) {
					entity.collisionOn = false;
					}
					if (gp.obj[tileNum2] instanceof Bomb && !((Bomb) gp.obj[tileNum2]).isActive) {
					entity.collisionOn = false;
					}
				}
				break;
		}
		// Sau khi xác định collisionOn, điều chỉnh vị trí nếu cần
		int nextWorldX = entity.worldX;
		int nextWorldY = entity.worldY;

		// Tính toán vị trí dự kiến sau khi di chuyển
		switch (entity.direction) {
			case "up": nextWorldY -= entity.speed; break;
			case "down": nextWorldY += entity.speed; break;
			case "left": nextWorldX -= entity.speed; break;
			case "right": nextWorldX += entity.speed; break;
			case "up-left":
                    nextWorldY -= entity.speed;
                    nextWorldX -= entity.speed;
                    break;
                case "up-right":
                    nextWorldY -= entity.speed;
                    nextWorldX += entity.speed;
                    break;
                case "down-left":
                    nextWorldY += entity.speed;
                    nextWorldX -= entity.speed;
                    break;
                case "down-right":
                    nextWorldY += entity.speed;
                    nextWorldX += entity.speed;
                    break;
		}

		// Kiểm tra collision tại vị trí dự kiến
		// boolean isColliding = isCollisionAt(nextWorldX, nextWorldY, entity);

		// if (isColliding) {
		// 	entity.collisionOn = true;
		// } else {
		// 	entity.worldX = nextWorldX;
		// 	entity.worldY = nextWorldY;
		// }
	}

	// private boolean isCollisionAt(int x, int y, Entity entity) {
	// 	// Tính toán vị trí tile dựa trên x và y
	// 	int leftCol = (x + entity.solidArea.x) / gp.tileSize;
	// 	int rightCol = (x + entity.solidArea.x + entity.solidArea.width) / gp.tileSize;
	// 	int topRow = (y + entity.solidArea.y) / gp.tileSize;
	// 	int bottomRow = (y + entity.solidArea.y + entity.solidArea.height) / gp.tileSize;

	// 	// Kiểm tra các tile xung quanh
	// 	return gp.tileM.tile[gp.tileM.mapTileNum[leftCol][topRow]].collision ||
	// 		gp.tileM.tile[gp.tileM.mapTileNum[rightCol][topRow]].collision ||
	// 		gp.tileM.tile[gp.tileM.mapTileNum[leftCol][bottomRow]].collision ||
	// 		gp.tileM.tile[gp.tileM.mapTileNum[rightCol][bottomRow]].collision;
	// }
    
    public int checkObject(Entity entity, boolean player) {
    	
    	int index = 999;
    	
    	for (int i = 0; i < gp.obj.length; i++) {
    		if (gp.obj[i] != null) {
    			
    			//get entity's solid area position
    			entity.solidArea.x = entity.worldX + entity.solidArea.x;
    			entity.solidArea.y = entity.worldY + entity.solidArea.y;
    			
    			//get object's solid area position
    			gp.obj[i].solidArea.x = gp.obj[i].worldX + gp.obj[i].solidArea.x;
    			gp.obj[i].solidArea.y = gp.obj[i].worldY + gp.obj[i].solidArea.y;
    			
				switch (entity.direction) {
				case "up":
					entity.solidArea.y -= entity.speed;
                    if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                        if (gp.obj[i].collision) {
                            entity.collisionOn = true;
                        }
                        if (player) {
                            index = i;
                        }
                    }
					break;
				case "down":
					entity.solidArea.y += entity.speed;
					if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
						if (gp.obj[i].collision) {
							entity.collisionOn = true;
						}
						if (player) {
							index = i;
						}
					}
					break;
				case "left":
					entity.solidArea.x -= entity.speed;
					if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
						if (gp.obj[i].collision) {
							entity.collisionOn = true;
						}
						if (player) {
							index = i;
						}
					}
					break;
				case "right":
					entity.solidArea.x += entity.speed;
					if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
						if (gp.obj[i].collision) {
							entity.collisionOn = true;
						}
						if (player) {
							index = i;
						}
					}
					break;
				}
    			entity.solidArea.x = entity.solidAreaDefaultX;
    			entity.solidArea.y = entity.solidAreaDefaultY;
    			gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
    			gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;
    		}
    	}
    	
    	return index;
    }

	// check enemy collision
	public int checkEntity(Entity entity, Entity[] target) {

		int index = 999;
		for (int i = 0; i < target.length; i++) {
			if (target[i] != null && target[i] != entity) {
				// Tính toán vùng va chạm
				Rectangle entityArea = new Rectangle(
					entity.worldX + entity.solidArea.x,
					entity.worldY + entity.solidArea.y,
					entity.solidArea.width,
					entity.solidArea.height
				);
				Rectangle targetArea = new Rectangle(
					target[i].worldX + target[i].solidArea.x,
					target[i].worldY + target[i].solidArea.y,
					target[i].solidArea.width,
					target[i].solidArea.height
				);
				if (entityArea.intersects(targetArea)) {
					System.out.println("[Debug] Collision detected with enemy " + i);
					index = i;
					break;
				}
			}
		}

		for (Flame flame : gp.flames) {
        if (flame != null && flame.collision) {
            Rectangle flameRect = new Rectangle(
                flame.worldX + flame.solidArea.x,
                flame.worldY + flame.solidArea.y,
                flame.solidArea.width,
                flame.solidArea.height
            );
            
            Rectangle entityRect = new Rectangle(
                entity.worldX + entity.solidArea.x,
                entity.worldY + entity.solidArea.y,
                entity.solidArea.width,
                entity.solidArea.height
            );
            
            if (flameRect.intersects(entityRect)) {
                entity.takeDamage(1);
                index = 0;
                break;
            }
        }
    }
    	return index;
	
	}

	public void checkPlayer(Entity entity) {
		//get entity's solid area position
		entity.solidArea.x = entity.worldX + entity.solidArea.x;
		entity.solidArea.y = entity.worldY + entity.solidArea.y;
		
		//get object's solid area position
		gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
		gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;
		
		switch (entity.direction) {
		case "up":
			entity.solidArea.y -= entity.speed;
			if (entity.solidArea.intersects(gp.player.solidArea)) {
				entity.collisionOn = true;
			}
			break;
		case "down":
			entity.solidArea.y += entity.speed;
			if (entity.solidArea.intersects(gp.player.solidArea)) {
				entity.collisionOn = true;
			}
			break;
		case "left":
			entity.solidArea.x -= entity.speed;
			if (entity.solidArea.intersects(gp.player.solidArea)) {
				entity.collisionOn = true;
			}
			break;
		case "right":
			entity.solidArea.x += entity.speed;
			if (entity.solidArea.intersects(gp.player.solidArea)) {
				entity.collisionOn = true;
			}
			break;
		}
		entity.solidArea.x = entity.solidAreaDefaultX;
		entity.solidArea.y = entity.solidAreaDefaultY;
		gp.player.solidArea.x = gp.player.solidAreaDefaultX;
		gp.player.solidArea.y = gp.player.solidAreaDefaultY;
	}
}