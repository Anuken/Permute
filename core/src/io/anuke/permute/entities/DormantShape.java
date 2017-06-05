package io.anuke.permute.entities;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.entities.Entity;
import io.anuke.ucore.util.Mathf;

public class DormantShape extends Entity{
	int sides = 3 + Mathf.random(6), size = 7;
	
	public void update(){
		if(selected() && Inputs.buttonDown(Buttons.LEFT)){
			remove();
			Shape shape = new Shape(true);
			shape.sides = sides;
			shape.set(x, y);
			shape.size = size;
			shape.add();
		}
	}
	
	public void draw(){
		Draw.color(Color.DARK_GRAY);
		if(selected()){
			Draw.color(Color.PURPLE);
		}
		
		Draw.polygon(sides, x, y, size, 90);
	}
	
	boolean selected(){
		float hs = size;
		float tx = Graphics.mouseWorld().x;
		float ty = Graphics.mouseWorld().y;
		return Mathf.inRect(tx, ty, x-hs, y-hs, x+hs, y+hs);
	}
}
