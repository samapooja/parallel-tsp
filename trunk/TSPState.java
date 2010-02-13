
public class TSPState {
	private final int[] nextBest;
	private final long[][] matrix;
	private final TSPState parent;
	private final int[] columnMap;
	private final int[] rowMap;
	private int size;
	private long optimalCost;

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
			optimalCost = parent.optimalCost + reduce();
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
			optimalCost = parent.optimalCost + reduce();
		} else {
			optimalCost = reduce();
		}
		this.nextBest = bestCoord();

	}

		
	public long getLowerBound() { return optimalCost; };
	public long[][] matrix() { return matrix; }

	// FAKE METHODS NEED IMPLEMENTATION!
	// 
	/**
	 * Check if this array represents a final state,
	 * a state that cannot be divided anymore, i.e. has only one path remaining.
	 */
	public boolean isFinalState() {
		return (matrix.length < 2);
	}
	/**
	 * This is a terminal node with only one path remaining.
	 * Return that path.
	 * @return
	 */
	public int[] getPath() {
		int[] pathX = new int[size+1];
		int[] pathY = new int[size+1];
		TSPState traversal = this.parent;
		int x = 0;
		while(traversal != null) {
			pathX[x] = traversal.rowMap[traversal.bestCoord()[0]];
			pathY[x] = traversal.columnMap[traversal.bestCoord()[1]];
			traversal = traversal.parent;
			x++;
		}
		for(int c = 0; c < pathX.length; c++) {
			System.out.println(pathX[c] + ", " + pathY[c]);
		}
		return new int[matrix.length];
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
		for (int c = 0; c < matrix.length-1; c++) {
			if(c >= x){
				System.out.println("Map col " + c + " to " + columnMap[c+1]);
				newCol[c] = columnMap[c+1];
			}else{
				newCol[c] = columnMap[c];
			}
			if(c >= y){
				System.out.println("Map row " + c + " to " + rowMap[c+1]);
				newRow[c] = rowMap[c+1];
			}else{
				newRow[c] = rowMap[c];
			}
		}
		
		boolean columnExists = false;
		boolean rowExists = false;
		
		for(int i=0; i< newCol.length; i++) {
			if (newCol[i] == x) {
				columnExists = true;
			}
			if(newRow[i] == y) {
				rowExists = true;
			}
		}
		if(columnExists && rowExists) {
			newmatrix[y][x] = Long.MAX_VALUE;
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
		newmatrix[x][y] = Long.MAX_VALUE;
		newmatrix[y][x] = Long.MAX_VALUE;
		return new TSPState(newmatrix, this, columnMap, rowMap, size);
	}

	/*
	 * This very ugly function calculates the coordinate where setting it to
	 * infinity will result in the largest increase in lower bound
	 */
	public final int[] bestCoord() {
		printMatrix();
		if(nextBest != null) return nextBest;
		int[] retVal = new int[2];
		
		System.out.println("Best coordinates are:" + retVal[0] + "," + retVal[1]);
		return retVal;
	}
	
	/*
	 * Print the state with labels reflecting ORIGINAL VALUES!
	 */
	public void printMatrix () {
		System.out.println("With proper headers:");
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
