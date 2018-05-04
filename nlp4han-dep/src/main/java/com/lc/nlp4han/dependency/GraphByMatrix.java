package com.lc.nlp4han.dependency;

/**
 * 用矩阵表示图
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class GraphByMatrix {

	// 节点数目
	private int nodes;
	private int[] nodelLabels; // 存放节点标签
	private int[][] edge; // 边
	private int[] visited;

	/**
	 * 构造
	 * 
	 * @param depIndexes
	 *            节点的数组
	 */
	public GraphByMatrix(String[] depIndexes) {
		this.nodes = depIndexes.length + 1;
		this.edge = new int[nodes][nodes];
		this.nodelLabels = new int[nodes];
		this.visited = new int[nodes];

		// 初始化边
		for (int i = 0; i < nodes - 1; i++) {
			if (Integer.parseInt(depIndexes[i]) != -1) {
				this.edge[Integer.parseInt(depIndexes[i])][i + 1] = 1;
			}

		}

		// 初始化节点
		for (int i = 0; i < nodes; i++) {
			this.nodelLabels[i] = i;
		}
	}

	/**
	 * 构造
	 * 
	 * @param depIndexes
	 *            词语组成的节点数组
	 * @param length
	 *            节点个数
	 */
	public GraphByMatrix(String[] depIndexes, int length) {
		this.nodes = length + 1;
		this.edge = new int[nodes][nodes];
		this.nodelLabels = new int[nodes];
		this.visited = new int[nodes];

		// 初始化边
		for (int i = 0; i < nodes - 1; i++) {
			if (Integer.parseInt(depIndexes[i]) != -1) {
				this.edge[Integer.parseInt(depIndexes[i])][i + 1] = 1;
			}

		}

		// 初始化节点
		for (int i = 0; i < nodes; i++) {
			this.nodelLabels[i] = i;
		}
	}

	/**
	 * 获取图的矩阵结果
	 * 
	 * @return 包含图中边的关系的二维数组
	 */
	public int[][] getMatrix() {
		return this.edge;
	}

	/**
	 * 用数字表示节点
	 * 
	 * @return 节点的数组
	 */
	public int[] getNodeData() {
		return this.nodelLabels;
	}

	/**
	 * 获取节点
	 * 
	 * @return 节点
	 */
	public int getNumOfNodes() {
		return this.nodes;
	}

	/**
	 * 记录节点是否被访问的数组
	 * 
	 * @return 记录访问与否的数组
	 */
	public int[] getVisited() {
		return this.visited;
	}

}
