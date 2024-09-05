package com.ismail.mario.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.ismail.mario.Mario;
import com.ismail.mario.Sprites.Enemy;
import com.ismail.mario.Sprites.InteractiveTileObject;

public class worldContactListener implements ContactListener{

	@Override
	public void beginContact(Contact contact) {
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();
		
		int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
		
		if (fixA.getUserData() == "head" || fixB.getUserData() == "head" ) {
			
			Fixture head = fixA.getUserData() == "head" ? fixA : fixB;
			Fixture object = head == fixA ? fixB : fixA;
			
			if (object.getUserData() != null && object.getUserData() instanceof InteractiveTileObject) {
				((InteractiveTileObject) object.getUserData()).onHeadHit();
			}
		}
		
		switch (cDef) {
		case Mario.ENEMY_HEAD_BIT | Mario.MARIO_BIT: {
			if(fixA.getFilterData().categoryBits == Mario.ENEMY_HEAD_BIT) {
				((Enemy) fixA.getUserData()).hitOnHead();
			} else {
				((Enemy) fixB.getUserData()).hitOnHead();
			}
			break;
		}
		case Mario.ENEMY_BIT | Mario.OBJECT_BIT: {
			if(fixA.getFilterData().categoryBits == Mario.ENEMY_BIT) {
				((Enemy) fixA.getUserData()).reverseVelocity(true, false);;
			} else {
				((Enemy) fixB.getUserData()).reverseVelocity(true, false);;
			}
			break;
		}
		case Mario.MARIO_BIT | Mario.ENEMY_BIT: {
			Gdx.app.log("mario", "dead");
			break;
		}
		case Mario.ENEMY_BIT | Mario.ENEMY_BIT: {
			((Enemy) fixA.getUserData()).reverseVelocity(true, false);;
			((Enemy) fixB.getUserData()).reverseVelocity(true, false);;
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
