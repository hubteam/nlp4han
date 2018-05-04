package com.lc.nlp4han.constituent.maxent;

import java.util.List;

import com.lc.nlp4han.constituent.TreeNode;


/**
 * 句法分析序列分类模型
 * @author 王馨苇
 *
 */
public interface SyntacticAnalysisSequenceClassificationModel<T extends TreeNode> {

	/**
	 * 得到最好的chunk结果
	 * @param posTree pos步得到的最好的K棵树
	 * @param ac 额外的信息
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	SyntacticAnalysisSequenceForChunk bestSequenceForChunk(List<List<T>> posTree, Object[] ac, SyntacticAnalysisContextGenerator<T> generator, SyntacticAnalysisSequenceValidator<T> validator) ;

	/**
	 * 得到最好的num个chunk结果
	 * @param num 最好的num个序列
	 * @param posTree pos步得到的最好的K棵树
	 * @param ac 额外的信息
	 * @param min 得分最低的限制
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	SyntacticAnalysisSequenceForChunk[] bestSequencesForChunk(int num, List<List<T>> posTree, Object[] ac, double min, SyntacticAnalysisContextGenerator<T> generator,
			SyntacticAnalysisSequenceValidator<T> validator);

	/**
	 * 得到最好的num个chunk结果
	 * @param num 最好的num个序列
	 * @param posTree pos步得到的最好的K棵树
	 * @param ac 额外的信息
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	SyntacticAnalysisSequenceForChunk[] bestSequencesForChunk(int num, List<List<T>> posTree, Object[] ac, SyntacticAnalysisContextGenerator<T> generator,
			SyntacticAnalysisSequenceValidator<T> validator);
	
	/**
	 * 得到最好的BuildAndCheck结果
	 * @param comnineChunkTree chunk步得到的最好的K棵树合并之后
	 * @param ac 额外的信息
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	SyntacticAnalysisSequenceForBuildAndCheck<T> bestSequenceForBuildAndCheck(List<List<T>> comnineChunkTree, Object[] ac, SyntacticAnalysisContextGenerator<T> generator, SyntacticAnalysisSequenceValidator<T> validator) ;

	/**
	 * 得到最好的num个BuildAndCheck结果
	 * @param num 最好的num个序列
	 * @param comnineChunkTree chunk步得到的最好的K棵树合并之后
	 * @param ac 额外的信息
	 * @param min 得分最低的限制
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	SyntacticAnalysisSequenceForBuildAndCheck<T>[] bestSequencesForBuildAndCheck(int num, List<List<T>> comnineChunkTree, Object[] ac, double min, SyntacticAnalysisContextGenerator<T> generator,
			SyntacticAnalysisSequenceValidator<T> validator);

	/**
	 * 得到最好的num个BuildAndCheck结果
	 * @param num 最好的num个序列
	 * @param comnineChunkTree chunk步得到的最好的K棵树合并之后
	 * @param ac 额外的信息
	 * @param generator 特征生成器
	 * @param validator 序列验证
	 * @return
	 */
	SyntacticAnalysisSequenceForBuildAndCheck<T>[] bestSequencesForBuildAndCheck(int num, List<List<T>> comnineChunkTree, Object[] ac, SyntacticAnalysisContextGenerator<T> generator,
			SyntacticAnalysisSequenceValidator<T> validator);
}
