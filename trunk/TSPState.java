
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
		this.nextBest = bestCoord();
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
	}

	public TSPState(long[][] weightMatrix, TSPState parent, int[] columnMap, int[] rowMap, int size) {
		this.matrix = weightMatrix;
		this.nextBest = bestCoord();
		this.parent = parent;
		this.rowMap = rowMap;
		this.columnMap = columnMap;
		this.size = size;
		if(parent != null) {
			optimalCost = parent.optimalCost + reduce();
		} else {
			optimalCost = reduce();
		}
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
	public final TSPState leftSplit(int[] best) {
		int x = best[0];
		int y = best[1];
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

		newmatrix[y][x] = Long.MAX_VALUE;
		return new TSPState(newmatrix, this, newCol, newRow, size);
	}

	/*
	 * Calculate the resulting weighted graph after a right split at coords x, y
	 */
	public final TSPState rightSplit(int[] best) {
		int x = best[0];
		int y = best[1];
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
		if(nextBest != null) return nextBest;
		int[] retVal = new int[2];
		long min = matrix[0][0];
		long min2 = min;
		int bestX = 0;
		int bestY = 0;
		if(min > matrix[0][1]) {
			min2 = matrix[0][1];
		}else{
			min = matrix[0][1];
			bestY = 1;
		}
		long bestVal = 0;
		for(int x = 0; x < matrix.length; x++) {
			min = Long.MAX_VALUE;
			for(int y = 0; y < matrix[x].length; y++) {
				if(matrix[x][y] < min) {
					min2 = min;
					min = matrix[x][y];
					bestX = x;
					bestY = y;
				} else if (matrix[x][y] < min2) {
					min2 = matrix[x][y];
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
}
