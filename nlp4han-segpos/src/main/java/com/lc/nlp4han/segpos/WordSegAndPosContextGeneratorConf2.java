package com.lc.nlp4han.segpos;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.lc.nlp4han.util.DictionaryLoader;
import com.lc.nlp4han.util.CharTypeUtil;
import com.lc.nlp4han.util.FullHalfWidthUtil;


public class WordSegAndPosContextGeneratorConf2 implements WordSegAndPosContextGenerator{

	private boolean c_2Set;
    private boolean c_1Set;
    private boolean c0Set;
    private boolean c1Set;
    private boolean c2Set;
    private boolean c_2c_1Set;
    private boolean c_1c0Set;
    private boolean c0c1Set;
    private boolean c1c2Set;
    private boolean c_1c1Set;
    private boolean c0prefix;
    private boolean w0c0Set;
    private boolean PuSet;
    private boolean TSet;
//    private boolean Pc_1w0Set;
//    private boolean Pc_2w0Pc_1w0Set;
    private boolean Bc_1w0Pc_1w0Set;
    private boolean Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set;
    Set<String> dictionalWords;
    
    private String dictResource;
    private String dictEncoding;
	/**
	 * 无参构造，加载feature配置文件
	 * @throws IOException
	 */
	public WordSegAndPosContextGeneratorConf2() throws IOException{
		Properties featureConf = new Properties();
        InputStream featureStream = WordSegAndPosContextGeneratorConf.class.getClassLoader().getResourceAsStream("com/lc/nlp4han/segpos/feature.properties");
        featureConf.load(featureStream);
        
        init(featureConf);
	}
	
	/**
	 * 有参数的构造函数
	 * @param properties 配置文件
	 * @throws IOException 
	 */
	public WordSegAndPosContextGeneratorConf2(Properties properties) throws IOException{
        
        init(properties);
	}
	
	/**
	 * 根据配置文件初始化特征
	 * @param properties 配置文件
	 * @throws IOException 
	 */
	private void init(Properties config) throws IOException {
	    dictResource = config.getProperty("feature.dict");
        dictEncoding = config.getProperty("feature.dict.encoding");
        
        InputStream dictIn = WordSegAndPosContextGeneratorConf2.class.getClassLoader().getResourceAsStream(dictResource);
        dictionalWords = DictionaryLoader.getWords(dictIn, dictEncoding);
        
//	    dictionalWords = DictionaryLoader.getWords("E:\\pku_training_maxentModelC.txt", "gbk");
	    
		c_2Set = (config.getProperty("feature.c_2", "true").equals("true"));
        c_1Set = (config.getProperty("feature.c_1", "true").equals("true"));
        c0Set = (config.getProperty("feature.c0", "true").equals("true"));
        c1Set = (config.getProperty("feature.c1", "true").equals("true"));
        c2Set = (config.getProperty("feature.c2", "true").equals("true"));

        c_2c_1Set = (config.getProperty("feature.c_2c_1", "true").equals("true"));
        c_1c0Set = (config.getProperty("feature.c_1c0", "true").equals("true"));
        c0c1Set = (config.getProperty("feature.c0c1", "true").equals("true"));
        c1c2Set = (config.getProperty("feature.c1c2", "true").equals("true"));

        c_1c1Set = (config.getProperty("feature.c_1c1", "true").equals("true"));
        
        c0prefix = (config.getProperty("feature.c0pre", "true").equals("true"));
        
        w0c0Set = (config.getProperty("feature.w0c0", "true").equals("true"));
        
        PuSet = (config.getProperty("feature.Pu", "true").equals("true"));
        
        TSet = (config.getProperty("feature.T", "true").equals("true"));
        
        Bc_1w0Pc_1w0Set = (config.getProperty("feature.Bc_1w0Pc_1w0", "true").equals("true"));
        
        Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set = (config.getProperty("feature.Bc_2w0Pc_2w0Bc_1w0Pc_1w0", "true").equals("true"));
        
//        Bc_1w0Pc_1w0Set = (config.getProperty("feature.Bc_1w0Pc_1w0", "true").equals("true"));
//        Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set = (config.getProperty("feature.Bc_2w0Pc_2w0Bc_1w0Pc_1w0", "true").equals("true"));	
        
	}

