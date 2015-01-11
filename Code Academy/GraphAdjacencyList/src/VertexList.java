import java.util.ArrayList;
import java.util.List;

/**
 * This class organize and initialize the graph's vertices in a list.  
 * 
 * @author Yael Mathov
 */

public class VertexList {
	
	private List<Vertex> vertexList;

	/**
	 * Create a vertices list with indexes from 0 to verticesNum-1
	 * @param verticesNum 
	 */
	public VertexList(int verticesNum) {
		this.vertexList = new ArrayList<Vertex>(verticesNum);
		for(int i = 0 ; i < verticesNum ; i++){
			this.vertexList.add(new Vertex(i));
		}
	}
	
	/**
	 * Add edge from head to tail with
	 * weight according to the weight input
	 * @param head - index of the head vertex
	 * @param tail - index of the tail vertex
	 * @param weight - weight of the edge
	 */
	public void addEdge(int head, int tail, int weight){
		this.vertexList.get(head).addNeighbor(tail, weight);
	}
	
	/**
	 * For each vertex, change its color to the given color.
	 * @param color
	 */
	public void setAllColor(int color){
		for(Vertex v : this.vertexList){
			v.setColor(color);
		}
	}
	
	
	
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (vertexList != null) {
			builder.append("******************\n");
			for(Vertex v : this.vertexList){
				builder.append(v.toString());
			}
		}
		
		return builder.toString();
	}
	
	
	
	

}
