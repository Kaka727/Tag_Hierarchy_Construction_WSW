package org.ossean.classification.util;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Filter {
	private Set<String> stopWords = new HashSet<String>();

	/**
	 * 
	 */
	public Filter() {
	}

	/**
	 * 
	 */
	public Filter(String dic) {
		this.init(dic);
	}

	private void init(String dic) {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(dic);
		Scanner sc = new Scanner(in);
		while (sc.hasNext()) {
			stopWords.add(sc.nextLine());
		}
	}

	public boolean contains(String word) {
		return stopWords.contains(word);
	}

	public String toString() {
		String tmp = "";
		for (String word : stopWords) {
			tmp += word;
		}
		return tmp;
	}

	public static void main(String args[]) {
		Filter ft = new Filter("stop_words");
		System.out.println(ft.contains("php"));
	}

}
