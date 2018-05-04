package com.lc.nlp4han.dependency;

/**
 * 封装最大熵得到的树的权重
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class DependencyParseMatrix {

	private double[][] proba;
	private String[][] dependency;

	private String[][] kprobas;// 之所以用字符串的形式，是将词对之间的前K个概率用字符串的形式拼接起来，中间是“+”链接
	private String[][] kdependency;

	private String[] sentence;
	private String[] pos;

	/**
	 * 构造
	 * 
	 * @param sentence
	 *            词语
	 * @param pos
	 *            词性
	 * @param proba
	 *            概率
	 * @param dependecy
	 *            依存关系
	 */
	public DependencyParseMatrix(String[] sentence, String[] pos, double[][] proba, String[][] dependecy) {
		this.sentence = sentence;
		this.dependency = dependecy;
		this.proba = proba;
		this.pos = pos;
	}

	/**
	 * 构造
	 * 
	 * @param sentence
	 *            词语
	 * @param pos
	 *            词性
	 * @param proba
	 *            K个最好的概率拼接后的结果
	 * @param dependecy
	 *            K个最好的依存关系拼接后的结果（与K个最好的概率拼接后的结果相对应）
	 */
	public DependencyParseMatrix(String[] sentence, String[] pos, String[][] kprobas, String[][] kdependency) {
		this.sentence = sentence;
		this.kdependency = kdependency;
		this.kprobas = kprobas;
		this.pos = pos;
	}

	/**
	 * 获取词语
	 * 
	 * @return 词语
	 */
	public String[] getSentence() {
		return this.sentence;
	}

	/**
	 * 获取依存关系
	 * 
	 * @return 依存关系
	 */
	public String[][] getDependency() {
		return this.dependency;
	}

	/**
	 * 获取两个词对之间的K个依存关系
	 * 
	 * @return
	 */
	public String[][] getKDependency() {
		return this.kdependency;
	}

	/**
	 * 获取概率
	 * 
	 * @return 概率
	 */
	public double[][] getProba() {
		return this.proba;
	}

	public String[][] getKProba() {
		return this.kprobas;
	}

	/**
	 * 获取词性
	 * 
	 * @return 词性
	 */
	public String[] getPos() {
		return this.pos;
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();
		for (int j2 = 0; j2 < proba.length; j2++) {
			for (int k = 0; k < proba.length; k++) {
				buf.append(proba[j2][k] + "\t");
			}
			buf.append("\n");
		}
		
		for (int j2 = 0; j2 < dependency.length; j2++) {
			for (int k = 0; k < dependency.length; k++) {
				buf.append(dependency[j2][k] + "\t");
			}
			buf.append("\n");
		}
		
		return buf.toString();
	}
}
