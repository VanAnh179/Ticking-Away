package main;

import enemy.E_Bitter;
import enemy.E_Sweet;
import enemy.E_Watermelon;
import object.Chest;
import object.Key;

public class AssetSetter {
	GamePanel gp;
	
	public AssetSetter(GamePanel gp) {
		this.gp = gp;
	}
	
	public void setObject() {
		gp.obj[0] = new Chest(gp);
		gp.obj[0].worldX = gp.tileSize * 34;
		gp.obj[0].worldY = gp.tileSize * 5;
	}

	public void setEnemy() {
        addSweet(20, 9);
        addSweet(14, 6);
        addBitter(28, 18);
        addWatermelon(41, 10);
        addSweet(8, 41);
        addSweet(14, 42);
        addWatermelon(16, 39);
        addWatermelon(6, 41);
        addSweet(25, 42);
        addBitter(40, 44);
        addWatermelon(33, 43);
        addSweet(42, 42);
        addSweet(40, 38);
        addSweet(42, 32);
        addBitter(14, 43);
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
}