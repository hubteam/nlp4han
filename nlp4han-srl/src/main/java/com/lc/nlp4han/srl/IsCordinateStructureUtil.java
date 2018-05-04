package com.lc.nlp4han.srl;

import com.lc.nlp4han.constituent.TreeNode;

/**
 * 判断当前子树是不是并列结构
 * @author 王馨苇
 *
 */
public class IsCordinateStructureUtil {

	/**
	 * 判断是否是并列结构
	 * @param tree 要判断的树
	 * @return
	 */
	public static boolean isCordinate(TreeNode tree){
		// X CC X
		// X , CC X [.,but.]
		// X : CC X
		// X , X
		// X : X [. ; .]
		if((tree.getFirstChildName().equals("''")) && (tree.getLastChildName().equals("''")) ||
				(tree.getFirstChildName().equals("``")) && (tree.getLastChildName().equals("``")) ||
				(tree.getFirstChildName().equals("''")) && (tree.getLastChildName().equals("``")) ||
				(tree.getFirstChildName().equals("``")) && (tree.getLastChildName().equals("''"))){
			if(tree.getChildrenNum() == 7){
				if(tree.getIChildName(5).equals(".")){
					if(tree.getIChild(1).equals(tree.getIChild(4)) &&
							tree.getIChildName(2).equals(",") && tree.getIChildName(3).equals("CC")){
						return true;
					}
					if(tree.getIChild(1).equals(tree.getIChild(4)) &&
							tree.getIChildName(2).equals(":") && tree.getIChildName(3).equals("CC")){
						return true;
					}
				}
			}else if(tree.getChildrenNum() == 6){
				if(tree.getIChildName(4).equals(".")){
					if(tree.getIChildName(1).equals(tree.getIChildName(3))){
						if(tree.getIChildName(2).equals("CC") || tree.getIChildName(2).equals(",") || tree.getIChildName(2).equals(":")){
							return true;
						}
					}
				}
			}
		}
		if(tree.getLastChildName().equals("''") || tree.getLastChildName().equals("``")){
			if(tree.getChildrenNum() == 6){
				if(tree.getIChildName(4).equals(".")){
					if(tree.getFirstChildName().equals(tree.getIChildName(3)) &&
							tree.getIChildName(1).equals(",") && tree.getIChildName(2).equals("CC")){
						return true;
					}
					if(tree.getFirstChildName().equals(tree.getIChildName(3)) &&
							tree.getIChildName(1).equals(":") && tree.getIChildName(2).equals("CC")){
						return true;
					}
				}
			}else if(tree.getChildrenNum() == 5){
				if(tree.getIChildName(3).equals(".")){
					if(tree.getFirstChildName().equals(tree.getIChildName(2))){
						if(tree.getIChildName(1).equals("CC") || tree.getIChildName(1).equals(",") || tree.getIChildName(1).equals(":")){
							return true;
						}
					}
				}
			}
		}
				
		if(tree.getLastChildName().equals(".")){	
			if(tree.getChildrenNum() == 6){
				if(tree.getFirstChildName().equals("''") || tree.getFirstChildName().equals("``")){
					if(tree.getIChildName(1).equals(tree.getIChildName(4)) &&
							tree.getIChildName(2).equals(",") && tree.getIChildName(3).equals("CC")){
						return true;
					}
					if(tree.getIChildName(1).equals(tree.getIChildName(4)) &&
							tree.getIChildName(2).equals(":") && tree.getIChildName(3).equals("CC")){
						return true;
					}
				}
			}else if(tree.getChildrenNum() == 5){
				if(tree.getFirstChildName().equals(tree.getIChildName(3)) &&
						tree.getIChildName(1).equals(",") && tree.getIChildName(2).equals("CC")){
					return true;
				}
				if(tree.getFirstChildName().equals(tree.getIChildName(3)) &&
						tree.getIChildName(1).equals(":") && tree.getIChildName(2).equals("CC")){
					return true;
				}
			}else if(tree.getChildrenNum() == 4){
				if(tree.getFirstChildName().equals(tree.getIChildName(2))){
					if(tree.getIChildName(1).equals("CC") || tree.getIChildName(1).equals(",") || tree.getIChildName(1).equals(":")){
						return true;
					}
				}
			}
		}else{
			if(tree.getChildrenNum() == 4){
				if(tree.getFirstChildName().equals(tree.getIChildName(3)) &&
						tree.getIChildName(1).equals(",") && tree.getIChildName(2).equals("CC")){
					return true;
				}
				if(tree.getFirstChildName().equals(tree.getIChildName(3)) &&
						tree.getIChildName(1).equals(":") && tree.getIChildName(2).equals("CC")){
					return true;
				}
			}else if(tree.getChildrenNum() == 3){
				if(tree.getFirstChildName().equals(tree.getIChildName(2))){
					if(tree.getIChildName(1).equals("CC") || tree.getIChildName(1).equals(",") || tree.getIChildName(1).equals(":")){
						return true;
					}
				}
			}
		}
		return false;
	}
}