	@Override
	public String toString() {
		return "WordSegPosContextGeneratorConf{" + "c_2Set=" + c_2Set + ", c_1Set=" + c_1Set + 
                ", c0Set=" + c0Set + ", c1Set=" + c1Set + ", c2Set=" + c2Set + 
                ", c_2c_1Set=" + c_2c_1Set + ", c_1c0Set=" + c_1c0Set + 
                ", c0c1Set=" + c0c1Set + ", c1c2Set=" + c1c2Set + 
                ", c_1c1Set=" + c_1c1Set + ",c0prefix=" + c0prefix +
                ",w0c0Set="+ w0c0Set +",PuSet="+ PuSet +",TSet="+ TSet +
                ",Bc_1w0Pc_1w0Set="+ Bc_1w0Pc_1w0Set +
                ",Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set="+ Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set +"";
	}

	/**
	 * 词性标注的训练语料生成特征
	 * @param i 当前字符的位置
	 * @param j 当前字符属于词语的位置
	 * @param characters 字符的序列
	 * @param tags 字符的标记序列
	 * @param words 词语序列
	 * @param poses 词性序列
	 * @param ac 额外的信息
	 * @return
	 */
	public String[] getContext(int i, int j, String[] characters, String[] tags, String[] words, String[] poses,
			Object[] ac) {
		
		return getContext(i, j, characters, tags, words, poses);
	}

