package com.lc.nlp4han.constituent.maxent;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.HeadGeneratorCollins;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.TreeToHeadTree;


/**
 * 测试句法树转换成动作序列，再将动作序列转换成句法树，比较原句法树和转换得到的句法树是否一致
 * @author 王馨苇
 *
 */
public class Tree2Action2TreeTest{

	/**
	 * 测试由句法树到动作序列，再从动作序列到句法树的过程
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws CloneNotSupportedException
	 */
	@Test
	public void testTreeToActions() throws FileNotFoundException, IOException, CloneNotSupportedException{
		
		AbstractHeadGenerator aghw = new HeadGeneratorCollins();
		//节点有多个子节点
		//1 一个子节点 
		//2 两个子节点
		//3  大于两个子节点
		String treestr = "((S(NP(NNP Mr.)(NNP Vinken))(VP(VBZ is)(NP(NP(NN chairman))(PP(IN of) "
				+ "(NP(NP(NNP Elsevier)(NNP N.V.))(, ,)"
				+ "(NP(DT the)(NNP Dutch)(VBG publishing)(NN group))))))(. .)))";
		TreeNode tree = BracketExpUtil.generateTree(treestr);
		HeadTreeNode headTree = TreeToHeadTree.treeToHeadTree(tree, aghw);		
		
		SyntacticAnalysisSample<HeadTreeNode> sample = HeadTreeToActions.headTreeToAction(headTree, aghw);
		List<String>words = sample.getWords();
		List<String>actions = sample.getActions();
		TreeNode resulttree = ActionsToTree.actionsToTree(words, actions); 
		assertEquals(tree, resulttree);
	}		
}
