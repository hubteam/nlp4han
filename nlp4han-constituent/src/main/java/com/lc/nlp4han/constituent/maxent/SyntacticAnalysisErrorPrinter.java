package com.lc.nlp4han.constituent.maxent;

import java.io.OutputStream;
import java.io.PrintStream;

import com.lc.nlp4han.constituent.HeadTreeNode;

/**
 * 错误打印类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisErrorPrinter extends SyntacticAnalysisEvaluateMonitor{

    private PrintStream errOut;
	
	public SyntacticAnalysisErrorPrinter(OutputStream out){
		errOut = new PrintStream(out);
	}
	
	/**
	 * 样本和预测的不一样的时候进行输出
	 * @param reference 参考的样本
	 * @param predict 预测的结果
	 */
	@Override
	public void missclassified(SyntacticAnalysisSample<HeadTreeNode> reference, SyntacticAnalysisSample<HeadTreeNode> predict) {
		 errOut.println("样本的结果：");
		 errOut.println(SyntacticAnalysisSample.toTree(reference.getWords(), reference.getActions()).toBracket());
		 errOut.println();
		 errOut.println("预测的结果：");
		 errOut.println(SyntacticAnalysisSample.toTree(predict.getWords(), predict.getActions()).toBracket());
		 errOut.println();
	}
}
