import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The adjacency list contain a vertex neighbors.
 * A neighbor can't appear twice in the list and, for now, also
 * can't be erased from the list.
 * The list can be sorted according to the edge's weight (used
 * in algorithms for finding MST). 
 * 
 * @author Yael Mathov
 */

public class AdjacencyList {
	
	private List<Neighbor> adjList;
	boolean isSorted;
	
	/**
	 * Constructor
	 */
	public AdjacencyList() {
		this.adjList = new ArrayList<Neighbor>();
		this.isSorted = false;
	}
	
	/**
	 * Set this.isSorted value.
	 * @param isSort - true if the list as been sorted 
	 * false in the list might not be sorted.
	 */
	private void setSorted(boolean isSort){
		this.isSorted = isSort;
	}
	
	
	/**
	 * Add new neighbor to the adjacency list.
	 * @param toAdd
	 */
	public void addNeighbor(Neighbor toAdd){
		//Check that the input isn't null. 
		if(toAdd == null){
			throw new NullPointerException("Try to add null neighbor.");
		}
		
		//Check that the input isn't in the list.
		if(this.adjList.contains(toAdd)){
			throw new IllegalArgumentException
				("The adjacency list already contains the input neighbor.");
		}
		
		//Add the neighbor to the list and mark the list as unsorted.
		this.adjList.add(toAdd);
		setSorted(false);
	}
	
	

	/**
	 * Sort the adjacency list (if need to sort).
	 */
	public void sortList(){
		if(!(this.isSorted)){
			Collections.sort(this.adjList);
			setSorted(true);
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (adjList != null) {
			builder.append("\n");
			for(Neighbor n : this.adjList){
				builder.append(n.toString());
				builder.append("\n");
			}
			
		}
		return builder.toString();
	}
	

}
