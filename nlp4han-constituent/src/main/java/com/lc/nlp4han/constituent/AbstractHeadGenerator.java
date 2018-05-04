package com.lc.nlp4han.constituent;

import java.util.HashMap;
import java.util.List;

/**
 * 生成头结点的抽象模板类
 * @author 王馨苇
 *
 */
public abstract class AbstractHeadGenerator{
	
	/**
	 * 为并列结构生成头结点
	 * @param node 子节点带头结点，父节点不带头结点的树
	 * @return
	 */
	protected abstract String generateHeadWordsForCordinator(HeadTreeNode node);

	/**
	 * 为特殊规则生成头结点
	 * @param node 子节点带头结点，父节点不带头结点的树
	 * @param specialRules 生成头结点的特殊规则
	 * @return
	 */
	protected abstract String generateHeadWordsForSpecialRules(HeadTreeNode node, HashMap<String,List<HeadRule>> specialRules);
	
	/**
	 * 为一般规则生成头结点
	 * @param node 子节点带头结点，父节点不带头结点的树
	 * @param normalRules 生成头结点的一般规则
	 * @return
	 */
	protected abstract String generateHeadWordsForNormalRules(HeadTreeNode node, HashMap<String,HeadRule> normalRules);

	/**
	 * 合并生成头结点的所有方法，提取头结点和头结点对应的词性
	 * @param node 子节点带头结点，父节点不带头结点的树
	 * @param normalRules 生成头结点的一般规则
	 * @param specialRules 生成头结点的特殊规则
	 * @return
	 */
	private String extractHeadWordAndPos(HeadTreeNode node, HashMap<String, HeadRule> normalRules, HashMap<String,List<HeadRule>> specialRules){
		String headWords = null;
		headWords = generateHeadWordsForCordinator(node);
		
		if(headWords == null && specialRules != null){			
			headWords = generateHeadWordsForSpecialRules(node, specialRules);	
		}
		
		if(headWords == null && normalRules != null){
			headWords = generateHeadWordsForNormalRules(node, normalRules);
		}
		return headWords;
	}
	
	/**
	 * 合并生成头结点的所有方法，提取头结点和头结点
	 * @param node 子节点带头结点，父节点不带头结点的树
	 * @param normalRules 生成头结点的一般规则
	 * @param specialRules 生成头结点的特殊规则
	 * @return
	 */
	public String extractHeadWord(HeadTreeNode node, HashMap<String,HeadRule> normalRules, HashMap<String,List<HeadRule>> specialRules){
		
		return extractHeadWordAndPos(node, normalRules, specialRules).split("_")[0];
	}
	
	/**
	 * 合并生成头结点的所有方法，提取头结点的词性
	 * @param node 子节点带头结点，父节点不带头结点的树
	 * @param normalRules 生成头结点的一般规则
	 * @param specialRules 生成头结点的特殊规则
	 * @return
	 */
	public String extractHeadWordPos(HeadTreeNode node, HashMap<String,HeadRule> normalRules,HashMap<String,List<HeadRule>> specialRules){
		
		return extractHeadWordAndPos(node, normalRules, specialRules).split("_")[1];
	}
}
