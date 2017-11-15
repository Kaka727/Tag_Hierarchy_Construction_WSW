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

public class sontree {
	public FileWriter fw;
	
	public List<String> father = new ArrayList<String>(); //存储从待优化结构中读出来的信息
	public List<String> son = new ArrayList<String>();
	public List<TagPair> tagpair = new ArrayList<TagPair>();
	public List<String> related = new ArrayList<String>();
	
	File sfFile = new File("e:/source.txt");
	public sontree() throws IOException{
		try {
			fw = new FileWriter("e:/tree" + new Date().getTime() + ".txt");
			Scanner tagSc = new Scanner(sfFile);
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
			tagSc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void draw(String str) throws IOException
	{
		int i = tagpair.size();
		System.out.println(str);
		for(int j=0; j<i; j++)
		{
			if(str.equals(tagpair.get(j).getTags()[0]))
			{
				fw.write(str + "->" + tagpair.get(j).getTags()[1] + ";");
				if(!related.contains(tagpair.get(j).getTags()[1]))
				{
					related.add(tagpair.get(j).getTags()[1]);
					draw(tagpair.get(j).getTags()[1]);
				}
			}
		}
		if(str.equals("web"))
			fw.close();
	}
	
	public static void main(String[] args) throws IOException {
		sontree csf = new sontree();
		String str = "web";
		csf.draw(str);
	}

}