	/**
	 * 词性标注的训练语料生成特征
	 * @param i 当前字符的位置
	 * @param j 当前字符属于词语的位置
	 * @param characters 字符的序列
	 * @param tags 字符的标记序列
	 * @param words 词语序列
	 * @param poses 词性序列
	 * @return
	 */
	private String[] getContext(int i, int j, String[] characters, String[] tags, String[] words, String[] poses) {
		String c1, c2, c3, c0, c_1, c_2, c_3;
        c1 = c2 = c3 = c0 = c_1 = c_2 = c_3 = null;
        String TC_1, TC_2, TC0, TC1, TC2;
        TC_1 = TC_2 = TC0 = TC1 = TC2 = null;
        String w0,w_1,w_2,p_1,p_2;
        w0 = w_1 = w_2 = p_1 = p_2 = null;
        c0 = characters[i];
        w0 = words[j];
        TC0 = CharTypeUtil.featureType(c0);
        if (characters.length > i + 1)
        {
            c1 = characters[i + 1];
            TC1 = CharTypeUtil.featureType(c1);

            if (characters.length > i + 2)
            {
                c2 = characters[i + 2];
                TC2 = CharTypeUtil.featureType(c2);
            }
        }

        if (i - 1 >= 0)
        {
            c_1 = characters[i - 1];
            TC_1 = CharTypeUtil.featureType(c_1);

            if (i - 2 >= 0)
            {
                c_2 = characters[i - 2].toString();
                TC_2 = CharTypeUtil.featureType(c_2);
            }
        }

        List<String> features = new ArrayList<String>();
       //c_2
        if(c_2 != null){
        	if(c_2Set){
        		features.add("c_2="+c_2);
        	}
        }
        //c_1
        if(c_1 != null){
        	if(c_1Set){
        		features.add("c_1="+c_1);
        	}
        }
		//c0
        if(c0Set){
        	features.add("c0="+c0);
        }
        //c1
        if(c1 != null){
        	if(c1Set){
        		features.add("c1="+c1);
        	}
        }
        //c2
        if(c2 != null){
        	if(c2Set){
        		features.add("c2="+c2);
        	}
        }
        //c_2c_1
        if(c_2 != null && c_1 != null){
        	if(c_2c_1Set){
        		features.add("c_2c_1="+c_2+c_1);
        	}
        }
        //c_1c0
        if(c_1 != null){
        	if(c_1c0Set){
        		features.add("c_1c0="+c_1+c0);
        	}
        }
        //c0c1
        if(c1 != null){
        	if(c0c1Set){
        		features.add("c0c1="+c0+c1);
        	}
        }
        //c1c2
        if(c1 != null && c2 != null){
        	if(c1c2Set){
        		features.add("c1c2="+c1+c2);
        	}
        }
        //c_1c1
        if(c_1 != null && c1 != null){
        	if(c_1c1Set){
        		features.add("c_1c1="+c_1+c1);
        	}
        }
        //c0pre
        if(c0prefix){
        	features = addC0Prefix(features, c0);
        }
        
        //W0C0
        if(w0c0Set){
        	features.add("w0c0="+w0+c0);
        }
        //Pu
        if(PuSet){
        	if (CharTypeUtil.isChinesePunctuation(FullHalfWidthUtil.toHalfWidth(c0)))
                features.add("Pu=" + 1);
            else
                features.add("Pu=" + 0);
        }
        //T
        if(TC_2 != null && TC_1 != null && TC1 != null && TC2 != null){
        	if(TSet){
        		features.add("T="+TC_2+TC_1+TC0+TC1+TC2);
        	}
        }
        
        //判断当前词语前面还有没有词
        if(j - 1 >= 0){
        	w_1 = words[j - 1];
        	p_1 = poses[j - 1];
        	if(j - 2 >= 0){
        		w_2 = words[j - 2];
        		p_2 = poses[j - 2];
        	}
        }
        
        //当前词前面一个词的长度为1
        if(w_1 != null && (w_1.length() == 1)){
        	if(Bc_1w0Pc_1w0Set){
        		//当前词的前面一个词的长度为1，字符标记肯定为S,
            	features.add("Bc_1w0Pc_1w0="+"S"+p_1);
            }
        	if(w_2 != null){
        		//当前词前面第二个词,长度为1,字符标记为S
        		if(w_2.length() == 1){
        			if(Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set){
            			features.add("Bc_2w0Pc_2w0Bc_1w0Pc_1w0="+"S"+p_2+"S"+p_1);
            		}
        		}else{//当前词前面第二个词,长度不为1,字符标记为E
        			if(Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set){
            			features.add("Bc_2w0Pc_2w0Bc_1w0Pc_1w0="+"E"+p_2+"S"+p_1);
            		}
        		}
        		
        	}
        }
      
        //当前词前面一个词语的长度不为1
        if(w_1 != null && w_1.length() > 1){
        	//只要当前词前面一个词语的长度不为1，字符标记肯定是E
        	if(Bc_1w0Pc_1w0Set){
            	features.add("Bc_1w0Pc_1w0="+"E"+p_1);
            }
        	//当前词前面一个词长度为二的话，前面第二个字符的标记为B
        	if(w_1.length() == 2){
        		if(Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set){
        			features.add("Bc_2w0Pc_2w0Bc_1w0Pc_1w0="+"B"+p_1+"E"+p_1);
        		}
        	//当前词前面一个词长度大于二的话，前面第二个字符的标记为M
        	}else if(w_1.length() > 2){
        		if(Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set){
        			features.add("Bc_2w0Pc_2w0Bc_1w0Pc_1w0="+"M"+p_1+"E"+p_1);
        		}
        	}
        	
        }
        String[] contexts = features.toArray(new String[features.size()]);
		return contexts;
	}

	/**
	 * 生成c0pre
	 * @param features 已有的特征
	 * @param c0 当前字符
	 * @return
	 */
	 private List<String> addC0Prefix(List<String> features, String c0){
	       List<String> result = new ArrayList<String>();
	        
	       for(String feature : features){
	          result.add(feature);
	            
	           int p = feature.indexOf("=");
	           String name = feature.substring(0, p);
	           String value = feature.substring(p+1);
	            
	           String cof = "c0_" + name + "=" + c0 + value;
	            
	           result.add(cof);
	       }
	        
	       return result; 
	  }
	
