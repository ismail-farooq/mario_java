package com.ismail.mario.Screens;

import java.util.LinkedList;
import java.util.Queue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ismail.mario.Mario;
import com.ismail.mario.Scenes.HUD;
import com.ismail.mario.Sprites.Enemy;
import com.ismail.mario.Sprites.Goomba;
import com.ismail.mario.Sprites.Item;
import com.ismail.mario.Sprites.ItemDef;
import com.ismail.mario.Sprites.MarioSprite;
import com.ismail.mario.Sprites.MarioSprite.State;
import com.ismail.mario.Sprites.Mushroom;
import com.ismail.mario.Tools.B2WorldCreator;
import com.ismail.mario.Tools.worldContactListener;

public class PlayScreen implements Screen{
	
	private Mario game;
	private TextureAtlas atlas;
	
	private OrthographicCamera gameCam;
	private Viewport viewPort;
	private HUD hud;
	
	//Tiled map
	private TmxMapLoader mapLoader;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	
	//Box2d
	private World world;
	private Box2DDebugRenderer b2dr;
	private B2WorldCreator creator;
	
	//mario
	private MarioSprite player;
	 
	//music
	private Music music;
	
	private Array<Item> items;
	private Queue<ItemDef> itemsToSpawn;
	
	public PlayScreen(Mario game) {
		atlas = new TextureAtlas("Mario_and_Enemies.pack");
		
		this.game = game;
		gameCam = new OrthographicCamera();
		
		//screen view aspect ratio
		viewPort = new FitViewport(Mario.V_WIDTH / Mario.PPM, Mario.V_HEIGHT / Mario.PPM, gameCam);
		
		//hud
		hud = new HUD(game.batch);
		
		//Load map
		mapLoader = new TmxMapLoader();
		map = mapLoader.load("level3.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 1/ Mario.PPM);
		
		gameCam.position.set(viewPort.getWorldWidth()/2 , viewPort.getWorldHeight() /2, 0);
		
		world = new World(new Vector2(0,-10), true);
		b2dr = new Box2DDebugRenderer();
		
		creator = new B2WorldCreator(this);
		
		player = new MarioSprite(this);
		
		world.setContactListener(new worldContactListener());
		
		music = Mario.manager.get("audio/music/eldenring.ogg", Music.class);
		music.setLooping(true);
		music.play();
		
		items = new Array<Item>();
		itemsToSpawn = new LinkedList<ItemDef>();
	}
	
	public void spawnItem(ItemDef idef) {
		itemsToSpawn.add(idef);
	}

	public void handleSpawningItems() {
		if (!itemsToSpawn.isEmpty()) {
			ItemDef idef = itemsToSpawn.poll();
			if (idef.type == Mushroom.class) {
				items.add(new Mushroom(this, idef.position.x, idef.position.y));
			}
		}
	}
	
	public TextureAtlas getAtlas() {
		
		return atlas;
		
	}
	
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}
	
	public void update(float dt) {
		handleInput(dt);
		handleSpawningItems();
		
		world.step(1/60f, 6, 2);
		
		player.update(dt);
		for (Enemy enemy: creator.getEnemies()) {
			enemy.update(dt);		
			if (enemy.getX() < player.getX() + 224/Mario.PPM) {
				enemy.b2body.setActive(true);	
			}
		}
		
		for (Item item : items) {
			item.update(dt);
		}
		
		hud.update(dt);
		
		if (player.currentState != State.DEAD) {
			gameCam.position.x = player.b2body.getPosition().x;	
		}
		gameCam.update();
		renderer.setView(gameCam);
	}

	private void handleInput(float dt) {
		
		if (player.currentState != State.DEAD) {
			if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
				player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
			}
			
			if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity() .x <= 2) {
				player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
			}
			if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity() .x >= -2) {
				player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
			}
		}
	}

	@Override
	public void render(float delta) {
			
		update(delta);
		
		// clear screen w black colour
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//render game map
		renderer.render();

		//render box2ddebuglines
		b2dr.render(world, gameCam.combined);
		
		game.batch.setProjectionMatrix(gameCam.combined);
		game.batch.begin();
		player.draw(game.batch);
		for (Enemy enemy: creator.getEnemies()) {
			enemy.draw(game.batch);			
		}
		for (Item item: items) {
			item.draw(game.batch);			
		}
		game.batch.end();
		
		game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
		hud.stage.draw();

		if (gameOver()) {
			game.setScreen(new GameOverScreen(game));
			dispose();
		}
	}
	
	public boolean gameOver() {
		if (player.currentState == State.DEAD && player.getStateTime() > 3) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		viewPort.update(width, height);
	}
	
	public TiledMap getMap() {
		return map;
	}
	
	public World getWorld() {
		return world;
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		map.dispose();
		renderer.dispose();
		world.dispose();
		b2dr.dispose();
		hud.dispose();		
	}

}
