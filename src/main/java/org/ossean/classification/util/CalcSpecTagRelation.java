package org.ossean.classification.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ossean.classification.type.TagPair;

public class CalcSpecTagRelation {
	private static Query query = new Query();
	public String prePath = "f:\\";
	public String postPath = ".dot";
	public static List<String> tags= new ArrayList<String>();
	public long now = System.currentTimeMillis();
	public static Map<TagPair, Integer> map = new HashMap<TagPair, Integer>();
	//public List<String> son = new ArrayList<String>();
	
	/*
	public boolean isright1(String tag1,String tag2)
	{
		for(int i=0; i<tags.size(); i++)
		{
			String mid = tags.get(i);
			String[] mid1 = {tag1, mid};
			String[] mid2 = {mid, tag2};
			TagPair trans1 = new TagPair(mid1);
			TagPair trans2 = new TagPair(mid2);
			if(query.getTagConum(tag1) > query.getTagConum(mid) &&
					query.getTagConum(mid) > query.getTagConum(tag2) &&
					map.containsKey(trans1) &&
					map.containsKey(trans2))
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean isright2(String tag1,String tag2)
	{
		for(int i=0; i<tags.size(); i++)
		{
			String mid = tags.get(i);
			String[] mid1 = {tag1, mid};
			String[] mid2 = {mid, tag2};
			TagPair trans1 = new TagPair(mid1);
			TagPair trans2 = new TagPair(mid2);
			if(query.getTagConum(tag1) < query.getTagConum(tags.get(i)) &&
					query.getTagConum(tags.get(i)) < query.getTagConum(tag2) &&
					map.containsKey(trans1) &&
					map.containsKey(trans2))
			{
				return false;
			}
		}
		return true;
	}
	*/
	/*
	public void calc(String tag) throws IOException {
		File f = new File(prePath + tag + postPath);
		FileWriter fw = new FileWriter(f);

		for(int j=0; j<tags.size(); j++)
		{
			for(int k=j+1; k<tags.size(); k++)
			{
				String tag1 = tags.get(j);
				String tag2 = tags.get(k);
				String[] t = {tag1, tag2};
				int num1 = query.getTagConum(tag1);
				int num2 = query.getTagConum(tag2);
				TagPair tagpair = new TagPair(t);
				if(num1 > num2)
				{
					if(this.isright1(tag1, tag2))
					{
						if(map.containsKey(tagpair))
						{
							tagpair.setDirection(1);
							son.add(tag2);
							fw.append(tagpair.toString() + ";" +"\n");
						}
					}
				}
				else if(num1 < num2)
				{
					if(this.isright2(tag1, tag2))
					{
						if(map.containsKey(tagpair))
						{
							tagpair.setDirection(-1);
							son.add(tag1);
							fw.append(tagpair.toString() + ";" +"\n");
						}
					}
				}
				else
				{
					tagpair.setDirection(0);
					fw.append(tagpair.toString() + ";" +"\n");
				}
			}
		}
		for(String a:tags)
		{
			if(!son.contains(a))
			{
				String[] fi = {tag, a};
				TagPair Fi = new TagPair(fi);
				Fi.setDirection(1);
				fw.append(Fi.toString() + ";" +"\n");
			}
		}
		fw.close();
	}*/
	
	public void calc(String tag) throws IOException {
		File f = new File(prePath + tag + postPath);
		FileWriter fw = new FileWriter(f);
		
		fw.append("digraph G {\n");
		int l = tags.size();
		System.out.println(l);
		for(String p : tags)
		{
			System.out.println(p);
			int max = 0;
			String index = null;
			for(String q : tags)
			{
				if(p != q)
				{
					int tag1num = query.getTagConum(p);
					int tag2num = query.getTagConum(q);
					String[] tags = {p , q};
					TagPair tagpair = new TagPair(tags);
					if(map.containsKey(tagpair))
					{
						if (tag1num > tag2num) {
							continue;
						}else if (tag1num < tag2num){
							int mid = query.getWeight(p, q);
							if(mid > max)
							{
								max = mid;
								index = q;
							}
						}else{
							tagpair.setDirection(0);
							fw.append(tagpair.toString() + ";" +"\n");
							continue;
						}
					}	
				}
				else
				{
					continue;
				}
			}
			String[] biggest = {p, index};
			TagPair mostsuitable = new TagPair(biggest);
			mostsuitable.setDirection(-1);
			fw.append(mostsuitable.toString() + ";" +"\n");
			System.out.println(mostsuitable.toString());
		}
		fw.append("}\n");
		fw.close();
	}

	public static void main(String[] args) throws IOException{
		CalcSpecTagRelation calc = new CalcSpecTagRelation();
		String tag = "web";
		tags = query.getSpecTags(tag);
		tags.add(tag);
		for(int i=0; i<tags.size(); i++)
		{
			System.out.println(tags.get(i));
		}
		map = query.getTagPairsOP();
		calc.calc(tag);
	}
}
