package com.ismail.mario.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.ismail.mario.Mario;
import com.ismail.mario.Sprites.Enemy;
import com.ismail.mario.Sprites.InteractiveTileObject;
import com.ismail.mario.Sprites.Item;
import com.ismail.mario.Sprites.MarioSprite;

public class worldContactListener implements ContactListener{

	@Override
	public void beginContact(Contact contact) {
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();
		
		int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
		
		switch (cDef) {
		case Mario.MARIO_HEAD_BIT | Mario.BRICK_BIT: 
		case Mario.MARIO_HEAD_BIT | Mario.COIN_BIT: {
			if(fixA.getFilterData().categoryBits == Mario.MARIO_HEAD_BIT) {
				((InteractiveTileObject) fixB.getUserData()).onHeadHit((MarioSprite) fixA.getUserData());
			} else {
				((InteractiveTileObject) fixA.getUserData()).onHeadHit((MarioSprite) fixB.getUserData());
			}
			break;
		}
		case Mario.ENEMY_HEAD_BIT | Mario.MARIO_BIT: {
			if(fixA.getFilterData().categoryBits == Mario.ENEMY_HEAD_BIT) {
				((Enemy) fixA.getUserData()).hitOnHead((MarioSprite) fixB.getUserData());
			} else {
				((Enemy) fixB.getUserData()).hitOnHead((MarioSprite) fixA.getUserData());
			}
			break;
		}
		case Mario.ENEMY_BIT | Mario.OBJECT_BIT: {
			if(fixA.getFilterData().categoryBits == Mario.ENEMY_BIT) {
				((Enemy) fixA.getUserData()).reverseVelocity(true, false);
			} else {
				((Enemy) fixB.getUserData()).reverseVelocity(true, false);
			}
			break;
		}
		case Mario.MARIO_BIT | Mario.ENEMY_BIT: {
			if(fixA.getFilterData().categoryBits == Mario.MARIO_BIT) {
				((MarioSprite) fixA.getUserData()).hit((Enemy) fixB.getUserData());
			} else {
				((MarioSprite) fixB.getUserData()).hit((Enemy) fixA.getUserData());
			}
			break;
		}
		case Mario.ENEMY_BIT | Mario.ENEMY_BIT: {
			((Enemy) fixA.getUserData()).onEnemyHit((Enemy) fixB.getUserData());
			((Enemy) fixB.getUserData()).onEnemyHit((Enemy) fixB.getUserData());
			break;
		}
		case Mario.ITEM_BIT | Mario.OBJECT_BIT: {
			if(fixA.getFilterData().categoryBits == Mario.ITEM_BIT) {
				((Item) fixA.getUserData()).reverseVelocity(true, false);
			} else {
				((Item) fixB.getUserData()).reverseVelocity(true, false);
			}
			break;
		}
		case Mario.ITEM_BIT | Mario.MARIO_BIT: {
			if(fixA.getFilterData().categoryBits == Mario.ITEM_BIT) {
				((Item) fixA.getUserData()).use((MarioSprite) fixB.getUserData());
			} else {
				if(!(fixA.getUserData() instanceof String)) {
				((Item) fixB.getUserData()).use((MarioSprite) fixA.getUserData());}
			}
			break;
		}
	}
}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
