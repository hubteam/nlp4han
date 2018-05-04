package com.lc.nlp4han.constituent;

import java.util.ArrayList;
import java.util.List;


/**
 * 将一颗完整的树转成非终端节点和包含的词语的开始结束序号的一个序列，用于比较
 * 
 * @author 王馨苇
 *
 */
public class TreeToEvalStructure {

	/**
	 * 将一颗完整的树转成非终端节点和包含的词语的开始结束序号的一个序列，用于比较
	 * 
	 * @param node 一颗完整的树
	 * @return
	 */
	public static List<EvalStructure> getNonterminalAndSpan(TreeNode node){
		List<EvalStructure> list = new ArrayList<>();
		for (int i = 0; i < node.getChildrenNum(); i++) {
			list.addAll(getNonterminalAndSpan(node.getIChild(i)));
		}
		
		if(node.getChildrenNum() != 0){			
			if(node.getChildrenNum() == 1 && node.getFirstChild().getChildrenNum() == 0){
				
			}else{
				TreeNode left = node;
				TreeNode right = node;
				while(left.getChildrenNum() != 0){
					left = left.getFirstChild();
				}
				
				while(right.getChildrenNum() != 0){
					right = right.getLastChild();
				}
				
				list.add(new EvalStructure(node.getNodeName(), left.getWordIndex(), (right.getWordIndex())+1));
			}
		}
		
		return list;
	}
}
