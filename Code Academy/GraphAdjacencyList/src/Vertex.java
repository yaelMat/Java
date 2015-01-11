/**
 * This class implements a vertex in a graph.
 * The vertex has an adjacency list that can be sorted. 
 * 
 * @author Yael Mathov
 */
public class Vertex {

	private int index;
	private AdjacencyList adjList = new AdjacencyList();
	private int color;
	
	/**
	 * Constructor
	 * @param id - Vertex's id.
	 */
	public Vertex(int id){
		this.index = id;
	}
	
	
	/**
	 * Index getter
	 * @return vertex's index.
	 */
	public int getIndex(){
		return this.index;
	}
	
	/**
	 * Color getter
	 * @return the color of the vertex.
	 */
	public int getColor(){
		return this.color;
	}
	
	/**
	 * Set a new color to the vertex 
	 * @param color
	 */
	public void setColor(int color){
		this.color = color;
	}
	

	/**
	 * Add a new vertex to the adjacency list.
	 * @param neighborID - neighbor's index
	 * @param edgeWeight - the weight of the edge between this and the neighbor vertex
	 */
	public void addNeighbor(int neighborID, int edgeWeight){
		if(neighborID < 0 || neighborID == this.index){
			throw new IllegalArgumentException("Illegal vertex");
		}
		this.adjList.addNeighbor(new Neighbor(neighborID, edgeWeight));
		
	}
	
	
	public void sortAdjList(){
		this.adjList.sortList();
	}
	

	/**
	 * Compare according to the vertex's index.
	 */
	public boolean equals(Object obj){
		return (obj instanceof Vertex && 
				(this.index == ((Vertex)obj).getIndex()));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Vertex ID : ");
		builder.append(index);
		builder.append("\n");
		if (adjList != null) {
			builder.append("Adjacency List:");
			builder.append(adjList.toString());
		}
		builder.append("******************\n");
		return builder.toString();
	}

}
