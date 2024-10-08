package com.ismail.mario.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.ismail.mario.Mario;
import com.ismail.mario.Screens.PlayScreen;

public class Goomba extends Enemy{
	
	private float stateTime;
	private Animation<TextureRegion> walkAnimation;
	private Array<TextureRegion> frames;
	private boolean setToDestroy;
	private boolean destroyed;
	
	public Goomba(PlayScreen screen, float x, float y) {
		super(screen, x, y);
		frames = new Array<TextureRegion>();
		for (int i = 0; i < 2; i++) {
			frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i*16, 0, 16, 16));
		}
		walkAnimation = new Animation<TextureRegion>(0.4f, frames);
		stateTime = 0;
		setBounds(getX(), getY(), 16 / Mario.PPM, 16 / Mario.PPM);
		setToDestroy = false;
		destroyed = false;
	}
	
	public void update(float dt) {
		stateTime += dt;
		if (setToDestroy && !destroyed) {
			world.destroyBody(b2body);
			destroyed = true;
			setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16));
			stateTime = 0;
			
			
		} else if(!destroyed) {
			b2body.setLinearVelocity(velocity);
			setPosition(b2body.getPosition().x - getWidth()/2, b2body.getPosition().y - getHeight()/2);
			setRegion(walkAnimation.getKeyFrame(stateTime, true));
		}

	}

	@Override
	protected void defineEnemy() {
		BodyDef bdef = new BodyDef();
		bdef.position.set(getX(), getY());
		bdef.type = BodyDef.BodyType.DynamicBody;
		
		b2body = world.createBody(bdef);
		
		FixtureDef fdef = new FixtureDef();
		CircleShape shape = new CircleShape();
		shape.setRadius(7 / Mario.PPM);
		fdef.filter.categoryBits = Mario.ENEMY_BIT;
		fdef.filter.maskBits = Mario.GROUND_BIT |
                Mario.COIN_BIT |
                Mario.BRICK_BIT |
                Mario.ENEMY_BIT |
                Mario.OBJECT_BIT |
                Mario.MARIO_BIT;		
		
		
		fdef.shape = shape;
		b2body.createFixture(fdef).setUserData(this);
		
		//head
		PolygonShape head = new PolygonShape();
		Vector2[] vertice = new Vector2[4];
		 vertice[0] = new Vector2(-7, 12).scl(1 / Mario.PPM);  // Increase the x and y values to enlarge the head
		    vertice[1] = new Vector2(7, 12).scl(1 / Mario.PPM);
		    vertice[2] = new Vector2(-7, 4).scl(1 / Mario.PPM);   // These two values control the bottom of the head fixture
		    vertice[3] = new Vector2(7, 4).scl(1 / Mario.PPM);
		head.set(vertice);
		
		fdef.shape = head;
		fdef.restitution = 1f;
		fdef.filter.categoryBits = Mario.ENEMY_HEAD_BIT;
		b2body.createFixture(fdef).setUserData(this);

		
	}

	public void draw(Batch batch) {
		if (!destroyed || stateTime < 1) {
			super.draw(batch);
		}
	}
	
	@Override
	public void hitOnHead(MarioSprite mario) {
		setToDestroy = true;
		Mario.manager.get("audio/sounds/sniper.wav", Sound.class).play(0.5f);
	}

	@Override
	public void onEnemyHit(Enemy enemy) {
		if (enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.MOVING_SHELL) {
			setToDestroy = true;
		} else {
			reverseVelocity(true, false);
		}
	}
}
