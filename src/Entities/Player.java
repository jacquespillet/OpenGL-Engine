package Entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import Models.TexturedModel;
import Terrains.Terrain;
import renderEngine.DisplayManager;

public class Player extends Entity {
	
	private static final float RUN_SPEED = 20;
	private static final float TURN_SPEED = 160;
	public static final float GRAVITY = -50;
	private static final float JUMP_POWER = 30;
	private static final float TERRAIN_POS = 0;
	
	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;
	private float upwardsSpeed= 0;
	
	private boolean isInAir = false;
	
	public Player(TexturedModel model, Vector3f position, Vector3f rot, float scale) {
		super(model, position, rot, scale);
	}
	
	public void move(Terrain terrain) {
		checkInputs();
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
		
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRot().y)));
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRot().y)));
		
		super.increasePosition(dx,  0,  dz);
		
		float terrainHeight = terrain.getHeight(super.getPosition().x, super.getPosition().z);
		
		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
		super.increasePosition(0,  upwardsSpeed * DisplayManager.getFrameTimeSeconds(), dz);
		if(super.getPosition().y < terrainHeight) {
			upwardsSpeed = 0;
			super.getPosition().y = terrainHeight;
			isInAir = false;
		}
	}
	
	private void jump() {
		if(!isInAir) {
			isInAir = true;
			this.upwardsSpeed = JUMP_POWER;
		}
	}
	
	private void checkInputs() {
		if(Keyboard.isKeyDown(Keyboard.KEY_Z)) {
			this.currentSpeed = RUN_SPEED;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			this.currentSpeed = -RUN_SPEED;
		} else {
			this.currentSpeed = 0;
		}
		

		if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			currentTurnSpeed = -TURN_SPEED;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_Q)) {
			currentTurnSpeed = TURN_SPEED;
		} else {
			currentTurnSpeed = 0;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			jump();
		}
	}
}
