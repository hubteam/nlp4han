package com.lc.nlp4han.constituent.maxent;

import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.HeadTreeNode;

/**
 * 将带头结点的句法树转换成动作序列
 * @author 王馨苇
 *
 */
public class HeadTreeToActions {

	//动作序列
	private static List<String> actions = new ArrayList<String>();
	//第一步POS后得到的n颗子树
	private static List<HeadTreeNode> posTree = new ArrayList<HeadTreeNode>();
	//记录第二部CHUNK后得到的n棵子树
	private static List<HeadTreeNode> chunkTree = new ArrayList<HeadTreeNode>();
	//第三部得到的列表
	private static List<List<HeadTreeNode>> buildAndCheckTree = new ArrayList<List<HeadTreeNode>>();
	private static int i = 0;//List<TreeNode> subTree中的index
		
	/**
	 * 第一步POS
	 * @param tree 一棵树
	 */
	private static void getActionPOS(HeadTreeNode tree){

		//如果是叶子节点，肯定是具体的词，父节点是词性
		if(tree.getChildren().size() == 0){
			posTree.add(tree.getParent());
			actions.add(tree.getParent().getNodeName());
		}else{
			//不是叶子节点的时候，递归
			for (HeadTreeNode node:tree.getChildren()) {
				getActionPOS(node);
			}
		}
	}
	
	/**
	 * 第二部CHUNK
	 * @param tree 一颗完整的句法树
	 * @param subTree 第一步POS后得到的若干子树
	 * @throws CloneNotSupportedException 
	 */
	private static void getActionCHUNK(HeadTreeNode tree,List<HeadTreeNode> subTree) throws CloneNotSupportedException{
		//为了防止原来的tree被修改
		HeadTreeNode treeCopy = (HeadTreeNode) tree.clone();
		//如果当前节点只有一颗子树，这子树可能就是具体的词了，但也存在特殊：（NP(NN chairman)）
		//这样得到的子树为1的都是具体的词性和词语组成的子树
		if(treeCopy.getChildrenNum() == 1 && treeCopy.getFirstChild().getChildrenNum() == 0){	
			//当前节点的父节点只有这一颗子树，也就是（NP(NN chairman)）这种情况
			if(treeCopy.getParent().getChildrenNum() == 1){	
				//用start标记作为当前节点的父节点
				actions.add("start_"+treeCopy.getParent().getNodeName());
				HeadTreeNode node = new HeadTreeNode("start_"+treeCopy.getParent().getNodeName());
				node.addChild(treeCopy);
				chunkTree.add(node);
				//当前节点的父节点不止一个，就遍历所有的子树，判断当前节点是否为flat结构
			}else if(treeCopy.getParent().getChildrenNum() > 1){
				
				int record = -1;
				for (int j = 0; j < treeCopy.getParent().getChildrenNum(); j++) {
					//如果有一颗子树破坏了flat结构，退出
					if(treeCopy.getParent().getIChild(j).getChildrenNum() > 1){
						record = j;
						break;
					//(PP-CLR(TO to)(NP(PRP it)))针对这种结构
					}else if(treeCopy.getParent().getIChild(j).getChildrenNum()  == 1
							&& treeCopy.getParent().getIChild(j).getFirstChild().getChildrenNum() != 0){
						record = j;
						break;
					}
				}
				//当前节点的父节点的所有子树满足flat结构
				if(record == -1){
					//当前节点是是第一颗子树，
					if(treeCopy.getParent().getFirstChild().equals(treeCopy)){
						actions.add("start_"+treeCopy.getParent().getNodeName());
						HeadTreeNode node = new HeadTreeNode("start_"+treeCopy.getParent().getNodeName());
						node.addChild(treeCopy);
						chunkTree.add(node);
					}else{
						//不是第一个
						actions.add("join_"+treeCopy.getParent().getNodeName());
						HeadTreeNode node = new HeadTreeNode("join_"+treeCopy.getParent().getNodeName());
						node.addChild(treeCopy);
						chunkTree.add(node);
					}
				//当前节点的父节点的子树不满足flat结构	，用other标记
				}else{
					actions.add("other");
					HeadTreeNode node = new HeadTreeNode("other");
					node.addChild(treeCopy);
					chunkTree.add(node);
				}		
			}
		}else{
			//当前节点不满足上述条件，递归
			for (HeadTreeNode node:treeCopy.getChildren()) {
				getActionCHUNK(node,subTree);
			}
		}
	}

