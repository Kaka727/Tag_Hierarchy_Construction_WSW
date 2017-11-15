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

public class hotspot_by_year {
	public FileWriter fw;
	private Query query = new Query();
	
	public hotspot_by_year() throws IOException{
		try {
			fw = new FileWriter("e:/hotspot" + new Date().getTime() + ".txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void cal() throws IOException
	{
		for(int i = 2005; i < 2018; i++)
		{
			List<Integer> mysql = new ArrayList<Integer>();
			mysql = query.mysql(i);
			int score = 0;
			for(int t = 0; t < mysql.size(); t++)
			{
				score += mysql.get(t);
			}
			score = score/(2018-i);
			fw.write(i + ":" + score + ";" + "\n");
		}
		fw.close();
	}
	
	public static void main(String[] args) throws IOException {
		hotspot_by_year csf = new hotspot_by_year();
		csf.cal();
	}

}
