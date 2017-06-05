package io.anuke.permute.android;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Vector2;

import io.anuke.permute.Vars;

public class GestureHandler extends GestureAdapter{
	Vector2 pinch1 = new Vector2(-1, -1), pinch2 = pinch1.cpy();
	Vector2 vector = new Vector2();
	float initzoom = -1;
	
	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY){
		if(touches() < 2) return false;
		
		
		return false;
	}
	
	@Override
	public boolean pinch (Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		if(pinch1.x < 0){
			pinch1.set(initialPointer1);
			pinch2.set(initialPointer2);
		}
		
		Vector2 vec = (vector.set(pointer1).add(pointer2).scl(0.5f)).sub(pinch1.add(pinch2).scl(0.5f));
		
		Vars.control.player.x -= vec.x*Vars.control.camera.zoom/Vars.control.cameraScale;
		Vars.control.player.y += vec.y*Vars.control.camera.zoom/Vars.control.cameraScale;
		
		pinch1.set(pointer1);
		pinch2.set(pointer2);
		
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance){
		
		if(initzoom <= 0)
			initzoom = initialDistance;
		
		Vars.control.targetZoom /= (distance/initzoom);
		Vars.control.clampZoom();
		Vars.control.camera.update();
		
		initzoom = distance;
		
		return false;
	}
	
	@Override
	public void pinchStop () {
		initzoom = -1;
		pinch2.set(pinch1.set(-1, -1));
	}
	
	int touches(){
		int sum = 0;
		for(int i = 0; i < 10; i ++){
			if(Gdx.input.isTouched(i)) sum++;
		}
		return sum;
	}
}
