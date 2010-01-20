import java.util.Stack;
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
	long[] matrixMins;
	long[] matrixMins2;

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
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

		solver.branch();

		System.out.println();
		solver.displayOptimal();
		long stop = System.currentTimeMillis();
		System.out.println("Runtime for optimal TSP   : " + (stop-start) + " milliseconds");

	}

	OptimalTSP(Graph inputGraph, int depth) {
		graphMatrix = inputGraph.getMatrix();
		optimalCost = Long.MAX_VALUE;
		if(graphMatrix.length <= depth)
			max_depth = graphMatrix.length - 1;
		else
			max_depth = depth;

		genMatrixMins();
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

	public long testPath(int[] testPath) {
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

	private class AlgoState {
		public AlgoState parent = null;
		public int[] free_nodes;
		public long cost;
		public int depth;
		public int node;

		public AlgoState(int node, int[] free_nodes, long cost, int depth) {
			this.free_nodes = free_nodes;
			this.cost = cost;
			this.depth = depth;
			this.node = node;
		}
		public AlgoState(AlgoState parent, int node, int[] free_nodes, long cost, int depth) {
			this.parent = parent;
			this.free_nodes = free_nodes;
			this.cost = cost;
			this.depth = depth;
			this.node = node;
		}

		public int[] path() {
			int[] path = new int[depth];
			AlgoState traverse = this;
			for(int x = path.length-1; x >= 0; x--) {
				path[x] = traverse.node;
				traverse = traverse.parent;
			}
			return path;
		}

		public int[] pathAndPool() {
			int[] path = new int[depth + this.free_nodes.length];
			AlgoState traverse = this;
			for(int x = depth-1; x >= 0; x--) {
				path[x] = traverse.node;
				traverse = traverse.parent;
			}
			System.arraycopy(free_nodes, 0, path,
							 depth, free_nodes.length);
			return path;
		}
	}
	/**
	 * Recursively branches until it reaches the max_depth
	 * it then binds and calculates the shortest path cost
	 * at the specified branch.
	 **/
	public void branch() {
		Stack<AlgoState> branchStack = new Stack<AlgoState>();
		int[] free_nodes = nodeList();
		AlgoState start_state = new AlgoState(0, free_nodes, 0, 1);
		branchStack.push(start_state);

		while(!branchStack.empty()) {
			AlgoState cur_state = branchStack.pop();
			
			// depth reaches the maximum, stop branching
			if(cur_state.depth > max_depth) {
				int[] new_path = cur_state.pathAndPool();
				bind(new_path, cur_state.depth);
			} else {

				int working_node = cur_state.node;
				for(int x = 0; x < cur_state.free_nodes.length; x++) {
					int new_node = cur_state.free_nodes[x];
					long new_cost = cur_state.cost + graphMatrix[working_node][new_node];

					int[] new_free = OptimalTSP.arrayCut(cur_state.free_nodes, x);
					
					AlgoState new_state = new AlgoState(cur_state, new_node, new_free, new_cost, cur_state.depth+1);
					long heuristic = calcHeuristic(cur_state);
					if(heuristic < optimalCost) {
						branchStack.push(new_state);
					}
				}
			}
		}
	}

	public long calcHeuristic(AlgoState state) {
		// calculate heuristic
		long heuristic_val = 0;
		for(int x : state.free_nodes) {
			heuristic_val += matrixMins[x] * 2;
		}
		heuristic_val += state.cost;
		return heuristic_val;
	}
	
	/**
	 * This method generates the matrix minimum values for each row
	 **/
	public void genMatrixMins() {
		matrixMins = new long[graphMatrix.length];
		matrixMins2 = new long[graphMatrix.length];
		for(int index = 0; index < graphMatrix.length; index++) {
			long minVal = 0;
			if(index == 0) {
				minVal = graphMatrix[index][1];
			} else {
				minVal = graphMatrix[index][0];
			}
			for(int x = 0; x < graphMatrix[index].length; x++) {
				if(x != index && graphMatrix[index][x] < minVal) {
					minVal = graphMatrix[index][x];
				}
			}
			matrixMins[index] = minVal;
		}
	}

	/**
	 * This cuts an array element out of the array
	 **/
	public static int[] arrayCut(int[] c_arr, int index) {
		int[] new_arr = new int[c_arr.length - 1];
		System.arraycopy(c_arr, 0, new_arr, 0, index);
		if(c_arr.length != index) {
			System.arraycopy(c_arr, index + 1, new_arr, index, c_arr.length - index - 1);
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
