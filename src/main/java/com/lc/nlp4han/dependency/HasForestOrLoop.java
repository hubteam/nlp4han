package com.lc.nlp4han.dependency;

/**
 * 判断是否有环路
 * 
 * @author 王馨苇
 *
 */
public class HasForestOrLoop {

	/**
	 * 判断是否有森林
	 * 
	 * @param word 图中的节点
	 * @param begin 遍历开始的位置
	 * @return 是否存在森林
	 */
	public static boolean hasForest(String[] word, String[] begin){
		MyGraph graph = new MyGraph(begin);
		DepthSearch search = new DepthSearch();
		int[] visited = graph.getVisited();
		
		int flag = search.DFS(graph, 0, visited);
		if(flag == 1){
			return true;
		}else{		
			return false;
		}
	}
	
	/**
	 * 判断是否有环路
	 * 
	 * @param begin 图中的各个节点
	 * @param position 遍历开始的位置
	 * @return 是否有环路
	 */
	public static boolean hasLoop(String[] begin, int position){
		MyGraph graph = new MyGraph(begin);
		DepthSearch search = new DepthSearch();
		int[] visited = graph.getVisited();
		
		int flag = search.DFSLoop(graph, position, visited);
		if(flag == 1){		
			return true;
		}else{		
			return false;
		}
	}
	
	
}
