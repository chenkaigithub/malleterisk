package types.graph;

public class Edge {
	private final Vertex source;
	private final Vertex destination;
	private double weight;
	private final EdgeType type;
	private final Object label;
	
	protected Edge(Vertex src, Vertex dst, double w, EdgeType t, Object l) {
		this.source = src;
		this.destination = dst;
		this.weight = w;
		this.type = t;
		this.label = l;
	}
	
	public void setWeight(double w) {
		this.weight = w;
	}
	
	public Vertex getSourceVertex() {
		return this.source;
	}
	
	public Vertex getDestinationVertex() {
		return this.destination;
	}

	public EdgeType getType() {
		return this.type;
	}
	
	public double getWeight() {
		return this.weight;
	}
	
	public Object getLabel() {
		return this.label;
	}
	
	//
	// Equality
	//
	
	public boolean equals(Object o) {
		if(o instanceof Edge) {
			Edge other = (Edge) o;
			return 
				other.source.equals(this.source) && 
				other.destination.equals(this.destination) &&
				other.type.equals(this.type) &&
				other.label.equals(this.label);
		}
		
		return false;
	}
	
	public int hashCode() {
		return this.source.hashCode() + this.destination.hashCode() + this.type.hashCode() + this.label.hashCode();
	}
}
