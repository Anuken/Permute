package io.anuke.permute;

import static io.anuke.permute.Vars.*;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.anuke.permute.GameState.State;
import io.anuke.permute.entities.*;
import io.anuke.permute.world.Block;
import io.anuke.permute.world.Generator;
import io.anuke.permute.world.Tile;
import io.anuke.ucore.core.*;
import io.anuke.ucore.entities.Entities;
import io.anuke.ucore.entities.Entity;
import io.anuke.ucore.entities.SolidEntity;
import io.anuke.ucore.graphics.Atlas;
import io.anuke.ucore.modules.RendererModule;
import io.anuke.ucore.noise.Noise;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;


public class Control extends RendererModule{
	public Player player;
	public Core core;
	public Tile[][] tiles;
	public float playTime;
	public float targetZoom = 1f;
	//GifRecorder recorder = new GifRecorder(batch);
	private boolean dying;
	private boolean reset = true;
	
	
	public Control(){
		cameraScale = 3;
		
		atlas = new Atlas("verro.atlas");
		
		Gdx.input.setCatchBackKey(true);
	
		KeyBinds.defaults(
			"up", Keys.W,
			"left", Keys.A,
			"down", Keys.S,
			"right", Keys.D,
			"faster", Keys.SHIFT_LEFT, 
			"pause", Gdx.app.getType() == ApplicationType.Android ? Keys.BACK : Keys.ESCAPE
		);
		
		Entities.initPhysics();
		EffectLoader.load();
		
		Draw.addSurface("shadow");
		
		Settings.loadAll("io.anuke.verro");
		
		Sounds.load("explode.wav", "shoot.wav", "spawn.wav");
		Sounds.setFalloff(10000);
		
		Musics.load("1.mp3", "2.mp3", "3.mp3");
		Musics.shuffleAll();
		
		player = new Player().add();
		
		Entities.setCollider(tilesize, (x, y)->{
			return false;//getTile(x + worldsize/2, y + worldsize/2) != null && getTile(x + worldsize/2, y + worldsize/2).wall.solid;
		});
		
		tiles = new Tile[worldsize][worldsize];
		
		restart();
	}
	
	public void restart(){
		playTime = 0f;
		Entities.clear();
		Shape.friendlies = 0;
		Timers.clear();
		
		player.add().set(0, 0);
		reset = true;
		core = new Core().add();
		
		Noise.setSeed(MathUtils.random(999999));
		tiles = new Tile[worldsize][worldsize];
		
		int rad = 5;
		for(int dx = -rad; dx <= rad; dx ++){
			for(int dy = -rad; dy <= rad; dy ++){
				if(Vector2.dst(0, 0, dx, dy) < rad)
				tiles[worldsize/2+dx][worldsize/2+dy] = new Tile(Block.air, Block.air);
			}
		}
		
		for(int i = 0; i < 4; i ++){
			Shape shape = new Shape(true).add().set(Mathf.range(40), Mathf.range(40));
			if(i == 0) shape.role = Role.healer;
			if(i == 1) shape.addMutation(Mutation.shielded);
			if(i == 2) shape.role = Role.replicator;
			shape.sides = 5;
		}
	}
	
	public void coreDestroyed(){
		Effects.effect("coreexplode", 0, 0);
		Effects.shake(9, 30);
		
		for(int i = 0; i < 30; i ++){
			Timers.run(Mathf.random(60), ()->{
				Effects.effect("explosion", Mathf.range(40), Mathf.range(40));
			});
		}
		
		dying = true;
		
		Timers.run(80, ()->{
			GameState.set(State.dead);
			Vars.ui.showGameOver();
			dying = false;
		});
	}
	
	public void clampZoom(){
		targetZoom = Mathf.clamp(targetZoom, 0.5f, 3f);
		camera.zoom = Mathf.clamp(camera.zoom, 0.5f, 3f);
	}
	
	@Override
	public void update(){
		
		if(Inputs.keyUp("pause") && GameState.is(State.paused)){
			GameState.set(State.playing);
			Vars.ui.hidePaused();
		}else if(GameState.is(State.playing)){
			
			if(Inputs.keyUp("pause")){
				GameState.set(State.paused);
				Vars.ui.showPaused();
			}
			
			Timers.update();
			playTime += delta();
			
			if(Inputs.scrolled()){
				targetZoom -= Inputs.scroll()/4f;
				clampZoom();
			}
			
			camera.zoom = MathUtils.lerp(camera.zoom, targetZoom, 0.5f*delta());
			
			resize();
				
			spawn();
			Entities.update();
			
			if(!dying){
				//if(Gdx.app.getType() == ApplicationType.Android){
				//	setCamera(player.x, player.y);
				//	camera.update();
				//}else{
					smoothCamera(player.x, player.y, 0.25f);
				//}
			}else{
				smoothCamera(0, 0, 0.25f);
			}

			updateShake();
		}else if (GameState.is(State.menu)){
			smoothCamera(500f, 500f, 0.25f);
		}
		
		drawDefault();
		//recorder.update();
		
		Inputs.update();
	}
	
