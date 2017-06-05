package io.anuke.permute.entities;

import io.anuke.ucore.entities.BulletEntity;
import io.anuke.ucore.entities.Entity;

public class Bullet extends BulletEntity{
	
	public Bullet(BulletType type, Entity owner, float angle){
		super(type, owner, angle);
	}
	
	@Override
	public void update(){
		super.update();
		
		if(this.collidesTile()){
			this.remove();
			type.removed(this);
		}
	}
}
