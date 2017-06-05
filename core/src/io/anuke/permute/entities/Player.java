package io.anuke.permute.entities;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

import io.anuke.permute.Vars;
import io.anuke.ucore.core.*;
import io.anuke.ucore.entities.Entities;
import io.anuke.ucore.entities.Entity;
import io.anuke.ucore.util.Timers;

public class Player extends Entity{
	Shape selected;
	boolean merging;

	public void update(){

		Shape hover = hovered();

		if(Inputs.buttonRelease(Buttons.LEFT) && merging && selected != null){
			if(hover == null || Graphics.mouseWorld().dst(hover.x, hover.y) > hover.size || (hover == selected)){
				float lx = selected.x;
				float ly = selected.y;

				selected.set(Graphics.mouseWorld().x, Graphics.mouseWorld().y);

				if(selected.collidesTile()){
					selected.set(lx, ly);
				}else{
					Effects.effect("merge", lx, ly);
					selected.set(Graphics.mouseWorld().x, Graphics.mouseWorld().y);
					Effects.effect("split", selected);
				}

			}else if(hover != null && hover != selected){
				Shape.merge(selected, hover);

				merging = false;
				selected = null;
			}
		}

		if(!Inputs.buttonDown(Buttons.LEFT)){
			merging = false;
		}

		if(hover != null && Inputs.buttonUp(Buttons.LEFT)){
			selected = hover;
		}

		if(hover == null && Inputs.buttonUp(Buttons.LEFT) && selected != null){
			selected = null;
		}

		if(selected != null && (selected.isDead())){
			selected = null;
		}

		if(selected != null && Inputs.buttonUp(Buttons.RIGHT) && selected.canSplit()){
			selected.split();
			selected.remove();
			selected = null;
		}

		if(Inputs.buttonUp(Buttons.LEFT) && selected != null){
			merging = true;
		}

		Vars.ui.updateUnitInfo(selected);

		float speed = 4f * delta;

		vector.set(0, 0);

		if(Inputs.keyDown("faster")){
			speed *= 3f;
		}

		if(Inputs.keyDown("up")){
			vector.y += speed;
		}

		if(Inputs.keyDown("down")){
			vector.y -= speed;
		}

		if(Inputs.keyDown("left")){
			vector.x -= speed;
		}

		if(Inputs.keyDown("right")){
			vector.x += speed;
		}

		vector.limit(speed);

		x += vector.x;
		y += vector.y;

		if(Gdx.app.getType() == ApplicationType.Desktop){

			if(Inputs.keyUp(Keys.T)){
				Effects.effect("levelup", this);
			}

			if(Inputs.keyUp(Keys.R)){
				Shape shape = new Shape(false).set(x, y).add();
				shape.role = Role.dasher;
			}
		}
	}

	@Override
	public void added(){
		selected = null;
	}

	Shape hovered(){
		Shape sel = null;

		for(Entity e : Entities.getNearby(Graphics.mouseWorld().x, Graphics.mouseWorld().y, 30)){
			if(!(e instanceof Shape))
				continue;

			Shape shape = (Shape) e;

			if(!shape.friendly)
				continue;

			if(shape.selected() && (sel == null || Graphics.mouseWorld().dst(e.x, e.y) < Graphics.mouseWorld().dst(sel.x, sel.y))){
				sel = shape;
			}
		}

		return sel;
	}

	void drawSelection(Shape shape, Color color){
		Draw.color(Color.ORANGE);
		Draw.polygon(8, shape.x, shape.y, 30 + shape.size, Timers.time());
		Draw.color(color);
		Draw.polygon(shape.sides, shape.x, shape.y, shape.size, shape.rotation - 90);

		Draw.color(Color.DARK_GRAY);
		Draw.circle(shape.x, shape.y, 14 + shape.size);
		Draw.color(Color.PURPLE);
		Draw.polysegment(30, 0, (int) (30 * (float) shape.xp / shape.targetXP()), shape.x, shape.y, 14 + shape.size, 0);
	}

	public void drawOver(){

		Draw.thick(1f);

		if(selected != null){
			drawSelection(selected, Color.CLEAR);
		}

		Shape hover = hovered();

		if(hover != null && hover != selected){
			drawSelection(hover, Color.ORANGE);
		}

		if(merging && selected != null){
			if((hover == null || hover == selected) || Graphics.mouseWorld().dst(hover.x, hover.y) > hover.size){
				Draw.color(Color.ORANGE);
				Draw.line(selected.x, selected.y, Graphics.mouseWorld().x, Graphics.mouseWorld().y);
			}else{
				Draw.color(Color.PURPLE);
				Draw.line(selected.x, selected.y, hover.x, hover.y);
			}
		}

		if(selected != null){
			Draw.tscl(1 / 6f);
			Draw.text("[YELLOW][[" + selected.role + "][RED]\nL" + selected.level, selected.x, selected.y + 10 + selected.size);
			Draw.tscl(0.5f);
		}

		Draw.color();

	}
}
