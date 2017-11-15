package org.ossean.classification.type;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class TagPair {
	private String tags[] = null;
	private int weight = -1;
	private int direction = -1;

	public TagPair(String[] tags) {
		if (tags.length == 2) {
			this.tags = tags;
		} else {
			System.out.println("error");
		}
	}

	public TagPair(String[] tags, int weight) {
		this.tags = tags;
		this.weight = weight;
	}

	/**
	 * @return the tags
	 */
	public String[] getTags() {
		return tags;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(String[] tags) {
		this.tags = tags;
	}

	/**
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}

	/**
	 * @param weight
	 *            the weight to set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}

	/**
	 * @return the direction
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * @param direction
	 *            the direction to set
	 */
	
	public void setDirection(int direction) {
		this.direction = direction;
	}

	public String getCoTag(String tag) {
		if (tag.equals(tags[0])) {
			return tags[1];
		} else if (tag.equals(tags[1])) {
			return tags[0];
		} else {
			return null;
		}
	}

	@Override
	public boolean equals(Object obj) {
		TagPair tp = (TagPair) obj;
		String[] tpTags = tp.getTags();
		if (this.direction == tp.direction) {
			if ((tags[0].equals(tpTags[0]) && tags[1].equals(tpTags[1]))
					|| ((tags[0].equals(tpTags[1]) && tags[1].equals(tpTags[0])))) {
				return true;
			}
		}
		return false;
	}

	public boolean tagsEquals(Object obj) {
		TagPair tp = (TagPair) obj;
		String[] tpTags = tp.getTags();
		if ((tags[0].equals(tpTags[0]) && tags[1].equals(tpTags[1]))
				|| ((tags[0].equals(tpTags[1]) && tags[1].equals(tpTags[0])))) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		// 请考虑是否值得这么做,因为此时name是会变的.
		return (tags[0] + tags[1]).hashCode();
		// 推荐让name不可改变
	}

	public String toString() {
		String tag0Tmp = tags[0];
		String tag1Tmp = tags[1];
		//tag0Tmp = normalizeTag(tag0Tmp);
		//tag1Tmp = normalizeTag(tag1Tmp);
/*
		if (direction == 1) {
			return tag0Tmp + "->" + tag1Tmp;
		} else if (direction == -1) {
			return tag1Tmp + "->" + tag0Tmp;
		} else if (direction == 0) {
			return tag0Tmp + "->" + tag1Tmp + "\n" + tag1Tmp + "->" + tag0Tmp;
		} else {
			return tag0Tmp + "--" + tag1Tmp;
		}
		*/
		return tag0Tmp + "--" + tag1Tmp;
	}

	private String normalizeTag(String tag) {
		tag = StringUtils.replace(tag, "(", "");
		tag = StringUtils.replace(tag, ")", "");
		tag = StringUtils.replace(tag, "-", "_");
		tag = StringUtils.replace(tag, "/", "_");
		tag = StringUtils.replace(tag, " ", "_");
		if (tag.equals("c++")) {
			tag = "cpp";
		}
		return tag;
	}

}
