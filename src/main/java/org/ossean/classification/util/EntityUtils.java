package org.ossean.classification.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class EntityUtils {

	public static String toString(final Object obj) {
		String[] attrs = getPrivateFieldNames(obj);
		return toString(obj, attrs);
	}

	@SuppressWarnings("rawtypes")
	public static List<Map<String, Object>> toJsonMapList(List list) {
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		if (list != null && !list.isEmpty()) {
			for (Object obj : list) {
				if (obj instanceof Jsonable) {
					Jsonable json = (Jsonable) obj;
					mapList.add(json.toJsonMap());
				}
			}
		}
		return mapList;
	}

	@SuppressWarnings("rawtypes")
	public static List<Map<String, Object>> toSimpleJsonMapList(List list) {
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		if (list != null && !list.isEmpty()) {
			for (Object obj : list) {
				if (obj instanceof Jsonable) {
					Jsonable json = (Jsonable) obj;
					mapList.add(json.toSimpleJsonMap());
				}
			}
		}
		return mapList;
	}

	public static String toString(final Object obj, String[] attrs) {
		Map<String, Object> map = getGetMethodsValue(obj);
		Set<String> keys = map.keySet();
		StringBuffer buffer = new StringBuffer();
		if (attrs != null && attrs.length > 0) {
			buffer.append("{");
			for (String attr : attrs) {
				if (keys.contains(attr)) {
					Object value = map.get(attr);
					buffer.append(attr);
					buffer.append("=");
					buffer.append(value == null ? "null" : value.toString());
					buffer.append(",");
				}
			}
			if (buffer.toString().endsWith(",")) {
				buffer.deleteCharAt(buffer.lastIndexOf(","));
			}
			buffer.append("}");
		}
		return buffer.toString();
	}

	public static Map<String, Object> toJsonMap(final Object obj) {
		String[] attrs = getPrivateFieldNames(obj);
		return toJsonMap(obj, attrs);
	}

	public static Map<String, Object> toJsonMap(final Object obj, String[] attrs) {
		// Map<String, Object> map = getGetMethodsValue(obj);
		Map<String, Object> map = getGetMethodsValueWithAttrs(obj, attrs);
		Set<String> keys = map.keySet();
		Map<String, Object> ret = new HashMap<String, Object>();
		if (attrs != null && attrs.length > 0) {
			for (String attr : attrs) {
				if (keys.contains(attr)) {
					Object value = map.get(attr);
					if (value != null) {
						if (value instanceof Jsonable) {
							Jsonable jvalue = (Jsonable) value;
							ret.put(attr, jvalue.toJsonMap());
						} else if (value instanceof Collection<?>) {
							List<Map<String, Object>> m = new ArrayList<Map<String, Object>>();
							Collection<Jsonable> jI = (Collection<Jsonable>) value;
							if (!jI.isEmpty()) {
								for (Jsonable j : jI) {
									m.add(j.toJsonMap());
								}
								ret.put(attr, m);
							}
						} else {
							ret.put(attr, value);
						}
					}

					/*
					 * else if (value != null && value instanceof Date) {
					 * SimpleDateFormat format = new SimpleDateFormat(
					 * "yyyy-MM-dd hh:mm:ss"); String fmtString =
					 * format.format(value); ret.put(attr, fmtString); }
					 */

				}
			}
		}
		return ret;
	}

	public static Map<String, Object> toFiniteJsonMap(final Object obj,
			Set<Object> serialized) {
		// Map<String, Object> map = getGetMethodsValue(obj);
		Map<String, Object> map = getGetMethodsValue(obj);
		Set<String> keys = map.keySet();
		Map<String, Object> ret = new HashMap<String, Object>();
		for (String attr : keys) {
			Object value = map.get(attr);
			if (value != null) {
				if (value instanceof Jsonable) {
					Jsonable jvalue = (Jsonable) value;
					ret.put(attr, jvalue.toJsonMap());
				} else if (value instanceof Collection<?>) {
					List<Map<String, Object>> m = new ArrayList<Map<String, Object>>();
					Collection<Jsonable> jI = (Collection<Jsonable>) value;
					if (!jI.isEmpty()) {
						for (Jsonable j : jI) {
							m.add(j.toJsonMap());
						}
						ret.put(attr, m);
					}
				} else {
					ret.put(attr, value);
				}
			}
		}
		return ret;
	}

	public static JSONObject toJsonObject(final Object obj) {
		String[] attrs = getPrivateFieldNames(obj);
		return toJsonObject(obj, attrs);
	}

	public static JSONObject toJsonObject(final Object obj, String[] attrs) {
		JSONObject json = new JSONObject();
		Map<String, Object> map = toJsonMap(obj, attrs);
		Set<String> keys = map.keySet();
		for (String key : keys) {
			try {
				Object value = map.get(key);
				if (value != null && value instanceof Jsonable) {
					Jsonable jvalue = (Jsonable) value;
					json.put(key, jvalue.toSimpleJson());
				} else {
					json.put(key, value);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	public static String[] getPrivateFieldNames(Object obj) {
		List<String> list = new ArrayList<String>();
		// Class<?> clazz = obj.getClass();
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			int mod = field.getModifiers();
			// field.getName().equals("serialVersionUID")
			if (Modifier.isStatic(mod) || Modifier.isPublic(mod)) {
				continue;
			}
			list.add(field.getName());
		}
		return list.toArray(new String[list.size()]);
	}

	public static Map<String, Object> getGetMethodsValue(Object obj) {
		Map<String, Object> keyValue = new HashMap<String, Object>();
		try {
			Class<?> clazz = obj.getClass();
			Field[] fields = obj.getClass().getDeclaredFields();
			for (Field field : fields) {
				int mod = field.getModifiers();
				// field.getName().equals("serialVersionUID")
				if (Modifier.isStatic(mod) || Modifier.isPublic(mod)) {
					continue;
				}
				PropertyDescriptor pd = new PropertyDescriptor(field.getName(),
						clazz);
				Method getMethod = pd.getReadMethod();
				Object value = getMethod.invoke(obj);
				keyValue.put(field.getName(), value);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return keyValue;
	}

	public static Map<String, Object> getGetMethodsValueWithAttrs(Object obj,
			String[] attrs) {
		Set<String> attrsSet = new HashSet<String>();
		for (String tmp : attrs) {
			attrsSet.add(tmp);
		}
		Map<String, Object> keyValue = new HashMap<String, Object>();
		try {
			Class<?> clazz = obj.getClass();
			Field[] fields = obj.getClass().getDeclaredFields();
			for (Field field : fields) {
				int mod = field.getModifiers();
				// field.getName().equals("serialVersionUID")
				if (Modifier.isStatic(mod) || Modifier.isPublic(mod)) {
					continue;
				}
				PropertyDescriptor pd = new PropertyDescriptor(field.getName(),
						clazz);
				Method getMethod = pd.getReadMethod();
				if (attrsSet.contains(field.getName())) {
					Object value = getMethod.invoke(obj);
					keyValue.put(field.getName(), value);
				}

			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return keyValue;
	}

	public static JSONArray toJSONArray(List<Object> list) {
		JSONArray array = new JSONArray();
		for (Object obj : list) {
			if (obj instanceof Jsonable) {
				Jsonable json = (Jsonable) obj;
				array.put(json.toSimpleJson());
			} else {
				array.put(obj);
			}

		}
		return array;
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> list2JsonMapList(List<?> objs) {
		if (objs != null) {
			if (!objs.isEmpty()) {
				List<Map<String, Object>> rt = new ArrayList<Map<String, Object>>();
				List<Jsonable> tmps = (List<Jsonable>) objs;
				for (Jsonable tmp : tmps) {
					rt.add(tmp.toJsonMap());
				}
				return rt;
			}
		}
		return null;
	}
	// /* 下面这个函数是给order专用的 */
	// public static List<Map<String, Object>> toJsonMap_getOrder(List<Order>
	// list) {
	// List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
	// if (list != null && !list.isEmpty()) {
	// for (Order obj : list) {
	//
	// mapList.add(obj.toSimpleJsonMap_2());
	//
	// }
	// }
	// return mapList;
	// }

}
