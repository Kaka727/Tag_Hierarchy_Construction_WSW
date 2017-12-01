package org.ossean.classification.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.ossean.classification.type.TagPair;

public class Query {
	private C3P0Utils c3p0Utils = null;

	/**
	 * 
	 */
	public Query() {
		this.c3p0Utils = C3P0Utils.getInstance();
	}
	
	public Connection getConnection(){
		return c3p0Utils.getConnection();
	}

	// 从重要标签表中获取标签对
	/*
	public List<TagPair> getTagPairs() {
		List<TagPair> tagPairs = new ArrayList<TagPair>();
		List<String> tags = new ArrayList<String>();
		Connection conn = c3p0Utils.getConnection();
		String sql = "select tag from important_tag";
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while (rs.next()) {
				tags.add(rs.getString("tag"));
			}
			rs.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String[] tmp;
		for (int i = 0; i < tags.size(); i++) {
			for (int j = i + 1; j < tags.size(); j++) {
				tmp = new String[2];
				tmp[0] = tags.get(i);
				tmp[1] = tags.get(j);
				tagPairs.add(new TagPair(tmp));
			}
		}
		return tagPairs;
	}*/

	/*
	public Set<TagPair> getTagPairsWithWeight() {
		Set<TagPair> tagPairs = new HashSet<TagPair>();
		Connection conn = c3p0Utils.getConnection();
		String sql = "select tag1, tag2, weight from co_tags";
		ResultSet rs = null;
		PreparedStatement pst = null;
		String[] tags = null;
		int weight = -1;
		try {
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while (rs.next()) {
				tags = new String[2];
				tags[0] = rs.getString("tag1");
				tags[1] = rs.getString("tag2");
				weight = rs.getInt("weight");
				tagPairs.add(new TagPair(tags, weight));
			}
			rs.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tagPairs;
	}*/

	/*
	public List<TagPair> getImportantTagPairs() {
		List<TagPair> tagPairs = new ArrayList<TagPair>();
		Connection conn = c3p0Utils.getConnection();
		String sql = "select tag1, tag2, weight from all_important_edge_2015_06_24_05_26";
		ResultSet rs = null;
		PreparedStatement pst = null;
		String[] tags = null;
		int weight = -1;
		try {
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while (rs.next()) {
				tags = new String[2];
				tags[0] = rs.getString("tag1");
				tags[1] = rs.getString("tag2");
				weight = rs.getInt("weight");
				tagPairs.add(new TagPair(tags, weight));
			}
			rs.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tagPairs;
	}*/

	/*public int getTagCoNum(String tag) {
		int num = -1;
		Connection conn = c3p0Utils.getConnection();
		String sql = "select num from all_tag_co_num_2015_06_24 where tag = ?";
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			pst.setString(1, tag);
			rs = pst.executeQuery();
			while (rs.next()) {
				num = rs.getInt("num");
			}
			rs.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return num;
	}*/
	
