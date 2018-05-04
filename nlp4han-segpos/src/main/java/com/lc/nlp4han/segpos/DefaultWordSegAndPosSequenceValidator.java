package com.lc.nlp4han.segpos;

import com.lc.nlp4han.ml.util.SequenceValidator;

/**
 * 序列验证类
 * @author 王馨苇
 *
 */
public class DefaultWordSegAndPosSequenceValidator implements SequenceValidator<String>{

	/**
	 * 验证序列是否正确
	 * @param i 当前字符下标
	 * @param characters 字符
	 * @param tags 字符序列
	 * @param out 得到的下一个字符的输出结果
	 */
	public boolean validSequence(int i, String[] characters, String[] tags, String out) {
		String temptag = out.split("_")[0];
		String temppos = out.split("_")[1];
		if (i == 0) {//第一个
            return temptag.equals("S") || temptag.equals("B");
        }else if(i-1 >= 0){
        	String tag = tags[i-1].split("_")[0];
    		String pos = tags[i-1].split("_")[1];
    		if (temptag.equals("S")) {//为S时，前面只能是S或者E，此时词性没有要求
                return tag.equals("S") || tag.equals("E");
            } else if (temptag.equals("B")) {//为B时，前面只能是S或者E，此时词性没有要求
                return tag.equals("S") || tag.equals("E");
            } else if (temptag.equals("M")) {//为M时，前面只能是B或者M，此时词性要与前面的词性保持一致
                return (tag.equals("B") || tag.equals("M")) && (temppos.equals(pos));
            } else if (temptag.equals("E")) {//为E时，前面只能是B或者M，此时词性要与前面的词性保持一致
                return (tag.equals("B") || tag.equals("M")) && (temppos.equals(pos));
            }
        }
		return true;
	}
}
