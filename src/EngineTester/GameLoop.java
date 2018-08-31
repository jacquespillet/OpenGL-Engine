package EngineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import Entities.Camera;
import Entities.Entity;
import Entities.Light;
import Entities.Player;
import Models.RawModel;
import Models.TexturedModel;
import ObjLoader.ModelData;
import ObjLoader.OBJFileLoader;
import Shaders.StaticShader;
import Terrains.Terrain;
import Textures.ModelTexture;
import Textures.TerrainTexture;
import Textures.TerrainTexturePack;
import Toolbox.MousePicker;
import guis.GuiRenderer;
import guis.GuiTexture;
import normalMappingObjConverter.NormalMappedObjLoader;
import particles.Particle;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import renderEngine.DisplayManager;
import renderEngine.MasterRenderer;
import renderEngine.ModelLoader;
import water.WaterFrameBuffer;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;
import renderEngine.EntityRenderer;

public class GameLoop {

	public static void main(String[] args) {
		
		/// GENERAL STUFF
		DisplayManager.createDisplay();
		ModelLoader loader = new ModelLoader();
		List<Entity> entities = new ArrayList<Entity>();

		ModelData data = OBJFileLoader.loadOBJ("player");
		RawModel playerModel = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		TexturedModel texturedPlayer= new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("player")));
		
		Player player = new Player(texturedPlayer, new Vector3f(400, 0, 500), new Vector3f(0, 0, 0), 1);
		entities.add(player);
		Camera camera = new Camera(player);
		MasterRenderer renderer = new MasterRenderer(loader, camera);

		//PARTICLES
		ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particles/particleAtlas"), 4);
		ParticleTexture smokeTexture = new ParticleTexture(loader.loadTexture("particles/smoke"), 8);
		ParticleTexture fireTexture = new ParticleTexture(loader.loadTexture("particles/fire"), 8);
		
		ParticleMaster.init(loader, renderer.getProjectionMatrix());
		ParticleSystem system = new ParticleSystem(200,  30, 0.6f, 4, 3, particleTexture);
		system.setDirection(new Vector3f(0, 1, 0), 0.1f);
		system.setLifeError(0.1f);
		system.setSpeedError(0.4f);
		system.setScaleError(0.2f);
		system.randomizeRotation();

		ParticleSystem smoke = new ParticleSystem(200,  20, 0.1f, 4, 100, smokeTexture);
		system.setDirection(new Vector3f(0, 1, 0), 0.1f);
		system.setLifeError(0.1f);
		system.setSpeedError(0.4f);
		system.setScaleError(0.2f);
		system.randomizeRotation();

		ParticleSystem fire = new ParticleSystem(100,  20, 0.01f, 2, 100, fireTexture);
		system.setDirection(new Vector3f(0, 1, 0), 0.1f);
		system.setLifeError(0.1f);
		system.setSpeedError(0.4f);
		system.setScaleError(0.2f);
		system.randomizeRotation();
		
		///TERRAINS
		List<Terrain> terrains = new ArrayList<Terrain>();
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("Terrain/grass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("Terrain/desert"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("Terrain/flower"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("Terrain/rock"));
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("Terrain/blend"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		Terrain terrain = new Terrain(0, 0, loader, texturePack, blendMap, "height");
		terrains.add(terrain);
		
		///ENTITIES
		data = OBJFileLoader.loadOBJ("dragonBlender");
		RawModel model = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		TexturedModel texturedModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("PLAN")));
		ModelTexture texture = texturedModel.getTexture();
		texture.setReflectivity(1);
		texture.setShineDamper(10);
		Entity entity = new Entity(texturedModel, new Vector3f(400, 0, 400),new Vector3f(0, 0, 0), 3f);

		
		
		data = OBJFileLoader.loadOBJ("tree");
		RawModel treeModel = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		TexturedModel texturedTree= new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("tree")));
		
		data = OBJFileLoader.loadOBJ("grassModel");
		RawModel grassModel = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		TexturedModel texturedGrass= new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("grassTexture")));
		ModelTexture grassTexture = texturedGrass.getTexture();
		grassTexture.setHasTransparency(true);
		grassTexture.setFakeLight(true);
		
		
		data = OBJFileLoader.loadOBJ("fern");
		RawModel fernModel = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("atlas"));
		fernTextureAtlas.setNumberOfRows(2);
		TexturedModel texturedFern= new TexturedModel(fernModel, fernTextureAtlas);
		
		
		data = OBJFileLoader.loadOBJ("lamp");
		RawModel lampModel = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
		TexturedModel texturedLamp= new TexturedModel(lampModel, new ModelTexture(loader.loadTexture("lamp")));
		
		///NORMAL MAPPED ENTITIES
		List<Entity> normalMappedEntities = new ArrayList<Entity>();
		TexturedModel crateModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("NormalObjs/crate", loader), new ModelTexture(loader.loadTexture("NormalObjs/crate")));
		crateModel.getTexture().setShineDamper(10);
		crateModel.getTexture().setReflectivity(0.5f);
		crateModel.getTexture().setNormalMap(loader.loadTexture("NormalObjs/crateNormal"));

		TexturedModel boulderModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("NormalObjs/boulder", loader), new ModelTexture(loader.loadTexture("NormalObjs/boulder")));
		boulderModel.getTexture().setShineDamper(10);
		boulderModel.getTexture().setReflectivity(0.5f);
		boulderModel.getTexture().setNormalMap(loader.loadTexture("NormalObjs/boulderNormal"));
		
		
		Random random = new Random();
		for(int i=0; i<250; i++) {
			float x = random.nextFloat() * 800;
			float z =random.nextFloat() * 800;
			float y = terrain.getHeight(x,  z);
			entities.add(new Entity(texturedTree, new Vector3f(x, y, z), new Vector3f(0, 0, 0), 1f));

			x = random.nextFloat() * 800 ;
			z =random.nextFloat() * 800;
			y = terrain.getHeight(x,  z);
			entities.add(new Entity(texturedGrass, new Vector3f(x, y, z), new Vector3f(0, 0, 0), 3f));

			x = random.nextFloat() * 800;
			z =random.nextFloat() * 800;
			y = terrain.getHeight(x,  z);
			entities.add(new Entity(texturedFern, random.nextInt(4), new Vector3f(x, y, z), new Vector3f(0, 0, 0), 1.5f));
			
			if(i % 5 == 0) {
				x = random.nextFloat() * 800;
				z =random.nextFloat() * 800;
				y = terrain.getHeight(x,  z);
				normalMappedEntities.add(new Entity(crateModel, new Vector3f(x, y, z), new Vector3f(0, 0, 0), 0.05f));				
			
				x = random.nextFloat() * 800;
				z =random.nextFloat() * 800;
				y = terrain.getHeight(x,  z);
				normalMappedEntities.add(new Entity(boulderModel, new Vector3f(x, y, z), new Vector3f(0, 0, 0), 1.0f));				
			
			}
			
		}
		entities.add(entity);
		
		
		/// LIGHTS
		List<Light> lights = new ArrayList<Light>();
		lights.add(new Light(new Vector3f(10000, 10000,10000), new Vector3f(1.3f, 1.3f, 1.3f)));		
	
		float y = terrain.getHeight(400,  300);
		lights.add(new Light(new Vector3f(400, y+15, 300), new Vector3f(1, 0, 1), new Vector3f(1, 0.01f, 0.002f)));
		Entity lamp = new Entity(texturedLamp, new Vector3f(400, y, 300), new Vector3f(0, 0, 0), 1.5f);
		
		y = terrain.getHeight(400,  200);
		lights.add(new Light(new Vector3f(400, y+15, 200), new Vector3f(1, 1, 0), new Vector3f(1, 0.01f, 0.002f)));
		Entity lamp2 = new Entity(texturedLamp, new Vector3f(400, y, 200), new Vector3f(0, 0, 0), 1.5f);
		
		y = terrain.getHeight(400,  350);
		lights.add(new Light(new Vector3f(400, y+15, 350), new Vector3f(0, 1, 1), new Vector3f(1, 0.01f, 0.002f)));
		Entity lamp3 = new Entity(texturedLamp, new Vector3f(400, y, 350), new Vector3f(0, 0, 0), 1.5f);
		

		

		///WATER
		WaterFrameBuffer fbos = new WaterFrameBuffer();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		WaterTile water = new WaterTile(0, 0, 0);
		WaterTile water2 = new WaterTile(0, 400, 0);
		WaterTile water3 = new WaterTile(400, 0, 0);
		WaterTile water4 = new WaterTile(400, 400, 0);
		waters.add(water);
		waters.add(water2);
		waters.add(water3);
		waters.add(water4);
		
		///GUI
		List<GuiTexture> guis = new ArrayList<GuiTexture>();
		GuiTexture gui = new GuiTexture(renderer.getShadowMapTexture(), new Vector2f(-0.4f, 0.6f), new Vector2f(0.25f, 0.25f));
