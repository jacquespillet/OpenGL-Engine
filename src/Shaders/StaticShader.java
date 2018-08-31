package Shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import Entities.Camera;
import Entities.Light;
import Toolbox.Maths;

public class StaticShader extends ShaderProgram {
	private static final int MAX_LIGHTS = 4;
	
	private static final String VERTEX_FILE = "src/shaders/shader.vert";
	private static final String FRAGMENT_FILE = "src/shaders/shader.frag";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPos[];
	private int location_attenuations[];
	private int location_lightColor[];
	private int location_shineDamper;
	private int location_reflectvity;
	private int location_useFakeLighting;
	private int location_skyColor;
	private int location_numberOfRows;
	private int location_offset;
	private int location_plane;
	
	
	public StaticShader() {
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
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");
		location_skyColor = super.getUniformLocation("skyColor");
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_offset= super.getUniformLocation("offset");
		location_plane= super.getUniformLocation("plane");
		
		location_lightPos = new int[MAX_LIGHTS];
		location_lightColor = new int[MAX_LIGHTS];
		location_attenuations = new int[MAX_LIGHTS];
		for(int i=0; i<MAX_LIGHTS; i++) {
			location_lightPos[i] = super.getUniformLocation("lightPos[" + i + "]");
			location_lightColor[i] = super.getUniformLocation("lightColor[" + i + "]");			
			location_attenuations[i] = super.getUniformLocation("attenuation[" + i + "]");			
		}
	}
	
	public void loadClipPlane(Vector4f plane) {
		super.loadVector4(location_plane, plane);
	}
	
	public void loadNumberOfRows(int numberOfRows) {
		super.loadFloat(location_numberOfRows, numberOfRows);
	}

	public void loadOffset(Vector2f offset) {
		super.loadVector2(location_offset, offset);
	}
	
	public void loadShineValues(float damper, float reflectivity) {
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectvity, reflectivity);
	}
	
	public void loadFakeLighting(boolean useFake) {
		super.loadBoolean(location_useFakeLighting, useFake);
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
