package io.anuke.permute.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.entities.*;
import io.anuke.ucore.modules.Module;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public enum Role{
	shooter{
		public void draw(Shape shape){
			Draw.color(Color.PURPLE);
			
			Draw.polygon(3, shape.x, shape.y, 3, Timers.time()/1f);
		}
		
		public void move(Shape shape){
			Entity target = shape.target;
			shape.rotation = MathUtils.lerpAngleDeg(shape.rotation, shape.angleTo(target), 0.1f);
		}
	},
	dasher{
		public void draw(Shape shape){
			Draw.color(Color.YELLOW);
			
			Draw.polygon(3, shape.x, shape.y, 3, Timers.time()/1f);
		}
		
		public boolean stationary(){
			return false;
		}
	},
	exploder{
		public void draw(Shape shape){
			Draw.color(Color.ORANGE);
			
			Draw.polygon(4, shape.x, shape.y, 3, Timers.time()*2f);
		}
		
		public void move(Shape shape){
			SolidEntity target = shape.target;
			shape.rotation = MathUtils.lerpAngleDeg(shape.rotation, shape.angleTo(target), 0.1f*Entity.delta);
			
			Module.vector.set(shape.speed*2f, 0).rotate(shape.rotation).scl(Entity.delta);
			shape.move(Module.vector.x, Module.vector.y);
			
			if(target.distanceTo(shape) <= (target.hitsize + shape.hitsize)*1.4f){
				Effects.effect("explosion", shape);
				((DestructibleEntity)target).damage(20);
				shape.remove();
			}
			
			//TODO EXPLODING
		}
		
		public void shoot(Shape shape){
			
		}
		
		public boolean stationary(){
			return false;
		}
	},
	healer{
		DestructibleEntity target(Shape shape){
			return (DestructibleEntity)Entities.getClosest(shape.x, shape.y, 100, e->{
				return e != shape && ((e instanceof Shape 
						&& ((Shape)e).friendly == shape.friendly) 
						|| (e instanceof Core && shape.friendly))  
						&& ((DestructibleEntity)e).health < ((DestructibleEntity)e).maxhealth;
			});
		}
		
		public void shoot(Shape shape){
			
		}
		
		public void update(Shape shape){
			DestructibleEntity to = target(shape);
			
			if(to == null) return;
			
			if(Timers.get(shape, shape.reload*1.5f)){
				to.health += 1;
				to.clampHealth();
			}
		}
		
		public void move(Shape shape){
			
		}
		
		public void draw(Shape shape){
			Draw.color(Color.SCARLET);
			
			Draw.polygon(4, shape.x, shape.y, 4, Timers.time()/1f);
			
			DestructibleEntity to = target(shape);
			
			if(to == null) return;
		
			Draw.color(Color.LIME, Color.WHITE, Math.abs(MathUtils.sin(Timers.time()/10f)));
			Draw.line(shape.x, shape.y, to.x, to.y);
			Draw.thick(1);
			Draw.circle(to.x, to.y, 4);
			
			Draw.circle(to.x, to.y, 0.4f);
			
		}
	},
	replicator{
		
		public void shoot(Shape shape){
			
		}
		
		public void move(Shape shape){
			
		}
		
		public void update(Shape shape){
			if(Timers.get(this + "replicate", 1050-shape.level*50)){
				Shape spawned = new Shape(shape.friendly);
				Effects.sound("spawn", spawned);
				Module.vector.setToRandomDirection().setLength(shape.size);
				spawned.set(shape.x+Module.vector.x, shape.y+Module.vector.y);
				Effects.effect("spawn", spawned);
				spawned.add();
			}
		}
		
		public void draw(Shape shape){
			Draw.color(Color.CYAN);
			
			Draw.polygon(5, shape.x, shape.y, 4, Timers.time()/1f);
		}
		
		
	},
	sniper{
		
	};
	
	public boolean stationary(){
		return true;
	}
	
	public void update(Shape shape){}
	
	public void move(Shape shape){
		Entity target = shape.target;
		shape.rotation = MathUtils.lerpAngleDeg(shape.rotation, shape.angleTo(target), 0.1f*Entity.delta);
		
		Module.vector.set(shape.speed, 0).rotate(shape.rotation).scl(Entity.delta);
		shape.move(Module.vector.x, Module.vector.y);
	}
	
	public void draw(Shape shape){
		
	}
	
	public void shoot(Shape shape){
		if(shape.distanceTo(shape.target) > 220)
			return;
		
		if(Timers.get(shape, shape.reload)){
			Module.vector.set(shape.size, 0).rotate(shape.rotation);
			
			for(int i = 0; i < shape.shots; i ++){
				
				float spacing = (shape.shots-1)*shape.spread;
				
				new Bullet(shape.bullet, shape, 
						shape.rotation + Mathf.range(shape.accuracy) + shape.spread*i-spacing/2)
				.set(shape.x+Module.vector.x, shape.y+Module.vector.y).add();
				
				Effects.sound("shoot", shape);
			}
		}
	}
}
