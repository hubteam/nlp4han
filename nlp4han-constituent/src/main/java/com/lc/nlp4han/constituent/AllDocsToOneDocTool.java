package com.lc.nlp4han.constituent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 将输入路径下的所有文件转成一个文件的运行类
 * @author 王馨苇
 *
 */
public class AllDocsToOneDocTool {
	
	/**
	 * 将输入路径下的所有文件合并成一个文件
	 * @param frompath 输入文件目录名
	 * @param topath 输出文件的文件名
	 * @throws IOException
	 */
	public static void allDocsToOneDocRun(String frompath, String topath) throws IOException{
		File directory = new File(frompath);
		File[] subFiles = directory.listFiles(new FileFilter(){
			@Override
			public boolean accept(File file) {
				//过滤掉readme文件
				return !file.getName().equals("README");
			}			
		});
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(topath)));
		BufferedReader br = null;
		
		for (File file : subFiles) {
			br = new BufferedReader(new FileReader(file));
			String line = "";
			while((line = br.readLine()) != null){
				if(!line .equals("")){
					bw.write(line);
					bw.newLine();
				}
			}
		}
		System.out.println("success");
		bw.close();
		br.close();
	}
	
	public static void main(String[] args) throws IOException {
		String cmd = args[0];
		if(cmd.equals("-combine")){
			String frompath = args[1];//读文件的路径
			String topath = args[2];//输出文件的路径
			AllDocsToOneDocTool.allDocsToOneDocRun(frompath, topath);
		}
	}
}
