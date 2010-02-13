import java.util.Stack;
/**
 * This class implements a brute force search
 * for the traveling salesman problem
 *
 * @author   Robert Clark
 */
public class OptimalTSP {
	long[][] weightMatrix;
	int[] optimalPath;
	long optimalCost = Long.MAX_VALUE;
	int max_depth = 8;
	Stack<TSPState> rightStack;
	Stack<TSPState> leftStack;

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		if(args.length != 1) {
			System.err.println("Usage: OptimalTSP graphFile");
			System.exit(0);
		}

		Graph theGraph = new Graph();
		try {
			theGraph.loadMatrix(args[0]);
		} catch(Exception e) {
			System.out.println("Unable to save matrix");
			System.exit(0);
		}

		OptimalTSP solver = new OptimalTSP(theGraph);
		solver.start();

		long stop = System.currentTimeMillis();
		System.out.println("Runtime for optimal TSP   : " + (stop-start) + " milliseconds");
		System.out.println();
	}

	OptimalTSP(Graph inputGraph) {
		weightMatrix = inputGraph.getMatrix();
		inputGraph.printMatrix();
	}

	public void start() {
		rightStack = new Stack<TSPState>();
		leftStack = new Stack<TSPState>();
		long[][] startMatrix = new long[weightMatrix.length][weightMatrix.length];
		System.arraycopy(weightMatrix, 0, startMatrix, 0, weightMatrix.length);
		TSPState startState = new TSPState(startMatrix, null);
		leftStack.push(startState.leftSplit());
		//rightStack.push(startState.rightSplit());
		run();
	}

	public void run() {
		TSPState state;
		while(!leftStack.empty() || !rightStack.empty() ) {
			//if(!leftStack.empty()) {
				state = leftStack.pop();
//			} else {
//				state = rightStack.pop();
//			}
			if( state.isFinalState() ) {
				int[] thisPath = state.getPath();
				long thisCost = getCost(thisPath);
				if( thisCost < optimalCost ) {
					optimalCost = thisCost;
					optimalPath = thisPath;
				}
			} else {
				if ( state.getLowerBound() > optimalCost ) {
					// Continuing down this path is worthless. Do nothing
				} else {
					leftStack.push(state.leftSplit());
					//rightStack.push(state.rightSplit(best));
				}
			}
		}
	}



	/*
	 * simply print a matrix
	 */
	public static void printMatrix (long[][] matrix) {
		System.out.println("Adjacency matrix of graph weights:\n");
		System.out.print("\t");
		for(int x = 0; x < matrix.length; x++) 
			System.out.print(x + "\t");

		System.out.println("\n");
		for(int x = 0; x < matrix.length; x++){
			System.out.print(x + "\t");
			for(int y = 0; y < matrix[x].length; y++) {
				if(matrix[x][y] > Long.MAX_VALUE - 10000) {
					System.out.print("Inf\t");
				}else{
					System.out.print(matrix[x][y] + "\t");
				}
			}
			System.out.println("\n");
		}
	}

	/** 
	 * Returns the length to complete a cycle in the order specified.
	 */
	public long getCost(int[] path) {
		int distance = 0;
		for(int i=0; i<path.length-1; i++) {
			distance += weightMatrix[path[i]][path[i+1]];
		}
		distance += weightMatrix[path[path.length-1]][path[0]];
		
		return distance;
	}

}
