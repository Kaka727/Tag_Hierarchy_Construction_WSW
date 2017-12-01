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

public class CalcTagOccurrence {
	public Query query = new Query();
	
	public void calc()
	{
		List<String> tags = new ArrayList<String>();
		tags = query.getAllTags();
		for (String tag : tags)
		{
			int x = query.tag_occurrence(tag);
			query.save_occurrence(tag, x);
		}
	}
	
	public static void main(String[] args )
	{
		CalcTagOccurrence ctg = new CalcTagOccurrence();
		ctg.calc();
	}

}
