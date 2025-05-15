package main;

import object.Chest;

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
}
