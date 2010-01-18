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
	int max_depth = 8;

	public static void main(String[] args) {
		String inputName = args[0];
		int depth = Integer.parseInt(args[1]);

		Graph theGraph = new Graph();
		try {
			theGraph.loadMatrix(args[0]);
		} catch(Exception e) {
			System.out.println("Unable to save matrix");
			System.exit(0);
		}

		OptimalTSP solver = new OptimalTSP(theGraph, depth);

		long start = System.currentTimeMillis();
		int[] start_path = new int[1];
		start_path[0] = 0;
		solver.branch(start_path, 0, 0);
		long stop = System.currentTimeMillis();

		System.out.println();
		solver.displayOptimal();
		System.out.println("Runtime for optimal TSP   : " + (stop-start) + " milliseconds");

	}

	OptimalTSP(Graph inputGraph, int depth) {
		graphMatrix = inputGraph.getMatrix();
		optimalCost = Long.MAX_VALUE;
		if(graphMatrix.length <= depth)
			max_depth = graphMatrix.length - 1;
		else
			max_depth = depth;
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

	/**
	 * Recursively branches until it reaches the max_depth
	 * it then binds and calculates the shortest path cost
	 * at the specified branch.
	 **/
	public void branch(int[] path, double cur_cost, int depth) {
		// Cost is too high, useless to bother attempting
		if(cur_cost > optimalCost) return;

		depth++;

		// depth reaches the maximum, stop branching
		if(depth > max_depth) {
			int[] new_path = new int[graphMatrix[0].length];
			System.arraycopy(path, 0, new_path, 0, path.length);

			// This will fill in new_path with nodes that aren't
			// currently in it
			// TODO find a better way, this works but its ugly
			int location = depth;
			for (int x = 0; x < new_path.length; x++) {
				boolean found = false;
				for(int y : new_path) {
					if(y == x) {
						found = true;
						break;
					}
				}
				if(!found) {
					new_path[location] = x;
					location++;
				}
			}

			bind(new_path, depth);
			return;
		}

		int working_node = path[path.length-1];
		for(int x = 0; x < graphMatrix[0].length; x++) {
			if(x != working_node) {
				boolean found = false;
				for (int y : path) {
					if(y == x) {
						found = true;
						break;
					}
				}
				if(!found) {
					double new_cost = cur_cost + graphMatrix[working_node][x];
					int[] new_path = new int[depth+1];
					System.arraycopy(path, 0, new_path, 0, path.length);
					new_path[new_path.length-1] = x;
					branch(new_path, new_cost, depth);
				}
			}
		}
	}

	public void bind(int[] input, int offset) {
		if(input.length - offset <= 1) {
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

			bind(input, offset+1);
			input[i] = y;
		}
		input[offset] = x;
	}

}
