package Shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import Entities.Camera;
import Entities.Light;
import Toolbox.Maths;

public class TerrainShader extends ShaderProgram {

	private static final int MAX_LIGHTS = 4;

	private static final String VERTEX_FILE = "src/shaders/terrain.vert";
	private static final String FRAGMENT_FILE = "src/shaders/terrain.frag";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPos[];
	private int location_lightColor[];
	private int location_attenuations[];
	private int location_shineDamper;
	private int location_reflectvity;
	private int location_skyColor;
	private int location_backgroundTexture;
	private int location_rTexture;
	private int location_gTexture;
	private int location_bTexture;
	private int location_blendMap;
	private int location_plane;
	private int location_toShadowMapSpace;
	private int location_shadowMap;

	public TerrainShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "aPosition");
		super.bindAttribute(1,  "aTexCoords");
		super.bindAttribute(2, "aNormal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper= super.getUniformLocation("shineDamper");
		location_reflectvity = super.getUniformLocation("reflectivity");
		location_skyColor = super.getUniformLocation("skyColor");
		location_backgroundTexture = super.getUniformLocation("textureBackground");
		location_rTexture = super.getUniformLocation("textureImageR");
		location_gTexture = super.getUniformLocation("textureImageG");
		location_bTexture = super.getUniformLocation("textureImageB");
		location_blendMap = super.getUniformLocation("blendMap");
		location_plane= super.getUniformLocation("plane");
		location_toShadowMapSpace= super.getUniformLocation("toShadowMapSpace");
		location_shadowMap= super.getUniformLocation("shadowMap");
		
		location_lightPos = new int[MAX_LIGHTS];
		location_lightColor = new int[MAX_LIGHTS];
		location_attenuations = new int[MAX_LIGHTS];

		for(int i=0; i<MAX_LIGHTS; i++) {
			location_lightPos[i] = super.getUniformLocation("lightPos[" + i + "]");
			location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");			
			location_attenuations[i] = super.getUniformLocation("attenuation[" + i + "]");			
		}
	}
	
	public void loadToShadowMapSpaceMatrix(Matrix4f matrix) {
		super.loadMatrix(location_toShadowMapSpace, matrix);
	}
	
	public void loadTextures() {
		super.loadInt(location_backgroundTexture, 0);
		super.loadInt(location_rTexture, 1);
		super.loadInt(location_gTexture, 2);
		super.loadInt(location_bTexture, 3);
		super.loadInt(location_blendMap, 4);
		super.loadInt(location_shadowMap, 5);
	}
	
	public void loadClipPlane(Vector4f plane) {
		super.loadVector4(location_plane, plane);
	}
	
	public void loadShineValues(float damper, float reflectivity) {
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectvity, reflectivity);
	}

	public void loadSkyColor(Vector3f color) {
		super.loadVector(location_skyColor, color);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}
	

	public void loadLights(List<Light> lights) {
		for(int i=0; i<MAX_LIGHTS; i++) {
			if(i < lights.size()) {
				super.loadVector(location_lightPos[i], lights.get(i).getPosition());
				super.loadVector(location_lightColor[i], lights.get(i).getColor());				
				super.loadVector(location_attenuations[i], lights.get(i).getAttenuation());				
			} else {
				super.loadVector(location_lightPos[i], new Vector3f(0, 0, 0));
				super.loadVector(location_lightColor[i], new Vector3f(0, 0, 0));		
				super.loadVector(location_attenuations[i], new Vector3f(1, 0, 0));					
			}
		}
	}
	
	
	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}
}
