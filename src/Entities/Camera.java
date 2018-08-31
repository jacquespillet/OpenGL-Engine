package Entities;


import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	private float distanceFromPlayer = 50;
	private float angleAroundPlayer = 0;
	
	private Vector3f position = new Vector3f(100, 50.0f, 100.0f);
	private float pitch;
	private float yaw;
	private float roll;
	
	private Player player;
	
	public Camera(Player player) {
		this.player = player;
		pitch = 45;
	}

	public Vector3f getPosition() {
		return position;
	}
	
	public void invertPitch() {
		this.pitch = - pitch;
	}
	
	public void move() {
		calculateZoom();
		calculateAngleAround();
		calculatePitch();
		
		float horizDistance = getHorizDistance();
		float verticDistance = getVerticDistance();
		computeCameraPos(horizDistance, verticDistance);
		
	}
	
	private float getHorizDistance() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}
	
	private float getVerticDistance() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}
	
	private void computeCameraPos(float horizDistance, float verticDistance) {
		float theta = player.getRot().y + angleAroundPlayer;
		float xOffset = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		float zOffset = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
		position.y = player.getPosition().y + verticDistance;
		position.x = player.getPosition().x - xOffset;
		position.z = player.getPosition().z - zOffset;
		
		yaw = 180 - theta;
	}
	
	public float getPitch() {
		return pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public float getRoll() {
		return roll;
	};
	
	private void calculateZoom() {
		float zoomLevel = Mouse.getDWheel() * 0.1f;
		distanceFromPlayer -= zoomLevel;
	}
	
	private void calculatePitch() {
		if(Mouse.isButtonDown(1)) {
			float pitchChange = Mouse.getDY() * 0.1f;
			pitch -= pitchChange;
		}
	}
	
	private void calculateAngleAround() {
		if(Mouse.isButtonDown(0)) {
			float angle = Mouse.getDX() * 0.1f;
			angleAroundPlayer -= angle;
		}
	}
	
	
}