//		guis.add(gui);
//		GuiTexture gui2 = new GuiTexture(fbos.getRefractionTexture(), new Vector2f(0.6f, 0.6f), new Vector2f(0.25f, 0.25f));
//		guis.add(gui2);
		GuiRenderer guiRenderer = new GuiRenderer(loader);
				
		while(!Display.isCloseRequested()) {
			player.move(terrain);
			camera.move();
			
			GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
			renderer.renderShadowMap(entities, lights.get(0));
			
			ParticleMaster.update(camera);
			system.generateParticles(new Vector3f(300, 20, 400));
			smoke.generateParticles(new Vector3f(300, 0, 300));
			fire.generateParticles(new Vector3f(400, 0, 300));
			
			
			
			fbos.bindReflectionFramebuffer();
			float distance = 2 * (camera.getPosition().y);
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entities, normalMappedEntities, terrains, lights, camera, new Vector4f(0, 1, 0, -water.getHeight() + 1f));
			fbos.unbindCurrentFramebuffer();
			camera.getPosition().y += distance;
			camera.invertPitch();

			fbos.bindRefractionFramebuffer();
			renderer.renderScene(entities, normalMappedEntities,terrains, lights, camera, new Vector4f(0, -1, 0, water.getHeight()));
			fbos.unbindCurrentFramebuffer();
			
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			renderer.renderScene(entities, normalMappedEntities, terrains, lights, camera, new Vector4f(0, -1, 0, 100000));
			waterRenderer.render(waters,  camera, lights.get(0));
			
			ParticleMaster.renderParticles(camera);
			
			guiRenderer.render(guis);
			
			DisplayManager.updateDisplay();
		}
		
		renderer.clean();
		fbos.clean();
		waterShader.clean();
		loader.clean();
		ParticleMaster.cleanUp();
		guiRenderer.clean();
		DisplayManager.closeDisplay();
	}

}
