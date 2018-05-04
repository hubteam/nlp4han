package com.lc.nlp4han.constituent;

import java.util.List;

/**
 * 规则的结构设计类
 * 说明：该规则包含规则的右部和遍历的方向
 * @author 王馨苇
 *
 */
public class HeadRule {

	private List<String> rightRules;
	private String direction;
	
	public HeadRule(List<String> rightRules, String direction){
		this.direction = direction;
		this.rightRules = rightRules;
	}
	
	public List<String> getRightRules(){
		return this.rightRules;
	}
	
	/**
	 * 右部规则中的第I个
	 * @param i
	 * @return
	 */
	public String getIRightRule(int i){
		return this.rightRules.get(i);
	}
	
	/**
	 * 右部规则的长度
	 * @return
	 */
	public int getRightRulesSize(){
		return this.rightRules.size();
	}
	
	public String getDirection(){
		return this.direction;
	}
}
