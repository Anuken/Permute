package io.anuke.permute.world;

import io.anuke.permute.Vars;
import io.anuke.permute.entities.DormantShape;
import io.anuke.ucore.noise.Noise;
import io.anuke.ucore.util.Mathf;

public class Generator{
	
	static{
		Noise.setSeed(Mathf.random(999999));
	}
	
	public static void generate(int x, int y, Tile[][] tiles){
		Block floor = Block.air;
		Block wall = Block.air;
		
		double d = Noise.nnoise(x, y, 8, 6) + Noise.nnoise(x, y/3, 13, 5);
		double f = Noise.nnoise(x, y, 9, 6);
		
		if(d > 1.05){
			wall = Block.wall; 
		}else if(d > 1.0){
			wall = Block.wall2; 
		}
		
		if(f > 1.0){
			floor = Block.tile1;
		}else if(f > 0.9){
			floor = Block.tile2;
		}
		
		if(Mathf.chance(0.0004)){
			DormantShape shape = new DormantShape().add();
			shape.set((x-Vars.worldsize/2)*Vars.tilesize, (y-Vars.worldsize/2)*Vars.tilesize);
		}
		
		Tile tile = new Tile(wall, floor);
		tiles[x][y] = tile;
	}
}
