package cn.com.njcb.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import cn.com.njcb.annotation.TypeAnnotation;

/**
 * 集合校验
 * @author ji.ye
 * @time 2019年5月13日下午8:07:33
 * @copyright NJCB
 */
@TypeAnnotation("@description:集合校验 @author:ji.ye @time:2019/4/25 19:17:10")
public class CollectionUtils {

	public static <T> boolean isEmpty(Collection<T> collection) {
		return collection == null || collection.isEmpty();
	}

    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }
	
	public static <T> boolean isEmpty(T[] obj) {
	    return obj == null || obj.length == 0;
	}
    public static <T> boolean isEmpty(int[] obj) {
        return obj == null || obj.length == 0;
    }

    public static <T> boolean isNotEmpty(T[] obj) {
        return !isEmpty(obj);
    }
	
	public static <T, K> boolean isEmpty(Map<T, K> map) {
		return map == null || map.isEmpty();
	}

	public static int mapValuePlus(Map<String, Integer> map) {
        int count = 0;
	    for(Map.Entry<String, Integer> entry : map.entrySet()) {
            count += entry.getValue();
        }
	    return count;
    }


    /**
     * @Description: 将map的key，value给对象赋值
     * @Author: cangsu
     * @Date: 2020-11-11 10:54
     **/
    public static <T> T mapToObject(Map<String, Object> map,Class<T> clazz) {
        if (map == null) return null;
        try {
            Class<?> classZ = Class.forName(clazz.getName());
            //获取属性列表
            Field[] declaredFields = classZ.getDeclaredFields();
            //获取对象实例
            T o = clazz.getDeclaredConstructor().newInstance();
            for (Field field : declaredFields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) continue;
                if(map.containsKey(field.getName())){
                    Object s = map.get(field.getName());
                    //取消属性的访问权限控制，即使private属性也可以进行访问。
                    field.setAccessible(true);
                    field.set(o,s);
                }
            }
            System.out.println(o);
            return o;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 有序数组查询是否包含元素使用二分法提高搜索速度
     * @param objs
     * @param obj
     * @return
     */
    public static boolean binarySearch(Object[] objs, Object obj) {
        if (isNotEmpty(objs) && null != obj && Arrays.binarySearch(objs, obj) >= 0) {
            return true;
        } else {
            return false;
        }
    }
}
