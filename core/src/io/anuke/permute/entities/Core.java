package io.anuke.permute.entities;

import com.badlogic.gdx.graphics.Color;

import io.anuke.permute.Vars;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.entities.*;
import io.anuke.ucore.util.Timers;

public class Core extends DestructibleEntity{
	float hit;
	float hitdur = 6;
	
	public Core(){
		maxhealth = 300;
		heal();
		hitsize = 25;
	}
	
	public float drawSize(){
		return 100;
	}
	
	@Override
	public void onDeath(){
		Vars.control.coreDestroyed();
		remove();
	}
	
	@Override
	public void collision(SolidEntity other){
		super.collision(other);
		hit = hitdur;
	}
	
	@Override
	public boolean collides(SolidEntity other){
		if(!(other instanceof Bullet)) return false;
		
		Bullet bullet = (Bullet)other;
		Entity e = bullet.owner;
		
		return e instanceof Shape && !((Shape)e).friendly;
	}
	
	@Override
	public void update(){
		if(hit > 0)
			hit -= delta;
	}
	
	@Override
	public void draw(){
		float rot = Timers.time()/2f;
		
		if(hit > 0)
			Draw.color(Color.WHITE, Color.SCARLET, (hit+delta)/hitdur);
		else
			Draw.color(Color.WHITE);
		
		Draw.polygon(5, x, y, 20, rot);
		Draw.spikes(x, y, 10, 10, 5, rot+90);
		Draw.polygon(5, x, y, 10, rot);
	}
	
	@Override
	public void drawOver(){
		float rad = 40;
		
		Draw.thick(1f);
		Draw.color(Color.DARK_GRAY);
		Draw.circle(x, y, rad);
		if(hit > 0)
			Draw.color(Color.SKY, Color.ORANGE, (hit+delta)/hitdur);
		else
			Draw.color(Color.SKY);
		
		Draw.polysegment(70, 0, (int)(70*healthfrac()), x, y, rad, 0);
		
		Draw.color(Color.RED);
		for(Entity entity : Entities.all()){
			if(entity instanceof Shape && !((Shape)entity).friendly){
				
				float angle = this.angleTo(entity);
				vector.setLength(rad-10);
				vector.setAngle(angle);
				
				Draw.polygon(3, vector.x, vector.y, 3, angle - 90);
			}
		}
	}
}
