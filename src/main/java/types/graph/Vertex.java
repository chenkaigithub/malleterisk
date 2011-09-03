package types.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Vertex {
	private final String identifier;
	private final Map<Object, Set<Edge>> labelEdges;
	
	protected Vertex(String id) {
		this.identifier = id;
		this.labelEdges = new HashMap<Object, Set<Edge>>();
	}
	
	public String getId() {
		return this.identifier;
	}
	
	public void addEdge(Edge e) {
		Object l = e.getLabel();
		
		Set<Edge> edges;
		
		if(!this.labelEdges.containsKey(l)) {
			edges = new HashSet<Edge>();
			this.labelEdges.put(l, edges);
		}
		else {
			edges = this.labelEdges.get(l);
		}
		
		edges.add(e);
	}
	
	public Set<Edge> getEdges(Object label) {
		return this.labelEdges.get(label);
	}
	
	public Set<Object> getLabels() {
		return this.labelEdges.keySet();
	}
	
	public Map<Object, Set<Edge>> getLabelEdges() {
		return this.labelEdges;
	}
	
	//
	// Equality
	//
	
	public boolean equals(Object o) {
		if(o instanceof Vertex)
			return ((Vertex) o).identifier.equals(this.identifier);
		
		return false;
	}
	
	public int hashCode() {
		return this.identifier.hashCode();
	}
}
