package com.lc.nlp4han.constituent;

/**
 * 成分树结构设计
 * @author 王馨苇
 *
 */
public class ConstituentTree {

	private TreeNode root;
	
	public ConstituentTree()
	{
		
	}
	
	public ConstituentTree(TreeNode rootNode)
	{
		this.root = rootNode;
	}
	
	public void setRoot(TreeNode treeNode){
		this.root = treeNode;
	}
	
	public TreeNode getRoot(){
		return this.root;
	}

	@Override
	public String toString() {
		return root.toString();
	}
}
