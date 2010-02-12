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

	public class TSPState {
		long[][] matrix;
		public TSPState(long[][] weightMatrix) {
			this.matrix = weightMatrix;
		}
		public long[][] matrix() { return matrix; }
	}

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
		solver.run();

		long stop = System.currentTimeMillis();
		System.out.println("Runtime for optimal TSP   : " + (stop-start) + " milliseconds");
		System.out.println();
	}

	OptimalTSP(Graph inputGraph) {
		weightMatrix = inputGraph.getMatrix();
		inputGraph.printMatrix();
		long lower_bound = OptimalTSP.reduce(inputGraph.getMatrix());
		inputGraph.printMatrix();
		System.out.println(lower_bound);
	}

	public void run() {
		Stack<TSPState> rightStack = new Stack<TSPState>();
		Stack<TSPState> leftStack = new Stack<TSPState>();

		long[][] startMatrix = new long[weightMatrix.length][weightMatrix.length];
		System.arraycopy(weightMatrix, 0, startMatrix, 0, weightMatrix.length);
		OptimalTSP.reduce(startMatrix);
		int[] startCoord = OptimalTSP.bestCoord(startMatrix);
		System.out.println("( " + startCoord[0] + ", " + startCoord[1] + " )");

		rightStack.push(new TSPState(OptimalTSP.rightSplit(startMatrix, startCoord[0], startCoord[1])));
		leftStack.push(new TSPState(OptimalTSP.leftSplit(startMatrix, startCoord[0], startCoord[1])));
	}

	/*
	 * This very ugly function calculates the coordinate where setting it to
	 * infinity will result in the largest increase in lower bound
	 */
	public static int[] bestCoord(long[][] matrix) {
		int[] retVal = new int[2];
		long min = matrix[0][0];
		long min2 = min;
		int bestX = 0;
		int secondBestX = 0;
		int bestY = 0;
		int secondBestY = 1;
		if(min > matrix[0][1]) {
			min2 = matrix[0][1];
		}else{
			min = matrix[0][1];
			secondBestY = 0;
			bestY = 1;
		}
		long bestVal = 0;
		for(int x = 0; x < matrix.length; x++) {
			min = Long.MAX_VALUE;
			for(int y = 0; y < matrix[x].length; y++) {
				if(matrix[x][y] < min) {
					min2 = min;
					secondBestY = bestY;
					secondBestX = bestX;
					min = matrix[x][y];
					bestX = x;
					bestY = y;
				} else if (matrix[x][y] < min2) {
					min2 = matrix[x][y];
					secondBestY = y;
					secondBestX = x;
				}
			}
			if (min2 > bestVal) {
				bestVal = min2;
				retVal[0] = bestX;
				retVal[1] = bestY;
			}
		}
		return retVal;
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

	/*
	 * Reduces the values by first subtracting the minimum of every
	 * column from each value, then subtracting the minimum of every
	 * row from each value. This in essesence normalizes the data.
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

	/*
	 * Calculate the resulting weighted graph after a right split at coords x, y
	 */
	public static long[][] rightSplit(long[][] matrix, int x, int y) {
		long[][] newmatrix = new long[matrix.length][matrix.length];
		for(int c = 0; c < matrix.length; c++) {
			System.arraycopy(matrix[c], 0, newmatrix[c], 0, matrix[c].length);
		}
		newmatrix[x][y] = Long.MAX_VALUE;
		newmatrix[y][x] = Long.MAX_VALUE;
		OptimalTSP.reduce(newmatrix);
		return newmatrix;
	}

	/*
	 * Calculate the resulting weighted graph after a left split at coords x, y
	 */
	public static long[][] leftSplit(long[][] matrix, int x, int y) {
		long[][] newmatrix = new long[matrix.length-1][matrix.length-1];
		int offset = 0;
		for(int c = 0; c < matrix.length; c++) {
			if(c == x) {
				offset = 1;
				continue;
			}
			System.arraycopy(matrix[c], 0, newmatrix[c-offset], 0, y);
			if(matrix.length != y) {
				System.arraycopy(matrix[c], y+1, newmatrix[c-offset], y, matrix[c].length - y - 1);
			}
		}
		newmatrix[y][x] = Long.MAX_VALUE;
		OptimalTSP.reduce(newmatrix);
		return newmatrix;
	}
}
