package io.anuke.permute;

import com.badlogic.gdx.graphics.Color;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.entities.Effect;

public class EffectLoader{
	
	public static void load(){
		
		Effect.create("explosion", 8, e->{
			Draw.thick(5-e.ifract()*2);
			
			Draw.color(Color.WHITE, Color.PURPLE, e.ifract());
			
			Draw.polygon(8, e.x, e.y, 2+e.ifract()*20);
			
			Draw.reset();
		});
		
		Effect.create("coreexplode", 20, e->{
			Draw.thick(5-e.ifract()*2);
			
			Draw.color(Color.WHITE, Color.PURPLE, e.ifract());
			
			Draw.polygon(30, e.x, e.y, 2+e.ifract()*70);
			
			Draw.reset();
		});
		
		Effect.create("split", 10, e->{
			Draw.thick(1);
			
			Draw.color(Color.WHITE, Color.GREEN, e.ifract());
			
			Draw.spikes(e.x, e.y, e.ifract()*10, 3, 6, 0);
			
			Draw.reset();
		});
		
		Effect.create("merge", 10, e->{
			Draw.thick(1);
			
			Draw.color(Color.WHITE, Color.PURPLE, e.fract());
			
			Draw.spikes(e.x, e.y, e.fract()*10, 3, 6, 0);
			
			Draw.reset();
		});
		
		Effect.create("spawn", 14, e->{
			Draw.thick(1);
			
			Draw.color(Color.PURPLE, Color.WHITE, e.fract());
			
			Draw.spikes(e.x, e.y, 7+e.fract()*10, 3, 3, -90);
			Draw.polygon(3, e.x, e.y, 7+e.fract()*10, -90);
			
			Draw.reset();
		});
		
		Effect.create("levelup", 40, e->{
			Draw.thick(2);
			
			Draw.color(Color.GREEN,  Color.ORANGE, e.ifract());
			
			Draw.polysegment(20, 0, 6, e.x, e.y, 2+e.fract()*30, e.ifract()*360f);
			Draw.polysegment(20, 0, 6, e.x, e.y, 2+e.fract()*30, e.ifract()*360f+180);
			Draw.spikes(e.x, e.y, 2+e.fract()*60, 5, 8);
			
			Draw.reset();
		});
		
		Effect.create("bullethit", 6, e->{
			Draw.thick(2);
			
			Draw.color(Color.WHITE, Color.PURPLE, e.ifract());
			
			Draw.spikes(e.x, e.y, e.ifract()*10, 2, 4, 45);
			
			Draw.reset();
		});
		
	}
}
