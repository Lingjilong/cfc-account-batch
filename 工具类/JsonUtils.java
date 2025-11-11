package cn.com.njcb.utils;

import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PascalNameFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.log4j.Log4j;
import cn.com.njcb.annotation.MethodAnnotation;
import cn.com.njcb.annotation.TypeAnnotation;
import cn.com.njcb.dto.LogInfoDto;

/**
 * <pre>JSON针对实体大小写转换，可参照@JSONField等注解</pre>
 * @author Uncle chang
 * @time 2020年1月13日下午12:08:43
 * @copyright NJCB
 */
@TypeAnnotation("@description:JSON Utils @author:ji.ye @time:2019/4/25 19:17:10")
@Log4j
public class JsonUtils {

	@MethodAnnotation("list transfer to jsonstr...")
	public static <T> String listToJsonStr(long token, List<T> list) {
		String returnValue = null;

		try {
			if (!CollectionUtils.isEmpty(list)) {
				returnValue = JSONObject.toJSONString(list, false);
			}
		} catch (Exception e) {
			LogUtils.error(log, token, e);
		}
		return returnValue;
	}

	@MethodAnnotation("get value by key...")
	public static String getValueByKey(long token, String jsonStr, String key) {
		String returnValue = null;
		JSONObject jsonObject = null;

		try {
			if (!StringUtils.isEmpty(jsonStr) && !StringUtils.isEmpty(key)) {
				jsonObject = JSON.parseObject(jsonStr);
			}
			if (null != jsonObject) {
				returnValue = String.valueOf(jsonObject.get(key));
			}
		} catch (Exception e) {
			LogUtils.error(log, token, e);
		}
		return returnValue;
	}

	@MethodAnnotation("get value by key...")
	public static String getValueByKey(String jsonStr, String key) {
		String returnValue = null;
		JSONObject jsonObject = null;

		try {
			if (!StringUtils.isEmpty(jsonStr) && !StringUtils.isEmpty(key)) {
				jsonObject = JSON.parseObject(jsonStr);
			}
			if (null != jsonObject) {
				returnValue = String.valueOf(jsonObject.get(key));
			}
		} catch (Exception e) {
			LogUtils.error(log,new LogInfoDto(),"获取JSON字符串中某个参数异常！");
		}
		return returnValue;
	}

    @MethodAnnotation("jsonstr transfer to object...")
    public static <T> T jsonToObject(long token, String jsonStr, Class<T> T) {
        T returnValue = null;

        try {
            if (!StringUtils.isEmpty(jsonStr)) {
                returnValue = JSON.parseObject(jsonStr, T);
            }
        } catch (Exception e) {
            LogUtils.error(log, token, e);
        }
        return returnValue;
    }

    @MethodAnnotation("jsonstr transfer to object...")
    public static <T> T jsonToObject(LogInfoDto logInfoDto, String jsonStr, Class<T> T) {
        T returnValue = null;

        try {
            if (!StringUtils.isEmpty(jsonStr)) {
                returnValue = JSON.parseObject(jsonStr, T);
            }
        } catch (Exception e) {
            LoggerUtils.error(log, logInfoDto, e, "JSON转化对象异常");
        }
        return returnValue;
    }

    @MethodAnnotation("object transfer to object...")
    public static <T> T objectToObject(LogInfoDto logInfoDto, Object obj, Class<T> T) {
        return jsonToObject(logInfoDto, objectToJsonStr(logInfoDto.getToken(), obj), T);
    }

	@MethodAnnotation("jsonstr transfer to map...")
	public static <K, V> Map<K, V> jsonStrToMap(long token, String str) {
		Map<K, V> returnValue = null;

		try {
			if (!StringUtils.isEmpty(str)) {
				returnValue = (Map<K, V>) JSON.parse(str);
			}
		} catch (Exception e) {
			LogUtils.error(log, token, e);
		}
		return returnValue;
	}

	@MethodAnnotation("map transfer to jsonstr...")
	public static <K, V> String mapToJsonStr(long token, Map<K, V> map) {
		String returnValue = null;

		try {
			if (!CollectionUtils.isEmpty(map)) {
				returnValue = JSONObject.toJSONString(map, false);
			}
		} catch (Exception e) {
			LogUtils.error(log, token, e);
		}
		return returnValue;
	}

	@MethodAnnotation("object transfer to jsonstr...")
	public static <T> String objectToJsonStr(long token, T object) {
		String returnValue = null;

		try {
			if (null != object) {
				returnValue = JSONObject.toJSONString(object);
			}
		} catch (Exception e) {
			LogUtils.error(log, token, e);
		}
		return returnValue;
	}

    @MethodAnnotation("object transfer to jsonstr...")
    public static <T> String objectToJsonStrWithPascalName(T object) {
        String returnValue = null;

        if (null != object) {
            returnValue = JSONObject.toJSONString(object, new PascalNameFilter());
        }
        return returnValue;
    }
	
	@MethodAnnotation("object transfer to jsonstr with all attribute names of null value...")
	public static <T> String objectToJsonStrAll(long token, T object) {
		String returnValue = null;
		
		try {
			if (null != object) {
				returnValue = String.valueOf(net.sf.json.JSONObject.fromObject(object));
			}
		} catch (Exception e) {
			LogUtils.error(log, token, e);
		}
		return returnValue;
	}

    @MethodAnnotation("(阿里巴巴)实体类转json，含空值，且以null显示")
	public static <T> String objectToJsonWithEmptyValueAsNull(T object) {
	   return JSONObject.toJSONString(object, SerializerFeature.WriteMapNullValue);
	}
	
	@MethodAnnotation("(阿里巴巴)实体类转json，含空值，且以双引号显示")
	public static <T> String objectToJsonWithEmptyValueAsQuotationMarks(T object) {
	    return JSONObject.toJSONString(object, SerializerFeature.WriteNullStringAsEmpty);
	}
}
