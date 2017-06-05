package io.anuke.permute.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectSet;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.entities.*;
import io.anuke.ucore.graphics.Hue;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class Shape extends DestructibleEntity{
	public static int friendlies;
	
	static final Color[] friendlyColors = {Color.GREEN, Color.YELLOW, Color.SKY, Color.WHITE, Color.PINK, Color.BLUE};
	static final Color[] enemyColors = {Color.RED, Color.CORAL, Color.ORANGE, Color.FIREBRICK, Color.GOLD};
	
	boolean friendly;
	DestructibleEntity target;
	float hit;
	float hittime = 5;
	
	public int shield = 0;
	public int maxshield;
	public int sides = 3, size = 7;
	public float rotation = 0f, speed = 0.3f;
	public float reload = 20, accuracy = 8f, spread = 15;
	public int shots = 1;
	public BulletType bullet = BulletType.basicshot;
	public ObjectSet<Mutation> mutations = new ObjectSet<>();
	public Role role = Role.shooter;
	
	public int level = 1;
	public int xp = 0;
	
	public static void merge(Shape a, Shape b){
		Shape shape = new Shape(a.friendly);
		
		shape.health = a.health + b.health;
		
		shape.maxhealth = a.maxhealth + b.maxhealth;
		
		for(Mutation mut : a.mutations)
			shape.addMutation(mut);
		for(Mutation mut : b.mutations)
			shape.addMutation(mut);
		
		shape.sides = a.sides + b.sides;
		shape.level = a.level + b.level;
		shape.addXP(a.xp+b.xp);
		shape.size = a.size + b.size;
		
		if(a.role.ordinal() > b.role.ordinal()){
			shape.role = a.role;
		}else{
			shape.role = b.role;
		}
		
		shape.set((a.x+b.x)/2f, (a.y+b.y)/2f);
		
		a.remove();
		b.remove();
		shape.add();
		
		Effects.effect("merge", shape);
		Effects.effect("split", a);
		Effects.effect("split", b);
	}
	
	public Shape(boolean friendly){
		this.friendly = friendly;
		if(friendly)
			friendlies ++;
		
		maxhealth = 40;
		heal();
	}

	void onKill(Shape shape){
		for(Mutation mut : shape.mutations){
			if(Mathf.chance(0.5))
			addMutation(mut);
		}
		
		addXP(shape.xp/2);
	}
	
	public void addXP(int amount){
		xp += amount;
		
		while(xp >= targetXP()){
			xp -= targetXP();
			level ++;
			Effects.effect("levelup", this);
			maxhealth += 50;
			sides ++;
			size += 2;
			speed += 0.03f;
			reload -= 4;
			accuracy -= 1.5f;
			if(accuracy < 0) accuracy = 0f;
			heal();
		}
	}
	
	public boolean canSplit(){
		return sides/2 >= 3 && size/2 >= 3;
	}
	
	public int targetXP(){
		return 10+level*5;
	}
	
	public void split(){
		
		if(canSplit()){
			int tsides = sides/2;
			int tsize = size/2;
			
			Shape a = new Shape(friendly).add(), b = new Shape(friendly).add();
			a.sides = b.sides = tsides;
			a.size = b.size = tsize;
			a.level = b.level = Math.max(level/2, 1);
			a.maxhealth = b.maxhealth = maxhealth/2;
			a.health = b.health = health/2;
			a.role = b.role = role;
			
			int range = size;
			a.set(x+Mathf.range(range/2, range), y+Mathf.range(range/2, range));
			b.set(x+Mathf.range(range/2, range), y+Mathf.range(range/2, range));
			
			for(Mutation mut : mutations){
				if(Mathf.chance(0.5)){
					a.addMutation(mut);
				}else{
					b.addMutation(mut);
				}
			}
			
			Effects.effect("split", this);
		}
	}
	
	public void addMutation(Mutation mut){
		mut.apply(this);
		mutations.add(mut);
	}
	
	public float drawSize(){
		return size*2;
	}
	
	@Override
	public void damage(int amount){
		if(shield > 0){
			shield -= amount;
		}else{
			super.damage(amount);
		}
	}
	
	@Override
	public void collision(SolidEntity other){
		super.collision(other);
		
		hit = hittime;
		
		if(dead){
			Bullet bullet = (Bullet)other;
			if(bullet.owner instanceof Shape){
				Shape killer = (Shape)bullet.owner;
				if(!killer.isDead())
				killer.onKill(this);
			}
		}
	}
	
	@Override
	public boolean collides(SolidEntity other){
		if(!(other instanceof Bullet)) return false;
		
		Bullet bullet = (Bullet)other;
		Entity e = bullet.owner;
		
		return e instanceof Shape && ((Shape)e).friendly != this.friendly;
	}
	
	@Override
	public void onDeath(){
		remove();
		Effects.effect("explosion", this);
		Effects.shake(5f, 3f);
		Effects.sound("explode", this);
		
		//split();
		
		if(friendly)
			friendlies --;
	}
	
	@Override
	public void update(){
		
		Entities.getNearby(x, y, size*2, e->{
			if(e != this && e instanceof Shape && e.distanceTo(this) < size*1.3f){
				Shape shape = (Shape)e;
				if(!shape.role.stationary() && role.stationary()) return;
				
				float dst = e.distanceTo(this);
				float scl = size*1.3f-dst;
				vector.set(e.x - x, e.y - y).scl(-scl*0.07f).limit(0.6f);
				move(vector.x, vector.y);
			}
		});
		
		if(hit > 0){
			hit -= delta;
		}
		
		if(target != null && (target.isDead() || target.health <= 0))
			target = null;
		
		if(Timers.get(this.hashCode() + "track", 20))
		target = (DestructibleEntity)Entities.getClosest(x, y, 500, e->{
			return e != this && ((e instanceof Shape && ((Shape)e).friendly != this.friendly) || (!friendly && e instanceof Core));
		});
		
		role.update(this);
		
		if(target != null){
			role.move(this);
			
			role.shoot(this);
		}
		
		hitsize = size*1.5f;
		
		for(Mutation mut : mutations){
			mut.update(this);
		}
	}
	
	boolean selected(){
		float hs = size*2;
		float tx = Graphics.mouseWorld().x;
		float ty = Graphics.mouseWorld().y;
		return Mathf.inRect(tx, ty, x-hs, y-hs, x+hs, y+hs);
	}
	
	@Override
	public void draw(){
		Draw.thick(1f);
		
		Draw.color(getColor());
		
		Draw.polygon(sides, x, y, size, rotation-90);
		
		if(shield > 0){
			float htime = hit > 0 ? (hit+1)/hittime : 0;
			Draw.color(Hue.mix(Color.SKY, Color.WHITE, Math.abs(MathUtils.sin(Timers.time()/10f))), Color.PURPLE, htime);
			
			Draw.polysegment(30, 0, (int)(20*((float)shield/maxshield)), x, y, size+10, Timers.time()*3f);
		}
		
		role.draw(this);
	}
	
	public Color getColor(){
		if(friendly){
			return (friendlyColors[Mathf.clamp(level-1, 0, friendlyColors.length-1)]);
		}else{
			return (enemyColors[Mathf.clamp(level-1, 0, enemyColors.length-1)]);
		}
	}
	
	@Override
	public void drawOver(){
		if(health == maxhealth) return;
		
		Draw.thick(1f);
		Draw.color(Color.DARK_GRAY);
		Draw.circle(x, y, size+4);
		Draw.color(friendly ? Color.LIME : Color.ORANGE);
		Draw.polysegment(40, 0, (int)(40*healthfrac()), x, y, size+4, 0);
	}
}
