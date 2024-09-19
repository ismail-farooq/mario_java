package com.ismail.mario.Sprites;

import java.util.Iterator;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ismail.mario.Mario;
import com.ismail.mario.Screens.PlayScreen;

public class MarioSprite extends Sprite{
	
	public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD};
	public State currentState;
	public State previousState;
	
	public World world;
	public Body b2body;
	
	private TextureRegion marioStand;
	private TextureRegion marioJump;
	private TextureRegion marioDead;
	private Animation<TextureRegion> marioRun;
	private TextureRegion bigMarioStand;
	private TextureRegion bigMarioJump;
	private Animation<TextureRegion> bigMarioRun;
	private Animation<TextureRegion> growMario;

	
	private float stateTimer;
	private boolean runningRight;
	
	private boolean isBig;
	private boolean runGrowAnimation;
	private boolean toDefineBigMario;
	private boolean toRedefineBigMario;
	private boolean isDead;
	
	
	public MarioSprite(PlayScreen screen) { //Constructor
		super(screen.getAtlas().findRegion("little_mario"));
		this.world = screen.getWorld();
		
		currentState = State.STANDING;
		previousState = State.STANDING;
		stateTimer = 0;
		runningRight = true;
		
		Array<TextureRegion> frames = new Array<TextureRegion>();
		
		//run animation
		for (int i = 1; i < 4; i++) {
			frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i*16, 0, 16, 16));
		}
		marioRun = new Animation<TextureRegion>(0.1f, frames);
		frames.clear();
		
		for (int i = 1; i < 4; i++) {
			frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i*16, 0, 16, 32));
		}
		bigMarioRun = new Animation<TextureRegion>(0.1f, frames);
		frames.clear();
		
		marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80,0,16,16);
		bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80,0,16,32);

		frames.clear();
		
		//animation to grow mario
		frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
		frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
		frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
		frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
		growMario = new Animation<>(0.2f, frames);

		
		//jump animation
		marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0,0,16,16);
		bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0,0,16,32);
		
		//dead animation
		marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96,0,16,16);
		
		
		defineMario();
		setBounds(0, 0, 16 / Mario.PPM, 16 / Mario.PPM);
		setRegion(marioStand);
	}

	private void defineMario() {
		BodyDef bdef = new BodyDef();
		bdef.position.set(32 / Mario.PPM, 32 / Mario.PPM);
		bdef.type = BodyDef.BodyType.DynamicBody;
		
		b2body = world.createBody(bdef);
		
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(7 / Mario.PPM);
		fdef.filter.categoryBits = Mario.MARIO_BIT;
		fdef.filter.maskBits = Mario.GROUND_BIT | Mario.COIN_BIT | Mario.BRICK_BIT | Mario.ENEMY_BIT | Mario.OBJECT_BIT | Mario.ENEMY_HEAD_BIT | Mario.ITEM_BIT;
		
		fdef.shape = shape;
		b2body.createFixture(fdef).setUserData(this);
		
		EdgeShape head = new EdgeShape();
		head.set(new Vector2(-2/Mario.PPM, 6/Mario.PPM), new Vector2(2/Mario.PPM, 6 /Mario.PPM));
		fdef.filter.categoryBits = Mario.MARIO_HEAD_BIT;
		fdef.shape = head;
		fdef.isSensor = true;
		
		b2body.createFixture(fdef).setUserData(this);
	}

	public void update(float dt) {
		if (isBig) {
			setPosition(b2body.getPosition().x - getWidth()/2, b2body.getPosition().y - getHeight()/2 - 6/Mario.PPM);
		} else {
			setPosition(b2body.getPosition().x - getWidth()/2, b2body.getPosition().y - getHeight()/2);
		}
		setRegion(getFrame(dt));
		
		if (toDefineBigMario) {
			defineBigMario();
		}
		if (toRedefineBigMario) {
			redefineMario();
		}
		 if (b2body.getPosition().y < -15 / Mario.PPM && !isDead()) {
		        hit(null);  // Trigger Mario's death
		    }
	}

	private void redefineMario() {
		Vector2 position = b2body.getPosition();
		world.destroyBody(b2body);
		
		BodyDef bdef = new BodyDef();
		bdef.position.set(position);
		bdef.type = BodyDef.BodyType.DynamicBody;
		
		b2body = world.createBody(bdef);
		
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(7 / Mario.PPM);
		fdef.filter.categoryBits = Mario.MARIO_BIT;
		fdef.filter.maskBits = Mario.GROUND_BIT | Mario.COIN_BIT | Mario.BRICK_BIT | Mario.ENEMY_BIT | Mario.OBJECT_BIT | Mario.ENEMY_HEAD_BIT | Mario.ITEM_BIT;
		
		fdef.shape = shape;
		b2body.createFixture(fdef).setUserData(this);
		
		EdgeShape head = new EdgeShape();
		head.set(new Vector2(-2/Mario.PPM, 6/Mario.PPM), new Vector2(2/Mario.PPM, 6 /Mario.PPM));
		fdef.filter.categoryBits = Mario.MARIO_HEAD_BIT;
		fdef.shape = head;
		fdef.isSensor = true;
		
		b2body.createFixture(fdef).setUserData(this);
		toRedefineBigMario = false;
	}

	private void defineBigMario() {
		// TODO Auto-generated method stub
		Vector2 currentPosition = b2body.getPosition();
		world.destroyBody(b2body);
		
		BodyDef bdef = new BodyDef();
		bdef.position.set(currentPosition.add(0, 10/Mario.PPM));
		bdef.type = BodyDef.BodyType.DynamicBody;
		
		b2body = world.createBody(bdef);
		
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(7 / Mario.PPM);
		fdef.filter.categoryBits = Mario.MARIO_BIT;
		fdef.filter.maskBits = Mario.GROUND_BIT | Mario.COIN_BIT | Mario.BRICK_BIT | Mario.ENEMY_BIT | Mario.OBJECT_BIT | Mario.ENEMY_HEAD_BIT | Mario.ITEM_BIT;
		
		fdef.shape = shape;
		b2body.createFixture(fdef).setUserData(this);
		shape.setPosition(new Vector2(0, -14/Mario.PPM));
		b2body.createFixture(fdef).setUserData(this);
		
		EdgeShape head = new EdgeShape();
		head.set(new Vector2(-2/Mario.PPM, 6/Mario.PPM), new Vector2(2/Mario.PPM, 6 /Mario.PPM));
		fdef.filter.categoryBits = Mario.MARIO_HEAD_BIT;
		fdef.shape = head;
		fdef.isSensor = true;
		
		b2body.createFixture(fdef).setUserData(this);
		toDefineBigMario = false;
	}

	private TextureRegion getFrame(float dt) {
		currentState = getState();
		TextureRegion region;
		switch (currentState) {
		case DEAD: {
			region = marioDead;
			break;
		}
		case GROWING: {
			region = growMario.getKeyFrame(stateTimer);
			if (growMario.isAnimationFinished(stateTimer)) {
				runGrowAnimation = false;
			}
			break;
		}
		case JUMPING: {
			region = isBig ? bigMarioJump : marioJump;
			break;
		}
		case RUNNING: {
			region = isBig ?  bigMarioRun.getKeyFrame(stateTimer, true) : marioRun.getKeyFrame(stateTimer, true);
			break;
		}
		case STANDING:
		case FALLING:
		default:
			region = isBig ? bigMarioStand: marioStand;
			break;
		}
		
		if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX() ){
			region.flip(true, false);
			runningRight = false;
		}
		else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX() ){
			region.flip(true, false);
			runningRight = true;
		}
	
		stateTimer = currentState == previousState ? stateTimer + dt : 0;
		previousState = currentState;
		return region;
	}
	
	public void grow() {
		runGrowAnimation = true;
		isBig = true;
		toDefineBigMario = true;
		setBounds(getX(), getY(), getWidth(), getHeight()* 2);
	}

	private State getState() {
		if (isDead) {
			return State.DEAD;
		}
		else if(runGrowAnimation) {
			return State.GROWING;
		}
		
		else if (b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING)) {
			return State.JUMPING;
		}
		else if (b2body.getLinearVelocity().y < 0) {
			return State.FALLING;
		}
		else if (b2body.getLinearVelocity().x != 0) {
			return State.RUNNING;
		}
		else {
			return State.STANDING;
		}
	}

	public boolean isBig() {
		return isBig;
	}

	public void hit(Enemy enemy) {
		if (enemy instanceof Turtle && ((Turtle) enemy).getState() == Turtle.State.STANDING_SHELL) {
			((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
		} else {
			if (isBig) {
				Mario.manager.get("audio/sounds/roblox.wav", Sound.class).play(1.0f);
				isBig = false;
				toRedefineBigMario = true;
				setBounds(getX(), getY(), getWidth(), getHeight()/2);
			} else {
				Mario.manager.get("audio/sounds/roblox.wav", Sound.class).play(1.0f);
				Mario.manager.get("audio/music/shumela.ogg", Music.class).stop();
				isDead = true;
				Filter filter = new Filter();
				filter.maskBits = Mario.NOTHING_BIT;
				for (Fixture fixture : b2body.getFixtureList()) {
					fixture.setFilterData(filter);
				}
				b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
			}
		}
	}
	

	public boolean isDead() {
		return isDead;
	}
	
	public float getStateTime() {
		return stateTimer;
	}
}