	/**
	 * 第三步：build和check
	 * @param tree 一棵完整的句法树
	 * @param subTree 第二步CHUNK得到的若干颗子树进行合并之后的若干颗子树
	 */
	private static void getActionBUILDandCHECK(HeadTreeNode tree,List<HeadTreeNode> subTree){
		
		//这里的subTree用于判断，定义一个subTree的副本用于过程中的改变
		//这里的TreeNode实现了克隆的接口，这里也就是深拷贝
		List<HeadTreeNode> subTreeCopy;
		//如果当前的节点子树是第二步CHUNK后合并后的一个结果
		if(subTree.get(i).equals(tree)){	
			
			if(tree.getParent().getChildrenNum() == 1){
				//添加start标记
				actions.add("start_"+tree.getParent().getNodeName());
				//改变subTreeCopy
				HeadTreeNode node = new HeadTreeNode("start_"+tree.getParent().getNodeName());
				node.addChild(subTree.get(i));
				subTree.set(i, node);
				subTreeCopy = new ArrayList<HeadTreeNode>(subTree);
				buildAndCheckTree.add(subTreeCopy);				
				actions.add("yes");
				
				//改动的地方【为yes的时候先不合并加入，用于yes的特征的生成】
				subTreeCopy = new ArrayList<HeadTreeNode>(subTree);
				buildAndCheckTree.add(subTreeCopy);	
				
				//然后再去合并，但是不加入
				HeadTreeNode tempnode = new HeadTreeNode(tree.getParent().getNodeName());
				tempnode.setParent(tree.getParent().getParent());
				tempnode.setHeadWords(tree.getParent().getHeadWords());
				tempnode.setHeadWordsPos(tree.getParent().getHeadWordsPos());
				tempnode.addChild(tree.getParent().getFirstChild());
				tree.getParent().getFirstChild().setParent(tempnode);
				subTree.set(i, tree.getParent());			
				//合并之后，以合并后的节点的父节点继续递归
				if(tree.getParent().getParent() == null){
					return;
				}else{
					getActionBUILDandCHECK(tree.getParent().getParent(),subTree);
				}
			}else if(tree.getParent().getChildren().size() > 1){
				if(tree.getIndex() == 0){
					//添加start标记
					actions.add("start_"+tree.getParent().getNodeName());	
					HeadTreeNode node = new HeadTreeNode("start_"+tree.getParent().getNodeName());
					node.addChild(subTree.get(i));
					subTree.set(i, node);
					subTreeCopy = new ArrayList<HeadTreeNode>(subTree);
					buildAndCheckTree.add(subTreeCopy);					
					actions.add("no");
					subTreeCopy = new ArrayList<HeadTreeNode>(subTree);
					//为no的时候没有合并的操作，其实是不变的
					buildAndCheckTree.add(subTreeCopy);
					i++;
					if(i >= subTree.size()){
						return;
					}
				}else if(tree.getIndex() == tree.getParent().getChildrenNum()-1){
					actions.add("join_"+tree.getParent().getNodeName());
					HeadTreeNode tempnode = new HeadTreeNode("join_"+tree.getParent().getNodeName());
					tempnode.addChild(subTree.get(i));
					subTree.set(i, tempnode);
					subTreeCopy = new ArrayList<HeadTreeNode>(subTree);
					buildAndCheckTree.add(subTreeCopy);					
					actions.add("yes");
					
					subTreeCopy = new ArrayList<HeadTreeNode>(subTree);
					buildAndCheckTree.add(subTreeCopy);	
					
					//需要合并,node为合并后的父节点
					HeadTreeNode node = new HeadTreeNode(tree.getParent().getNodeName());
					node.setParent(tree.getParent().getParent());
					node.setHeadWords(tree.getParent().getHeadWords());
					node.setHeadWordsPos(tree.getParent().getHeadWordsPos());
					for (int j = 0; j < tree.getParent().getChildrenNum(); j++) {								
						node.addChild(tree.getParent().getIChild(j));
						tree.getParent().getIChild(j).setParent(node);						
					}
					//对subTreeCopy更改
					//要更改的位置
					int index = i - tree.getParent().getChildren().size() + 1;
					subTree.set(index,node);
					//删除那些用于合并的join
					for (int k = i; k >= index+1; k--) {
						subTree.remove(index+1);
					}
					//更改i为了下一次
					i = index;
					//合并之后，以合并后的节点的父节点继续递归，直到没有父节点，退出递归
					if(node.getParent() == null){
						return;
					}else{
						getActionBUILDandCHECK(node.getParent(),subTree);
					}
				}else{
					actions.add("join_"+tree.getParent().getNodeName());
					HeadTreeNode node = new HeadTreeNode("join_"+tree.getParent().getNodeName());
					node.addChild(subTree.get(i));
					subTree.set(i, node);
					subTreeCopy = new ArrayList<HeadTreeNode>(subTree);
					buildAndCheckTree.add(subTreeCopy);					
					actions.add("no");
					subTreeCopy = new ArrayList<HeadTreeNode>(subTree);
					buildAndCheckTree.add(subTreeCopy);
					i++;
					if(i >= subTree.size()){
						return;
					}
				}
			}

		}else{		
			for (HeadTreeNode node:tree.getChildren()) {
				getActionBUILDandCHECK(node,subTree);
			}
		}
	}
	
	/**
	 * 由句法树生成动作序列
	 * 说明：在chunk步骤之后要合并，合并时候需要重新生成头结点
	 * @param tree 树
	 * @param aghw 生成头结点的对象
	 * @throws CloneNotSupportedException
	 */
	public static SyntacticAnalysisSample<HeadTreeNode> headTreeToAction(HeadTreeNode tree, AbstractHeadGenerator aghw) throws CloneNotSupportedException{
		i = 0;
		posTree.clear();
		chunkTree.clear();
		buildAndCheckTree.clear();
		actions.clear();
		getActionPOS(tree);		
		getActionCHUNK(tree, posTree);
		getActionBUILDandCHECK(tree, ChunkTreeCombineUtil.combineToHeadTree(chunkTree,aghw));
		return new SyntacticAnalysisSample<HeadTreeNode>(posTree,chunkTree,buildAndCheckTree,actions);
	}
}
