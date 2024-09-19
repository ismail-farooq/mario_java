package com.ismail.mario.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ismail.mario.Mario;
import com.ismail.mario.Screens.PlayScreen;
import com.ismail.mario.Sprites.Brick;
import com.ismail.mario.Sprites.Coin;
import com.ismail.mario.Sprites.Enemy;
import com.ismail.mario.Sprites.Goomba;
import com.ismail.mario.Sprites.Turtle;

public class B2WorldCreator {
	
	private Array<Goomba> goombas;
	private Array<Turtle> turtles;
	
	public B2WorldCreator(PlayScreen screen) {
		World world = screen.getWorld();
		TiledMap map = screen.getMap();
		BodyDef bdef = new BodyDef();
		PolygonShape shape = new PolygonShape();
		FixtureDef fdef = new FixtureDef();
		Body body;
		
		
		
		//ground
		for (MapObject object: map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rectangle = ((RectangleMapObject) object).getRectangle();	
			
			bdef.type = BodyDef.BodyType.StaticBody;
			bdef.position.set((rectangle.getX() + rectangle.getWidth()/2)/ Mario.PPM, (rectangle.getY() + rectangle.getHeight()/2)/ Mario.PPM);
			
			body = world.createBody(bdef);
			
			shape.setAsBox(rectangle.getWidth()/2/ Mario.PPM, rectangle.getHeight()/2/ Mario.PPM);
			fdef.shape = shape;
			body.createFixture(fdef);
		}
		
		//pipe
		for (MapObject object: map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rectangle = ((RectangleMapObject) object).getRectangle();	
			
			bdef.type = BodyDef.BodyType.StaticBody;
			bdef.position.set((rectangle.getX() + rectangle.getWidth()/2)/ Mario.PPM, (rectangle.getY() + rectangle.getHeight()/2)/ Mario.PPM);
			
			body = world.createBody(bdef);
			
			shape.setAsBox(rectangle.getWidth()/2/ Mario.PPM, rectangle.getHeight()/2/ Mario.PPM);
			fdef.shape = shape;
			fdef.filter.categoryBits = Mario.OBJECT_BIT;
			body.createFixture(fdef);
		}
		
		//bricks
		for (MapObject object: map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {	
			new Brick(screen, object);
		}
		
		//coins
		for (MapObject object: map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
			new Coin(screen, object);
		}
		
		//goombas
		goombas = new Array<Goomba>();
		for (MapObject object: map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rectangle = ((RectangleMapObject) object).getRectangle();	
			goombas.add(new Goomba(screen, rectangle.getX() / Mario.PPM, rectangle.getY()/ Mario.PPM));
		}
		
		//turtles
		turtles = new Array<Turtle>();
		for (MapObject object: map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rectangle = ((RectangleMapObject) object).getRectangle();	
			turtles.add(new Turtle(screen, rectangle.getX() / Mario.PPM, rectangle.getY()/ Mario.PPM));
		}
		
	}
	
	public Array<Goomba> getGoobas(){
		return goombas;
	}
	
	public Array<Enemy> getEnemies() {
		Array<Enemy> enemies = new Array<Enemy>();
		enemies.addAll(goombas);
		enemies.addAll(turtles);
		return enemies;
	}

}
