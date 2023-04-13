package net.mingsoft.utils;

import java.util.List;
import java.util.Map;

/**
 * 判断bean，数组，集合是否为空
 * 
 * @author yangriguang
 *
 */
public class BeanUtil {

	public static boolean isBlank(Object obj) {
		if (obj == null) {
			return true;
		}
		return false;
	}

	public static boolean isBlank(List<?> list) {
		if (list == null || list.size() <= 0) {
			return true;
		}
		return false;
	}

	public static boolean isBlank(Map<?, ?> map) {
		if (map == null || map.size() <= 0) {
			return true;
		}
		return false;
	}

	public static boolean isBlank(Object[] obj) {
		if (obj == null || obj.length <= 0) {
			return true;
		}
		return false;
	}

	public static boolean isNotBlank(Object obj) {
		return !isBlank(obj);
	}

	public static boolean isNotBlank(List<?> list) {
		return !isBlank(list);
	}

	public static boolean isNotBlank(Map<?, ?> map) {
		return !isBlank(map);
	}

	public static boolean isNotBlank(Object[] obj) {
		return !isBlank(obj);
	}

}
