package org.ossean.Co_occurrence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.ossean.classification.type.TagPair;

public class StringTest {
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
		String tmp ="topic :: software development :: libraries";
		//StringUtils.spli
		for(String str :StringUtils.splitByWholeSeparator(tmp, " :: ")){
			System.out.println(str);
		}
	}

}