	/**
	 * 没有分词的测试语料生成特征
	 * @param i 当前字符的位置
	 * @param characters 字符的序列
	 * @param tags 字符的标记序列
	 * @param ac 额外的信息
	 */
	public String[] getContext(int i, String[] characters, String[] tags, Object[] ac) {
		return getContext(i, characters,tags);
	}

	/**
	 * 没有分词的测试语料生成特征
	 * @param i 当前字符的位置
	 * @param characters 字符的序列
	 * @param tags 字符的标记序列
	 */
	private String[] getContext(int i, String[] characters, String[] tags) {
		//HashMap<String,List<String>> dictionary = measure.getDictionary();
//		System.out.println(i);
		
		String c1, c2, c0, c_1, c_2, c_3;
        c1 = c2 = c0 = c_1 = c_2 = c_3 = null;
        String TC_1, TC_2, TC0, TC1, TC2;
        TC_1 = TC_2 = TC0 = TC1 = TC2 = null;
        String w0,w_1,w_2,p_1,p_2;
        w0 = w_1 = w_2 = p_1 = p_2 = null;
        c0 = characters[i];
        TC0 = CharTypeUtil.featureType(c0);
        if (characters.length > i + 1)
        {
            c1 = characters[i + 1];
            TC1 = CharTypeUtil.featureType(c1);

            if (characters.length > i + 2)
            {
                c2 = characters[i + 2];
                TC2 = CharTypeUtil.featureType(c2);
            }
        }

        if (i - 1 >= 0)
        {
            c_1 = characters[i - 1];
            TC_1 = CharTypeUtil.featureType(c_1);

            if (i - 2 >= 0)
            {
                c_2 = characters[i - 2].toString();
                TC_2 = CharTypeUtil.featureType(c_2);
            }
        }

        List<String> features = new ArrayList<String>();
       //c_2
        if(c_2 != null){
        	if(c_2Set){
        		features.add("c_2="+c_2);
        	}
        }
        //c_1
        if(c_1 != null){
        	if(c_1Set){
        		features.add("c_1="+c_1);
        	}
        }
		//c0
        if(c0Set){
        	features.add("c0="+c0);
        }
        //c1
        if(c1 != null){
        	if(c1Set){
        		features.add("c1="+c1);
        	}
        }
        //c2
        if(c2 != null){
        	if(c2Set){
        		features.add("c2="+c2);
        	}
        }
        //c_2c_1
        if(c_2 != null && c_1 != null){
        	if(c_2c_1Set){
        		features.add("c_2c_1="+c_2+c_1);
        	}
        }
        //c_1c0
        if(c_1 != null){
        	if(c_1c0Set){
        		features.add("c_1c0="+c_1+c0);
        	}
        }
        //c0c1
        if(c1 != null){
        	if(c0c1Set){
        		features.add("c0c1="+c0+c1);
        	}
        }
        //c1c2
        if(c1 != null && c2 != null){
        	if(c1c2Set){
        		features.add("c1c2="+c1+c2);
        	}
        }
        //c_1c1
        if(c_1 != null && c1 != null){
        	if(c_1c1Set){
        		features.add("c_1c1="+c_1+c1);
        	}
        }
  
        //c0pre
        if(c0prefix){
        	features = addC0Prefix(features, c0);
        }
        
        //Pu
        if(PuSet){
        	if (CharTypeUtil.isChinesePunctuation(FullHalfWidthUtil.toHalfWidth(c0)))
                features.add("Pu=" + 1);
            else
                features.add("Pu=" + 0);
        }
        //T
        if(TC_2 != null && TC_1 != null && TC1 != null && TC2 != null){
        	if(TSet){
        		features.add("T="+TC_2+TC_1+TC0+TC1+TC2);
        	}
        }
        
        
        
        if(i-1 >= 0){
        	
        	//将 词的边界_词性 转成两个数组，分别是词的边界数组，词性数组
            String[] tag = new String[i];
            String[] pos = new String[i];
            for (int j = i-1; j >= 0; j--) {
    			tag[j] = tags[j].split("_")[0];
    			pos[j] = tags[j].split("_")[1];
    		}
            
            //如果当前字前面一个为E
            if(tag[i-1].equals("E")){
            	w0 = characters[i];
            	if(w0c0Set){
                 	features.add("w0c0="+w0+c0);
                }       	      
            	if(Bc_1w0Pc_1w0Set){          	
            		features.add("Bc_1w0Pc_1w0="+"E"+pos[i-1]);            
            	} 
            	if(i-2 >= 0){
            		//如果当前词的前面第二字语为E
//                	if(tag[i-2].equals("M")){
//                		if(Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set){
//                			features.add("Bc_2w0Pc_2w0Bc_1w0Pc_1w0="+"M"+pos[i-2]+"E"+pos[i-1]);
//                		}
//                	}else if(tag[i-2].equals("B")){//如果当前词的前面第二字语为B
//                		if(Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set){
//                			features.add("Bc_2w0Pc_2w0Bc_1w0Pc_1w0="+"B"+pos[i-2]+"E"+pos[i-1]);
//                		}
//                	}	
            		if(Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set){
            			features.add("Bc_2w0Pc_2w0Bc_1w0Pc_1w0="+tag[i-2]+pos[i-2]+tag[i-1]+pos[i-1]);
            		}
            	}
            	
            }
          //如果当前字前面一个为S
            if(tag[i-1].equals("S")){
            	w0 = characters[i];
            	if(w0c0Set){
                 	features.add("w0c0="+w0+c0);
                }       	      
            	if(Bc_1w0Pc_1w0Set){          	
            		features.add("Bc_1w0Pc_1w0="+"S"+pos[i-1]);            
            	}  
            	if(i-2 >= 0){
//            		if(tag[i-2].equals("E")){
//                		if(Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set){
//                			features.add("Bc_2w0Pc_2w0Bc_1w0Pc_1w0="+"E"+pos[i-2]+"S"+pos[i-1]);
//                		}
//                	}else if(tag[i-2].equals("S")){//如果当前词的前面第二字语为B
//                		if(Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set){
//                			features.add("Bc_2w0Pc_2w0Bc_1w0Pc_1w0="+"S"+pos[i-2]+"S"+pos[i-1]);
//                		}
//                	}	
            		if(Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set){
            			features.add("Bc_2w0Pc_2w0Bc_1w0Pc_1w0="+tag[i-2]+pos[i-2]+tag[i-1]+pos[i-1]);
            		}
            	}
            	
            }
            
          //如果当前字前面一个为B
            if(tag[i-1].equals("B")){
            	w0 = characters[i-1]+characters[i];
            	if(w0c0Set){
                 	features.add("w0c0="+w0+c0);
                }       	      
            	if(i-2 >=0 ){
            		if(Bc_1w0Pc_1w0Set){          	
                		features.add("Bc_1w0Pc_1w0="+tag[i-2]+pos[i-2]);            
                	} 
            		if(i-3 >= 0){
            			if(Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set){
                			features.add("Bc_2w0Pc_2w0Bc_1w0Pc_1w0="+tag[i-3]+pos[i-3]+tag[i-2]+pos[i-2]);
                		}
            		}
            		
//            		if(tag[i-2].equals("E")){
//                		if(Bc_1w0Pc_1w0Set){          	
//                    		features.add("Bc_1w0Pc_1w0="+"E"+pos[i-2]);            
//                    	}  
//                		if(i-3 >= 0){
//                			if(tag[i-3].equals("B")){
//                    			if(Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set){
//                        			features.add("Bc_2w0Pc_2w0Bc_1w0Pc_1w0="+"B"+pos[i-3]+"E"+pos[i-2]);
//                        		}
//                    		}else if(tag[i-3].equals("M")){
//                    			if(Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set){
//                        			features.add("Bc_2w0Pc_2w0Bc_1w0Pc_1w0="+"M"+pos[i-3]+"E"+pos[i-2]);
//                        		}
//                    		}
//                		}
//                		
//                	}else if(tag[i-2].equals("S")){//如果当前词的前面第二字语为B
//                		if(Bc_1w0Pc_1w0Set){          	
//                    		features.add("Bc_1w0Pc_1w0="+"S"+pos[i-2]);            
//                    	} 
//                		if(i-3 >= 0){
//                			if(tag[i-3].equals("S")){
//                    			if(Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set){
//                        			features.add("Bc_2w0Pc_2w0Bc_1w0Pc_1w0="+"S"+pos[i-3]+"S"+pos[i-2]);
//                        		}
//                    		}else if(tag[i-3].equals("E")){
//                    			if(Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set){
//                        			features.add("Bc_2w0Pc_2w0Bc_1w0Pc_1w0="+"E"+pos[i-3]+"S"+pos[i-2]);
//                        		}
//                    		}  
//                		}
//                		     		
//                	}
            	}          		
            }
            
          //如果当前字前面一个为M
            String word = characters[i-1] + characters[i];
            int record = -1;
            if(tag[i-1].equals("M")){
            	if(i-2 >= 0){
            		for (int j = i-2; j > 0; j--) {
        				if(tag[j].equals("B")){
        					record = j;
        					word = characters[j] + word;
        					break;
        				}else{
        					word = characters[j] + word;
        				}
        			}
            	}
            	
            	if(record != -1){
            		if(w0c0Set){
                     	features.add("w0c0="+word+c0);
                    }   
            		if(record-1 >= 0){
            			if(Bc_1w0Pc_1w0Set){          	
                    		features.add("Bc_1w0Pc_1w0="+tag[record-1]+pos[record-1]);            
                    	}  
            			if(record - 2 >= 0){
            				if(Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set){
                    			features.add("Bc_2w0Pc_2w0Bc_1w0Pc_1w0="+tag[record-2]+pos[record-2]+tag[record-1]+pos[record-1]);
                    		}
            			}
//            			if(tag[record-1].equals("E")){
//                    		if(Bc_1w0Pc_1w0Set){          	
//                        		features.add("Bc_1w0Pc_1w0="+"E"+pos[record-1]);            
//                        	}  
//                    		if(record-2 >= 0){
//                    			if(tag[record-2].equals("B")){
//                        			if(Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set){
//                            			features.add("Bc_2w0Pc_2w0Bc_1w0Pc_1w0="+"B"+pos[record-2]+"E"+pos[record-1]);
//                            		}
//                        		}else if(tag[record-2].equals("M")){
//                        			if(Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set){
//                            			features.add("Bc_2w0Pc_2w0Bc_1w0Pc_1w0="+"M"+pos[record-2]+"E"+pos[record-1]);
//                            		}
//                        		}
//                    		}
//                    		
//                    	}else if(tag[record-1].equals("S")){//如果当前词的前面第二字语为B
//                    		if(Bc_1w0Pc_1w0Set){          	
//                        		features.add("Bc_1w0Pc_1w0="+"S"+pos[record-1]);            
//                        	}  
//                    		if(record-2 >= 0){
//                    			if(tag[record-2].equals("E")){
//                        			if(Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set){
//                            			features.add("Bc_2w0Pc_2w0Bc_1w0Pc_1w0="+"E"+pos[record-2]+"S"+pos[record-1]);
//                            		}
//                        		}else if(tag[record-2].equals("S")){
//                        			if(Bc_2w0Pc_2w0Bc_1w0Pc_1w0Set){
//                            			features.add("Bc_2w0Pc_2w0Bc_1w0Pc_1w0="+"S"+pos[record-2]+"S"+pos[record-1]);
//                            		}
//                        		}
//                    		}                   		
//                    	}
            		}                	
            	}    		
            }    
        }
        String[] contexts = features.toArray(new String[features.size()]);
		return contexts;
	}
	
	//判断是否为词典中的词语
	public boolean isDictionalWords(String words){
		if(dictionalWords.contains(words)){
			return true;
		}else{
			return false;
		}
	}

}
