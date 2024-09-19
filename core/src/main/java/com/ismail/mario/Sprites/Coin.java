package com.ismail.mario.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.ismail.mario.Mario;
import com.ismail.mario.Scenes.HUD;
import com.ismail.mario.Screens.PlayScreen;

public class Coin extends InteractiveTileObject {
	
	private static TiledMapTileSet tileSet;
	private final int BLANK_COIN = 28;

	public Coin(PlayScreen screen, MapObject object) {
		super(screen, object);
		tileSet = map.getTileSets().getTileSet("tileset_gutter");
		fixture.setUserData(this);
		setCategoryFilter(Mario.COIN_BIT);
	}

	@Override
	public void onHeadHit(MarioSprite mario) {
		if (getCell().getTile().getId() == BLANK_COIN) {
			Mario.manager.get("audio/sounds/AUGH.wav", Sound.class).play(1.0f);
		} else {
			Mario.manager.get("audio/sounds/undertaker.wav", Sound.class).play(2.0f);
			if (object.getProperties().containsKey("mushroom")) {
			screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x,  body.getPosition().y + 16 / Mario.PPM), Mushroom.class));
		
			}}
		getCell().setTile(tileSet.getTile(BLANK_COIN));
		HUD.addScore(1000);
	}

}
