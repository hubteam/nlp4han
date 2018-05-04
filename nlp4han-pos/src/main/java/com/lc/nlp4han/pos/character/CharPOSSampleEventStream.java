package com.lc.nlp4han.pos.character;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.AbstractEventStream;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * 根据上下文得到事件
 * 
 * @author 王馨苇
 * 
 */
public class CharPOSSampleEventStream extends AbstractEventStream<CharPOSSample>
{

    private CharPOSContextGenerator generator;

    /**
     * 构造
     * 
     * @param samples
     *            样本流
     * @param generator
     *            上下文产生器
     */
    public CharPOSSampleEventStream(ObjectStream<CharPOSSample> samples, CharPOSContextGenerator generator)
    {
        super(samples);
        this.generator = generator;
    }

    /**
     * 创建事件
     * 
     * @param sample
     *            样本流
     */
    @Override
    protected Iterator<Event> createEvents(CharPOSSample sample)
    {
    	String[] words = sample.getWords();
		String[] tagsAndposes = sample.getTagsAndPoses();
		String[] characters = sample.getCharacters();
		String[][] ac = sample.getAditionalContext();
		List<Event> events = generateEvents(characters, words, tagsAndposes,ac);
        return events.iterator();
    }

    /**
	 * 产生事件
	 * @param characters 字符
	 * @param words 词语
	 * @param tagsAndposes 词性
	 * @param ac
	 * @return
	 */
	private List<Event> generateEvents(String[] characters, String[] words, String[] tagsAndposes,
			String[][] ac) {
		List<Event> events = new ArrayList<Event>(tagsAndposes.length);

		for (int i = 0; i < characters.length; i++) {
			//产生事件的部分
			String[] context = generator.getContext(i, characters, tagsAndposes, words);

            events.add(new Event(tagsAndposes[i], context));
		}
		return events;
	}

}
