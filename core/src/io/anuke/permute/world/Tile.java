package io.anuke.permute.world;

public class Tile{
	public Block wall, floor;
	
	public Tile(Block wall, Block floor){
		this.wall = wall;
		this.floor = floor;
	}
}
