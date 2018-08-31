package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Models.TexturedModel;
import Shaders.StaticShader;
import Shaders.TerrainShader;
import Terrains.Terrain;
import normalMappingRenderer.NormalMappingRenderer;
import shadows.ShadowMapMasterRenderer;
import skybox.SkyboxRenderer;

public class MasterRenderer {

	public static final float FOV = 70;
	public static final float NEAR_PLANE= 0.1f;
	public static final float FAR_PLANE = 1000;
	public static final float RED = 0.5f;
	public static final float GREEN = 0.5f;
	public static final float BLUE = 0.6f;
	
	private Matrix4f projectionMatrix;
	
	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;
	
	private TerrainShader terrainShader = new TerrainShader();
	private TerrainRenderer terrainRenderer;
	
	private NormalMappingRenderer normalMapRenderer;
	
	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private Map<TexturedModel, List<Entity>> normalMappedEntities = new HashMap<TexturedModel, List<Entity>>();
	private List<Terrain> terrains = new ArrayList<Terrain>();
	
	private SkyboxRenderer skyboxRenderer;
	private ShadowMapMasterRenderer shadowMapRenderer;
	
	public MasterRenderer(ModelLoader loader, Camera camera) {
		enableCulling();
		createProjectionMatrix();
		renderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
		skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
		normalMapRenderer = new NormalMappingRenderer(projectionMatrix);
		shadowMapRenderer = new ShadowMapMasterRenderer(camera);
	}
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	
	public void renderScene(List<Entity> entities, List<Entity> normalMappedEntities, List<Terrain> terrains, List<Light> lights, Camera camera, Vector4f clipPlane) {
		for(Terrain terrain:terrains) {
			processTerrain(terrain);
		}
		for(Entity entity:entities) {
			processEntity(entity);
		}
		for(Entity entity:normalMappedEntities) {
			processNormalMappedEntity(entity);;
		}
		render(lights, camera, clipPlane);
	}
	
	public void render(List<Light> lights, Camera camera, Vector4f clipPlane) {
		this.prepare();
		
		shader.start();
		shader.loadClipPlane(clipPlane);
		shader.loadSkyColor(new Vector3f(RED, GREEN, BLUE));
		shader.loadLights(lights);
		shader.loadViewMatrix(camera);
		renderer.render(entities);
		shader.stop();
		
		normalMapRenderer.render(normalMappedEntities, clipPlane, lights, camera);
		
		terrainShader.start();
		terrainShader.loadClipPlane(clipPlane);
		terrainShader.loadLights(lights);
		terrainShader.loadViewMatrix(camera);
		terrainShader.loadSkyColor(new Vector3f(RED, GREEN, BLUE));
		terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());
		terrainShader.stop();
		
		skyboxRenderer.render(camera, new Vector3f(RED, GREEN, BLUE));
		
		terrains.clear();
		entities.clear();
		normalMappedEntities.clear();
	}
	
	public void processTerrain(Terrain terrain) {
		terrains.add(terrain);
	}
	
	public void processEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if(batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}

	public void processNormalMappedEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = normalMappedEntities.get(entityModel);
		if(batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			normalMappedEntities.put(entityModel, newBatch);
		}
	}
	
	public void renderShadowMap(List<Entity> entityList, Light sun) {
		for(Entity entity: entityList) {
			processEntity(entity);
		}
		shadowMapRenderer.render(entities, sun);
		entities.clear();
	}
	
	public int getShadowMapTexture() {
		return shadowMapRenderer.getShadowMap();
	}
	
	
	public void clean() {
		shader.clean();
		terrainShader.clean();
		normalMapRenderer.cleanUp();
		shadowMapRenderer.cleanUp();
	}
	
	
	public void prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(RED,  GREEN,  BLUE,  1);
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D,  getShadowMapTexture());
	}
	
	private void createProjectionMatrix() {
		projectionMatrix = new Matrix4f();

		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustrum_length = FAR_PLANE - NEAR_PLANE;
		
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustrum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustrum_length);
		projectionMatrix.m33 = 0;
		
	}
	
	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
}
