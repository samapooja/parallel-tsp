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
	long optimalCost;
	int max_depth = 8;
	Stack<TSPState> rightStack;
	Stack<TSPState> leftStack;

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		String inputName = args[0];

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
		OptimalTSP.printMatrix(startMatrix);
		System.arraycopy(weightMatrix, 0, startMatrix, 0, weightMatrix.length);
		OptimalTSP.reduce(startMatrix);
		OptimalTSP.printMatrix(startMatrix);
		TSPState startState = new TSPState(startMatrix, null);
		leftStack.push(startState.leftSplit());
		OptimalTSP.printMatrix(leftStack.peek().matrix());
		rightStack.push(startState.rightSplit());
		OptimalTSP.printMatrix(rightStack.peek().matrix());
		// run();

	}

	public void run() {
		TSPState state;
		while(!leftStack.empty() && !rightStack.empty() ) {
			if(!leftStack.empty()) {
				state = leftStack.pop();
			} else {
				state = rightStack.pop();
			}
			if( state.isFinalState() ) {
				int[] thisPath = state.getPath();
				long thisCost = getCost(thisPath);
				if( thisCost < optimalCost ) {
					optimalCost = thisCost;
					optimalPath = thisPath;
				}
			} else {
				if ( OptimalTSP.reduce(state.matrix()) > optimalCost ) {
					// Continuing down this path is worthless. Do nothing
				} else {
					leftStack.push(state.leftSplit());
					rightStack.push(state.rightSplit());
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
				if(matrix[x][y] == Long.MAX_VALUE) {
					System.out.print("Inf\t");
				}else{
					System.out.print(matrix[x][y] + "\t");
				}
			}
			System.out.println("\n");
		}
	}

	/** 
	 * Returns the length to complete a cylce in the order specified.
	 * @return
	 */
	public long getCost(int[] path) {
		int distance = 0;
		for(int i=0; i<path.length-1; i++) {
			distance += weightMatrix[path[i]][path[i+1]];
		}
		distance += weightMatrix[path[path.length-1]][path[0]];
		
		return distance;
	}
	
	/*
	 * Reduces the values by first subtracting the minimum of every
	 * column from each value, then subtracting the minimum of every
	 * row from each value. This in essesence normalizes the data.
	 * 
	 * Returns the minimum possible value for a complete loop.
	 */
	public static long reduce(long[][] matrix) {
		long lower_bound = 0;
		for(int x = 0; x < matrix.length; x++) {
			long min = matrix[x][0];
			for(int y = x; y < matrix[x].length; y++) {
				min = Math.min(min, matrix[x][y]);
			}
			for(int y = x; y < matrix[x].length; y++) {
				if(matrix[x][y] != Long.MAX_VALUE){
					matrix[x][y] = matrix[x][y] - min;
					matrix[y][x] = matrix[y][x] - min;
				}
			}
			lower_bound += min;
		}
		for(int x = 0; x < matrix.length; x++) {
			long min = matrix[0][x];
			for(int y = x; y < matrix[x].length; y++) {
				min = Math.min(min, matrix[y][x]);
			}
			for(int y = x; y < matrix[x].length; y++) {
				if(matrix[x][y] != Long.MAX_VALUE){
					matrix[x][y] = matrix[x][y] - min;
					matrix[y][x] = matrix[y][x] - min;
				}
			}
			lower_bound += min;
		}
		return lower_bound;
	}
}
