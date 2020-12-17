package com.itactic.core.utils;

import com.alibaba.fastjson.JSONArray;

import java.lang.reflect.Field;
import java.util.*;

public class ArrayToolkitUtils {
	/**
	 * 获取数组中某一列的集合
	 * @param list
	 * @param columnName
	 * @return
	 */
	public static <T> List<Object> column(List<T> list, String columnName){
		List<Object> result = new ArrayList<Object>();
		if(null == list || list.size() == 0){
			return result;
		}
		for(int i=0; i<list.size(); i++){
			Object o = invokeMethod(list.get(i), columnName);
			if(null != o && !result.contains(o)){
				result.add(o);
			}
		}
		return result;
	}
	
	/**
	 * 按给定的key值将list转为map
	 * @param <T>
	 * @param list
	 * @param columnName
	 * @return
	 */
	public static <T> Map<String, T> index(List<T> list, String columnName){
		Map<String, T> result = new LinkedHashMap<String, T>();
		if(null == list || list.size() == 0){
			return result;
		}
		for(int i=0; i<list.size(); i++){
			Object o = invokeMethod(list.get(i), columnName);
			if(null != o){
				result.put(String.valueOf(o), list.get(i));
			}
		}
		return result;
	}
	
	/**
	 * 通过反射获得属性值
	 * @param o
	 * @param name
	 * @return
	 */
	public static Object invokeMethod(Object o, String name) {
		Object object = null;
		try {
			Field field = o.getClass().getDeclaredField(name); // 取得类中属性所对应的类的实例
			field.setAccessible(true);// 将其属性强制设为可访问的，强暴反射
			object = field.get(o);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return object;
	}
	
	public static <T> Map<String, List<T>> group(List<T> list, String key){
		Map<String, List<T>> result = new LinkedHashMap<String, List<T>>();
		if(null == list || list.size() == 0){
			return result;
		}
		for(int i=0; i<list.size(); i++){
			Object o = invokeMethod(list.get(i), key);
			if(null != o){
				if(!result.containsKey(String.valueOf(o))){
					result.put(String.valueOf(o), new ArrayList<T>());
				}
				List<T> groupList = result.get(String.valueOf(o));
				if(!groupList.contains(list.get(i))){
					groupList.add(list.get(i));
				}
				result.put(String.valueOf(o), groupList);
			}
		}
		return result;
	}
	
	public static <T> boolean requireds(Map<String, T> array, List<String> keys){
		if(null == keys || keys.size() == 0){
			return false;
		}
		for(String key : keys){
			if(!array.containsKey(key)){
				return false;
			}
		}
		return true;
	}
	
	public static Map<String, Object> parts(Object obj, List<String> fields){
		Map<String, Object> res = new HashMap<String, Object>();
		if(null == fields || fields.size() == 0){
			return null;
		}
		for(String field : fields){
			res.put(field, invokeMethod(obj, field));
		}
		return res;
	}

	public static <T> List<T> pageList (List<T> list, Integer page, Integer limit) {
		if (null == list) {
			return new ArrayList<>();
		}
		if (null == page || page <= 0) {
			page = 1;
		}
		if (null == limit || limit < 0) {
			limit = 10;
		}
		Integer startIndex = (page - 1) * limit;
		Integer endIndex = Math.min(list.size(), startIndex + limit);
		if (startIndex > endIndex) {
			startIndex = endIndex;
		}
		return list.subList(startIndex, endIndex);
	}

	public static List<Object> pageJSONArray (JSONArray jsonArray, Integer page, Integer limit) {
		if (null == jsonArray) {
			return new JSONArray();
		}
		if (null == page || page <= 0) {
			page = 1;
		}
		if (null == limit || limit < 0) {
			limit = 10;
		}
		Integer startIndex = (page - 1) * limit;
		Integer endIndex = Math.min(jsonArray.size(), startIndex + limit);
		if (startIndex > endIndex) {
			startIndex = endIndex;
		}
		return jsonArray.subList(startIndex, endIndex);
	}
}
