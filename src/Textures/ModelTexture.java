package Textures;

public class ModelTexture {
	
	private int textureID;
	private int normalMap;
	private float shineDamper = 1;
	private float reflectivity = 0;
	private boolean hasTransparency = false;
	private boolean isFakeLight = false;
	
	private int numberOfRows = 1;
	
	public ModelTexture(int id) {
		this.textureID = id;
	}

	public int getTextureID() {
		return textureID;
	}

	public int getNormalMap() {
		return normalMap;
	}

	public void setNormalMap(int normalMap) {
		this.normalMap = normalMap;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public boolean isHasTransparency() {
		return hasTransparency;
	}

	public void setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}

	public boolean isFakeLight() {
		return isFakeLight;
	}

	public void setFakeLight(boolean isFakeLight) {
		this.isFakeLight = isFakeLight;
	}
}
