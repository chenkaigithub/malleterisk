package types.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph {
	private final Map<String, Vertex> vertices;
	private final List<Edge> edges;
	private final Map<Vertex, Set<Object>> vertexLabels;
	
	public Graph() {
		this.vertices = new HashMap<String, Vertex>();
		this.edges = new LinkedList<Edge>();
		
		this.vertexLabels = new HashMap<Vertex, Set<Object>>();
	}
	
	//
	// Vertex & Edge Creation and Retrieval Methods
	//
	
	public Vertex vertex(String id) {
		return vertices.containsKey(id) ? vertices.get(id) : createVertex(id);
	}
	
	public Edge edge(Vertex src, Vertex dst, double weight, EdgeType type, Object label) {
		// create temp edge | we keep single instances of edges
		Edge e = createEdge(src, dst, weight, type, label);
		
		int idx = this.edges.indexOf(e);
		if(idx == -1) {				// new edge between vertices
			this.edges.add(e);
			src.addEdge(e);
			dst.addEdge(e);
		}
		else {						// existing edge; update weight
			e = this.edges.get(idx);
			e.setWeight(e.getWeight() + weight);
		}
		
		return e;
	}
	
	
	private Vertex createVertex(String id) {
		Vertex v = new Vertex(id);
		vertices.put(id, v);
		return v;
	}
	
	private Edge createEdge(Vertex src, Vertex dst, double weight, EdgeType type, Object label) {
		addLabel(src, label);
		addLabel(dst, label);
		
		return new Edge(src, dst, weight, type, label);
	}
	
	
	private void addLabel(Vertex v, Object l) {
		Set<Object> labels;
		
		if(!this.vertexLabels.containsKey(v)) {
			labels = new HashSet<Object>();
			this.vertexLabels.put(v, labels);
		}
		else {
			labels = this.vertexLabels.get(v);
		}
		
		labels.add(l);
	}
	
	public Set<Object> getLabels(Vertex v) {
		return this.vertexLabels.get(v);
	}
}
