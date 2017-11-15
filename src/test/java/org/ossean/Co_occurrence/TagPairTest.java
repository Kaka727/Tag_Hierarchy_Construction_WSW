package org.ossean.Co_occurrence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.ossean.classification.type.TagPair;

public class TagPairTest {
	public static Random random = new Random();
	public static int length = 5;

	public static String getRandomString() { // length表示生成字符串的长度
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int max = 100000;
		int times = 10000;
		String[] arr1 = new String[] { "tag1", "tag2" };
		String[] arr2 = new String[] { "tag2", "tag1" };
		String[] arr3 = new String[] { "tag1", "tag3" };
		TagPair tp1 = new TagPair(arr1);
		Random r = new Random();

		Map<TagPair, Integer> map = new HashMap<TagPair, Integer>();
		List<TagPair> list = new ArrayList<TagPair>();
		for (int i = 0; i < max; i++) {
			String[] tmp = new String[] { getRandomString(), getRandomString() };
			TagPair tp = new TagPair(tmp);
			map.put(tp, 10);
			list.add(tp);
			System.out.println("create: " + i);
		}

		for (int i = 0; i < times; i++) {
			int rand = r.nextInt(max);
			System.out.println(map.containsKey(list.get(rand)));
			System.out.println(i);
		}

		// TagPair tp1 = new TagPair(arr1);
		// TagPair tp2 = new TagPair(arr2);
		// TagPair tp3 = new TagPair(arr3);
		// // tp1.setDirection(0);
		// // tp2.setDirection(1);
		// // System.out.println(tp1.equals(tp2));
		// // System.out.println(tp2.equals(tp1));
		// // System.out.println(tp1.equals(tp3));
		// Set<TagPair> set = new LinkedHashSet<TagPair>();
		// set.add(tp1);
		// set.add(tp2);
		// set.add(tp3);
		// for (TagPair tp : set) {
		// System.out.println(tp.toString());
		// }
		// String[] arr4 = new String[] { "tag1", "tag2" };
		// set.remove(new TagPair(arr4));
		// System.out.println("after rm");
		// for (TagPair tp : set) {
		// System.out.println(tp.toString());
		// }
	}

}
