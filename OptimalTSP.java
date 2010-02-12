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

	}

	public static int[] bestCoord(long[][] matrix) {
		int[] retVal = new int[2];
		for(int x = 0; x < matrix.length; x++) {
			long min = matrix[x][0];
			for(int y = x; y < matrix[x].length; y++) {
				min = Math.min(min, matrix[x][y]);
			}
		}
		return retVal;
	}

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

	public static long[][] leftSplit(long[][] matrix, int x, int y) {
		long[][] newmatrix = new long[matrix.length-1][matrix.length-1];
		for(int c = 0; c < matrix.length; c++) {
			if(c == x) {
				c++;
				continue;
			}
			System.arraycopy(matrix[c], 0, newmatrix[c], 0, y);
			if(matrix.length != y) {
				System.arraycopy(matrix[c], y + 1, newmatrix[c], y, matrix[c].length - y - 1);
			}
		}
		newmatrix[y][x] = Long.MAX_VALUE;
		OptimalTSP.reduce(newmatrix);
		return newmatrix;
	}
}
