package io.anuke.permute.world;

import static io.anuke.permute.Vars.tilesize;

import io.anuke.ucore.core.Draw;

public class Block{
	public static final Block 
	
	air= new Block("air"){
		public void draw(int x, int y){}
	},
	
	tile1 = new Block("tile1"),
	
	tile2 = new Block("tile2"),
	
	wall = new Block("wall"){{
		solid = true;
	}},
	
	wall2 = new Block("wall2"){{
		solid = true;
	}}
	;
	
	public boolean solid;
	public final String name;
	
	protected Block(String name){
		this.name = name;
	}
	
	
	public void draw(int x, int y){
		Draw.rect(name, x*tilesize, y*tilesize);
	}
}
