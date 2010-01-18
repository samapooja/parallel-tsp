/**
 * This class implements a brute force search
 * for the traveling salesman problem
 *
 * @author   Robert Clark
 */
public class OptimalTSP {
	long[][] graphMatrix;
	int[] optimalPath;
	long optimalCost;
	int[] input;

	public static void main(String[] args) {
		String inputName = args[0];
		
		Graph theGraph = new Graph();
		try {
			theGraph.loadMatrix(args[0]);
		} catch(Exception e) {
			System.out.println("Unable to save matrix");
			System.exit(0);
		}

		OptimalTSP solver = new OptimalTSP(theGraph);

		long start = System.currentTimeMillis();
		solver.permutations(1);
		long stop = System.currentTimeMillis();

		System.out.println();
		solver.displayOptimal();
		System.out.println("Runtime for optimal TSP   : " + (stop-start) + " milliseconds");

	}

	OptimalTSP(Graph inputGraph) {
		graphMatrix = inputGraph.getMatrix();
		optimalCost = Long.MAX_VALUE;

		input = new int[graphMatrix.length];
		for(int x = 0; x < graphMatrix.length; x++){
			input[x] = x;
		}
	}

	/**
	 * Print out the current optimal path
	 **/
	public void displayOptimal() {
		System.out.print("Optimal distance: " + optimalCost);
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
			if(cost >= optimalCost) {
				return 0;
			}
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
