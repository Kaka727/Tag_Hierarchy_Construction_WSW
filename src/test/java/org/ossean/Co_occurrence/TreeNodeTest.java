package org.ossean.Co_occurrence;

import net.sf.json.JSONObject;

import org.ossean.classification.type.TreeNode;

public class TreeNodeTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TreeNode root = new TreeNode("root1");
		TreeNode child1 = new TreeNode("child1");
		TreeNode child2 = new TreeNode("child2");
		root.getChildren().add(child1);
		root.getChildren().add(child2);
		
		JSONObject json = JSONObject.fromObject(root);
		System.out.println(json);
	}

}
