package io.anuke.permute;

public class GameState{
	private static State state = State.menu;
		
	public static void set(State astate){
		state = astate;
	}
	
	public static boolean is(State astate){
		return state == astate;
	}
	
	public static enum State{
		menu, playing, dead, paused;
	}
}
