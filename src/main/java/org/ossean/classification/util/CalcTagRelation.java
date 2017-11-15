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

public class CalcTagRelation {
	private Query query = new Query();
	private List<TagPair> tagPairs = new ArrayList<TagPair>();
	private Set<TagPair> tagPairswithWeight = null;
	private List<TagPair> importantEdges = new ArrayList<TagPair>();
	public String prePath = "f:\\";
	public String postPath = ".dot";
	public long now = System.currentTimeMillis();
	public List<String> importantPoint = new ArrayList<String>();
	public Map<TagPair, Integer> map1 = new HashMap<TagPair, Integer>();
	public Map<TagPair, Integer> map2 = new HashMap<TagPair, Integer>();
	public Map<String, Integer> map3 = new HashMap<String, Integer>();
	private void init() {
		// this.tagPairs = query.getTagPairs();
		// this.tagPairswithWeight = query.getTagPairsWithWeight();
		//this.importantEdges = query.getImportantTagPairs();
		/*Map<TagPair, Integer> map = query.getTagPairsSOF();  
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()){
			Map.Entry entry = (Map.Entry) iter.next();
			TagPair key = (TagPair) entry.getKey();
			int value = map.get(key);
			importantEdges.add(key);
		}*/
		importantPoint = query.getimportantPoint();
		map1 = query.getTagPairsOP();
		map2 = query.getTagPairsSOF();
		map3 = query.OpenhubSpecTagGenerality();
	}

	public void calc(String tag) throws IOException {
		//Set<String> tagsSet=new HashSet<String>();
		File f = new File(prePath + now + postPath);
		FileWriter fw = new FileWriter(f);
		fw.append("digraph G {\n");
		this.init();
		int l = importantPoint.size();
		System.out.println(l);
		
		for(String p : importantPoint)
		{
			//Map<String, Integer> mapofp = query.getWeightMap(p);
			System.out.println(p);
			int max = 0;
			String index = null;
			Map<String, Integer> mapofp = query.getWeightMap(p);
			for(String q : importantPoint)
			{
				if(p != q)
				{
					{						
						int tag1num = map3.get(p);
						int tag2num = map3.get(q);
						String[] tags = {p , q};
						TagPair tagpair = new TagPair(tags);
						if(map1.containsKey(tagpair))
						{
							if (tag1num > tag2num) {
								continue;
							}else if (tag1num < tag2num){
								int mid = 0;
								if(map2.containsKey(tags))
									mid = mapofp.get(q) + map2.get(tags);
								else
									mid = mapofp.get(q);
								if(mid > max)
								{
									max = mid;
									index = q;
								}
							}else{
								tagpair.setDirection(0);
								continue;
							}
						}
					}	
				}
				else
				{
					continue;
				}
			}
			if(max != 0)
			{
				String[] biggest = {p, index};
				int tmp = query.getWeight(p, index);
				if(tmp > 10)
				{
					TagPair mostsuitable = new TagPair(biggest);
					mostsuitable.setDirection(-1);
					fw.append(mostsuitable.toString() + ";" +"\n");
					System.out.println(mostsuitable.toString());
				}
			}        
		}
		fw.append("}\n");
		fw.close();
	}
                   
	public void printTagPairs() {
		this.init();
		for (TagPair tagPair : tagPairs) {
			System.out.println(tagPair.toString());
		}
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		CalcTagRelation calc = new CalcTagRelation();
		String tag = "database";
		calc.calc(tag);
		//String path = calc.prePath + calc.now;
		//Runtime.getRuntime().exec(
		//		"dot -Tpng " + path + ".dot -o " + path
		//				+ ".png");
	}

}
