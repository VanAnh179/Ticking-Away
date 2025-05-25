package main;

import enemy.E_Bitter;
import enemy.E_Sweet;
import enemy.E_Watermelon;
import object.Chest;
import object.Key;
import object.Portal;

public class AssetSetter {
	GamePanel gp;
	
	public AssetSetter(GamePanel gp) {
		this.gp = gp;
	}
	
	public void setObject() {
		addPortal(14, 45);
		addPortal(15, 45);
		addPortal(6, 24);
        addKey(30, 5);
        addKey(34, 41);
        addKey(7, 44);
        addKey(44, 19);
        addKey(9, 13);
        addKey(22, 9);
        addKey(14, 23);
        addKey(16, 40);
        addKey(39, 10);
        addKey(17, 21);
        addKey(21, 31);
	}

	public void setEnemy() {
        addSweet(20, 9);
        addSweet(24, 34);
        addSweet(14, 6);
        addSweet(6, 8);
        addBitter(28, 18);
        addBitter(5, 9);
        addWatermelon(41, 10);
        addWatermelon(12, 5);
        addWatermelon(10, 17);
        addWatermelon(10, 32);
        addSweet(8, 41);
        addSweet(14, 42);
        addSweet(21, 23);
        addBitter(16, 39);
        addBitter(10, 22);
        addWatermelon(6, 41);
        addWatermelon(22, 27);
        addWatermelon(26, 42);
        addWatermelon(14, 37);
        addWatermelon(21, 22);
        addWatermelon(43, 14);
        addSweet(25, 42);
        addSweet(37, 32);
        addSweet(22, 23);
        addSweet(39, 23);
        addBitter(40, 44);
        addBitter(26, 42);
        addWatermelon(33, 43);
        addWatermelon(39, 41);
        addWatermelon(34, 29);
        addBitter(8, 42);
        addWatermelon(34, 22);
        addSweet(42, 42);
        addSweet(42, 26);
        addSweet(40, 38);
        addSweet(42, 32);
        addSweet(41, 43);
        addSweet(13, 37);
        addSweet(5, 41);
        addSweet(8, 44);
        addBitter(38, 31);
        addBitter(5, 37);
        addBitter(14, 43);
        addBitter(43, 43);
	} 

	private void addBitter(int worldX, int worldY) {
        for (int i = 0; i < gp.enemy.length; i++) {
            if (gp.enemy[i] == null) {
                gp.enemy[i] = new E_Bitter(gp);
                gp.enemy[i].worldX = worldX * gp.tileSize;
                gp.enemy[i].worldY = worldY * gp.tileSize;
                ((E_Bitter) gp.enemy[i]).setIndexInEnemyArray(i); // Đã gán index
                break;
            }
        }
    }

    private void addSweet(int worldX, int worldY) {
        for (int i = 0; i < gp.enemy.length; i++) {
            if (gp.enemy[i] == null) {
                gp.enemy[i] = new E_Sweet(gp);
                gp.enemy[i].worldX = worldX * gp.tileSize;
                gp.enemy[i].worldY = worldY * gp.tileSize;
                ((E_Sweet) gp.enemy[i]).setIndexInEnemyArray(i); // Đã gán index
                break;
            }
        }
    }

    private void addWatermelon(int worldX, int worldY) {
        for (int i = 0; i < gp.enemy.length; i++) {
            if (gp.enemy[i] == null) {
                gp.enemy[i] = new E_Watermelon(gp);
                gp.enemy[i].worldX = worldX * gp.tileSize;
                gp.enemy[i].worldY = worldY * gp.tileSize;
                ((E_Watermelon) gp.enemy[i]).setIndexInEnemyArray(i); // Đã gán index
                break;
            }
        }
    }

    private void addPortal(int worldX, int worldY) {
        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] == null) {
                gp.obj[i] = new Portal(gp);
                gp.obj[i].worldX = worldX * gp.tileSize;
                gp.obj[i].worldY = worldY * gp.tileSize;
                break;
            }
        }
    }

    private void addKey(int worldX, int worldY) {
        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] == null) {
                gp.obj[i] = new Key(gp);
                gp.obj[i].worldX = worldX * gp.tileSize;
                gp.obj[i].worldY = worldY * gp.tileSize;
                break;
            }
        }
    }
}