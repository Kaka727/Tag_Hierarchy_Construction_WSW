package org.ossean.classification.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class CalcTagNum {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		File f = new File("F:/compare_sourceforge/rs1000_high_tolerance_07_11.dot");
		Scanner sc = new Scanner(f);
		Set<String> tagSet = new HashSet<String>();
		for (int i = 1; sc.hasNextLine(); i++) {
			String tmp = sc.nextLine();
			if(tmp.equals("digraph g{")|| tmp.equals("}")){
				continue;
			}
			String[] rs = StringUtils.split(tmp, "->");
			tagSet.add(rs[0]);
			tagSet.add(rs[1]);
			System.out.println(i + " : " + tagSet.size());
		}

	}

}
