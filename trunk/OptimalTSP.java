import java.util.HashMap;
import java.util.Stack;
/**
 * This class implements a brute force search
 * for the traveling salesman problem
 *
 * @author   Robert Clark
 */
public class OptimalTSP {
	long[][] staticMatrix;
	long[][] weightMatrix;
	HashMap<Integer, Integer> optimalPath;
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
			System.out.println("Unable to load matrix");
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
		int length = weightMatrix.length;

		staticMatrix = new long[length][length];
		inputGraph.printMatrix();
		for(int i=0; i< length; i++ ) {
			for(int j=0; j< length; j++ ) {
				staticMatrix[i][j] = weightMatrix[i][j];
			}
		}
	}

	public void start() {
		rightStack = new Stack<TSPState>();
		leftStack = new Stack<TSPState>();
		long[][] startMatrix = new long[weightMatrix.length][weightMatrix.length];
		System.arraycopy(weightMatrix, 0, startMatrix, 0, weightMatrix.length);
		TSPState startState = new TSPState(startMatrix, null);
		leftStack.push(startState.leftSplit());
		rightStack.push(startState.rightSplit());

		run();
	}

	public void run() {
		TSPState state;
		while(!leftStack.empty() || !rightStack.empty() ) {
			if(!leftStack.empty()) {
				state = leftStack.pop();
			} else {
				state = rightStack.pop();
			}
			if( state.isFinalState() ) {
				HashMap<Integer, Integer> thisPath = state.getPath();
				long thisCost = getCost(thisPath);
				
				if( ( thisPath.size() >= staticMatrix.length ) && ( thisCost < optimalCost ) ) {
					optimalCost = thisCost;
					optimalPath = thisPath;
				}
				// System.out.println("The shortest cycle is of distance " + optimalCost);

				// Having it halt after completion, there's something wrong
				// with the right states being created. 
			} else {
				if ( state.getLowerBound() < optimalCost ) {
					leftStack.push(state.leftSplit());
					TSPState rightVal = state.rightSplit();
					if(rightVal != null)
						rightStack.push(state.rightSplit());
				}
			}
		}
		// This is where this should be. That return is just blocking all the badly
		// created states from executing. Our focus should be on figuring out why
		// the states are bad (lower bound = Long.MIN ? Weird shit) and fixing it.
		// Once that happens, removing the return should result in a working TSP Solver.
		// 
		
		System.out.println("The shortest cycle is of distance " + optimalCost);
		TSPState.printPath(optimalPath);
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
	public long getCost(HashMap<Integer, Integer> path) {
		long distance = 0;
		int start = 0;
		int end = 0;
		int count = 0;
		do {
			if(!path.containsKey(start))
				return Long.MAX_VALUE;
			end = path.get(start);
			distance = distance + staticMatrix[start][end];
			start = end;
			count++;
		} while (start != 0);
		
		if(count < path.size())
			return Long.MAX_VALUE;
		return distance;
	}

}