	public int getTagConum(String tag) {
		int num = -1;
		Connection conn = c3p0Utils.getConnection();
		String sql = "select generality from tag_generality where tag = ?";
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			pst.setString(1, tag);
			rs = pst.executeQuery();
			while (rs.next()) {
				num = rs.getInt("generality");
			}
			rs.close();
			pst.close();
			conn.close();
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return num;
	}

	
	public int getWeight(String t1, String t2)
	{
		int num = 0;
		Connection conn = c3p0Utils.getConnection();
		String sql = "select * from tag_correlation where tag1 = ? and tag2 = ?";
		ResultSet rs = null;
		PreparedStatement pst = null;
		try{
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			pst.setString(1, t1);
			pst.setString(2, t2);
			rs = pst.executeQuery();
			while(rs.next())
			{
				num = rs.getInt(3);
			}
			rs.close();
			pst.close();
			conn.close();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return num;
	}
	
	public Map<String, Integer> getWeightMap(String tag)
	{
		Map<String, Integer> map = new HashMap<String, Integer>();
		Connection conn = c3p0Utils.getConnection();
		String sql = "select * from tag_correlation where tag1 = ?";
		ResultSet rs = null;
		PreparedStatement pst = null;
		try{
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			pst.setString(1, tag);
			rs = pst.executeQuery();
			while(rs.next())
			{
				String t = rs.getString("tag2");
				int i = rs.getInt("weight");
				map.put(t, i);
			}
			rs.close();
			pst.close();
			conn.close();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	
	public Map<String, Integer> OpenhubSpecTagGenerality(){
		Map<String, Integer> map = new HashMap<String, Integer>();
		Connection conn = c3p0Utils.getConnection();
		String sql = "select * from tag_generality where generality > 1000 ";
		ResultSet rs = null;
		PreparedStatement pst = null;
		try{
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			//pst.setString(1, tag);
			rs = pst.executeQuery();
			while(rs.next())
			{
				String t = rs.getString("tag");
				int i = rs.getInt("generality");
				map.put(t, i);
			}
			rs.close();
			pst.close();
			conn.close();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	
	/*
	public List<String> getMostRelativeTags(String tag, int limit) {
		List<String> tags = new ArrayList<String>();
		Connection conn = c3p0Utils.getConnection();
		String sql = "SELECT * FROM `openhub_co_tags_2015_04_30` WHERE tag1 = ? OR tag2 = ? ORDER BY weight DESC LIMIT ?;";
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, tag);
			pst.setString(2, tag);
			pst.setInt(3, limit);
			rs = pst.executeQuery();
			String tag1;
			String tag2;
			while (rs.next()) {
				tag1 = rs.getString("tag1");
				tag2 = rs.getString("tag2");
				if (tag.equals(tag1)) {
					tags.add(tag2);
				} else {
					tags.add(tag1);
				}
			}
			rs.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tags;
	}*/

	/*public List<String> getTopTags(int limit) {
		List<String> tags = new ArrayList<String>();
		Connection conn = c3p0Utils.getConnection();
		String sql = "SELECT * FROM `openhub_tag_co_num_without_weight_2015_04_30` ORDER BY num DESC LIMIT ?;";
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(sql);
			pst.setInt(1, limit);
			rs = pst.executeQuery();
			while (rs.next()) {
				tags.add(rs.getString("tag"));
			}
			rs.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tags;
	}*/

	// 从标签同现表中获取所有标签对
	/*public List<TagPair> getTagPairs2() {
		List<TagPair> tagPairs = new ArrayList<TagPair>();
		Connection conn = c3p0Utils.getConnection();
		String sql = "select * from openhub_co_tags_2015_05_13";
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			
			int weight = 0;
			TagPair tp = null;
			while (rs.next()) {
				String[] tmp = new String[2];
				tmp[0] = rs.getString("tag1");
				tmp[1] = rs.getString("tag2");
				weight = rs.getInt("weight");
				tp = new TagPair(tmp, weight);
				tagPairs.add(tp);
			}
			rs.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tagPairs;
	}*/
	
	//计算特定标签的层次结构时使用
	public List<String> getSpecTags(String tag){
		List<String> tags = new ArrayList<String>();
		Connection conn = c3p0Utils.getConnection();
		//String sql = "select * from tag_correlation where tag1=" + tag + "and weight>10";
		StringBuilder sb = new StringBuilder();
		sb.append("select * from tag_correlation where tag1 = '");
		sb.append(tag);
		sb.append("' and weight > 200");
		String sql = sb.toString();
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while(rs.next())
			{
				String tmp = rs.getString("tag2");
				tags.add(tmp);
			}
			rs.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tags;
	}

	// 从标签同现表中获取所有标签对
	public List<String> getAllTags() {
		List<String> tags = new ArrayList<String>();
		Connection conn = c3p0Utils.getConnection();
		//String sql = "select * from openhub_tag_co_num_2015_05_13";
		String sql = "select * from tag_generality where generality > 1000";
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while (rs.next()) {
				tags.add(rs.getString("tag"));
			}
			rs.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tags;
	}
	
	/*public Map<String, Integer> getTagNums() {
		Map<String , Integer> tagNums= new HashMap<String, Integer>();
		Connection conn = c3p0Utils.getConnection();
		//String sql = "select * from openhub_tag_co_num_2015_05_13";
		String sql = "select * from tags";
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while (rs.next()) {
				tagNums.put(rs.getString("TagName"), rs.getInt("Count"));
			}
			rs.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tagNums;
	}*/
	
	public List<String> getimportantPoint(){
		List<String> point = new ArrayList<String>();
		Connection conn = c3p0Utils.getConnection();
		String sql = "select * from tag_generality where generality > 6000";
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while (rs.next()) {
				point.add(rs.getString("tag"));	
				System.out.println(rs.getString("tag"));
			}
			System.out.println("endinginginginging");
			rs.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return point;
	}
	
	public Boolean is_cooccurrence(String t1, String t2){
		Connection conn = c3p0Utils.getConnection();
		String sql = "select * from tag_correlation where tag1 = ? and tag2 = ? ";
		ResultSet rs = null;
		PreparedStatement pst = null;
		Boolean i = false;
		try {
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			pst.setString(1, t1);
			pst.setString(2, t2);
			rs = pst.executeQuery();
			while (rs.next()) {
					if(rs.getInt("weight") > 100)
					i = true;
					break;
			}
			rs.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
	}
	
	
	
	public Map<String, Integer> getgenerality(List<String> t){
		//List<String> point = new ArrayList<String>();
		Connection conn = c3p0Utils.getConnection();
		String sql = "select * from tag_generality";
		ResultSet rs = null;
		PreparedStatement pst = null;
		HashMap<String, Integer> a = new HashMap<String, Integer>();
		try {
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while (rs.next()) {
				if(t.contains(rs.getString("tag")))
				{
					a.put(rs.getString("tag"), rs.getInt("generality"));
					System.out.println(rs.getString("tag"));
				}
			}
			System.out.println("endedededededd");
			rs.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return a;
	}
	
	public Integer getcooccurrence(String t1, String t2){
		Connection conn = c3p0Utils.getConnection();
		String sql = "select * from tag_correlation where tag1 = ? and tag2 = ? ";
		ResultSet rs = null;
		PreparedStatement pst = null;
		int i = 0;
		try {
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			pst.setString(1, t1);
			pst.setString(2, t2);
			rs = pst.executeQuery();
			while (rs.next()) {
					i = rs.getInt("weight");
			}
			rs.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
	}
	
	
	public Map<TagPair, Integer> getTagPairsOP(){
		Map<TagPair, Integer> tagPairs = new HashMap<TagPair, Integer>();
		Connection conn = c3p0Utils.getConnection();
		String sql = "select * from openhub_datas";
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> a = new ArrayList<String>();
				String str = rs.getString("Tags");
				while(true)
				{
					try
					{
						int head = str.indexOf("<");
						if(head == -1)
							break;
						int tail = str.indexOf(">");
						if(tail == -1)
							break;
						String res = str.substring(head+1, tail);
						a.add(res);
						str = str.substring(tail + 1);
					}
					catch(NullPointerException e)
					{
						System.out.print(rs.getInt("Id"));
					}
				}
				int l = a.size();
				if(l >= 2)
				{
					for(int s1=0; s1<l; s1++)
					{
						for(int s2=s1+1; s2<l; s2++)
						{
							String[] tags1 = {a.get(s1), a.get(s2)};
							TagPair hh1 = new TagPair(tags1);	
							if(tagPairs.get(hh1) instanceof Integer)
							{
								int ans1 = tagPairs.get(hh1);
								ans1 += 1;
								tagPairs.put(hh1,ans1);
							}
							else
							{
								tagPairs.put(hh1, 1);
							}
							System.out.println(hh1.toString());
							String[] tags2 = {a.get(s2), a.get(s1)};
							TagPair hh2 = new TagPair(tags2);
							if(tagPairs.get(hh2) instanceof Integer)
							{
								int ans2 = tagPairs.get(hh2);
								ans2 += 1;
								tagPairs.put(hh2,ans2);
							}
							else
							{
								tagPairs.put(hh2,1);
							}
							System.out.println(hh2.toString());
						}
					}
				}
			}
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tagPairs;
	}
	
	public Map<TagPair, Integer> getTagPairsSOF(){
		Map<TagPair, Integer> tagPairs = new HashMap<TagPair, Integer>();
		Connection conn = c3p0Utils.getConnection();
		String sql = "select * from sof_datas";
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> a = new ArrayList<String>();
				String str = rs.getString("Tags");
				while(true)
				{
					try
					{
						int head = str.indexOf("<");
						if(head == -1)
							break;
						int tail = str.indexOf(">");
						if(tail == -1)
							break;
						String res = str.substring(head+1, tail);
						a.add(res);
						str = str.substring(tail + 1);
					}
					catch(NullPointerException e)
					{
						System.out.print(rs.getInt("Id"));
					}
				}
				int l = a.size();
				if(l >= 2)
				{
					for(int s1=0; s1<l; s1++)
					{
						for(int s2=s1+1; s2<l; s2++)
						{
							String[] tags1 = {a.get(s1), a.get(s2)};
							TagPair hh1 = new TagPair(tags1);	
							if(tagPairs.get(hh1) instanceof Integer)
							{
								int ans1 = tagPairs.get(hh1);
								ans1 += 1;
								tagPairs.put(hh1,ans1);
							}
							else
							{
								tagPairs.put(hh1, 1);
							}
							System.out.println(hh1.toString());
							String[] tags2 = {a.get(s2), a.get(s1)};
							TagPair hh2 = new TagPair(tags2);
							if(tagPairs.get(hh2) instanceof Integer)
							{
								int ans2 = tagPairs.get(hh2);
								ans2 += 1;
								tagPairs.put(hh2,ans2);
							}
							else
							{
								tagPairs.put(hh2,1);
							}
							System.out.println(hh2.toString());
						}
					}
				}
			}
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tagPairs;
	}
	
	public void save(String tag, int g){
		Connection conn = c3p0Utils.getConnection();
		String sql = "insert into tag_generality (tag, generality) VALUES (?, ?)";
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, tag);
			pst.setInt(2, g);
			pst.execute();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void save_occurrence(String tag, int g){
		Connection conn = c3p0Utils.getConnection();
		String sql = "insert into tag_occurrence (tag, occurrence) VALUES (?, ?)";
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, tag);
			pst.setInt(2, g);
			pst.execute();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void save2(String s1, String s2, int g){
		Connection conn = c3p0Utils.getConnection();
		String sql = "insert into tag_correlation (tag1, tag2, weight) VALUES (?, ?, ?)";
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, s1);
			pst.setString(2, s2);
			pst.setInt(3, g);
			pst.execute();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<Integer> mysql(int i){
		Connection conn = c3p0Utils.getConnection();
		StringBuilder sb = new StringBuilder();
		sb.append("select * from relative_memo_to_open_source_projects_w where osp_id=779758 and Year(created_time)=");
		sb.append(i);
		String sql = sb.toString();
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<Integer> res = new ArrayList<Integer>();
		try {
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while (rs.next()) {
				int tmp = rs.getInt("view_num_crawled");
				res.add(tmp);
			}
			rs.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public List<Integer> tensor(int i1, int i2){
		Connection conn = c3p0Utils.getConnection();
		/*StringBuilder sb = new StringBuilder();
		sb.append("select * from relative_memo_to_open_source_projects_w where osp_id=771145 and Year(created_time)=");
		sb.append(i1);
		sb.append("and Month(created_time)=");
		sb.append(i2);*/
		StringBuilder sb1 = new StringBuilder();
		sb1.append(i1);
		sb1.append("-");
		sb1.append(i2);
		sb1.append("-");
		sb1.append(1);
		StringBuilder sb2 = new StringBuilder();
		sb2.append(i1);
		sb2.append("-");
		sb2.append(i2);
		sb2.append("-");
		sb2.append(31);
		String sql = "select * from relative_memo_to_open_source_projects_w where osp_id=942024 and created_time between " + "'" + sb1 +"'" + " and " + "'" + sb2 + "'";
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<Integer> res = new ArrayList<Integer>();
		try {
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while (rs.next()) {
				int tmp = rs.getInt("view_num_crawled");
				res.add(tmp);
			}
			rs.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	/*
	public Map<Integer, String> getOpenhubTags(){
		Map<Integer , String> openhubTags= new LinkedHashMap<Integer, String>();
		Connection conn = c3p0Utils.getConnection();
		String sql = "select * from database_related_hand_origin";
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while (rs.next()) {
				openhubTags.put(rs.getInt("id"), rs.getString("tags"));
			}
			rs.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return openhubTags;
	}*/
	
	public int tag_occurrence (String t){
		Connection conn = c3p0Utils.getConnection();
		String sql = "select * from openhub_datas";
		int i = 0;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while (rs.next()) {
				String str = rs.getString("tags");
				if(str.contains(t))
				{
					i++;
				}
			}
			rs.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i;
	}
	
}
