package org.ossean.co_occurrence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.management.RuntimeErrorException;

import org.ossean.classification.type.TagPair;
import org.ossean.classification.util.Filter;
import org.ossean.classification.util.Query;

/*
public class Subsumption {
	public double t = 0.6;
	public int dMin = 2;
	public long now = System.currentTimeMillis();

	public String prePath = "f:/rs";
	public String postPath = ".dot";
	public Filter filter = new Filter("stop_words_for_database");
	public Query query = new Query();
	public Map<Integer, Set<String>> openhubTagMap = new LinkedHashMap<Integer, Set<String>>();
	public Map<String, Set<Integer>> tagMap = new LinkedHashMap<String, Set<Integer>>();
	public List<TagPair> tagPairList;
	public int reviseCount=0;

	public Set<String> parse(String tagStr) {
		Set<String> tags = new TreeSet<String>();
		if (tagStr != null && !"".equals(tagStr)) {
			while (tagStr.length() > 0) {
				int leftPos = tagStr.indexOf('<');
				int rightPost = tagStr.indexOf('>');
				String tagName = tagStr.substring(leftPos + 1, rightPost);
				tagStr = tagStr.substring(rightPost + 1);
				if (!filter.contains(tagName)) {
					tags.add(tagName);
				}
			}
		}
		return tags;
	}

	public Subsumption() {
		System.out.println("init");
		init();
	}

	public void init() {
		Map<Integer, String> tags = query.getOpenhubTags();
		System.out.println("get all tags");
		for (Entry<Integer, String> entry : tags.entrySet()) {
			openhubTagMap.put(entry.getKey(), parse(entry.getValue()));
		}
		System.out.println("start transform");
		transform();
		System.out.println("end transform");
	}

	// 项目id，Set<tag>类型的Map转换成tag,Set<id>类型的Map
	public void transform() {
		Map<TagPair, Integer> tmpMap = new HashMap<TagPair, Integer>();
		int i = 0;
		for (Entry<Integer, Set<String>> entry : openhubTagMap.entrySet()) {
			int id = entry.getKey();
			Set<String> set = entry.getValue();
			for (String tag : set) {
				if (tagMap.containsKey(tag)) {
					tagMap.get(tag).add(id);
				} else {
					Set<Integer> tmp = new LinkedHashSet<Integer>();
					tmp.add(id);
					tagMap.put(tag, tmp);
				}
			}
			processOneItem(set, tmpMap);
			i++;
		}
		tagPairList = tagPairMapToList(tmpMap);
	}

	public List<TagPair> tagPairMapToList(Map<TagPair, Integer> tmp) {
		List<TagPair> rt = new ArrayList<TagPair>();
		for (Entry<TagPair, Integer> entry : tmp.entrySet()) {
			TagPair tp = entry.getKey();
			Integer weight = entry.getValue();
			tp.setWeight(weight);
			if (weight > dMin) {
				rt.add(tp);
			}
		}
		return rt;
	}

	public void processOneItem(Set<String> set, Map<TagPair, Integer> map) {
		List<String> list = new ArrayList<String>();
		list.addAll(set);
		if (list.size() > 2) {
			for (int i = 0; i < list.size(); i++) {
				for (int j = i + 1; j < list.size(); j++) {
					String str1 = list.get(i);
					String str2 = list.get(j);
					if (!str1.equals(str2)) {
						String[] tmp = new String[] { str1, str2 };
						TagPair tp = new TagPair(tmp);
						if (map.containsKey(tp)) {
							map.put(tp, map.get(tp) + 1);
						} else {
							map.put(tp, 1);
						}

					}
				}
			}
		}
	}

	// 计算subsume关系
	public void calcSubsumption() {
		for (int i = 0; i < tagPairList.size(); i++) {
			TagPair tp = tagPairList.get(i);
			String[] strArr = tp.getTags();
			Set<Integer> set0 = tagMap.get(strArr[0]);
			Set<Integer> set1 = tagMap.get(strArr[1]);
//			if((strArr[0].equals("relational")&&strArr[1].equals("engine"))||((strArr[1].equals("relational")&&strArr[0].equals("engine")))){
//				System.out.println(strArr[0]+": ");
//				printSet(set0);
//				System.out.println(strArr[1]+": ");
//				printSet(set1);
//				//System.out.println(set1.toArray());
//			}
			tp.setDirection(compareTwoSet(set0, set1));
		}
	}
	
	public void reviseRelation(){
		String[] strArr=new String[]{"relational", "software_development"};
		TagPair tmp = new TagPair(strArr);
		
		for(TagPair tagPair:tagPairList){
			if(tagPair.tagsEquals(tmp)){
				System.out.println("");
			}
			String tag1=tagPair.getTags()[0];
			String tag2=tagPair.getTags()[1];
			int reviseNum=calcRiseNum(tag1, tag2);
//			System.out.print(tag1+"  "+tag2+": "+tagPair.getDirection()+"revised: "+reviseNum+"\n");
			if(reviseNum!=-1&&(tagPair.getDirection()==0||tagPair.getDirection()==1)){
				if(tagPair.getDirection()!=reviseNum){
					reviseCount++;
				}
				tagPair.setDirection(reviseNum);
			}
		}
	}
	
	int calcRiseNum(String tag1,String tag2){
		int right=0;
		int left=0;
		for(Entry<String, Set<Integer>> entry:tagMap.entrySet()){
			String tag= entry.getKey();
			
			if(!tag.equals(tag1)&&!tag.equals(tag2)){
				String[] tmp1 = new String[]{tag1,tag};
				TagPair tp1=new TagPair(tmp1);
				
				String[] tmp2=new String[]{tag,tag2};
				TagPair tp2=new TagPair(tmp2);
				
				int direct = -2;
				int d1=getDirection(tp1);
				int d2=getDirection(tp2);
				if(d1!=-1&&d2!=-1){
					if(d1==1&&d2==1){
						right++;
					}else if(d1==0&&d2==0){
						left++;
					}
				}
			}
		}
		
		if(left>right){
			return 0;
		}else if(left<right){
			return 1;
		}else{
			return -1;
		}
	}
	
	int getDirection(TagPair tp){
		for(TagPair tmp:tagPairList){
			if(tmp.tagsEquals(tp)){
				return tmp.getDirection();
			}
		}
		return -1;
	}
	
	public void printSet(Set<Integer> set){
		//System.out.println("set: ");
		for(Integer i:set){
			System.out.print(i+" ");
		}
	}

	// 比较两个集合的关系	p(x|y)>t, p(y|x)<t
	public int compareTwoSet(Set<Integer> set0, Set<Integer> set1) {
		int rt = 0;
		int intersectNum = 0;
		for (Integer num : set0) {
			if (set1.contains(num)) {
				intersectNum++;
			}
		}
		// int set0Remain=set0.size()-intersectNum;
		// int set1Remain=set1.size()-intersectNum;
		if (((intersectNum / set1.size()) >= t)
				&& ((intersectNum / set0.size()) < t)) {
			if (((intersectNum / set0.size()) >= t)
					&& ((intersectNum / set1.size()) < t)) {
				return 2;
			} else {
				return 1;
			}
		} else {
			if (((intersectNum / set0.size()) >= t)
					&& ((intersectNum / set1.size()) < t)) {
				return 0;
			}
			return -1;
		}
	}

	public void print() {
		File f = new File(prePath + postPath);

		try {
			if (f.exists()) {
				// f.delete();
				f = new File(prePath + now + postPath);
			}
			FileWriter fw = new FileWriter(f);
			fw.append("digraph G {\n");
			for (TagPair tp : tagPairList) {
				if (tp.getDirection() != -1) {
					fw.append(tp.toString() + "\n");
				}
			}
			fw.append("}\n");
			fw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Subsumption s = new Subsumption();
		s.calcSubsumption();
		s.reviseRelation();
		System.out.println(s.reviseCount);
		s.print();
		String path = s.prePath + s.now;
		Runtime.getRuntime().exec(
				"dot -Tpng " + path + ".dot -o " + path
						+ ".png");
	}

}
*/