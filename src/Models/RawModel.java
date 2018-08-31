package Models;

public class RawModel {
	private int vaoID;
	private int numVertex;
	
	public RawModel(int vaoID, int numVertex) {
		this.vaoID = vaoID;
		this.numVertex = numVertex;
	}

	public int getNumVertex() {
		return numVertex;
	}

	public int getVaoID() {
		return vaoID;
	}
}
