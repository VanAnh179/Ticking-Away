package main;

import enemy.E_Bitter;
import enemy.E_Sweet;
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
		//addSweet(30, 5);
		//addBitter(30, 5);
		gp.enemy[0] = new E_Bitter(gp);
		gp.enemy[0].worldX = 30 * gp.tileSize;
		gp.enemy[0].worldY = 5 * gp.tileSize;
	} 

// 	private void addBitter(int worldX, int worldY) {
//        for (int i = 0; i < gp.enemy.length; i++) {
//            if (gp.enemy[i] == null) {
//                gp.enemy[i] = new E_Bitter(gp);
//                gp.enemy[i].worldX = worldX * gp.tileSize;
// 				gp.enemy[i].worldY = worldY * gp.tileSize;
//                ((E_Bitter) gp.enemy[i]).setIndexInEnemyArray(i); // Gán index
//                break;
//            }
//        }
//    }
	private void addSweet(int worldX, int worldY) {
       for (int i = 0; i < gp.enemy.length; i++) {
			if (gp.enemy[i] == null) {
				gp.enemy[i] = new E_Sweet(gp);
				gp.enemy[i].worldX = worldX * gp.tileSize;
				gp.enemy[i].worldY = worldY * gp.tileSize;
				((E_Sweet) gp.enemy[i]).setIndexInEnemyArray(i); // Gán index
				break;
			}
		}
   }
}