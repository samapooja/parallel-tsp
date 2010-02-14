import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.Stack;
import java.util.TreeMap;

import edu.rit.pj.Comm;
import edu.rit.pj.ParallelRegion;
import edu.rit.pj.ParallelTeam;
import edu.rit.pj.reduction.SharedLong;
import edu.rit.pj.reduction.SharedObject;
/**
 * This class implements a brute force search
 * for the traveling salesman problem
 *
 * @author   Robert Clark
 */
public class OptimalTSPSMP {
	long[][] staticMatrix;
	long[][] weightMatrix;
	SharedObject<HashMap<Integer, Integer>> optimalPath = new SharedObject<HashMap<Integer, Integer>>();
	SharedLong optimalCost = new SharedLong(Long.MAX_VALUE);
	Stack<TSPState> rightStack;
	Stack<TSPState> leftStack;
	SortedMap<Long, TSPState> sharedStack;





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

		OptimalTSPSMP solver = new OptimalTSPSMP(theGraph);
		System.out.println("Starting Solver");

		solver.start();

		long stop = System.currentTimeMillis();
		System.out.println("Runtime for optimal TSP   : " + (stop-start) + " milliseconds");
		System.out.println();
	}

	OptimalTSPSMP(Graph inputGraph) {
		weightMatrix = inputGraph.getMatrix();
		int length = weightMatrix.length;

		staticMatrix = new long[length][length];
		inputGraph.printMatrix();
		for(int i=0; i< length; i++ ) {
			for(int j=0; j< length; j++ ) {
				staticMatrix[i][j] = weightMatrix[i][j];
			}
		}
		sharedStack = Collections.synchronizedSortedMap(new TreeMap<Long, TSPState>()); 

	}

	public void start() throws Exception {
		long[][] startMatrix = new long[weightMatrix.length][weightMatrix.length];
		System.arraycopy(weightMatrix, 0, startMatrix, 0, weightMatrix.length);
		TSPState startState = new TSPState(startMatrix, null);

		for(int i = 0 ; i <= 8; i++) {
			if( startState != null && !startState.isFinalState() ) {
				TSPState left = startState.leftSplit();
				TSPState right = startState.rightSplit();
				
				long lowerBound = right.getLowerBound();
				while(sharedStack.containsKey(lowerBound)) {
					lowerBound++;
				}
				sharedStack.put(lowerBound, right);
				startState = left;
			} else {
				break;
			}
		}
		long lowerBound = startState.getLowerBound();
		while(sharedStack.containsKey(lowerBound)) {
			lowerBound++;
		}
		sharedStack.put(lowerBound, startState);
		run(); 
	}

	public void run() throws Exception {
		new ParallelTeam().execute (new ParallelRegion() {

			public void run() throws Exception {
				TreeMap<Long, TSPState> leftStack = new TreeMap<Long, TSPState>();
				TSPState state;
				synchronized(sharedStack) {
					if(!sharedStack.isEmpty()) {
						state = sharedStack.remove(sharedStack.firstKey());
						leftStack.put(state.getLowerBound(), state);
					}
				}

				while(!leftStack.isEmpty() || !sharedStack.isEmpty() ) {
					if(!leftStack.isEmpty()) {
						state = leftStack.remove(leftStack.firstKey());
					} else {
						synchronized(sharedStack) {
							if(!sharedStack.isEmpty()) {
								state = sharedStack.remove(sharedStack.firstKey());
							} else {
								state = null;
							}
						}
					}


					if( state != null && state.isFinalState() ) {
						HashMap<Integer, Integer> thisPath = state.getPath();
						long thisCost = getCost(thisPath);
						if( ( thisPath.size() >= staticMatrix.length ) && ( thisCost < optimalCost.get() ) ) {
							optimalCost.set(thisCost);
							optimalPath.set(thisPath);
						}
					} else if (state != null ){
						if ( state.getLowerBound() < optimalCost.get() ) {
							TSPState left = state.leftSplit();
							leftStack.put(left.getLowerBound(), left);
							TSPState right = state.rightSplit();
							if(right != null) {
								synchronized(sharedStack) {
									long lowerBound = right.getLowerBound();
									while(sharedStack.containsKey(lowerBound)) {
										lowerBound++;
									}
									sharedStack.put(lowerBound, right);
								}
							}
						}
					}
				}
			}
		});


		System.out.println("The shortest cycle is of distance " + optimalCost);
		TSPState.printPath(optimalPath.get());
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
