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
		int[] start_path = {0};
		int[] free_nodes = solver.nodeList();
		solver.branch(start_path, free_nodes, 0, 0);
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
	 * Generates an array of all nodes
	 **/
	public int[] nodeList() {
		int[] nodes = new int[graphMatrix.length-1];
		for(int x = 1; x < graphMatrix.length; x++) {
			nodes[x-1] = x;
		}
		return nodes;
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
	public void branch(int[] path, int[] free_nodes, double cur_cost, int depth) {
		int working_node = path[path.length-1];
		
		// Cost is too high, useless to bother attempting
		long heuristic_val = 0;
		for(int x : free_nodes) {
			heuristic_val += getMatrixMin(x);
		}
		if((cur_cost+heuristic_val) > optimalCost) return;

		depth++;

		// depth reaches the maximum, stop branching
		if(depth > max_depth) {
			int[] new_path = new int[graphMatrix[0].length];
			System.arraycopy(path, 0, new_path, 0, path.length);

			// This will fill in new_path with nodes that aren't
			// currently in it
			System.arraycopy(free_nodes, 0, new_path,
							 path.length, free_nodes.length);

			bind(new_path, depth);
			return;
		}

		for(int x = 0; x < free_nodes.length; x++) {
			int new_node = free_nodes[x];
			double new_cost = cur_cost + graphMatrix[working_node][new_node];

			// Create a new path and list of free nodes
			int[] new_path = new int[depth+1];
			System.arraycopy(path, 0, new_path, 0, path.length);
			int[] new_free = OptimalTSP.arrayCut(free_nodes, x);
			
			new_path[new_path.length-1] = new_node;
			branch(new_path, new_free, new_cost, depth);
		}
	}
	
	public double getMatrixMin(int index) {
		double minVal = 0;
		double minVal2 = 0;
		if(index == 0) {
			minVal = graphMatrix[index][1];
		} else {
			minVal = graphMatrix[index][0];
		}
		minVal2 = minVal;
		for(int x = 0; x < graphMatrix[index].length; x++) {
			if(x != index && graphMatrix[index][x] < minVal) {
				minVal2 = minVal;
				minVal = graphMatrix[index][x];
			}
		}
		return minVal * 2;
	}
	/**
	 * This cuts an array element out of the array
	 **/
	public static int[] arrayCut(int[] c_arr, int index) {
		int[] new_arr = new int[c_arr.length - 1];
		if(index != 0 && index != c_arr.length-1) {
			System.arraycopy(c_arr, 0, new_arr, 0, index);
			System.arraycopy(c_arr, index+1, new_arr, index, c_arr.length - (index+1));
		} else if(index == 0 && c_arr.length != 1) {
			System.arraycopy(c_arr, 1, new_arr, 0, c_arr.length-1);
		} else if(index == c_arr.length-1) {
			System.arraycopy(c_arr, 0, new_arr, 0, c_arr.length-1);
		}
		return new_arr;
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
