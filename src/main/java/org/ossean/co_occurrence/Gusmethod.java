package org.ossean.co_occurrence;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ossean.classification.type.TagPair;
import org.ossean.classification.util.Query;

public class Gusmethod {

    public Query query = new Query();
	public FileWriter fw;
	public List<String> tags = new ArrayList<String>();
	public Map<String, Integer> generality = new HashMap<String, Integer>();
	public List<TagPair> tagPairs = new ArrayList<TagPair>();
	
	public Gusmethod()
	{
		tags = query.getimportantPoint();
		generality = query.getgenerality(tags);
		try {
			fw = new FileWriter("e:/out" + new Date().getTime() + ".txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void calc() throws IOException
	{
		System.out.println(tags.size());
		for(String p:tags)
		{
			if(p.equals("css"))
			{
			for(String q:tags)
			{
				//Tagpair a = new Tagpair();
				String[] str1 = {p, q};
				String[] str2 = {q, p};
				TagPair a1 = new TagPair(str1);
				TagPair a2 = new TagPair(str2);
				if(!p.equals(q) && query.is_cooccurrence(p, q) && !tagPairs.contains(a1))
				{
					tagPairs.add(a1);
					tagPairs.add(a2);
					if(generality.get(p) > generality.get(q))
					{
						fw.write(p + "->" + q +";" + "\n");
						System.out.println(p + "->" + q +";" + "\n");
					}
					else
					{
						fw.write(q + "->" + p +";" + "\n");
						System.out.println(q + "->" + p +";" + "\n");
					}
				}
			}
			}
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		Gusmethod ctg = new Gusmethod();
		ctg.calc();
	}
}
