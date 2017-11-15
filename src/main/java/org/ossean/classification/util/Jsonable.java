package org.ossean.classification.util;

import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

public interface Jsonable {

	JSONObject toSimpleJson();

	JSONObject toSimpleJson(String[] attrs);

	Map<String, Object> toJsonMap();

	Map<String, Object> toJsonMap(String[] attrs);

	Map<String, Object> toSimpleJsonMap();
	
	Map<String, Object> toFiniteJsonMao(Set<Object> serialized);
}
