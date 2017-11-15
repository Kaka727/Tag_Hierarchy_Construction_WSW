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

public class CompareSourceForge {
	public static double similarThreashhold = 0.6;

	public Query query = new Query();
	public List<TagPair> sourceTagPairs = new ArrayList<TagPair>();
	public List<String> rs = new ArrayList<String>();
	public List<String> sourceForgeTaxonomy = new ArrayList<String>();
	public Set<String> sourceTagSet = new HashSet<String>();

	public Set<String> tagSetForStatistics = new HashSet<String>();

	public LuceneIndex li = new LuceneIndex();
	public LuceneSearcher ls;

	public PorterStemmer ps = new PorterStemmer(); // 词干提取

	public FileWriter fw;

	public CompareSourceForge() throws IOException {
		File sfFile = new File("e:/sf_topics.txt");
		File tagFile = new File("e:/raw_rs.txt");

		Scanner sfSc = new Scanner(sfFile);
		Scanner tagSc = new Scanner(tagFile);
		while (sfSc.hasNextLine()) {
			sourceForgeTaxonomy.add(sfSc.nextLine().replace("-", " ")
					.replace("_", " ").replace(";", ""));
		}

		Analyzer ikanalyzer = new IKAnalyzer(true);
		// li.createIndex(sourceForgeTaxonomy, ikanalyzer);
		ls = new LuceneSearcher();

		String tmp;
		TagPair tp;
		while (tagSc.hasNextLine()) {
			tmp = tagSc.nextLine();
			String[] strArr = StringUtils.splitByWholeSeparator(tmp, "->");
			sourceTagSet.add(strArr[0]);
			sourceTagSet.add(strArr[1]);
			tp = new TagPair(strArr);
			tp.setDirection(1);
			sourceTagPairs.add(tp);
		}
		sfSc.close();
		tagSc.close();
		try {
			fw = new FileWriter("e:/out" + new Date().getTime() + ".txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void compare() {
		int i = 0;
		Set<String> rs = new HashSet<String>();
		for (TagPair tp : sourceTagPairs) {
			// String[] tags = tp.getTags();
			// tags预处理
			List<String> rt = findByLucene(tp);

			// fw.write(tp.toString() + "       " + direction + "\n");
			if (rt != null) {
				for (String tmp : rt) {
					rs.add(tmp);
				}
			}

			System.out.println(i);
			i++;
		}

		for (String tmp : rs) {
			try {
				fw.write(tmp + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(tagSetForStatistics.size());
	}

	public List<String> findByLucene(TagPair tp) { // 1，正向；-1反向；0：两个都出现了，但是没有之间关系；
		List<String> rt = new ArrayList<String>();
		// 2至少有一个未出现
		String[] tags = tp.getTags();
		List<String> tag0s = new ArrayList<String>();
		List<String> tag1s = new ArrayList<String>();
		for (String tag : StringUtils.splitByWholeSeparator(tags[0], "_")) {
			if ("web".equals(tag)) {
				tag = "WWW";
			}
			tag0s.add(tag.toLowerCase());
		}

		for (String tag : StringUtils.splitByWholeSeparator(tags[1], "_")) {
			if ("web".equals(tag)) {
				tag = "WWW";
			}
			tag1s.add(tag.toLowerCase());
		}
		List<String> comb = new ArrayList<String>();
		comb.addAll(tag0s);
		comb.addAll(tag1s);

		List<Integer> searchRs1 = ls.search(tag0s);
		List<Integer> searchRs2 = ls.search(tag1s);

		// 至少有一个标签没在sourceforge中出现
		if (searchRs1.isEmpty() || searchRs2.isEmpty()) {
			// rt.add(tp.toString()); // high tolerance
			// tagSetForStatistics.add(tags[0]);
			// tagSetForStatistics.add(tags[1]);
			// return rt;

			return null; // low tolerance
		}

		// 两个标签在sourceforge中没有关联的边
		if (!haveIntersection(searchRs1, searchRs2)) {
			return null;
		}

		for (Integer i : ls.search(comb)) {
			// List<String> rt = new ArrayList<String>();
			String line = sourceForgeTaxonomy.get(i);
			String[] relations = StringUtils
					.splitByWholeSeparator(line, " :: ");
			int loc1 = findLocation(tag0s, relations);
			int loc2 = findLocation(tag1s, relations);

			int min = (loc1 < loc2 ? loc1 : loc2);
			int max = (loc1 > loc2 ? loc1 : loc2);
			for (int j = min; j < max; j++) {
				String similarTag = similarTag(relations[j]);
				if (similarTag == null) {
					continue;
				}
				for (int k = j + 1; k <= max; k++) {
					String tmp = similarTag(relations[k]);
					if (tmp == null) {
						continue;
					} else {
						rt.add(similarTag + "->" + tmp);
						tagSetForStatistics.add(similarTag);
						tagSetForStatistics.add(tmp);
						break;
					}
				}
			}
			return rt;
		}
		return null;
	}

	private boolean haveIntersection(List<Integer> list1, List<Integer> list2) {
		Set<Integer> set1 = new HashSet<Integer>(list1);
		Set<Integer> set2 = new HashSet<Integer>(list2);
		for (Integer i : set1) {
			if (set2.contains(i)) {
				return true;
			}
		}
		return false;
	}

	private String similarTag(String tag) {
		for (String sourceTag : sourceTagSet) {
			if (isSimilar(tag, sourceTag)) {
				return sourceTag;
			}
		}
		return null;
	}

	private boolean isSimilar(String str1, String str2) {
		str1 = str1.toLowerCase();
		str2 = str2.toLowerCase();
		// Set<String> str1Set = new HashSet<String>();
		Set<String> str2Set = new HashSet<String>();
		for (char c : str2.toCharArray()) {
			str2Set.add(c + "");
		}

		String[] str1Arr = StringUtils.splitByWholeSeparator(str1, "/");

		for (String tmp : str1Arr) {
			if ("www".equals(tmp)) {
				tmp = "web";
			}

			// if(tmp.equals(str2)){
			// return true;
			// }

			String[] innerArr1 = StringUtils.splitByWholeSeparator(tmp, " ");
			String[] innerArr2 = StringUtils.splitByWholeSeparator(str2, "_");
			if (innerArr1.length <= innerArr2.length) {
				for (int i = 0; i < innerArr1.length; i++) {
					if (!innerArr1[i].equals(innerArr2[i])) {
						return false;
					}
				}
				return true;
			}

			// Set<String> tmpSet = new HashSet<String>();
			//
			// for (char c : tmp.toLowerCase().toCharArray()) {
			// tmpSet.add(c + "");
			// }
			//
			// int rt = 0;
			// Integer intersectNum = 0;
			// for (String c : tmpSet) {
			// if (str2Set.contains(c)) {
			// intersectNum++;
			// }
			// }
			//
			// if ((intersectNum.floatValue() * 2)
			// / (str1.length() + str2.length()) >
			// CompareSourceForge.similarThreashhold) {
			// String lower1 = tmp.toLowerCase();
			// String lower2 = str2.toLowerCase();
			// if (lower1.contains(lower2) || lower2.contains(lower1)) {
			// System.out.println("simlar string:" + str1 + "    " + str2);
			// return true;
			// }
			// }

		}
		return false;
	}

	// 寻找标签对在sourceforge中的关系
	public int find(String[] tags) { // 1，正向；-1反向；0：未出现
		List<String> tag0s = new ArrayList<String>();
		List<String> tag1s = new ArrayList<String>();
		for (String tag : StringUtils.splitByWholeSeparator(tags[0], "_")) {
			if ("web".equals(tag)) {
				tag = "WWW";
			}
			tag0s.add(ps.stem(tag.toLowerCase()));
		}

		for (String tag : StringUtils.splitByWholeSeparator(tags[1], "_")) {
			if ("web".equals(tag)) {
				tag = "WWW";
			}
			tag1s.add(ps.stem(tag.toLowerCase()));
		}

		for (String line : sourceForgeTaxonomy) {
			if (contains(tag0s, line) && contains(tag1s, line)) {
				String[] relations = StringUtils.splitByWholeSeparator(line,
						" :: ");
				int loc1 = findLocation(tag0s, relations);
				int loc2 = findLocation(tag1s, relations);
				if (loc1 > loc2) {
					return -1;
				} else {
					return 1;
				}
			}
		}
		return 0;
	}

	public boolean contains(List<String> terms, String line) {
		for (String tmp : terms) {
			if (StringUtils.containsIgnoreCase(line, tmp)) {
				return true;
			}
		}
		return false;
	}

	public int findLocation(List<String> terms, String[] relations) { // -1未出现
		for (int i = 0; i < relations.length; i++) {
			for (String term : terms) {
				if (StringUtils.containsIgnoreCase(relations[i], term)) {
					return i;
				}
			}
		}
		return -1;
	}

	public static void main(String[] args) throws IOException {
		CompareSourceForge csf = new CompareSourceForge();
		csf.compare();
		// List<String> words = new ArrayList<String>();
		// words.add("libraries");
		// words.add("php");
		// words.add("classes");
		// //words.add("libraries");
		// //words.add("ends");
		// System.out.println(csf.ls.search(words).isEmpty());
		// //csf.ls.search(words);

	}
}
