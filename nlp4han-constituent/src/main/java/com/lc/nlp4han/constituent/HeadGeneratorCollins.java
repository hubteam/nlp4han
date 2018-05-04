package com.lc.nlp4han.constituent;

import java.util.HashMap;
import java.util.List;

/**
 * 具体的生成头结点的实现类
 * 参考：Collins 1999论文
 * @author 王馨苇
 *
 */
public class HeadGeneratorCollins extends AbstractHeadGenerator{

	/**
	 * 为并列结构生成头结点
	 * @param node 子节点带头结点，父节点不带头结点的树
	 * @return
	 */
	@Override
	public String generateHeadWordsForCordinator(HeadTreeNode node) {
		//有些非终端节点需要进行处理，因为它可能是NP-SBJ的格式，我只需要拿NP的部分进行匹配操作
		String parentNonTerminal = node.getNodeName().split("-")[0];
		//处理X-X CC X的情况
		boolean flag = false;
		int record = -1;
		//先判断是不是这种结构
		for (int i = 0; i < node.getChildrenNum() - 2; i++) {
			if(node.getIChildName(i).split("-")[0].equals(parentNonTerminal) &&
					node.getIChildName(i+1).equals("CC") &&
					node.getIChildName(i+2).split("-")[0].equals(parentNonTerminal)){
				flag = true;
				record = i;
				break;
			}
		}
		if(flag == true && record != -1){
			return node.getIChildHeadWord(record)+"_"+node.getIChildHeadWordPos(record);
		}
		return null;
	}

	/**
	 * 为特殊规则生成头结点
	 * @param node 子节点带头结点，父节点不带头结点的树
	 * @param specialRules 生成头结点的特殊规则
	 * @return
	 */
	@Override
	public String generateHeadWordsForSpecialRules(HeadTreeNode node, HashMap<String, List<HeadRule>> specialRules) {
		String currNodeName = node.getNodeName();
		//如果最后一个是POS，返回最后一个
		if(node.getLastChildName().equals("POS")){
			return node.getLastChildHeadWord()+"_"+node.getLastChildHeadWordPos();
		}
		if(specialRules.containsKey(currNodeName)){
			for (int k = 0; k < specialRules.get(currNodeName).size(); k++) {
				if(specialRules.get(currNodeName).get(k).getDirection().equals("left")){
					//用所有的子节点从左向右匹配规则中每一个
					for (int i = 0; i < specialRules.get(currNodeName).get(k).getRightRulesSize(); i++) {
						for (int j = 0; j < node.getChildrenNum(); j++) {
							if(node.getIChildName(j).equals(specialRules.get(currNodeName).get(k).getIRightRule(i))){
								return node.getIChildHeadWord(j)+"_"+node.getIChildHeadWordPos(j);
							}
						}
					}
				}else if(specialRules.get(currNodeName).get(k).getDirection().equals("right")){
					for (int i = specialRules.get(currNodeName).get(k).getRightRulesSize() -1 ; i >= 0; i--) {
						for (int j = 0; j < node.getChildrenNum(); j++) {
							if(node.getIChildName(j).equals(specialRules.get(currNodeName).get(k).getIRightRule(i))){
								return node.getIChildHeadWord(j)+"_"+node.getIChildHeadWordPos(j);
							}
						}
					}
				}
			}
			//否则返回最后一个		
			return node.getLastChildHeadWord()+"_"+node.getLastChildHeadWordPos();
		}else{
			return null;
		}
	}

	/**
	 * 为一般规则生成头结点
	 * @param node 子节点带头结点，父节点不带头结点的树
	 * @param normalRules 生成头结点的一般规则
	 * @return
	 */
	@Override
	public String generateHeadWordsForNormalRules(HeadTreeNode node, HashMap<String, HeadRule> normalRules) {
		String currentNodeName = node.getNodeName();
		if(normalRules.containsKey(currentNodeName)){
			if(normalRules.get(currentNodeName).getDirection().equals("left")){
				//用所有的子节点从左向右匹配规则中每一个
				for (int i = 0; i < normalRules.get(currentNodeName).getRightRulesSize(); i++) {
					for (int j = 0; j < node.getChildrenNum(); j++) {
						if(node.getIChildName(j).equals(normalRules.get(currentNodeName).getIRightRule(i))){
							return node.getIChildHeadWord(j)+"_"+node.getIChildHeadWordPos(j);
						}
					}
				}
			}else if(normalRules.get(currentNodeName).getDirection().equals("right")){
				for (int i = normalRules.get(currentNodeName).getRightRulesSize() -1 ; i >= 0; i--) {
					for (int j = 0; j < node.getChildrenNum(); j++) {
						if(node.getIChildName(j).equals(normalRules.get(currentNodeName).getIRightRule(i))){
							return node.getIChildHeadWord(j)+"_"+node.getIChildHeadWordPos(j);
						}
					}
				}
			}
			//如果所有的规则都没有匹配，返回最左边的第一个
			return node.getFirstChildHeadWord()+"_"+node.getFirstChildHeadWordPos();
		}else{
			return node.getFirstChildHeadWord()+"_"+node.getFirstChildHeadWordPos();
		}
	}
}