	@Override
	public void draw(){
		drawWorld();
		Entities.draw();
		//Entities.debugColliders();
	}
	
	@Override
	public void resize(){
		float f = Float.MAX_VALUE;
		float minx = f, maxx = f, miny = f, maxy = f;
		
		for(Entity entity : Entities.all()){
			
			if(entity instanceof SolidEntity){
				SolidEntity s = (SolidEntity)entity;
				
				float topx = s.x + s.hitsize;
				float topy = s.y + s.hitsize;
				
				float botx = s.x - s.hitsize;
				float boty = s.y - s.hitsize;
				
				if(minx == f || botx < minx){
					minx = botx;
				}
				
				if(maxx == f || topx > maxx){
					maxx = topx;
				}
				
				if(miny == f || boty < miny){
					miny = boty;
				}
				
				if(maxy == f || topy > maxy){
					maxy = topy;
				}
			}
		}
		
		Entities.resizeTree(minx, miny, maxx-minx, maxy-miny);
	}
	
	void spawn(){
		
		if(Timers.get("spawnin", 1200)){
			Shape shape = new Shape(true);
			
			Effects.sound("spawn", shape);
			
			shape.set(Mathf.range(10), Mathf.range(10));
			Effects.effect("merge", shape);
			Effects.effect("spawn", shape);
			
			if(Mathf.chance(0.1)){
				shape.role = Role.healer;
			}
			
			if(Mathf.chance(0.1)){
				shape.role = Role.replicator;
			}
			
			if(Mathf.chance(0.3)){
				shape.role = Role.dasher;
			}
			
			shape.add();
		}
		
		if(Mathf.chance(0.002 * Timers.time()/33000f) || Timers.get("enemyspawn", 5000)){
			boolean spawn = reset;
			reset = false;
			
			int amount = 1 + Mathf.random(3);
			
			vector.setToRandomDirection().scl(Mathf.random(150, 500));
			
			if(spawn)
				amount = 2;
			
			for(int id = 0; id < amount; id ++){
				float rand = 10;
				
				Shape shape = new Shape(false).set(vector.x+Mathf.range(rand), vector.y+Mathf.range(rand)).add();
				
				if(shape.collidesTile()){
					shape.remove();
					continue;
				}
				
				if(!spawn)
					shape.addXP(Mathf.random(70));
				else
					shape.addXP(Mathf.random(20));
				
				shape.role = Role.dasher;
				
				if(!spawn){
					if(Mathf.chance(0.05)){
						shape.role = Role.replicator;
					}
					
					if(Mathf.chance(0.1)){
					//	shape.role = Role.shooter;
					}
					
					if(Mathf.chance(0.05)){
						shape.role = Role.healer;
					}
					
					if(Mathf.chance(0.05)){
						shape.role = Role.exploder;
					}
				}
				
				int mutations = Mathf.random(0, 1);
				
				if(!spawn)
				for(int i = 0; i < mutations; i ++){
					shape.addMutation(Mutation.values()[Mathf.random(0, Mutation.values().length-1)]);
				}
				
				Effects.effect("spawn", shape);
				Effects.effect("merge", shape);
			}
		}
	}
	
	public Tile getTile(int dx, int dy){
		if(!Mathf.inBounds(dx, dy, tiles)){
			return null;
		}
		
		if(tiles[dx][dy] == null) Generator.generate(dx, dy, tiles);
		
		return tiles[dx][dy];
	}
	
	void drawWorld(){
		int rangex = Mathf.scl2(Gdx.graphics.getWidth()/cameraScale*camera.zoom, tilesize);
		int rangey = Mathf.scl2(Gdx.graphics.getHeight()/cameraScale*camera.zoom, tilesize);
		int camx = Mathf.scl(camera.position.x, tilesize);
		int camy = Mathf.scl(camera.position.y, tilesize);
		
		Draw.color();
		
		for(int i = 0; i < 2; i ++){
			
			for(int dx = camx - rangex+worldsize/2; dx < camx + rangex+worldsize/2; dx ++){
				for(int dy = camy - rangey+worldsize/2; dy < camy + rangey+worldsize/2; dy ++){
					Tile tile = getTile(dx, dy);
					
					if(tile == null) continue;
					
					if(i == 0){
						tile.floor.draw(dx-worldsize/2, dy-worldsize/2);
					}else if(i == 1){
						tile.wall.draw(dx-worldsize/2, dy-worldsize/2);
					}
				}
			}
		}
	}
}
