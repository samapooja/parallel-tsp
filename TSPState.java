import java.util.HashMap;

public class TSPState {
	private final int[] nextBest;
	private final long[][] matrix;
	private final TSPState parent;
	private final int[] columnMap;
	private final int[] rowMap;
	private final int[] theBest = new int[2];
	private int size;
	private long optimalCost;
	private HashMap<Integer, Integer> path;

	public TSPState(long[][] weightMatrix, TSPState parent) {
		this.matrix = weightMatrix;
		this.size = weightMatrix.length;
		this.parent = parent;
		this.columnMap = new int[weightMatrix.length];
		this.rowMap = new int[weightMatrix.length];
		for(int x = 0; x < weightMatrix.length; x++) {
			this.columnMap[x] = x;
			this.rowMap[x] = x;
		}
		if(parent != null) {
			long reduction = reduce();
			optimalCost = parent.optimalCost + reduction;
			if(optimalCost < parent.optimalCost)
				optimalCost = Long.MAX_VALUE;
		} else {
			optimalCost = reduce();
		}
		this.nextBest = bestCoord();

	}

	public TSPState(long[][] weightMatrix, TSPState parent, int[] columnMap, int[] rowMap, int size) {
		this.matrix = weightMatrix;
		this.parent = parent;
		this.rowMap = rowMap;
		this.columnMap = columnMap;
		this.size = size;
		if(parent != null) {
			long reduction = reduce();
			optimalCost = parent.optimalCost + reduction;
			if(optimalCost < parent.optimalCost)
				optimalCost = Long.MAX_VALUE;
		} else {
			optimalCost = reduce();
		}
		this.nextBest = bestCoord();

	}


	public long getLowerBound() { return optimalCost; };
	public long[][] matrix() { return matrix; }

	/**
	 * Check if this array represents a final state,
	 * a state that cannot be divided anymore, i.e. has only one path remaining.
	 */
	public boolean isFinalState() {
		return (matrix.length < 1 || matrix[0].length < 1);
	}
	/**
	 * This is a terminal node with only one path remaining.
	 * Return that path.
	 * @return
	 */
	public HashMap<Integer, Integer> getPath() {
		if(path == null) {
			if(this.parent == null) {
				path = new HashMap<Integer, Integer>(size);
			} else {
				path = (HashMap<Integer, Integer>)this.parent.getPath().clone();
				path.put(this.parent.theBest[0], this.parent.theBest[1]);
			}
		}
		return this.path;
	}

	public static void printPath(HashMap<Integer, Integer> path) {
		System.out.println("Printing best route:");
		int index = 0;
		// Counter only exists to stop incorrect cycles, like the ones produced from bad 
		int counter = path.size();
		do {
			System.out.print(index + " , ");
			if(!path.containsKey(index)) {
				return;
			}
			index = path.get(index);
			counter--;
		} while (index != 0 && counter > 0);
		System.out.println(0);
	}

	/*
	 * Reduces the values by first subtracting the minimum of every
	 * column from each value, then subtracting the minimum of every
	 * row from each value. This in essesence normalizes the data.
	 * 
	 * Returns the minimum possible value for a complete loop.
	 */
	public long reduce() {
		long lower_bound = 0;
		int length = matrix.length;
		// For each row
		for(int x = 0; x < length; x++) {
			// discover this row's minimum
			long min = matrix[x][0];
			for(int y = 1; y < length; y++) {
				if( matrix[x][y] < min ) {
					min = matrix[x][y];
				}
			}
			// The subtract it from each value
			for(int y=0; y< length; y++) {
				matrix[x][y] = matrix[x][y] - min;
			}
			// And add it to the lower bound.
			lower_bound = lower_bound + min;
		}
		// For each column
		for(int y = 0; y < length; y++) {
			// discover this column's minimum
			long min = matrix[0][y];
			for(int x = 1; x < length; x++) {
				if( matrix[x][y] < min ) {
					min = matrix[x][y];
				}
			}

			// The subtract it from each value
			for(int x=0; x< length; x++) {
				matrix[x][y] = matrix[x][y] - min;
			}
			// And add it to the lower bound.
			lower_bound = lower_bound + min;
		}
		//printMatrix();
		return lower_bound;
	}
	/*
	 * Calculate the resulting weighted graph after a left split at coords x, y
	 */
	public final TSPState leftSplit() {
		int x = nextBest[0];
		int y = nextBest[1];


		long[][] newmatrix = new long[matrix.length-1][matrix.length-1];

		int offset = 0;
		for(int c = 0; c < matrix.length; c++) {
			// skip x
			if(c == x) {
				offset = 1;
				continue;
			}
			System.arraycopy(matrix[c], 0, newmatrix[c-offset], 0, y);
			if(matrix.length != y) {
				System.arraycopy(matrix[c], y+1, newmatrix[c-offset], y, matrix[c].length - y - 1);
			}
		}

		int[] newCol = new int[matrix.length-1];
		int[] newRow = new int[matrix.length-1];
		
		// Map the old rows and columns into new rows and columns
		System.arraycopy(columnMap, 0, newCol, 0, y);
		System.arraycopy(columnMap, y+1, newCol, y, (matrix.length-1)-y);
		System.arraycopy(rowMap, 0, newRow, 0, x);
		System.arraycopy(rowMap, x+1, newRow, x, (matrix.length-1)-x);

		int columnExists = -1;
		int rowExists = -1;

		for(int i=0; i< newCol.length; i++) {
			if(newRow[i] == y) {
				rowExists = i;
			}
			if (newCol[i] == x) {
				columnExists = i;
			}
			
		}
		if(columnExists >= 0 && rowExists >= 0) {
			newmatrix[rowExists][columnExists] = Long.MAX_VALUE;
		}
		return new TSPState(newmatrix, this, newCol, newRow, size);
	}

