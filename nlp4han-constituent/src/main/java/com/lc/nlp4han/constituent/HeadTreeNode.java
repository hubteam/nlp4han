package com.lc.nlp4han.constituent;

import java.util.ArrayList;
import java.util.List;

/**
 * 带中心词的成分树节点
 * 
 * @author 王馨苇
 *
 */
public class HeadTreeNode extends TreeNode{

	private String headWords;
	private String headWordsPos;

	public HeadTreeNode(String nodename){
		super(nodename);
	}

	public void setHeadWords(String headWords){
		this.headWords = headWords;
	}
	
	public String getHeadWords(){
		return this.headWords;
	}

	public void setHeadWordsPos(String headWordsPos){
		this.headWordsPos = headWordsPos;
	}
	
	public String getHeadWordsPos(){
		return this.headWordsPos;
	}
	
	//返回父节点
	public HeadTreeNode getParent(){
		return (HeadTreeNode) parent;
	}

	/**
	 * 第一个儿子
	 * @return
	 */
	public HeadTreeNode getFirstChild(){
		return (HeadTreeNode) this.children.get(0);
	}
	
	/**
	 * 获取第一个儿子头结点
	 * @return
	 */
	public String getFirstChildHeadWord(){
		return ((HeadTreeNode) this.children.get(0)).getHeadWords();
	}
	
	/**
	 * 获取第一个儿子头结点词性
	 * @return
	 */
	public String getFirstChildHeadWordPos(){
		return ((HeadTreeNode) this.children.get(0)).getHeadWordsPos();
	}
	
	/**
	 * 获取最后一个儿子头结点
	 * @return
	 */
	public String getLastChildHeadWord(){
		return ((HeadTreeNode) this.children.get(this.children.size()-1)).getHeadWords();
	}
	
	/**
	 * 获取最后一个儿子头结点词性
	 * @return
	 */
	public String getLastChildHeadWordPos(){
		return ((HeadTreeNode) this.children.get(this.children.size()-1)).getHeadWordsPos();
	}
	
	/**
	 * 获取最后一个儿子
	 * @return
	 */
	public HeadTreeNode getLastChild(){
		return (HeadTreeNode) this.children.get(this.children.size()-1);
	}
	
	/**
	 * 获取第i个儿子
	 * @param i 儿子的序数
	 * @return
	 */
	public HeadTreeNode getIChild(int i){
		return (HeadTreeNode) this.children.get(i);
	}
	
	/**
	 * 获取第i个儿子头结点
	 * @param i 儿子的序数
	 * @return
	 */
	public String getIChildHeadWord(int i){
		return ((HeadTreeNode) this.children.get(i)).getHeadWords();
	}
	
	/**
	 * 获取第i个儿子头结点词性
	 * @param i 儿子的序数
	 * @return
	 */
	public String getIChildHeadWordPos(int i){
		return ((HeadTreeNode) this.children.get(i)).getHeadWordsPos();
	}
	
	//返回子节点列表
	public List<HeadTreeNode> getChildren(){
		List<HeadTreeNode> hnode = new ArrayList<>();
		for (TreeNode treeNode : children) {
			HeadTreeNode node = (HeadTreeNode) treeNode;
			hnode.add(node);
		}
		return hnode;
	}
	
	/**
	 * 带有头结点的树的输出（一行括号表达式）
	 */
	@Override
	public String toString() {
		if(super.children.size() == 0){
			return " " + this.nodename + "[" + this.getWordIndex() + "]";
		}else{
			String treestr = "";
			treestr = "(" + this.nodename + "{" + this.headWords + "[" + this.headWordsPos + "]}";
			
			for (HeadTreeNode node:getChildren()) {
				treestr += node.toString();
			}
			treestr += ")";
			return treestr;
		}
	}
	
	/**
	 * 没有头结点的括号表达式
	 * @return
	 */
	public String toBracket(){
		if(this.children.size() == 0){
			return " " + this.nodename + "[" + getWordIndex() + "]";
		}else{
			String treestr = "";
			treestr = "(" + this.nodename;
			for (TreeNode node:this.children) {
				treestr += node.toBracket();
			}
			treestr += ")";
			return treestr;
		}
	}

	@Override
	public boolean equals(Object obj) {
		HeadTreeNode node = (HeadTreeNode)obj;
		if(this.toString().equals(node.toString())){
			return true;
		}else{
			return false;
		}
	}
}
