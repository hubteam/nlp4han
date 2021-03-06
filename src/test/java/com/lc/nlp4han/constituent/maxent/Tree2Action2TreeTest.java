package com.lc.nlp4han.constituent.maxent;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


/**
 * 测试树到动作再到树是否合法
 * @author 王馨苇
 *
 */
public class Tree2Action2TreeTest{

	private URL is;
	private PlainTextByTreeStream lineStream ;
	private String txt ;
	private PhraseGenerateTree pgt ;
	private TreeToHeadTree ttht;
	private TreeNode tree ;
    private HeadTreeNode headTree;
	private HeadTreeToActions tta ;
	private SyntacticAnalysisSample<HeadTreeNode> sample ;
	private List<String> words ;
	private List<String> actions ;

	private ActionsToTree att ;
	private TreeNode newTree ;
	
	@Before
	public void setUP() throws UnsupportedOperationException, FileNotFoundException, IOException, CloneNotSupportedException{
		is = Tree2Action2TreeTest.class.getClassLoader().getResource("com/lc/nlp4han/constituent/maxent/wsj_0015new.mrg");
		lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File(is.getFile())), "utf8");
		txt = lineStream.read();
		pgt = new PhraseGenerateTree();
		ttht = new TreeToHeadTree();
		tree = pgt.generateTree(txt);
        headTree = ttht.treeToHeadTree(tree);
		tta = new HeadTreeToActions();
		sample = tta.treeToAction(headTree);
		words = sample.getWords();
		actions = sample.getActions();

		att = new ActionsToTree();
		newTree = att.actionsToTree(words, actions);
	}
	
	/**
	 * 测试由句法树到动作序列，再从动作序列到句法树的过程
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws CloneNotSupportedException
	 */
	@Test
	public void testTreeToActions() throws FileNotFoundException, IOException, CloneNotSupportedException{
		assertEquals(tree, newTree);
	}	
	
}
