package com.lc.nlp4han.constituent.maxent;

import java.util.List;

import com.lc.nlp4han.constituent.HeadTreeNode;

/**
 * 序列验证实现类
 * @author 王馨苇
 *
 */
public class DefaultSyntacticAnalysisSequenceValidator implements SyntacticAnalysisSequenceValidator<HeadTreeNode>{

	/**
	 * 检测chunk步骤的标记是否正确【chunk的标记有start join other】
	 * @param i 当前位置
	 * @param posTree pos步得到的树
	 * @param outcomes 当前位置之前的结果序列
	 * @param out 当前位置的结果
	 * @return
	 */
	@Override
	public boolean validSequenceForChunk(int i, List<HeadTreeNode> posTree, List<String> outcomes, String out) {
		
		if(i == 0){
			if(out.equals("other")){
				return true;
			}else if(out.split("_")[0].equals("start")){
				return true;
			}
		}else if(i == posTree.size() -1){
			if(out.equals("other") || out.split("_")[0].equals("start")){
				if(outcomes.get(i-1).split("_")[0].equals("join") || outcomes.get(i-1).equals("other") || outcomes.get(i-1).split("_")[0].equals("start")){
					return true;
				}
			}else if(out.split("_")[0].equals("join")){
				if(outcomes.get(i-1).split("_")[0].equals("start") || outcomes.get(i-1).split("_")[0].equals("join")){
					if(outcomes.get(i-1).split("_")[1].equals(out.split("_")[1])){					
						return true;
					}
				}										
			}
		}else{
			if(out.equals("other") || out.split("_")[0].equals("start")){
				if(outcomes.get(i-1).split("_")[0].equals("join") || outcomes.get(i-1).equals("other") || outcomes.get(i-1).split("_")[0].equals("start")){
					return true;
				}
			}else if(out.split("_")[0].equals("join")){
				if(outcomes.get(i-1).split("_")[0].equals("start") || outcomes.get(i-1).split("_")[0].equals("join")){
					if(outcomes.get(i-1).split("_")[1].equals(out.split("_")[1])){					
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 检验build和check步骤的标记是否正确【这里只检测build步骤的，check的有yes或者no两种，选大的概率的那个】
	 * @param i 当前位置
	 * @param combineChunkTree 合并后chunk步的结果
	 * @param out 当前位置的结果
	 * @return
	 */
	@Override
	public boolean validSequenceForBuildAndCheck(int i, List<HeadTreeNode> combineChunkTree, String out) {
		if(i == 0){
			if(out.split("_")[0].equals("start")){
				return true;
			}
		}else{
			if(out.split("_")[0].equals("start")){
				
				return true;
			}else if(out.split("_")[0].equals("join")){
				if(combineChunkTree.get(i-1).getNodeNameLeftPart().equals("start") || combineChunkTree.get(i-1).getNodeNameLeftPart().equals("join")){
					if(combineChunkTree.get(i-1).getNodeNameRightPart().equals(out.split("_")[1])){
						return true;
					}
				}
			}
		}
		return false;
	}
}
