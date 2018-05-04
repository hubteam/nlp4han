package com.lc.nlp4han.dependency;

/**
 * 深度遍历算法
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class DepthSearch {

	private int countNode = 0;

	/**
	 * 获取当前顶点k的第一个邻接顶点的位置
	 * 
	 * @param graph 图
	 * @param k 当前节点
	 * @return 第一个邻接顶点
	 */
	private int getFirst(GraphByMatrix graph, int k){
		int node = graph.getNumOfNodes();
		int[][] edge = graph.getMatrix();
		if(k<0 || k>node-1){
			return -1;
		}
		
		for (int i = 0; i < node; i++) {
			if(edge[k][i] == 1){
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * 获取当前顶点k的第t个邻接顶点的位置
	 * 
	 * @param graph 边连接出来的图
	 * @param k 当前节点
	 * @param t 当前顶点k的第t个邻接顶点
	 * @return 第t个邻接顶点的位置
	 */
	private int getNext(GraphByMatrix graph,int k,int t){
		int node = graph.getNumOfNodes();
		int[][] edge = graph.getMatrix();
		if(k<0 || k>node-1 || t<0 || t>node-1){
			return -1;
		}
		
		for(int i = t+1; i < node; i++){
			if(edge[k][i] == 1){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 深度遍历算法
	 * 
	 * @param graph 图
	 * @param k 当前节点
	 * @param visited 节点是否被访问的数组
	 * 
	 * @return 判断是否有森林的标记
	 */
	public boolean hasForestDFS(GraphByMatrix graph, int k, int[] visited){
		int u;//k的邻接顶点
		
		countNode++;
		visited[k] = 1;
		u = getFirst(graph, k);
		while(u != -1){
			if(visited[u] == 0){
				hasForestDFS(graph, u, visited);
			}
			u = getNext(graph, k, u);
		}

		if(countNode == graph.getNumOfNodes()){
			return false;
		}else{
			return true;
		}
	}
	
	private int flag = 1;
	
	/**
	 * 判断是否有环
	 * 
	 * @param graph 图
	 * @param k 当前节点
	 * @return 是否有环路
	 */
	public boolean hasLoopDFS(GraphByMatrix graph, int k, int[] visited){
		int u;//k的邻接顶点

		visited[k] = 1;
		u = getFirst(graph, k);
		while(u != -1){
			if(visited[u] == 1){
				flag = -1;
				break;
			}else if(visited[u] == 0){
				hasLoopDFS(graph, u, visited);
			}
			
			u = getNext(graph, k, u);
		}

		if(flag == -1){
			return false;
		}else{
			return true;
		}
	}
}
