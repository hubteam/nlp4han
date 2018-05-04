package com.lc.nlp4han.constituent.maxent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.TreeToHeadTree;
import com.lc.nlp4han.ml.util.FilterObjectStream;
import com.lc.nlp4han.ml.util.ObjectStream;


/**
 * 将样本流解析成生成特征需要的信息
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisSampleStream extends FilterObjectStream<String, SyntacticAnalysisSample<HeadTreeNode>>{

	
	private Logger logger = Logger.getLogger(SyntacticAnalysisSampleStream.class.getName());
	private AbstractHeadGenerator aghw ;
	
	/**
	 * 构造
	 * @param samples 样本流
	 */
	public SyntacticAnalysisSampleStream(ObjectStream<String> samples, AbstractHeadGenerator aghw) {
		super(samples);
		this.aghw = aghw;
	}

	/**
	 * 读取样本进行解析
	 * @return 
	 */	
	@Override
	public SyntacticAnalysisSample<HeadTreeNode> read() throws IOException {
		String sentence = samples.read();	
		SyntacticAnalysisSample<HeadTreeNode> sample = null;
		if(sentence != null){
			if(sentence.compareTo("") != 0){
				try{
					TreeNode tree = BracketExpUtil.generateTree(sentence);
					HeadTreeNode headtree = TreeToHeadTree.treeToHeadTree(tree,aghw);
					sample = HeadTreeToActions.headTreeToAction(headtree,aghw);
				}catch(Exception e){
					if (logger.isLoggable(Level.WARNING)) {						
	                    logger.warning("Error during parsing, ignoring sentence: " + sentence);
	                }	
					sample = new SyntacticAnalysisSample<HeadTreeNode>(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
				}
				return sample;
			}else {
				sample = new SyntacticAnalysisSample<HeadTreeNode>(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
				return null;
			}
		}
		else{
			return null;
		}
	}
}
