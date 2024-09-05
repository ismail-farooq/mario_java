package com.ismail.mario.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.ismail.mario.Mario;
import com.ismail.mario.Scenes.HUD;
import com.ismail.mario.Screens.PlayScreen;

public class Brick extends InteractiveTileObject {

	public Brick(PlayScreen screen, Rectangle bounds) {
		super(screen, bounds);
		fixture.setUserData(this);
		setCategoryFilter(Mario.BRICK_BIT);
	}

	@Override
	public void onHeadHit() {
		Gdx.app.log("brick", "collision");
		setCategoryFilter(Mario.DESTROYED_BIT);
		getCell().setTile(null);
		HUD.addScore(200);
		Mario.manager.get("audio/sounds/breakblock.wav", Sound.class).play(1.0f);
		
	}

}
