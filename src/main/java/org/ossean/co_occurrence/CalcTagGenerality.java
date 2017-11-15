package org.ossean.co_occurrence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ossean.classification.type.TagPair;
import org.ossean.classification.util.Query;

public class CalcTagGenerality {
	public Query query = new Query();
	public List<TagPair> tagPairs = new ArrayList<TagPair>();
	public List<String> tags = new ArrayList<String>();
	public Map<String, Integer> tagNums = new HashMap<String, Integer>();
	public Map<String, String> coTagMap = new HashMap<String, String>();
	public Map<TagPair, Integer> tagPairsOP = new HashMap<TagPair, Integer>();

	public CalcTagGenerality() {
		//tagPairs = query.getTagPairs2();
		tags = query.getAllTags();
		//tagNums = query.getTagNums();
		tagPairsOP = query.getTagPairsOP();
		Iterator iter = tagPairsOP.entrySet().iterator();
		while (iter.hasNext()){
			Map.Entry entry = (Map.Entry) iter.next();
			TagPair key = (TagPair) entry.getKey();
			int value = tagPairsOP.get(key);
			String[] str = key.getTags();
			query.save2(str[0], str[1], value);
			System.out.println(key.toString());
			System.out.println(value);
			tagPairs.add(key);
		}
		initCotag();
		// tagPairs.clear();
		// tags.clear();
		// tagNums.clear();
		// String[] tmp = { "tag1", "tag2" };
		// tagPairs.add(new TagPair(tmp, 1));
		// tags.add("tag1");
		// tagNums.put("tag2", 5);
	}

	public void initCotag() {
		for (TagPair tp : tagPairs) {
			String[] tmp = tp.getTags();
			if (coTagMap.containsKey(tmp[0])) {
				coTagMap.put(tmp[0], coTagMap.get(tmp[0]) + "#####" + tmp[1]);
			} else {
				coTagMap.put(tmp[0], tmp[1]);
			}

			if (coTagMap.containsKey(tmp[1])) {
				coTagMap.put(tmp[1], coTagMap.get(tmp[1]) + "#####" + tmp[0]);
			} else {
				coTagMap.put(tmp[1], tmp[0]);
			}
		}
	}

	public void calc() {

		List<String> coTags = new ArrayList<String>();
		int i = 1;
		for (String tag : tags) {
			int g1 = 0;
			int g2 = 0;
			int g = 0;
			coTags = this.getCoTags2(tag);
			g1 = coTags.size();
			int j = 0;
			for (String tmp : coTags) {
				if (j == g1){
					System.out.println("tmp");
				}
				String[] str = {tag,tmp};
				TagPair tp = new TagPair(str);
				try
				{
					int num = tagPairsOP.get(tp);
					g2 += num;
					j++;
				}
				catch(NullPointerException e)
				{
					System.out.print("cannot find");
				}
			}
			g = g2/2;
			query.save(tag, g);
			System.out.print(tag);
			System.out.print(g);
			i++;
		}
	}

	private List<String> getCoTags(String tag) {
		List<String> tags = new ArrayList<String>();
		String tmp;
		for (TagPair tp : tagPairs) {
			tmp = tp.getCoTag(tag);
			if (tmp != null) {
				tags.add(tmp);
			}
		}
		return tags;
	}

	private List<String> getCoTags2(String tag) {
		List<String> tags = new ArrayList<String>();
		String tmp;
		tmp = coTagMap.get(tag);
		String[] strArray = StringUtils.split(tmp, "#####");
		// if (strArray != null) {
		// Collections.addAll(tags, strArray);
		// }
		if (strArray != null) {
			for (String tmp2 : strArray) {
				if (tmp2 != null) {
					tags.add(tmp2);
				}
			}
		}
		return tags;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CalcTagGenerality ctg = new CalcTagGenerality();
		ctg.calc();
	}

}
