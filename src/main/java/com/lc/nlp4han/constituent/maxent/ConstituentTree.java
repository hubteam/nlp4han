package com.lc.nlp4han.constituent.maxent;

/**
 * 成分树
 * @author 王馨苇
 *
 */
public class ConstituentTree {

	private TreeNode treeNode;
	
	public void setTreeNode(TreeNode treeNode){
		this.treeNode = treeNode;
	}
	
	public TreeNode getTreeNode(){
		return this.treeNode;
	}
}
