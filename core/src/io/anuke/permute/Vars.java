package io.anuke.permute;

import io.anuke.ucore.scene.ui.layout.Unit;

public class Vars{
	public static Control control;
	public static UI ui;
	
	public static final int tilesize = 12;
	public static final int worldsize = 300;
	
	public static final float fontScale = 0.5f * Unit.dp.inPixels(1f);
	
	public static final String[] aboutText = {
		"[yellow]Made in 72 hours by [red]Anuke[] \nfor the [orange]GDL June Gene Jam.[]",
		"\nMusic from [green]freemusicarchive.org."
	};
	
	public static final String[] tutorialText = {
		"[yellow]How to play:",
		"",
		"[orange]- Click and drag on units to move them.",
		"- Rightclick selected units to split them in two.",
		"- Drag units into others to combine them.",
		"- Use [[WASD] to move the camera around.",
		"- You can duplicate unit types by merging them with a unit of a lower type, then splitting them agian.",
		"",
		"[SKY]Shapes get XP when they kill others.",
		"When a shake gets enough XP, they level up.",
		"",
		"[crimson]Units can also acquire random mutations from other shapes when they kill them.",
		"",
		"[royal]The objective is to defend the base and survive as long as possible."
	};
}
