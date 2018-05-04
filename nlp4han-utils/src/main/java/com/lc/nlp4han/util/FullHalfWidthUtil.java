package com.lc.nlp4han.util;

/**
 * 全角半角转换
 * 
 * @author 刘小峰
 *
 */
public class FullHalfWidthUtil
{

	/**
	 * 全角转半角
	 * 
	 * @param text
	 *            待转换含有全角的字符串
	 * @return
	 */
	public static String toHalfWidth(String text)
	{
		return String.valueOf(toHalfWidth(text.toCharArray()));
	}

	public static char[] toHalfWidth(char[] text)
	{
		char[] newText = new char[text.length];
		for (int i = 0; i < text.length; i++)
		{
			char ch = text[i];
			if (ch == 12288)
				newText[i] = ' ';
			else if (ch >= 65281 && ch <= 65374)
			{
				ch -= 65248;
				newText[i] = ch;
			}
			else
			{
				newText[i] = ch;
			}
		}
		return newText;
	}

	/**
	 * 半角转全角
	 * 
	 * @param value
	 * @return
	 */
	public static String toFullWidth(String value)
	{
		char[] cha = value.toCharArray();

		for (int i = 0; i < cha.length; i++)
		{
			if (cha[i] == 32)
			{
				cha[i] = (char) 12288;
			}
			else if (cha[i] < 127)
			{
				cha[i] = (char) (cha[i] + 65248);
			}
		}
		return new String(cha);
	}

}
