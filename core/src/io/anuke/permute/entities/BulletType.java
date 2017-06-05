package io.anuke.permute.entities;

import com.badlogic.gdx.graphics.Color;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.entities.BaseBulletType;

public abstract class BulletType extends BaseBulletType<Bullet>{
	public static final BulletType 
	
	shot = new BulletType(){
		{
			speed = 3.5f;
		}
		@Override
		public void draw(Bullet b){
			Draw.thick(2);
			Draw.color(Color.SKY);
			
			Draw.lineAngleCenter(b.x, b.y, b.angle(), 6);
			
			Draw.reset();
		}
		
		@Override
		public void removed(Bullet b){
			Effects.effect("bullethit", b);
		}
	},
	basicshot = new BulletType(){
		{
			speed = 1.8f;
			lifetime = 140;
		}
		@Override
		public void draw(Bullet b){
			Shape shape = (Shape)b.owner;
			
			Draw.thick(2);
			Draw.color(shape.getColor());
			
			Draw.lineAngleCenter(b.x, b.y, b.angle(), 4);
			
			Draw.reset();
		}
		
		@Override
		public void removed(Bullet b){
			Effects.effect("bullethit", b);
		}
	},
	snipeshot = new BulletType(){
		{
			speed = 3f;
			lifetime = 100;
			damage = 3;
		}
		@Override
		public void draw(Bullet b){
			Shape shape = (Shape)b.owner;
			
			Draw.thick(2);
			
			Draw.color(shape.getColor());
			Draw.lineAngleCenter(b.x, b.y, b.angle(), 5);
			
			Draw.color(Color.ORANGE);
			Draw.lineAngle(b.x, b.y, b.angle(), 2);
			
			Draw.reset();
		}
		
		@Override
		public void removed(Bullet b){
			Effects.effect("bullethit", b);
		}
	};

}
