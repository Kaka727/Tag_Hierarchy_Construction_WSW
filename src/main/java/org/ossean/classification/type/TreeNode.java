package org.ossean.classification.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.ossean.classification.util.EntityUtils;
import org.ossean.classification.util.Jsonable;

public class TreeNode implements Jsonable {
	private String name;
	private List<TreeNode> children = new ArrayList<TreeNode>();

	public TreeNode(String name) {
		super();
		this.name = name;
	}

	public Map<String, Object> toJsonMap() {
		Map<String, Object> rt = new HashMap<String, Object>();
		rt.put("'name': ", "'" + name + "'");

		if (children.size() != 0) {
			List<Map<String, Object>> m = new ArrayList<Map<String, Object>>();
			for (TreeNode tn : children) {
				m.add(tn.toJsonMap());
			}
			
			rt.put("'children'", m);
		}
		
		return rt;

//		String[] attrs = { "name", "children" };
//		return EntityUtils.toJsonMap(this, attrs);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the children
	 */
	public List<TreeNode> getChildren() {
		return children;
	}

	/**
	 * @param children
	 *            the children to set
	 */
	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}

	public JSONObject toSimpleJson() {
		// TODO Auto-generated method stub
		return null;
	}

	public JSONObject toSimpleJson(String[] attrs) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> toJsonMap(String[] attrs) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> toSimpleJsonMap() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> toFiniteJsonMao(Set<Object> serialized) {
		// TODO Auto-generated method stub
		return null;
	}

}
