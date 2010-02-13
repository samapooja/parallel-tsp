
public class TSPState {
	private final int[] nextBest;
	private final long[][] matrix;
	private final TSPState parent;
	public TSPState(long[][] weightMatrix, TSPState parent) {
		this.matrix = weightMatrix;
		this.nextBest = bestCoord();
		this.parent = parent;
	}
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
		TSPState traversal = this;

		return new int[matrix.length];
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
		return new TSPState(newmatrix, this);
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
		OptimalTSP.reduce(newmatrix);
		return new TSPState(newmatrix, this);
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
