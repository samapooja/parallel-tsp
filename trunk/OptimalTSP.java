import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.rit.pj.Comm;

/**
 * This class implements a brute force search
 * for the traveling salesman problem
 *
 * @author   Robert Clark
 * @author   Danny Iland
 */
public class OptimalTSP {
	long[][] staticMatrix;
	long[][] weightMatrix;
	HashMap<Integer, Integer> optimalPath = new HashMap<Integer, Integer>();
	long  optimalCost = Long.MAX_VALUE;
	SortedMap<Long, TSPState> rightMap;
	SortedMap<Long, TSPState> leftMap;

	public static void main(String[] args) throws Exception {

		long start = System.currentTimeMillis();
		Comm.init(args);
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
		System.out.println("Starting Solver");

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
		rightMap = new TreeMap<Long, TSPState>(); 
		leftMap = new TreeMap<Long, TSPState>();

	}

	public void start() throws Exception {
		long[][] startMatrix = new long[weightMatrix.length][weightMatrix.length];
		System.arraycopy(weightMatrix, 0, startMatrix, 0, weightMatrix.length);
		TSPState startState = new TSPState(startMatrix, null);
		
		TSPState left = startState.leftSplit();
		leftMap.put(left.getLowerBound(), left);
		TSPState right = startState.rightSplit();
		rightMap.put(right.getLowerBound(), right);
		run();

}

public void run() throws Exception {
	TSPState state;
	while(!leftMap.isEmpty() || !rightMap.isEmpty() ) {
		if(!leftMap.isEmpty()) {
			state = leftMap.remove(leftMap.firstKey());
		} else {
			if(!rightMap.isEmpty()) {
				state = rightMap.remove(rightMap.firstKey());
			} else {
				state = null;
			}
		}
		if( state != null && state.isFinalState() ) {
			HashMap<Integer, Integer> thisPath = state.getPath();
			long thisCost = getCost(thisPath);
			if( ( thisPath.size() >= staticMatrix.length ) && ( thisCost < optimalCost ) ) {
				optimalCost = thisCost;
				optimalPath = thisPath;
			}
		} else if (state != null ){
			if ( state.getLowerBound() < optimalCost ) {
				TSPState left = state.leftSplit();
				long lowerBound = left.getLowerBound();
				while(leftMap.containsKey(lowerBound)) {
					lowerBound++;
				}
				leftMap.put(lowerBound, left);
				TSPState right = state.rightSplit();
				if(right != null) {
					lowerBound = right.getLowerBound();
					while(rightMap.containsKey(lowerBound)) {
						lowerBound++;
					}
					rightMap.put(lowerBound, right);
				}
			}
		}
	}
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
