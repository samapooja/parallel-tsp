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
	private long weights[][];
	private int graphSize;
	private int edgeCount;

	Graph(int size) {
		graphSize = size;
		weights = new long[size][size];
		edgeCount = 0;
	}

	public void randomize(int max) {
		Random rng = new Random();
		for(int x = 0; x < graphSize; x++) {
			for(int y = x; y < graphSize; y++) {
				if(x == y) {
					weights[x][y] = 0;
				} else {
					long next = rng.nextInt(max);
					weights[x][y] = next;
					weights[y][x] = next;
				}
			}
		}
	}

	public long[][] getMatrix() {
		return weights;
	}

	public void printMatrix () {
		System.out.println("Adjacency matrix of graph weights:\n");
		System.out.print("\t");
		for(int x = 0; x < graphSize; x++) 
			System.out.print(x + "\t");

		System.out.println("\n");
		for(int x = 0; x < graphSize; x++){
			System.out.print(x + "\t");
			for(int y = 0; y < graphSize; y++) {
				if(weights[x][y] == 0) {
					System.out.print("0\t");
				}else{
					System.out.print(weights[x][y] + "\t");
				}
			}
			System.out.println("\n");
		}
	}
}
