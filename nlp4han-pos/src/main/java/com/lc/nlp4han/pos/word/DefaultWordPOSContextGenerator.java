package com.lc.nlp4han.pos.word;

import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.ml.util.AbstractStringContextGenerator;

public class DefaultWordPOSContextGenerator extends AbstractStringContextGenerator
{

    protected final String SE = "*SE*";
    protected final String SB = "*SB*";

    public DefaultWordPOSContextGenerator()
    {
    }

    public String[] getContext(int index, String[] sequence, String[] priorDecisions, Object[] additionalContext)
    {
        //System.out.println("WordPOSContextGenerator");
        return getContext(index, sequence, priorDecisions);
    }

    public String[] getContext(int index, String[] tokens, String[] tags)
    {
        String next, nextnext = null, lex, prev, prevprev = null;
        String tagprev, tagprevprev;
        tagprev = tagprevprev = null;

        lex = tokens[index].toString();
        if (tokens.length > index + 1)
        {
            next = tokens[index + 1].toString();
            if (tokens.length > index + 2)
                nextnext = tokens[index + 2].toString();
            else
                nextnext = SE; // Sentence End

        }
        else
        {
            next = SE; // Sentence End
        }

        if (index - 1 >= 0)
        {
            prev = tokens[index - 1].toString();
            tagprev = tags[index - 1];

            if (index - 2 >= 0)
            {
                prevprev = tokens[index - 2].toString();
                tagprevprev = tags[index - 2];
            }
            else
            {
                prevprev = SB; // Sentence Beginning
            }
        }
        else
        {
            prev = SB; // Sentence Beginning
        }
        
        List<String> e = new ArrayList<>();
//        e.add("default");
        // add the word itself
        
        e.add("w0=" + lex);
        
        // add the words and pos's of the surrounding context
        if (prev != null)
        {
            e.add("w_1=" + prev);
            
            e.add("w_1w0=" + prev +"," + lex);
            
            if (tagprev != null)
            {
                e.add("t_1=" + tagprev);
            }
            
            if (prevprev != null)
            {
                e.add("w_2=" + prevprev);
                
                e.add("w_2w_1=" + prevprev + "," + prev);
                
                if (tagprevprev != null)
                {
                    e.add("t_2t_1=" + tagprevprev + "," + tagprev);
                }
            }
            
            if (next != null)
                e.add("w_1w1=" + prev + "," + next);
        }

        if (next != null)
        {
            e.add("w1=" + next);
            
            e.add("w0w1=" + lex + "," + next);
            
            if (nextnext != null)
            {
                e.add("w2=" + nextnext);
                
                e.add("w1w2=" + next + "," + nextnext);
            }
        }
        
        String[] contexts = e.toArray(new String[e.size()]);
        
        return contexts;
    }

}
