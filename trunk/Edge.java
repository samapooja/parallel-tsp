/*
 * Edge.java
 *
 * Version:
 *     $Id$
 *
 * Revisions:
 *     $Log$
 */
import java.util.*;
/**
 * This is the edge class to go along with the graph for
 * the traveling salesman problem
 *
 * @author   Robert Clark
 */
public class Edge implements Comparable {
	Vertex one, two;
	double vertexCost;
	Edge(Vertex i, Vertex j) {
		i.addEdge(this);
		j.addEdge(this);
		one = i;
		two = j;
		double a = Math.abs( one.getX() - two.getX() );
		double b = Math.abs( one.getY() - two.getY() );
		vertexCost = Math.sqrt ( Math.pow(a, 2) + Math.pow(b,2) );
	}
	
	Edge(Vertex i, Vertex j, boolean tellV) {
		if(tellV == false){
			one = i;
			two = j;
			double a = Math.abs( one.getX() - two.getX() );
			double b = Math.abs( one.getY() - two.getY() );
			vertexCost = Math.sqrt ( Math.pow(a, 2) + Math.pow(b,2) );
		} else{
			//Edge(i, j);
		}
	}

	public Vertex vertexOne() {
		return one;
	}

	public Vertex vertexTwo() {
		return two;
	}

	public double getCost() {
		return vertexCost;
	}

	public int compareTo( Object other) {
		return compareTo((Edge) other);
	}

	public int compareTo( Edge other ) {
		if(other.getCost() < vertexCost) {
			return 1;
		}else if(other.getCost() > vertexCost) {
			return -1;
		}else{
			if(other.vertexOne().getIndex() < one.getIndex()){
				return 1;
			}else if(other.vertexOne().getIndex() > one.getIndex()) {
				return -1;
			}else{
				if(other.vertexTwo().getIndex() < two.getIndex()) {
					return 1;
				}else{
					return -1;
				}
			}
		}
	}
	
	public String toString() {
		return ("From: " + one.getIndex() + " To: " + two.getIndex() + " Weight: " + vertexCost);
	}
}
