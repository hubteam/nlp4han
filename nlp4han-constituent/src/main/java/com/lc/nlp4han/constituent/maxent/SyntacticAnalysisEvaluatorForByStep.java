package com.lc.nlp4han.constituent.maxent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lc.nlp4han.constituent.AbstractHeadGenerator;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.SyntacticAnalysisMeasure;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.ml.util.Evaluator;

/**
 * 对分步骤训练句法分析模型进行评估的类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisEvaluatorForByStep extends Evaluator<SyntacticAnalysisSample<HeadTreeNode>>{

	private Logger logger = Logger.getLogger(SyntacticAnalysisEvaluatorForByStep.class.getName());
	private SyntacticAnalysisForPos<HeadTreeNode> postagger;
	private SyntacticAnalysisMEForChunk chunktagger;
	private SyntacticAnalysisMEForBuildAndCheck buildAndChecktagger;
	private SyntacticAnalysisMeasure measure;
	private AbstractHeadGenerator aghw;
	
	public SyntacticAnalysisEvaluatorForByStep(SyntacticAnalysisForPos<HeadTreeNode> postagger, SyntacticAnalysisMEForChunk chunktagger, SyntacticAnalysisMEForBuildAndCheck buildAndChecktagger, AbstractHeadGenerator aghw) {
		this.postagger = postagger;
		this.chunktagger = chunktagger;
		this.buildAndChecktagger = buildAndChecktagger;
		this.aghw = aghw;
	}
	
	public SyntacticAnalysisEvaluatorForByStep(SyntacticAnalysisForPos<HeadTreeNode> postagger, SyntacticAnalysisMEForChunk chunktagger, SyntacticAnalysisMEForBuildAndCheck buildAndChecktagger, AbstractHeadGenerator aghw, SyntacticAnalysisEvaluateMonitor... evaluateMonitors) {
		super(evaluateMonitors);
		this.postagger = postagger;
		this.chunktagger = chunktagger;
		this.buildAndChecktagger = buildAndChecktagger;
		this.aghw = aghw;
	}
	
	/**
	 * 设置评估指标的对象
	 * @param measure 评估指标计算的对象
	 */
	public void setMeasure(SyntacticAnalysisMeasure measure){
		this.measure = measure;
	}
	
	/**
	 * 得到评估的指标
	 * @return
	 */
	public SyntacticAnalysisMeasure getMeasure(){
		return this.measure;
	}

	@Override
	protected SyntacticAnalysisSample<HeadTreeNode> processSample(SyntacticAnalysisSample<HeadTreeNode> sample) {
		SyntacticAnalysisSample<HeadTreeNode> samplePre = null;
		HeadTreeNode treePre = null;
		//在验证的过程中，有些配ignore的句子，也会来验证，这是没有意义的，为了防止这种情况，就加入判断
		if(sample.getActions().size() == 0 && sample.getWords().size() == 0){
			return new SyntacticAnalysisSample<HeadTreeNode>(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		}else{
			try {
				List<String> words = sample.getWords();
				List<String> actionsRef = sample.getActions();
				//参考样本没有保存完整的一棵树，需要将动作序列转成一颗完整的树
				TreeNode treeRef = ActionsToTree.actionsToTree(words, actionsRef);
				List<List<HeadTreeNode>> posTree = postagger.posTree(20,words.toArray(new String[words.size()]));
				List<List<HeadTreeNode>> chunkTree = chunktagger.tagKChunk(20, posTree, null);	
				treePre = buildAndChecktagger.tagBuildAndCheck(chunkTree, null);
				
				if(treePre == null){
					samplePre = new SyntacticAnalysisSample<HeadTreeNode>(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
					measure.countNodeDecodeTrees(treePre);
				}else{
					samplePre = HeadTreeToActions.headTreeToAction(treePre,aghw);
					measure.update(treeRef, treePre);
				}	
			} catch(Exception e){
				if (logger.isLoggable(Level.WARNING)) {						
                    logger.warning("Error during parsing, ignoring sentence: " + treePre.toBracket());
                }	
				samplePre = new SyntacticAnalysisSample<HeadTreeNode>(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
			}
			return samplePre;
		}
	}
}
