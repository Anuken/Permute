package io.anuke.permute;

import io.anuke.ucore.modules.Core;

public class Permute extends Core {
	
	@Override
	public void init(){
		add(Vars.control = new Control());
		add(Vars.ui = new UI());
	}
	
}
