package com.lc.nlp4han.constituent.maxent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lc.nlp4han.ml.util.FilterObjectStream;
import com.lc.nlp4han.ml.util.ObjectStream;


/**
 * 将样本流解析成生成特征需要的信息
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisSampleStream extends FilterObjectStream<String,SyntacticAnalysisSample<HeadTreeNode>>{

	
	private Logger logger = Logger.getLogger(SyntacticAnalysisSampleStream.class.getName());
	/**
	 * 构造
	 * @param samples 样本流
	 */
	public SyntacticAnalysisSampleStream(ObjectStream<String> samples) {
		super(samples);
	}

	/**
	 * 读取样本进行解析
	 * @return 
	 */	
	@Override
	public SyntacticAnalysisSample<HeadTreeNode> read() throws IOException {
		String sentence = samples.read();	
		SyntacticAnalysisSample<HeadTreeNode> sample = null;
		PhraseGenerateTree pgt = new PhraseGenerateTree();
		TreeToHeadTree ttht = new TreeToHeadTree();
		HeadTreeToActions tta = new HeadTreeToActions();
		if(sentence != null){
			if(sentence.compareTo("") != 0){
				try{
					TreeNode tree = pgt.generateTree(sentence);
					HeadTreeNode headtree = ttht.treeToHeadTree(tree);
					sample = tta.treeToAction(headtree);
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
