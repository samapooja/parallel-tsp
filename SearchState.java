class SearchState {
	public SearchState parent = null;
	public long cost;
	public int depth;
	public int node;
	private int[] free_nodes;
	public long[][] matrixWeights;

	public SearchState(int[] free_nodes, long[][] matrixWeights) {
		this.free_nodes = free_nodes;
		this.matrixWeights = matrixWeights;
		this.cost = 0;
		this.depth = 1;
		this.node = 0;
	}
	
	public SearchState() {
		// blank ctor
	}

	public SearchState genNextState(int nodeIndex) {
		SearchState nextState = new SearchState();
		nextState.parent = this;
		nextState.matrixWeights = this.matrixWeights;
		nextState.free_nodes = SearchState.arrayCut(this.free_nodes, nodeIndex);
		nextState.node = this.free_nodes[nodeIndex];
		nextState.cost = this.cost + matrixWeights[this.node][nextState.node];
		nextState.depth = this.depth + 1;
		return nextState;
	}

	/**
	 * This cuts an array element out of the array
	 **/
	public static int[] arrayCut(int[] c_arr, int index) {
		int[] new_arr = new int[c_arr.length - 1];
		System.arraycopy(c_arr, 0, new_arr, 0, index);
		if(c_arr.length != index) {
			System.arraycopy(c_arr, index + 1, new_arr, index, c_arr.length - index - 1);
		}
		return new_arr;
	}

	public int[] getFreeNodes() {
		return free_nodes;
	}

	public int freeCount() {
		return free_nodes.length;
	}

	public int[] pathAndPool() {
		int[] path = new int[depth + this.free_nodes.length];
		SearchState traverse = this;
		for(int x = depth-1; x >= 0; x--) {
			path[x] = traverse.node;
			traverse = traverse.parent;
		}
		System.arraycopy(free_nodes, 0, path,
						 depth, free_nodes.length);
		return path;
	}
}
