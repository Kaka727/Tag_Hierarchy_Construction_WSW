package org.ossean.classification.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

public class YinsMethod {
	private static Query query = new Query();
	public static int limit = 5;
	public static int deepth = 2;
	public static FileWriter fw = null;
	public static SortedSet<String> rs = new TreeSet<String>();

	public static void recurseConstruct(List<String> tags) {
		System.out.println(deepth + ": " + tags);
		List<String> next = new ArrayList<String>(); // 存储下一层所有孩子节点
		if (tags == null) {
			//next = query.getTopTags(limit);
			recurseConstruct(next);
			deepth--;
			return;
		}
		if (deepth > 0) {
			List<String> tmp = new ArrayList<String>(); // 取得标签的孩子节点
			for (String tag : tags) {
				//tmp = query.getMostRelativeTags(tag, limit);
				for (String tmpTag : tmp) {
					next.add(tmpTag);

					rs.add(handleTag(tag) + "->" + handleTag(tmpTag) + "\n");

				}
			}
			deepth--;
			recurseConstruct(next);
		}

	}

	private static String handleTag(String tag) {
		String tmp;
		tmp = StringUtils.replace(tag, "-", "_");
		tmp = StringUtils.replace(tmp, "3d", "three_dimension");
		return tmp;
	}

	public static void init() throws IOException {
		File f = new File("f:/rs.dot");
		if (f.exists()) {
			f.delete();
		}
		try {
			fw = new FileWriter(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fw.append("digraph G {\n");
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		init();
		recurseConstruct(null);
		
		for(String edge:rs){
			fw.append(edge);
		}
		
		fw.append("}");
		try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

		String cmd = "dot -Tpng f:/rs.dot -o f:/rs.png";
		Runtime run = Runtime.getRuntime();
		run.exec(cmd);
	}

}