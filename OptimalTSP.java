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
		System.out.println();
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

	/**
	 * Recursively branches until it reaches the max_depth
	 * it then binds and calculates the shortest path cost
	 * at the specified branch.
	 **/
	public void branch() {
		Stack<SearchState> branchStack = new Stack<SearchState>();
		SearchState start_state = new SearchState(nodeList(), this.graphMatrix);
		branchStack.push(start_state);

		while(!branchStack.empty()) {
			SearchState cur_state = branchStack.pop();
			
			// depth reaches the maximum, stop branching
			if(cur_state.depth > max_depth) {
				int[] new_path = cur_state.pathAndPool();
				bind(new_path, cur_state.depth);
			} else {
				for(int x = 0; x < cur_state.freeCount(); x++) {
					SearchState new_state = cur_state.genNextState(x);
					long heuristic = calcHeuristic(new_state);
					if(heuristic < optimalCost) {
						branchStack.push(new_state);
					}
				}
			}
		}
	}

	public long calcHeuristic(SearchState state) {
		// calculate heuristic
		long heuristic_val = 0;
		for(int x : state.getFreeNodes()) {
			heuristic_val += matrixMins[x];
			heuristic_val += matrixMins2[x];
		}
		heuristic_val = heuristic_val / 2;
		heuristic_val += matrixMins[0];
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
			long minVal2 = minVal;
			for(int x = 0; x < graphMatrix[index].length; x++) {
				if(x != index && graphMatrix[index][x] < minVal) {
					minVal2 = minVal;
					minVal = graphMatrix[index][x];
				} else if(x != index && graphMatrix[index][x] < minVal2) {
					minVal2 = graphMatrix[index][x];
				}
			}
			matrixMins[index] = minVal;
			matrixMins2[index] = minVal2;
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
