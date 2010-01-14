/*
 * Graph.java
 *
 * Version:
 *     $Id$
 *
 * Revisions:
 *     $Log$
 */
import java.util.Random;
import java.text.DecimalFormat;
/**
 * This is the graph glass for the traveling salesman problem
 *
 * @author   Robert Clark
 */
public class Graph {
	private double weights[][];
	private Vertex vertices[];
	private Edge edges[];
	private int graphSize;
	private boolean randomGraph = true;
	private int edgeCount;
	private int vertexCount = 0;

	Graph(int size, int seed) {
		int nextX, nextY;
		nextX=0;nextY=0;
		graphSize = size;
		Random xGen = new Random(seed);
		Random yGen = new Random(2*seed);
		vertices = new Vertex[size];
		edges = new Edge[(((size*size)-size)/2)+size];
		weights = new double[size][size];
		
		// Generate all of the vertices
		while(vertexCount < size){
			nextX = Math.abs(xGen.nextInt(size));
			nextY = Math.abs(yGen.nextInt(size));
			// If the X is taken then define new coords
			boolean doCont = false;
			for(int y = 0; y < vertexCount; y++){
				if(vertices[y].getX() == nextX)
					doCont = true;
			}
			if (doCont == true)
				continue;
			vertices[vertexCount] = new Vertex(nextX, nextY, vertexCount);
			vertexCount++;
			//x++;
		}
		// Connect all the vertices
		edgeCount = 0;
		for(int i = 0; i < size; i++) {
			for (int j = i; j < size; j++) {
				addEdge(i, j);
			}
		}
		if(size <= 10) {
			printVertices();
			printMatrix();
		}
	}

	Graph(int size) {
		graphSize = size;
		randomGraph = false;
		weights = new double[size][size];
		vertices = new Vertex[size];
		edges = new Edge[size];
		edgeCount = 0;
	}

	public void addEdge(int i, int j) {
		edges[edgeCount] = new Edge(vertices[i], vertices[j]);
		weights[i][j] = edges[edgeCount].getCost();
		weights[j][i] = edges[edgeCount].getCost();
		edgeCount++;
	}
	
	public void addVertex(Vertex newVert) {
		int index = newVert.getIndex();
		//int x = newVert.getX();
		//int y = newVert.getY();
		vertices[index] = newVert;//new Vertex(x, y, index);
		vertexCount++;
	}

	public void addVertex(int x, int y, int index) {
		vertices[index] = new Vertex(x, y, index);
		vertexCount++;
	}

	public Vertex getVertex(int x) {
		return vertices[x];
	}
	public void sortVertices() {
		quickSortV(0, vertices.length-1);
	}
	private void quickSortV(int p, int r) {
		if(p < r) {
			int q = partitionVertices(p,r);
			quickSortV(p,q-1);
			quickSortV(q+1,r);
		}
	}
	private int partitionVertices( int p, int r ) {
		Vertex x = vertices[r];
		int i = p-1;
		for(int j = p; j <= (r-1); j++) {
			if(vertices[j].compareTo(x) <= 0) {
				i += 1;
				swapVertices(i,j);
			}
		}
		swapVertices(i+1,r);
		return i+1;
	}
	private void swapVertices( int i, int j ) {
		Vertex temp = vertices[i];
		vertices[i] = vertices[j];
		vertices[j] = temp;
	}
	public Edge[] getEdges() {
		return edges;
	}
	
	public void sortEdges() {
		quickSort(0, edges.length-1);
	}

	private void quickSort(int p, int r) {
		if(p < r) {
			int q = partition(p, r);
			quickSort(p, q-1);
			quickSort(q+1, r);
		}
	}

	private int partition(int p, int r) {
		Edge x = edges[r];
		int i = p-1;
		for(int j = p; j <= (r-1); j++) {
			if(edges[j].compareTo(x) <= 0){
				i += 1;
				swapEdges(i, j);
			}
		}
		swapEdges(i+1, r);
		return i+1;
	}

	private void swapEdges( int i, int j ) {
		Edge temp = edges[i];
		edges[i] = edges[j];
		edges[j] = temp;
	}

	public void printVertices () {
		System.out.println("X-Y Coordinates:");
		for(int x = 0; x < graphSize; x++){
			System.out.print("v" + vertices[x].getIndex() + ": (" + vertices[x].getX()); 
			System.out.print("," + vertices[x].getY() + ") ");
		}
		System.out.println("\n");
	}
	
	public double[][] getMatrix() {
		return weights;
	}

	public void printMatrix () {
		System.out.println("Adjacency matrix of graph weights:\n");
		System.out.print("   ");
		DecimalFormat formatter = new DecimalFormat("0.00");
		for(int x = 0; x < graphSize; x++) 
			System.out.print("  " + x + "   ");
		System.out.println("\n");
		for(int x = 0; x < graphSize; x++){
			System.out.print(x + "  ");
			for(int y = 0; y < graphSize; y++) {
				if(weights[x][y] == 0) {
					System.out.print("0.00  ");
				}else{
					System.out.print(formatter.format(weights[x][y]) + "  ");
				}
			}
			System.out.println("\n");
		}
	}
	
	public void printEdges() {
		System.out.println("Edge listing");
		for(int x = 0; x < edges.length; x++){
			System.out.println(edges[x]);
		}
	}

	public int vertCount() {
		return vertexCount;
	}
}
