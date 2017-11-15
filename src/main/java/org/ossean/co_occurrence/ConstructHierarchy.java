package org.ossean.co_occurrence;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JTree;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.ossean.classification.type.TagPair;
import org.ossean.classification.type.TreeNode;

public class ConstructHierarchy {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		File f = new File("e:/fortest/total_optimized.txt");
		Scanner sc = new Scanner(f);
		TreeNode root = new TreeNode("root");
		
		List<TagPair> tps = new ArrayList<TagPair>();
		while(sc.hasNextLine()){
			tps.add(constructTP(sc.nextLine()));
		}
		
		constructTree(root, tps);
		
		
		JSONObject json = JSONObject.fromObject(root);
		System.out.println(json);
		
	}
	
	private static void constructTree(TreeNode node, List<TagPair> tps){
		List<String> strs = new ArrayList<String>();
		for(TagPair tp:tps){
			if(node.getName().equals(tp.getTags()[0])){
				TreeNode tn = new TreeNode(tp.getTags()[1]);
				node.getChildren().add(tn);
				constructTree(tn, tps);
			}
		}
	}
	
	private static TagPair constructTP(String in){
		String[] rs = StringUtils.split(in, "->");
		return new TagPair(rs);
	}

}
