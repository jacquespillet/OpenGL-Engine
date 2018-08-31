package skybox;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import Entities.Camera;
import Models.RawModel;
import renderEngine.DisplayManager;
import renderEngine.ModelLoader;

public class SkyboxRenderer {
	private static final float SIZE = 500f;
	
	private static final float[] VERTICES = {        
	    -SIZE,  SIZE, -SIZE,
	    -SIZE, -SIZE, -SIZE,
	    SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE, -SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,

	    -SIZE, -SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE,
	    -SIZE, -SIZE,  SIZE,

	    -SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE, -SIZE,
	     SIZE,  SIZE,  SIZE,
	     SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE,  SIZE,
	    -SIZE,  SIZE, -SIZE,

	    -SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE, -SIZE,
	     SIZE, -SIZE, -SIZE,
	    -SIZE, -SIZE,  SIZE,
	     SIZE, -SIZE,  SIZE
	};
	
	private static String[] TEXTURE_FILES = { "Day/right", "Day/left", "Day/top", "Day/bottom", "Day/back", "Day/front"};
	private static String[] TEXTURE_FILES_NIGHT = { "Night/right", "Night/left", "Night/top", "Night/bottom", "Night/back", "Night/front"};

	private RawModel cube;
	private int texture;
	private int nightTexture;
	private SkyboxShader shader;
	
	private float time = 0;
	
	public SkyboxRenderer(ModelLoader loader, Matrix4f projectionMatrix) {
		cube = loader.loadToVAO(VERTICES,  3);
		texture = loader.loadCubeMap(TEXTURE_FILES);
		nightTexture = loader.loadCubeMap(TEXTURE_FILES_NIGHT);
		shader = new SkyboxShader();
		shader.start();
		shader.loadTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(Camera camera, Vector3f fogColor) {
		shader.start();
		shader.loadFogColor(fogColor);
		shader.loadViewMatrix(camera);
		GL30.glBindVertexArray(cube.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		bindTextures();
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getNumVertex());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}
	
	private void bindTextures() {
//		time += DisplayManager.getFrameTimeSeconds() * 1000;
//		time %= 24000;
//		int texture1;
//		int texture2;
		float blendFactor = 0.5f;		
//		if(time >= 0 && time < 5000){
//			texture1 = nightTexture;
//			texture2 = nightTexture;
//			blendFactor = (time - 0)/(5000 - 0);
//		}else if(time >= 5000 && time < 8000){
//			texture1 = nightTexture;
//			texture2 = texture;
//			blendFactor = (time - 5000)/(8000 - 5000);
//		}else if(time >= 8000 && time < 21000){
//			texture1 = texture;
//			texture2 = texture;
//			blendFactor = (time - 8000)/(21000 - 8000);
//		}else{
//			texture1 = texture;
//			texture2 = nightTexture;
//			blendFactor = (time - 21000)/(24000 - 21000);
//		}
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, nightTexture);
		shader.loadBlendFactor(blendFactor);
	}
}
