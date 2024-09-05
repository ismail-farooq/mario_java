package com.ismail.mario.Sprites;

import java.util.Iterator;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ismail.mario.Mario;
import com.ismail.mario.Screens.PlayScreen;

public class MarioSprite extends Sprite{
	
	public enum State { FALLING, JUMPING, STANDING, RUNNING };
	public State currentState;
	public State previousState;
	public World world;
	public Body b2body;
	private TextureRegion marioStand;
	private Animation<TextureRegion> marioJump;
	private Animation<TextureRegion> marioRun;
	private float stateTimer;
	private boolean runningRight;
	
	
	public MarioSprite(PlayScreen screen) {
		super(screen.getAtlas().findRegion("little_mario"));
		this.world = screen.getWorld();
		
		currentState = State.STANDING;
		previousState = State.STANDING;
		stateTimer = 0;
		runningRight = true;
		
		Array<TextureRegion> frames = new Array<TextureRegion>();
		for (int i = 1; i < 4; i++) {
			frames.add(new TextureRegion(getTexture(), i*16, 11, 16, 16));
		}
		marioRun = new Animation<TextureRegion>(0.1f, frames);
		frames.clear();
		
		for (int i = 4; i < 6; i++) {
			frames.add(new TextureRegion(getTexture(), i*16, 11, 16, 16));
		}
		marioJump = new Animation<TextureRegion>(0.1f, frames);
		frames.clear();
		
		
		defineMario();
		marioStand = new TextureRegion(getTexture(), 1,11,16,16);
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
		fdef.filter.maskBits = Mario.GROUND_BIT | Mario.COIN_BIT | Mario.BRICK_BIT | Mario.ENEMY_BIT | Mario.OBJECT_BIT | Mario.ENEMY_HEAD_BIT;
		
		
		
		fdef.shape = shape;
		b2body.createFixture(fdef);
		
		EdgeShape head = new EdgeShape();
		head.set(new Vector2(-2/Mario.PPM, 6/Mario.PPM), new Vector2(2/Mario.PPM, 6 /Mario.PPM));
		fdef.shape = head;
		fdef.isSensor = true;
		
		b2body.createFixture(fdef).setUserData("head");
	}

	public void update(float dt) {
		setPosition(b2body.getPosition().x - getWidth()/2, b2body.getPosition().y - getHeight()/2);
		setRegion(getFrame(dt));
	}

	private TextureRegion getFrame(float dt) {
		currentState = getState();
		TextureRegion region;
		switch (currentState) {
		case JUMPING: {
			region = marioJump.getKeyFrame(stateTimer);
			break;
		}
		case RUNNING: {
			region = marioRun.getKeyFrame(stateTimer, true);
			break;
		}
		case STANDING:
		case FALLING:
		default:
			region = marioStand;
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

	private State getState() {
		if (b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING)) {
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
}
