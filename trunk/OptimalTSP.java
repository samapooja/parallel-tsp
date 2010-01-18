/*
 * OptimalTSP.java
 *
 * Version:
 *     $Id$
 *
 * Revisions:
 *     $Log$
 */
import java.text.DecimalFormat;
/**
 * 
 *
 * @author   Robert Clark
 */
public class OptimalTSP {
	Graph optGraph;
	long[][] graphMatrix;
	int[] optimalPath;
	long optimalCost;
	int[] input;
	int graphSize;
	DecimalFormat formatter;

	public static void main(String[] args) {
		int size = Integer.parseInt(args[0]);
		int seed = Integer.parseInt(args[1]);

		Graph theGraph = new Graph(size);
		theGraph.randomize(100);
		theGraph.printMatrix();
		OptimalTSP solver = new OptimalTSP(theGraph, size);

		long start = System.currentTimeMillis();
		solver.permutations(1);
		long stop = System.currentTimeMillis();

		System.out.println();
		solver.displayOptimal();
		System.out.println("Runtime for optimal TSP   : " + (stop-start) + " milliseconds");
	}

	OptimalTSP(Graph inputGraph, int size) {
		optGraph = inputGraph;
		graphMatrix = optGraph.getMatrix();

		formatter = new DecimalFormat("0.00");
		optimalCost = Long.MAX_VALUE;
		graphSize = size;

		input = new int[size];
		for(int x = 0; x < size; x++){
			input[x] = x;
		}
	}

	public void displayOptimal() {
		System.out.print("Optimal distance: " + formatter.format(optimalCost));
		System.out.print(" for path ");
		for(int x = 0; x < optimalPath.length; x++) {
			System.out.print(optimalPath[x] + " ");
		}
		System.out.println("0");
	}

	public double testPath(int[] testPath) {
		long cost = 0;
		int x = 0;

		for(x = 0; x < (testPath.length-1); x++) {
			cost += graphMatrix[testPath[x]][testPath[x+1]];
		}
		cost += graphMatrix[testPath[x]][0];
		
		if(cost < optimalCost) {
			optimalPath = testPath;
			optimalCost = cost;
		}
		return cost;
	}

	public void permutations(int offset) {
		if(input.length - offset == 1) {
			int[] output = new int[input.length];
			System.arraycopy(input, 0, output,0,input.length);
			testPath(output);
			return;
		}
		int x = input[offset];
		for(int i = offset; i<input.length; i++) {
			int y = input[i];
			input[i] = x;
			input[offset] = y;

			permutations(offset+1);
			input[i] = y;
		}
		input[offset] = x;
	}

}