	/*
	 * Calculate the resulting weighted graph after a right split at coords x, y
	 */
	public final TSPState rightSplit() {
		int x = nextBest[0];
		int y = nextBest[1];
		long[][] newmatrix = new long[matrix.length][matrix.length];
		for(int c = 0; c < matrix.length; c++) {
			System.arraycopy(matrix[c], 0, newmatrix[c], 0, matrix[c].length);
		}
		if(newmatrix[x][y] == Long.MAX_VALUE) {
			return null;
		}
		newmatrix[x][y] = Long.MAX_VALUE;

		// This shouldn't really be here, but it needs to be run because they are looked
		// at later.
		fixNextBest();
		TSPState newState = new TSPState(newmatrix, this, columnMap, rowMap, size);
		
		/*
		if(newState.optimalCost == Long.MAX_VALUE) {
			return null;
		}
		*/

		return newState;
	}
	
	public final void fixNextBest() {
		theBest[0] = rowMap[nextBest[0]];
		theBest[1] = columnMap[nextBest[1]];
	}

	/*
	 * Find the ZERO that when set to infinity, allows the most to
	 * be reduced from its row and column. AKA find the element that,
	 * when ignored, results in the largest minimum element of row +
	 * minimum element of column.
	 */
	public final int[] bestCoord() {
		if(nextBest != null) return nextBest;
		long largestSoFar = 0;
		int[] retVal = new int[2]; // X, Y
		// For each element of the array
		for(int x = 0; x < matrix.length; x++) {
			for(int y = 0; y < matrix.length; y++) {
				// If this element is zero
				if(matrix[x][y] == 0 ) {
					// add the next lowest row value and the next lowest column value.
					long reduction = 
						getNextLowestRowValue(matrix[x], y) 
						+ getNextLowestColumnValue(matrix, y, x);
					
					if( reduction >= largestSoFar) {
						retVal[0] = x;
						retVal[1] = y;
						largestSoFar = reduction;
					}
				}
			}
		}
		// Return the lowest sum
		return retVal;
	}

	// Helpers for bestCoord().
	private final long getNextLowestRowValue(long[] row, int index) {
		long lowest = Long.MAX_VALUE;
		for(int i=0; i<row.length; i++) {
			if(row[i] < lowest && i != index) {
				lowest = row[i];
			}
		}
		return lowest;
	}

	private final long getNextLowestColumnValue(long[][] array, int columnIndex, int elementIndex) {
		long lowest = Long.MAX_VALUE;
		for(int i=0; i<array.length; i++) {
			if(array[i][columnIndex] < lowest && i != elementIndex) {
				lowest = array[i][columnIndex];
			}
		}
		return lowest;
	}
	/*
	 * Print the state with labels reflecting ORIGINAL VALUES!
	 */
	public final void printMatrix () {
		System.out.println("Adjacency matrix of graph weights:\n");
		System.out.print("\t");
		for(int x = 0; x < matrix.length; x++) 
			System.out.print(columnMap[x] + "\t");

		System.out.println("\n");
		for(int x = 0; x < matrix.length; x++){
			System.out.print(rowMap[x] + "\t");
			for(int y = 0; y < matrix[x].length; y++) {
				if(matrix[x][y] > Long.MAX_VALUE - 100000) {
					System.out.print("Inf\t");
				}else{
					System.out.print(matrix[x][y] + "\t");
				}
			}
			System.out.println("\n");
		}
	}

}
