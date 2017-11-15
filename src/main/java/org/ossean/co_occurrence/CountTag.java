package org.ossean.co_occurrence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.plaf.SliderUI;

import org.ossean.classification.util.Filter;

public class CountTag {
	public static Filter filter = new Filter();

	public static Set<String> parse(String tagStr) {
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

	public static void byFile(String path) {

		try {
			File file = new File(path);
			Scanner s = new Scanner(new FileInputStream(file));
			while (s.hasNextLine()) {
				int id = s.nextInt();
				String tagStr = s.next("\\S+");
				// Map<String,Integer> tags = parse(tagStr);
				// System.out.println(id + ":" + tags);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void bySQL() throws InterruptedException {
		try {
			int batchSize = 10000;// ÿ�δ���1000�����
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager
					.getConnection("jdbc:mysql://localhost/classification?user=root&password=1234");
			conn.setAutoCommit(false);
			PreparedStatement stat;
			ResultSet rs;
			stat = conn
					.prepareStatement("select COUNT(*) AS total from osp_tags");
			rs = stat.executeQuery();
			int total = 0;
			if (rs.next())
				total = rs.getInt("total");
			else
				System.out.println("fail to get total ");
			Map<String, Integer> pairs = new TreeMap<String, Integer>();

			Map<String, Integer> tagCoCount = new HashMap<String, Integer>();

			Map<String, Integer> tagCoCountWithoutWeight = new HashMap<String, Integer>();
			for (int i = 0; i < total; i += batchSize) {
				stat = conn
						.prepareStatement("select id,tags from osp_tags where id>? AND id<=?");
				stat.setInt(1, i);
				stat.setInt(2, i + batchSize);

				rs = stat.executeQuery();
				System.out.println("���ڴ����   " + (i + 1) + " �е���  "
						+ (i + batchSize + 1) + " �м�¼");
				int z = 0;
				while (rs.next()) {
					System.out.println(rs.getInt("id"));
					Set<String> tags = parse(rs.getString("tags"));// ��һ�еı�ǩ
					List<String> tagsList = new ArrayList(tags);
					for (int j = 0; j < tagsList.size(); j++) {
						for (int k = j + 1; k < tagsList.size(); k++) {
							String pair = tagsList.get(j) + "!!!!!"
									+ tagsList.get(k);
							if (!pairs.containsKey(pair)) {
								pairs.put(pair, 1);
							} else {
								pairs.put(pair, pairs.get(pair) + 1);
							}
						}

						if (!tagCoCount.containsKey(tagsList.get(j))) {
							tagCoCount
									.put(tagsList.get(j), tagsList.size() - 1);
						} else {
							tagCoCount.put(
									tagsList.get(j),
									tagCoCount.get(tagsList.get(j))
											+ tagsList.size() - 1);
						}
					}
					System.out.println(z++);
				}
				System.out.println("�Ѿ���Բ��� " + pairs.size() + " �����");
				System.out
						.println("----------------------------------------------------------------------");
			}
			List<Map.Entry<String, Integer>> mappingList = new ArrayList<Map.Entry<String, Integer>>(
					pairs.entrySet());
			//
			Collections.sort(mappingList,
					new Comparator<Map.Entry<String, Integer>>() {
						public int compare(Map.Entry<String, Integer> mapping1,
								Map.Entry<String, Integer> mapping2) {
							return mapping2.getValue() - mapping1.getValue();
						}
					});

			// conn.close();
			String[] tags = new String[2];
			String sql = "insert into all_co_tags_2015_06_24 (tag1, tag2, weight) VALUES (?, ?, ?)";
			PreparedStatement pst = conn.prepareStatement(sql);
			for (Map.Entry<String, Integer> item : mappingList) {
				tags = item.getKey().split("!!!!!");
				if (tags.length == 2) {
					pst.setString(1, tags[0]);
					pst.setString(2, tags[1]);
					pst.setInt(3, item.getValue());
					pst.addBatch();
					System.out.println("add " + tags[0] + tags[1]);
				} else {
					System.out.println("split error");
					Thread.sleep(1000);
				}

				// 包含tag1
				if (!tagCoCountWithoutWeight.containsKey(tags[0])) {
					tagCoCountWithoutWeight.put(tags[0], 1);
				} else {
					tagCoCountWithoutWeight.put(tags[0],
							tagCoCountWithoutWeight.get(tags[0]) + 1);
				}

				// 包含tag2
				if (!tagCoCountWithoutWeight.containsKey(tags[1])) {
					tagCoCountWithoutWeight.put(tags[1], 1);
				} else {
					tagCoCountWithoutWeight.put(tags[1],
							tagCoCountWithoutWeight.get(tags[1]) + 1);
				}
			}
			pst.executeBatch();
			conn.commit();
			pst.close();

			System.out.println("tagCoCount: " + tagCoCount.size());
			System.out.println("tagCoCountWithoutWeight: "
					+ tagCoCountWithoutWeight.size());

			String tagCoCountSql = "insert into all_tag_co_num_2015_06_24 (tag, num) VALUES (?, ?)";
			PreparedStatement tagCoCountPst = conn
					.prepareStatement(tagCoCountSql);
			for (Entry<String, Integer> entry : tagCoCount.entrySet()) {
				tagCoCountPst.setString(1, entry.getKey());
				tagCoCountPst.setInt(2, entry.getValue());
				tagCoCountPst.addBatch();
			}
			tagCoCountPst.executeBatch();
			tagCoCountPst.close();
			conn.commit();

			String tagCoCountWithoutWeightSql = "insert into all_tag_co_num_without_weight_2015_06_24 (tag, num) VALUES (?, ?)";
			PreparedStatement tagCoCountWithoutWeightPst = conn
					.prepareStatement(tagCoCountWithoutWeightSql);
			for (Entry<String, Integer> entry : tagCoCountWithoutWeight
					.entrySet()) {
				tagCoCountWithoutWeightPst.setString(1, entry.getKey());
				tagCoCountWithoutWeightPst.setInt(2, entry.getValue());
				tagCoCountWithoutWeightPst.addBatch();
			}
			tagCoCountWithoutWeightPst.executeBatch();
			tagCoCountWithoutWeightPst.close();
			conn.commit();
			conn.close();

			// File f = new File("result");
			// FileWriter fw = new FileWriter(f);
			// int count = 1;
			// for (Map.Entry<String, Integer> item : mappingList) {
			// fw.write(item.getKey() + "$$$$$" + (count++) + "@@@@@"
			// + item.getValue() + "\n");
			// }
			// fw.close();
			// System.out.println("д�����");

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String args[]) throws ClassNotFoundException,
			SQLException {
		// Map<String,Integer> tags = parse("<tag1><tag2><tag4>");
		// System.out.println(tags);
		try {
			bySQL();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Class.forName("com.mysql.jdbc.Driver");
		// Connection conn = DriverManager
		// .getConnection("jdbc:mysql://localhost/match?user=star&password=1234567890");
		// PreparedStatement stat;
		// ResultSet rs;
		// stat = conn
		// .prepareStatement("update pointers set position=3 where pointer_type='project_history'");
		// boolean i = stat.execute();
	}
}
