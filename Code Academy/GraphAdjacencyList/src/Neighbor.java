/**
 * It class implements an element in the adjacency list - the
 * neighbor vertex and the weight of the edge between the vertex and
 * it's neighbor.
 * 
 * @author Yael Mathov
 */
public class Neighbor implements Comparable<Neighbor> {

	private int adjVertexIndex;
	private int edgeWeight;
	
	/**
	 * Constructor
	 * @param vertexID - The index of the vertex
	 * @param w - Weight of the edge 
	 */
	public Neighbor(int vertexID, int w) {
		this.adjVertexIndex = vertexID;
		this.edgeWeight = w;
	}
	
	
	
	/**
	 * @return the weight of this edge
	 */
	public int getWeight(){
		return this.edgeWeight;
	}
	
	/**
	 * @return the index of the neighbor vertex
	 */
	public int getVertexIndex(){
		return this.adjVertexIndex;
	}




	/**
	 * Compare according to the vertex index.
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof Neighbor){
			return (this.adjVertexIndex == ((Neighbor)obj).getVertexIndex());
		}
		return false;

	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\tNeighbor ID = ");
		builder.append(adjVertexIndex);
		builder.append(", Edge's Weight = ");
		builder.append(edgeWeight);
		return builder.toString();
	}


	/**
	 * Compare according to edge weight
	 * if weight is equal, compare according to vertex's index.
	 * @param o
	 * @return
	 */
	@Override
	public int compareTo(Neighbor o) {
		if(this.edgeWeight < o.getWeight()){
			return -1;
		}
		else if(this.edgeWeight > o.getWeight()){
			return 1;
		}
		else if(this.adjVertexIndex < o.getVertexIndex()){
			return -1;
		}
		else if(this.adjVertexIndex > o.getVertexIndex()){
			return 1;
		}
		return 0;
	}
	
	
	

}
