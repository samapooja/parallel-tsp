import java.util.Stack;

import edu.rit.pj.Comm;
import edu.rit.pj.IntegerForLoop;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;
import edu.rit.pj.reduction.ObjectOp;
import edu.rit.pj.reduction.SharedObject;

/**
 * This class implements a brute force search
 * for the traveling salesman problem
 *
 * @author   Robert Clark
 * @author   Daniel Iland
 */
public class OptimalTSPSMP {
	static long[][] graphMatrix;
	static int[] optimalPath;
	static long optimalCost;
	static int max_depth = 8;
	static long[] matrixMins;
	static long[] matrixMins2;
	static SharedObject<Stack<SearchState>> sharedStack = new SharedObject<Stack<SearchState>>();
	static ObjectOp <Stack<SearchState>> combineStacks = new ObjectOp<Stack<SearchState>>() {

		public Stack<SearchState> op(Stack<SearchState> x,
				Stack<SearchState> y) {
			Stack<SearchState> returnVal = new Stack<SearchState>();
			returnVal.addAll(x);
			returnVal.addAll(y);
			return returnVal;
		}
	};


	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		Comm.init(args);

		if(args.length != 2) {
			System.err.println("Usage: OptimalTSP inputFile branchDepth");
			System.exit(-1);
		}
		int depth = Integer.parseInt(args[1]);
		Graph theGraph = new Graph();
		try {
			theGraph.loadMatrix(args[0]);
		} catch(Exception e) {
			System.out.println("Unable to load matrix, check filename");
			System.exit(0);
		}
		
		graphMatrix = theGraph.getMatrix();
		optimalCost = Long.MAX_VALUE;
		if(graphMatrix.length <= depth)
			max_depth = graphMatrix.length - 1;
		else
			max_depth = depth;
		
		genMatrixMins();
		
		branch();

		System.out.println();
		displayOptimal();
		long stop = System.currentTimeMillis();
		System.out.println("Runtime for optimal TSP   : " + (stop-start) + " milliseconds");
		System.out.println();
	}

	/**
	 * Generates an array of all nodes
	 **/
	public static int[] nodeList() {
		int[] nodes = new int[graphMatrix.length-1];
		for(int x = 1; x < graphMatrix.length; x++) {
			nodes[x-1] = x;
		}
		return nodes;
	}

	/**
	 * Print out the current optimal path
	 **/
	public static void displayOptimal() {
		System.out.print("Optimal distance: " + optimalCost);
		System.out.print(" for path ");
		for(int x = 0; x < optimalPath.length; x++) {
			System.out.print(optimalPath[x] + " ");
		}
		System.out.println("0");
	}

	public static long testPath(int[] testPath) {
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
	 * @throws Exception 
	 **/
	public static void branch() throws Exception {
		ParallelTeam team = new ParallelTeam();
		SearchState start_state = new SearchState(nodeList(), graphMatrix);
		Stack<SearchState> stack = new Stack<SearchState>();
		stack.add(start_state);
		sharedStack.set(stack);
				while(!sharedStack.get().isEmpty()) {
			SearchState cur_state = sharedStack.get().pop();
			// depth reaches the maximum, stop branching
			if(cur_state.depth > max_depth) {
				int[] new_path = cur_state.pathAndPool();
				bind(new_path, cur_state.depth);
			} else {
				team.execute( getRegion(cur_state.freeCount()-1, cur_state) );
			}
		}
	}

	public static ParallelRegion getRegion( int count, SearchState state) {
		final int freeCount = count;
		final SearchState cur_state = state;
		
		
		ParallelRegion region = new ParallelRegion() {

			public void run() throws Exception {
				
				execute(0, freeCount, new IntegerForLoop() {
					Stack<SearchState> states;

					public void start() {
						states = new Stack<SearchState>();
					}
					public void run(int first, int last) throws Exception {
						// TODO Auto-generated method stub
						for(int x = first; x <= last; ++x) {
							SearchState new_state = cur_state.genNextState(x);
							long heuristic = calcHeuristic(new_state);
							if(heuristic < optimalCost) {
								states.push(new_state);
							}
						}
					}
					public void finish() {
						sharedStack.reduce(states, combineStacks);
					}
				});
			}	
		};
		
		return region;
	}
	public static long calcHeuristic(SearchState state) {
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
	public static void genMatrixMins() {
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


	public static void bind(int[] input, int offset) {
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
