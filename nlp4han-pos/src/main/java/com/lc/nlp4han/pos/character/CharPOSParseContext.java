package com.lc.nlp4han.pos.character;

/**
 * 解析不同类型的文本语料的策略模式上下文类
 * 
 * @author 刘小峰
 * @author 王馨苇
 * 
 */
public class CharPOSParseContext
{
    private CharPOSSampleParser strage;

    /**
     * 构造函数
     * 
     * @param strage
     *            解析语料对应的策略类
     */
    public CharPOSParseContext(CharPOSSampleParser strage)
    {
        this.strage = strage;
    }

    /**
     * 解析语句
     * 
     * @param sentence
     *            要解析的语句
     * 
     * @return 解析之后要的格式
     */
    public CharPOSSample parseSample(String sentence)
    {
        return strage.parse(sentence);
    }
}
