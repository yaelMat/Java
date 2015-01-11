/**
 *  This class implements a graph with adjacency list.
 *  Each vertex has an index from 0 to numVertex-1 (numVertex is the number of
 *  of vertices, one of the constructor's parameters) and an adjacency list.
 *  The graph can be either a direct graph or an undirected graph.
 * 
 * @author Yael Mathov
 */
public class Graph {

	private int numVertex;
	private VertexList vertices;
	private boolean isDirected;
	
	/**
	 * Create graph with vertices according to numV.
	 * @param numV - number of vertices, numV > 0
	 * @param isDirect - true if this graph is a direct graph
	 * and false if it isn't.
	 */
	public Graph(int numV, boolean isDirect) {
		if(numV < 1){
			throw new IllegalArgumentException("Number of vertices must be > 0");
		}
		this.numVertex = numV;
		this.vertices = new VertexList(numV);
		this.isDirected = isDirect;

	}
	
	
	/**
	 * Add a new edge, from head to tail, to the graph, if the graph is
	 * an undirected graph, also add the opposite edge. 
	 * @param head - index of the head vertex
	 * @param tail - index of the tail vertex
	 * @param weight - weight of the edge
	 */
	public void addEdge(int head, int tail, int weight){
		if(head == tail || head < 0 || head >= this.numVertex || 
				tail < 0 || tail >= this.numVertex){
			throw new IllegalArgumentException("Illeagal Vertex index.");
		}
		
		//Add edge from head to tail.
		this.vertices.addEdge(head, tail, weight);
		
		//If the graph isn't directed graph add  the opposite edge. 
		if(!this.isDirected){
			this.vertices.addEdge(tail, head, weight);
		}

	}
	
	/**
	 * For each vertex, change its color to the given color.
	 * @param color
	 */
	public void setAllColor(int color){
		this.vertices.setAllColor(color);
	}
	

	
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Graph: \nNumber og Vertices: ");
		builder.append(numVertex);
		builder.append("\n");
		if (vertices != null) {
			builder.append("Vertices:\n");
			builder.append(vertices.toString());
		}
		
		return builder.toString();
	}
	
	
	

}
