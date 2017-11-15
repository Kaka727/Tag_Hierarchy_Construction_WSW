package org.ossean.classification.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

public class PreprocessTag {
	public static Filter filter = new Filter("stop_words_for_database");

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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Connection conn = new Query().getConnection();
		String sql = "select * from database_related_hand_origin";
		String updateSql = "update database_related_hand_origin set tags=? where id =?";
		ResultSet rs = null;
		PreparedStatement pst = null;
		PreparedStatement updatePst = null;
		try {
			pst = conn.prepareStatement(sql);
			updatePst = conn.prepareStatement(updateSql);
			rs = pst.executeQuery();
			while (rs.next()) {
				String tags = rs.getString("tags");
				int id = rs.getInt("id");
				//tags=tags.substring(0, tags.length()-1);
				Set<String> tagSet = PreprocessTag.parse(tags);
				String combTag = "";
				for (String tag : tagSet) {
					combTag += "<" + tag + ">,";
				}
				combTag=combTag.substring(0, combTag.length()-1);
				updatePst.setString(1, combTag);
				updatePst.setInt(2, id);
				updatePst.addBatch();
			}
			updatePst.executeBatch();
			rs.close();
			pst.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
