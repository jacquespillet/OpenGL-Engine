package skybox;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import Entities.Camera;
 
import Shaders.ShaderProgram;
import Toolbox.Maths;
import renderEngine.DisplayManager;
 
public class SkyboxShader extends ShaderProgram{
 
    private static final String VERTEX_FILE = "src/skybox/skybox.vert";
    private static final String FRAGMENT_FILE = "src/skybox/skybox.frag";
    
    private static final float ROTATE_SPEED = 0.4f;
     
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_fogColor;
    private int location_blendFactor;
    private int location_cubeMap;
    private int location_cubeMap2;
    
    private float currentRotation;
    
    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }
     
    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }
 
    public void loadViewMatrix(Camera camera){
        Matrix4f matrix = Maths.createViewMatrix(camera);
        matrix.m30=0;
        matrix.m31=0;
        matrix.m32=0;
        currentRotation += ROTATE_SPEED * DisplayManager.getFrameTimeSeconds() % 360;
        Matrix4f.rotate((float) Math.toRadians(currentRotation),  new Vector3f(0, 1, 0),  matrix,  matrix);
        super.loadMatrix(location_viewMatrix, matrix);
    }
    
    public void loadFogColor(Vector3f color) {
    	super.loadVector(location_fogColor,  color);
    }
    
    public void loadBlendFactor(float blendFactor) {
    	super.loadFloat(location_blendFactor, blendFactor);
    }
    
    public void loadTextureUnits() {
    	super.loadInt(location_cubeMap, 0);
    	super.loadInt(location_cubeMap2, 1);
    }
    
     
    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_fogColor = super.getUniformLocation("fogColor");
        location_blendFactor = super.getUniformLocation("blendFactor");
        location_cubeMap = super.getUniformLocation("cubeMap");
        location_cubeMap2 = super.getUniformLocation("cubeMap2");
    }
 
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
 
}