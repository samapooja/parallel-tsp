/*
 * Vertex.java
 *
 * Version:
 *     $Id$
 *
 * Revisions:
 *     $Log$
 */
import java.util.*;
/**
 * This is the vertex class to go with the graph class for the traveling
 * salesman problem.
 *
 * @author   Robert Clark
 */
public class Vertex {
	int coordX, coordY, vIndex;
	LinkedList<Edge> edges;
	boolean flag; // Left to right flag

	Vertex(int x, int y, int index){
		flag = false;
		edges = new LinkedList<Edge>();
		coordX = x;
		coordY = y;
		vIndex = index;
	}


	public void setFlag(boolean value) {
		flag = value;
	}

	public boolean getFlag() {
		return flag;
	}

	public int getX() {
		return coordX;
	}

	public int getY() {
		return coordY;
	}

	public void addEdge(Edge i) {
		edges.add(i);
	}

	public LinkedList<Edge> getEdges() {
		return edges;
	}
	public void clearEdges() {
		edges.clear();
	}
	public Edge[] getEdgesArr() {
		return (Edge[])edges.toArray();
	}

	public int getIndex() {
		return vIndex;
	}
	
	public int edgesCount() {
		return edges.size();
	}

	public int compareTo(Object other) {
		return compareTo((Vertex) other);
	}

	public int compareTo(Vertex other) {
		return (coordX - other.coordX);
	}

}
