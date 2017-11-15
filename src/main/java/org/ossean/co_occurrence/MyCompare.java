package org.ossean.co_occurrence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.ossean.classification.type.TagPair;
import org.ossean.classification.util.LuceneIndex;
import org.ossean.classification.util.LuceneSearcher;
import org.ossean.classification.util.PorterStemmer;
import org.ossean.classification.util.Query;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class MyCompare {
	public FileWriter fw;
	//public FileWriter fw2;
	
	public List<String> father = new ArrayList<String>(); //存储从待优化结构中读出来的信息
	public List<String> son = new ArrayList<String>();
	public List<TagPair> tagpair = new ArrayList<TagPair>();
	public List<String> changed = new ArrayList<String>(); //根据领域知识修改过的子节点
	public List<String> equal = new ArrayList<String>();  //领域知识中的同义词
	public List<String> mem = new ArrayList<String>();  //要改为的___形式
	
	File sfFile = new File("e:/sf_topics_new.txt");
	File tagFile = new File("e:/raw_rs.txt");
	
	public MyCompare() throws IOException{
		try {
			fw = new FileWriter("e:/out" + new Date().getTime() + ".txt");
			//fw2 = new FileWriter("e:/out" + new Date().getTime() + ".txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int score(String tmp1, String tmp2)
	{
		if(!father.contains(tmp1)  || (father.contains(tmp1) && !son.contains(tmp2))  )
				return 2;
		if(isfather(tmp1, tmp2))
			return 1;
		else if(isfather(tmp2, tmp1))
			return 0;
		else
			return -1;
	}
	
	public boolean isfather(String tmp1, String tmp2)
	{
		String[] p = {tmp1, tmp2};
		if(tagpair.contains(p))
			return true;
		else
			for(TagPair q:tagpair)
			{
				String[] r = q.getTags();
				if(r[0] == tmp1)
					return isfather(r[1], tmp2);
			}
		return false;
	}
	
	public void compare() throws IOException
	{
		Scanner sfSc = new Scanner(sfFile);
		Scanner tagSc = new Scanner(tagFile);
		Scanner tagSc2 = new Scanner(tagFile);
		String tmp;
		
		while(tagSc.hasNextLine())
		{
			tmp = tagSc.nextLine();
			String[] strOP = StringUtils.splitByWholeSeparator(tmp, "->");
			String[] stropp = {strOP[0].trim(), strOP[1].trim()}; 
			father.add(stropp[0]);
			son.add(stropp[1]);
			TagPair a = new TagPair(stropp);
			tagpair.add(a);
		}
		
		while(sfSc.hasNextLine())
		{
			tmp = sfSc.nextLine();
			String[] strSF = StringUtils.splitByWholeSeparator(tmp, "::");
			int size = strSF.length;
			for(int i=1; i<size-1; i++)
			{
				int j = i+1;
				String tmp11 = strSF[i].toLowerCase();
				String tmp22 = strSF[j].toLowerCase();
				String tmp1 = tmp11.trim();
				String tmp2 = tmp22.trim();
				if(tmp1.contains("___"))
				{
					String[] str = StringUtils.splitByWholeSeparator(tmp1, "___");
					if(!equal.contains(str[0]))
					{
						equal.add(str[0]);
						mem.add(tmp1);
					}
					if(!equal.contains(str[1]))
					{
						equal.add(str[1]);
						mem.add(tmp1);
					}
				}
				if(tmp2.contains("___"))
				{
					String[] str = StringUtils.splitByWholeSeparator(tmp2, "___");
					if(!equal.contains(str[0]))
					{
						equal.add(str[0]);
						mem.add(tmp2);
					}
					if(!equal.contains(str[1]))
					{
						equal.add(str[1]);
						mem.add(tmp2);
					}
				}
				String[] c = {tmp1, tmp2};
				TagPair d = new TagPair(c);
				int res = score(tmp1, tmp2);
				if(res == 0)                       //方向错误
				{
					fw.write(tmp1 + "->" + tmp2  + ";" + "\n");
					changed.add(tmp2);
				}
				else if(res == -1)  //tmp1在father里，tmp2在son里，但是没有tmp1->tmp2的路径
				{
					fw.write(tmp1 + "->" + tmp2  + ";" + "\n");
					changed.add(tmp2);
				}
				else if(res == 2)                 //标签在sof中未出现过
				{
					fw.write(tmp1 + "->" + tmp2  + ";" + "\n");
					changed.add(tmp2);
				}
				else 
					continue;
			}
		}
		sfSc.close();
		tagSc.close();
		
		while(tagSc2.hasNextLine())
		{
			tmp = tagSc2.nextLine();
			String[] strop = StringUtils.splitByWholeSeparator(tmp, "->");
			String[] strp = {strop[0].trim(), strop[1].trim()};
			if(!changed.contains(strp[1]) && !equal.contains(strp[0]) && !equal.contains(strp[1]))
			{
				fw.write(tmp + ";" + "\n");
			}
			if(equal.contains(strp[0])  && !equal.contains(strp[1]) && !changed.contains(strp[1]))
			{
				for(int tk1=0; tk1<equal.size(); tk1++)
				{
					if(strp[0].equals(equal.get(tk1)))
					{
						fw.write(mem.get(tk1) + "->" + strp[1] + ";" + "\n");
						break;
					}
				}
			}
			if(equal.contains(strp[1]) && !equal.contains(strp[0])  && !changed.contains(strp[1]))
			{
				for(int tk2=0; tk2<equal.size(); tk2++)
				{
					if(strp[1].equals(equal.get(tk2)))
					{
						fw.write(strp[0] + "->" + mem.get(tk2) + ";" + "\n");
						break;
					}
				}
			}
			if(equal.contains(strp[1]) && equal.contains(strp[0]) && !changed.contains(strp[1]))
			{
				for(int tk3=0; tk3<equal.size(); tk3++ )
				{
					for(int tk4=0; tk4<equal.size(); tk4++ )
					{
						if(strp[0].equals(equal.get(tk3)) && strp[1].equals(equal.get(tk4)))
						{
							fw.write(mem.get(tk3) + "->" + mem.get(tk4) + ";" + "\n");
							break;
						}
					}
				}
			}
		}
		tagSc2.close();
		fw.close();
	}

	public static void main(String[] args) throws IOException {
		MyCompare csf = new MyCompare();
		csf.compare();
	}
}
     