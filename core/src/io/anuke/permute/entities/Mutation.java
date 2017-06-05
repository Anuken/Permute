package io.anuke.permute.entities;

import io.anuke.ucore.util.Timers;

public enum Mutation{
	speed1{
		public void apply(Shape shape){
			shape.speed += 0.1f;
		}
	},
	speed2{
		public void apply(Shape shape){
			shape.speed += 0.2f;
		}
	},
	speed3{
		public void apply(Shape shape){
			shape.speed += 0.3f;
		}
	},
	reload1{
		public void apply(Shape shape){
			shape.reload -= 2f;
		}
	},
	reload2{
		public void apply(Shape shape){
			shape.reload -= 4f;
		}
	},
	health1{
		public void apply(Shape shape){
			shape.maxhealth += 15;
			shape.health += 15;
		}
	},
	health2{
		public void apply(Shape shape){
			shape.maxhealth += 30;
			shape.health += 30;
		}
	},
	shielded{
		public void apply(Shape shape){
			shape.maxshield = 20;
			shape.shield = shape.maxshield;
		}
		
		public void update(Shape shape){
			if(shape.shield < shape.maxshield && Timers.get(shape.hashCode() + "shield", 30)){
				shape.shield ++;
			}
		}
	},
	extrashot{
		public void apply(Shape shape){
			shape.shots ++;
		}
	},
	berserker{
		public void apply(Shape shape){
			shape.reload -= 6f;
			shape.accuracy += 10f;
			shape.bullet = BulletType.basicshot;
		}
	},
	regen1{
		public void update(Shape shape){
			if(Timers.get("regen", 40)){
				shape.health ++;
				shape.clampHealth();
			}
		}
	},
	regen2{
		public void update(Shape shape){
			if(Timers.get("regen", 30)){
				shape.health ++;
				shape.clampHealth();
			}
		}
	};
	
	public void apply(Shape shape){}
	public void update(Shape shape){}
}
