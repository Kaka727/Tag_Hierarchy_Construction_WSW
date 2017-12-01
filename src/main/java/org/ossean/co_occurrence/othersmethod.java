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

public class othersmethod {
	public Query query = new Query();
	
	public FileWriter fw;
	
	public String root = new String();
	public List<String> tags = new ArrayList<String>();
	public Map<String, Integer> tag_to_generality = new HashMap<String, Integer>();
	public List<String> exist = new ArrayList<String>();
	public List<String> waiting = new ArrayList<String>();
	public List<String> tag_after_sort = new ArrayList<String>();
	public List<String> past = new ArrayList<String>();
	public Integer times = 0;
	public othersmethod()
	{
		tags = query.getimportantPoint();
		tag_to_generality = query.getgenerality(tags);
		try {
			fw = new FileWriter("e:/out" + new Date().getTime() + ".txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//sort();
	}
	
	
	/*public void sort(){
		int len = tags.size();
		for(int l=0;l<len;l++)
		{
			System.out.println(tags.get(l));
		}
		for(int i=0; i<len-1;i++)
		{
			for(int j=i+1; j<len;j++)
			{
				if(tag_to_generality.get(tags.get(i)) > tag_to_generality.get(tags.get(j)));
				{
					//System.out.println(tag_to_generality.get(tags.get(i)));
					String temp = tags.get(i);
					tags.set(i, tags.get(j));
					tags.set(j, temp);
				}
			}
			
		}
		for(int l=0;l<len;l++)
		{
			System.out.println(tags.get(l));
		}
	}*/
	
	public void calc() throws IOException
	{
		while(times <tags.size() )
		{
			System.out.println(times);
			for(String p:tags)
			{
				if(!past.contains(p))
				{
					System.out.println(p);
					System.out.println(tag_to_generality.get(p));
					int tmp = 1;
				
					for(String q:tags)
					{
						if(tag_to_generality.get(q) > tag_to_generality.get(p) && !past.contains(q))
						{
							System.out.println("some one bigger");
							tmp = 0;
							break;
						}
					}
					if(tmp == 1)
					{
						times++;
						past.add(p);
						System.out.println("Hello,I am coming");
						if(exist.size() == 0)
						{
							root = p;
							exist.add(p);
						}
						else if(tag_to_generality.get(p) > 20000)
						{
							waiting.add(p);
						}
						else
						{
							if(exist.size() == 1)
							{
								fw.write(root + "->" + p +";" + "\n");
								exist.add(p);
								System.out.println(root + "->" + p +";" + "\n");
							}
							else
							{
								String record_tag = new String();
								int record_value = 0;
								for(String r:exist)
								{
									if(!r.equals(root))
									{
										int t = query.getcooccurrence(p, r);
										if(t > record_value)
										{
											record_tag = r;
											record_value = t;
										}
									}
								}
								if(record_value > 10)
								{
										fw.write(record_tag + "->" + p +";" + "\n");
										exist.add(p);
										System.out.println(record_tag + "->" + p +";" + "\n");
								}
								else
								{
									waiting.add(p);
								}
							}
						}
					}
				}
				
			}//tags.remove(p);	
		
		}
		System.out.println("overoveroveroverover");
		for(String a:waiting)
		{
			String record_again_tag = new String();
			int record_again_value = 0;
			for(String b:exist)
			{
				int tt = query.getcooccurrence(a, b);
				if(tt > record_again_value)
				{
					record_again_tag = b;
					record_again_value = tt;
				}
			}
			if(record_again_value > 10)
			{
				fw.write(record_again_tag + "->" + a +";" + "\n");
				System.out.println(record_again_tag + "->" + a +";" + "\n");
				exist.add(a);
			}
			else
			{
				fw.write(root + "->" + a + ";" + "\n");
				System.out.println(root + "->" + a + ";" + "\n");
				exist.add(a);
			}
		}
		
		fw.close();
		System.out.println(exist.size());
	}
	
	

	public static void main(String[] args) throws IOException
	{
		othersmethod ctg = new othersmethod();
		ctg.calc();
	}
}
