package particles;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import Entities.Camera;
import Entities.Player;
import renderEngine.DisplayManager;

public class Particle {
	private Vector3f position;
	private Vector3f velocity;
	private float gravityEffect;
	private float lifeLength;
	private float rotation;
	private float scale;
	
	private ParticleTexture texture;
	private Vector2f texOffset1 = new Vector2f();
	private Vector2f texOffset2 = new Vector2f();
	private float blendFactor;
	
	private float distance;
	
	
	private float elapsedTime = 0;

	public Particle(Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float rotation,
			float scale, ParticleTexture texture) {
		super();
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.texture = texture;
		this.scale = scale;
		
		ParticleMaster.addParticle(this);
	}

	
	public Vector2f getTexOffset1() {
		return texOffset1;
	}


	public float getDistance() {
		return distance;
	}


	public void setDistance(float distance) {
		this.distance = distance;
	}


	public Vector2f getTexOffset2() {
		return texOffset2;
	}


	public float getBlendFactor() {
		return blendFactor;
	}


	public ParticleTexture getTexture() {
		return texture;
	}


	public Vector3f getPosition() {
		return position;
	}

	public float getRotation() {
		return rotation;
	}

	public float getScale() {
		return scale;
	}
	
	protected boolean update(Camera camera) {
		velocity.y += Player.GRAVITY * gravityEffect * DisplayManager.getFrameTimeSeconds();
		Vector3f change = new Vector3f(velocity);
		change.scale(DisplayManager.getFrameTimeSeconds());
		Vector3f.add(change,  position,  position);
		updateTextureCoordInfos();
		
		distance = Vector3f.sub(camera.getPosition(),  position,  null).lengthSquared();
		
		elapsedTime += DisplayManager.getFrameTimeSeconds();
		return elapsedTime < lifeLength;
	}
	
	private void updateTextureCoordInfos() {
		float lifeFactor = elapsedTime / lifeLength;
		int stageCount = texture.getNumberOfRows() * texture.getNumberOfRows();
		float atlasProgression = lifeFactor * stageCount;
		int index1 = (int) Math.floor(atlasProgression);
		int index2 = index1 < stageCount -1 ? index1 +1 : index1;
		
		this.blendFactor = atlasProgression % 1;
		setTextureOffset(texOffset1, index1);
		setTextureOffset(texOffset2, index2);
	}
	
	private void setTextureOffset(Vector2f offset, int index) {
		int column = index % texture.getNumberOfRows();
		int row = index / texture.getNumberOfRows();
		offset.x = (float) column / texture.getNumberOfRows();
		offset.y = (float) row / texture.getNumberOfRows();
	}

}
