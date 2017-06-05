package io.anuke.permute;

import static io.anuke.ucore.scene.actions.Actions.*;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;

import io.anuke.permute.GameState.State;
import io.anuke.permute.entities.Mutation;
import io.anuke.permute.entities.Shape;
import io.anuke.ucore.function.VisibilityProvider;
import io.anuke.ucore.modules.SceneModule;
import io.anuke.ucore.scene.actions.Actions;
import io.anuke.ucore.scene.builders.*;
import io.anuke.ucore.scene.ui.*;
import io.anuke.ucore.scene.ui.layout.Table;

public class UI extends SceneModule{
	Dialog gameover, about, paused, tutorial;
	Table shapeinfo;
	SettingsDialog settings;
	KeybindDialog keybinds;
	boolean showedTutorial = false;
	
	VisibilityProvider inmenu = ()->{
		return GameState.is(State.menu);
	};
	
	VisibilityProvider playing = ()->{
		return GameState.is(State.playing);
	};
	
	public UI(){
		Dialog.setShowAction(()->{
			return sequence(Actions.rotateBy(90), Actions.scaleTo(3, 3), 
						parallel(Actions.scaleTo(1, 1, 0.09f, Interpolation.smooth), 
								Actions.rotateTo(0, 0.09f), 
								Actions.fadeIn(0.09f, Interpolation.fade)));
		});
		
		Dialog.setHideAction(()->{
			return sequence(
					parallel(Actions.moveBy(Gdx.graphics.getWidth()/2, 0, 0.08f, Interpolation.fade), 
							Actions.fadeOut(0.08f, Interpolation.fade)));
		});
		
	}
	
	@Override
	public void init(){
		settings = new SettingsDialog();
		settings.screenshakePref();
		settings.volumePrefs();
		
		tutorial = new TextDialog("Tutorial", Vars.tutorialText);
		
		tutorial.getButtonTable().addButton("OK", ()->{
			GameState.set(State.playing);
			Vars.control.restart();
			tutorial.hide();
		}).padBottom(6).size(60, 40);
		
		keybinds = new KeybindDialog();
		
		paused = new Dialog("Paused", "dialog");
		paused.getTitleLabel().setColor(Color.ORANGE);
		paused.content().pad(14);
		paused.content().defaults().size(200, 50);
		
		paused.content().addButton("Resume", ()->{
			GameState.set(State.playing);
			paused.hide();
		});
		paused.content().row();
		
		paused.content().addButton("Settings", ()->{
			settings.show();
		});
		paused.content().row();
		
		paused.content().addButton("Controls", ()->{
			keybinds.show();
		});
		paused.content().row();
		
		paused.content().addButton("Back to Menu", ()->{
			GameState.set(State.menu);
			paused.hide();
		});
		paused.content().row();
		
		gameover = new Dialog("Game Over");
		
		build.begin(gameover.content());
		
		new label(()->{
			int seconds = (int)(Vars.control.playTime/60);
			return "You survived for\n[crimson]" + seconds/60 + " [yellow]minutes, [crimson]" + seconds%60 + "[yellow] seconds.";
		}).pad(5);
		
		gameover.content().row();
		
		new button("Back to menu", ()->{
			Vars.control.restart();
			GameState.set(State.menu);
			gameover.hide();
		}).size(200, 60).padLeft(20).padRight(20);
		
		gameover.content().pad(8);
		
		build.end();
		
		about = new TextDialog("About", Vars.aboutText);
		
		build.begin();
		
		new table(){{
			
			defaults().size(200, 70);
			
			new button("Play", ()->{
				
				if(!showedTutorial){
					tutorial.show();
					showedTutorial = true;
				}else{
					GameState.set(State.playing);
					Vars.control.restart();
				}
			});
			
			row();
			
			new button("Settings", ()->{
				settings.show();
			});
			
			row();
			
			new button("Controls", ()->{
				keybinds.show();
			});
			
			row();
			
			new button("About", ()->{
				about.show();
			});
			
		}}.end().visible(inmenu);
		
		new table(){{
			atop();
			Image image = new Image("logo");
			add(image);
		}}.end().visible(inmenu);
		
		new table(){{
			abottom().aright();
			
			new table("button"){{
				shapeinfo = get();
			}};
		}}.end().visible(playing);
		
		if(Gdx.app.getType() == ApplicationType.Desktop)
		new table(){{
			atop().aleft();
			
			new label(()->{
				return Gdx.graphics.getFramesPerSecond() + " FPS";
			});
		}}.end().visible(playing);
		
		
		build.end();
	}
	
	public void showTutorial(){
		tutorial.show();
	}
	
	public void updateUnitInfo(Shape shape){
		if(shape == null){
			shapeinfo.setVisible(false);
		}else{
			shapeinfo.clearChildren();
			shapeinfo.setVisible(true);
			
			shapeinfo.left();
			shapeinfo.pad(10);
			
			shapeinfo.defaults().left().pad(4);
			
			shapeinfo.add("[yellow][["+shape.role + "]");
			shapeinfo.row();
			shapeinfo.add("[green]Level " + shape.level);
			shapeinfo.row();
			shapeinfo.add("[purple]XP: [yellow]" + shape.xp + "[purple]-[yellow]" + shape.targetXP());
			shapeinfo.row();
			shapeinfo.add("[yellow]Health: [scarlet]" + shape.health + "[purple]-[scarlet]" + shape.maxhealth);
			shapeinfo.row();
			shapeinfo.add("[orange]Size: " + shape.size);
			shapeinfo.row();
			
			if(shape.mutations.size == 0){
				shapeinfo.add("[yellow]Mutations: [crimson]none");
			}else{
				shapeinfo.add("[yellow]Mutations: ");
				shapeinfo.row();
				for(Mutation mut : shape.mutations){
					shapeinfo.add("- [pink]" + mut.name());
				}
			}
			
		}
	}
	
	public void showPaused(){
		paused.show();
	}
	
	public void hidePaused(){
		paused.hide();
	}
	
	public void showGameOver(){
		gameover.content().pack();
		gameover.invalidateHierarchy();
		gameover.show();
	}
	
}
