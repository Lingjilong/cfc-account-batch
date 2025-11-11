package cn.com.njcb.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import lombok.extern.log4j.Log4j;
import cn.com.njcb.annotation.FieldAnnotation;
import cn.com.njcb.annotation.LogFilter;
import cn.com.njcb.annotation.MethodAnnotation;
import cn.com.njcb.common.Basic;
import cn.com.njcb.common.CfcSysHead;
import cn.com.njcb.common.ChannelConfigInfos;
import cn.com.njcb.dto.LogInfoDto;
import cn.com.njcb.enums.CFCBusinessEnum;
import cn.com.njcb.enums.ChannelInfosEnum;
import cn.com.njcb.enums.GydLoanInfoEnum;
import cn.com.njcb.enums.QueueSuffixEnum;
import cn.com.njcb.enums.ResultEnum;
import cn.com.njcb.request.BasicRequest;
import cn.com.njcb.request.JsonUnionRequest;
import cn.com.njcb.response.BasicRes;
import cn.com.njcb.response.BasicResponse;
import cn.com.njcb.ws.client.request.BasicServiceRequest;


/**
 * 通用工具
 * @author 王彬
 * @date 2018年8月21日
 */
@Log4j
@Service("commonUtils")
public class CommonUtils implements CommonUtil {
	
	/**
	 * 获取本机的配置
	 * @param prefix
	 * @param suffix
	 * @return
	 */
	public static String getConfigKey(String prefix, QueueSuffixEnum suffix){
		return getIpAndPort() + "_" + prefix + suffix.getSuffix();
	}
	
	/**
	 * 获取本机IP地址和端口
	 * @return
	 */
	public static String getIpAndPort(){
		long token = System.nanoTime();
		String ip = CommonUtils.getIpAddress(token);
		String port = CommonUtils.getPort(token);
		return ip + "_" + port;
	}
	
	/**
	 * 获取服务器ip
	 * @param token
	 * @return
	 */
	public static String getIpAddress(long token){
		String idAddress = "";
		
		try {
			idAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			LogUtils.error(log, token, "get ip address exception", e);
		}
		
		if(StringUtils.isEmpty(idAddress)) idAddress = "UNKNOWN";
		
		return idAddress;
	}
	
	/**
	 * 获取服务器端口
	 * @param token
	 * @return
	 */
	public static String getPort(long token){
		String port = "";
		
		try {
			ArrayList<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
			for (MBeanServer mBeanServer : mBeanServers) {
				Set<ObjectName> objectNames = mBeanServer.queryNames(new ObjectName("Catalina:type=Connector,*"), null);
				for (ObjectName object : objectNames) {
					String protocol = String.valueOf(mBeanServer.getAttribute(object, "protocol"));
					if("HTTP/1.1".equals(protocol) || "org.apache.coyote.http11.Http11NioProtocol".equals(protocol)){
						port = String.valueOf(mBeanServer.getAttribute(object, "port"));
						break;
					}
				}
			}
		} catch (Exception e) {
			LogUtils.error(log, token, "get port exception", e);
		}
		
		if(StringUtils.isEmpty(port)) port = "UNKNOWN";
		
		return port;
	}
	
	/**
	 * 获取日志传输对象
	 * @param channelInfosEnum
	 * @return
	 */
	public static LogInfoDto getLogInfoDto(ChannelInfosEnum channelInfosEnum){
		LogInfoDto logInfoDto = getLogInfoDto();//日志信息对象
		
		if(channelInfosEnum != null){
			logInfoDto.setChannel(channelInfosEnum.getChannelNum());
			logInfoDto.setProduct(channelInfosEnum.getProductNum());
			logInfoDto.setDesc(channelInfosEnum.getDesc());
            logInfoDto.setChannelInfosEnum(channelInfosEnum);
		}
		
		return logInfoDto;
	}

	/**
	 * 获取日志传输对象
	 * @param basicServiceRequest
	 * @return
	 */
	public static LogInfoDto getLogInfoDto(BasicServiceRequest basicServiceRequest) {
		LogInfoDto logInfoDto = getLogInfoDto();//日志信息对象

		if(basicServiceRequest != null) {
            ChannelInfosEnum channelInfosEnum = ChannelInfosEnum.getChannelInfos(basicServiceRequest.getApplicationId());
			logInfoDto.setChannel(channelInfosEnum.getChannelNum());
			logInfoDto.setProduct(channelInfosEnum.getProductNum());
			logInfoDto.setDesc(channelInfosEnum.getDesc());
            logInfoDto.setChannelInfosEnum(channelInfosEnum);
            if (StringUtils.isNotEmpty(basicServiceRequest.getToken())) {
                logInfoDto.setToken(Long.parseLong(basicServiceRequest.getToken()));
            }
		}

		return logInfoDto;
	}

    /**
     * 获取日志传输对象
     * @param channelInfosEnum
     * @return
     */
    public static LogInfoDto getLogInfoDto(ChannelInfosEnum channelInfosEnum, CFCBusinessEnum cfcBusinessEnum) {
        LogInfoDto logInfoDto = getLogInfoDto();//日志信息对象

        if(channelInfosEnum != null) {
            logInfoDto.setChannel(channelInfosEnum.getChannelNum());
            logInfoDto.setProduct(channelInfosEnum.getProductNum());
            logInfoDto.setDesc(channelInfosEnum.getDesc() + cfcBusinessEnum.getDesc());
            logInfoDto.setChannelInfosEnum(channelInfosEnum);
        }

        return logInfoDto;
    }

    /**
     * 重置日志传输对象
     * @param logInfoDto
     * @param channelInfosEnum
     * @return
     */
    public static void resetLogInfoDto(LogInfoDto logInfoDto, ChannelInfosEnum channelInfosEnum) {
        if(channelInfosEnum != null) {
            logInfoDto.setChannel(channelInfosEnum.getChannelNum());
            logInfoDto.setProduct(channelInfosEnum.getProductNum());
            logInfoDto.setDesc(channelInfosEnum.getDesc());
            logInfoDto.setChannelInfosEnum(channelInfosEnum);
        }
    }
	
	/**
	 * 获取GYD日志传输对象
	 * @param gydLoanInfoEnum
	 * @return
	 */
	public static LogInfoDto getLogInfoDtobyGYD(GydLoanInfoEnum gydLoanInfoEnum){
		LogInfoDto logInfoDto = getLogInfoDto();//日志信息对象
		
		if(gydLoanInfoEnum != null){
			logInfoDto.setChannel(gydLoanInfoEnum.getChannelNum());
			logInfoDto.setProduct(gydLoanInfoEnum.getProductNum());
			logInfoDto.setDesc(gydLoanInfoEnum.getDesc());
		}
		
		return logInfoDto;
	}
	
	/**
	 * 获取日志传输对象
	 * @return
	 */
	public static LogInfoDto getLogInfoDto(){
		LogInfoDto logInfoDto = new LogInfoDto();//日志信息对象
		long token = System.nanoTime();
		logInfoDto.setToken(token);
		logInfoDto.setStartTime(token);
		
		return logInfoDto;
	}
	
	/**
	 * 获取日志传输对象，一般用在接口请求的最开始
	 * @param request
	 * @return
	 */
	public static LogInfoDto getLogInfoDto(Basic request){
		LogInfoDto logInfoDto = new LogInfoDto();//日志信息对象
		logInfoDto.setStartTime(System.nanoTime());
		if(request != null){
			logInfoDto.setToken(request.getToken());
			logInfoDto.setDesc(request.getRemark());
			logInfoDto.setChannel(request.getChannel());
			logInfoDto.setProduct(request.getProduct());
			logInfoDto.setChannelInfosEnum(ChannelInfosEnum.getChannelInfos(request.getChannel()));
		}
		
		return logInfoDto;
	}



    /**
     * 获取日志传输对象，一般用在接口请求的最开始
     * @param request
     * @return
     */
    public static LogInfoDto getLogInfoDto(CfcSysHead request){
        LogInfoDto logInfoDto = new LogInfoDto();//日志信息对象
        logInfoDto.setStartTime(System.nanoTime());
        if(request != null){
            logInfoDto.setToken(request.getToken());
            logInfoDto.setDesc(request.getRemark());
            logInfoDto.setChannel(request.getChannel());
            logInfoDto.setProduct(request.getProduct());
            logInfoDto.setOrgCode(request.getOrgCode());
        }

        return logInfoDto;
    }

	public static LogInfoDto getLogInfoDto(BasicRequest request){
		LogInfoDto logInfoDto = new LogInfoDto();//日志信息对象
		logInfoDto.setStartTime(System.nanoTime());
		if(request != null){
			logInfoDto.setToken(request.getToken());
			logInfoDto.setDesc(request.getRemark());
			logInfoDto.setChannel(getChannel(request));
			logInfoDto.setProduct(getProduct(request));
            logInfoDto.setOrgCode(request.getOrgCode());
		}

		return logInfoDto;
	}

    public static LogInfoDto getLogInfoDto(JsonUnionRequest request){
        LogInfoDto logInfoDto = new LogInfoDto();//日志信息对象
        if(request != null && request.getLogInfoDto() == null){
            logInfoDto.setToken(request.getToken());
            logInfoDto.setDesc(request.getRemark());
            logInfoDto.setChannel(StringUtils.isEmpty(request.getChannelNum()) ? request.getChannelId() : request.getChannelNum());
            logInfoDto.setProduct(StringUtils.isEmpty(request.getProductNum()) ? request.getProductId() : request.getProductNum());
        } else if(request != null && request.getLogInfoDto() != null) {
            logInfoDto = request.getLogInfoDto();
        } else {
            logInfoDto.setToken(System.nanoTime());
        }
        logInfoDto.setStartTime(System.nanoTime());
        return logInfoDto;
    }

    public static LogInfoDto newLogInfoDto(BasicRequest request) {
        LogInfoDto logInfoDto = new LogInfoDto();//日志信息对象
        logInfoDto.setStartTime(System.nanoTime());
        if(request != null){
            logInfoDto.setToken(request.getToken());
            logInfoDto.setDesc(request.getRemark());
            logInfoDto.setChannel(request.getChannel());
            logInfoDto.setProduct(request.getProduct());
        }

        return logInfoDto;
    }
	
	/**
	 * 设置请求对象的部分属性
	 * @param logInfoDto
	 * @param request
	 */
	public static void initRequest(LogInfoDto logInfoDto, Basic request){
        if(logInfoDto != null && request != null) {
            if(StringUtils.isEmpty(request.getChannel())) request.setChannel(logInfoDto.getChannel());
            if(StringUtils.isEmpty(request.getProduct())) request.setProduct(logInfoDto.getProduct());
            if(StringUtils.isEmpty(request.getRemark())) request.setRemark(logInfoDto.getDesc());
            if(0 == request.getToken()) request.setToken(logInfoDto.getToken());

        }
	}

	/**
	 * 设置请求对象的部分属性
	 * @param request
	 * @param request
	 */
	public static void initResponse(Basic request, Basic response){
        if(request != null && response != null) {
            response.setChannel(request.getChannel());
            response.setProduct(request.getProduct());
            response.setRemark(request.getRemark());
            response.setToken(request.getToken());
        }
	}

	/**
	 * 设置请求对象的部分属性
	 * @param logInfoDto
	 * @param basicRequest
	 */
	public static void initBasicRequest(LogInfoDto logInfoDto, BasicRequest basicRequest) {
	    if(logInfoDto != null && basicRequest != null) {
            if(StringUtils.isEmpty(basicRequest.getChannel())) basicRequest.setChannel(logInfoDto.getChannel());
            if(StringUtils.isEmpty(basicRequest.getProduct())) basicRequest.setProduct(logInfoDto.getProduct());
            if(StringUtils.isEmpty(basicRequest.getRemark())) basicRequest.setRemark(logInfoDto.getDesc());
            if(0 == basicRequest.getToken()) basicRequest.setToken(logInfoDto.getToken());
        }
	}

	/**
	 * 设置请求对象的部分属性
	 * @param logInfoDto
	 * @param basic
	 */
	public static void initBasic(LogInfoDto logInfoDto, Basic basic) {
	    if(logInfoDto != null && basic != null) {
            if(StringUtils.isEmpty(basic.getChannel())) basic.setChannel(logInfoDto.getChannel());
            if(StringUtils.isEmpty(basic.getProduct())) basic.setProduct(logInfoDto.getProduct());
            if(StringUtils.isEmpty(basic.getRemark())) basic.setRemark(logInfoDto.getDesc());
            if(0 == basic.getToken()) basic.setToken(logInfoDto.getToken());
        }
	}
    /**
     * 设置请求对象的部分属性
     * @param logInfoDto
     * @param cfcSysHead
     */
    public static void initCfcSysHead(LogInfoDto logInfoDto, CfcSysHead cfcSysHead) {
        if(logInfoDto != null && cfcSysHead != null) {
            if(StringUtils.isEmpty(cfcSysHead.getChannel())) {
                cfcSysHead.setChannel(logInfoDto.getChannel());
            }
            if(StringUtils.isEmpty(cfcSysHead.getProduct())) {
                cfcSysHead.setProduct(logInfoDto.getProduct());
            }
            if(StringUtils.isEmpty(cfcSysHead.getRemark())) {
                cfcSysHead.setRemark(logInfoDto.getDesc());
            }
            if(0 == cfcSysHead.getToken()) {
                cfcSysHead.setToken(logInfoDto.getToken());
            }
            if(StringUtils.isEmpty(cfcSysHead.getOrgCode())) {
                cfcSysHead.setOrgCode(logInfoDto.getOrgCode());
            }
        }
    }

    public static CfcSysHead initCfcSysHead(LogInfoDto logInfoDto) {
        CfcSysHead cfcSysHead = new CfcSysHead();
        if(logInfoDto != null) {
            cfcSysHead.setChannel(logInfoDto.getChannel());
            cfcSysHead.setProduct(logInfoDto.getProduct());
            cfcSysHead.setRemark(logInfoDto.getDesc());
            cfcSysHead.setToken(logInfoDto.getToken());
            cfcSysHead.setOrgCode(logInfoDto.getOrgCode());
        }
        return cfcSysHead;
    }
	
	/**
	 * 打印对象的非空属性方法
	 * @param obj
	 * @return
	 */
	public static <T> String getNotNullValue(T obj){
		StringBuffer sb = new StringBuffer();
		String type = obj.getClass().getName();
		if("java.util.HashSet".equals(type) 
				|| "java.util.ArrayList".equals(type)
				|| "java.util.HashMap".equals(type)){
			sb.append(type + obj);
		}else{
			String value = getNotNullValue(0, obj);
			sb.append(dealValue(0, value));
		}
		return sb.toString();
	}
	
	/**
	 * 打印对象的非空属性方法
	 * @param token
	 * @param obj
	 * @return
	 */
	public static <T> String getNotNullValue(long token, T obj){
		StringBuffer sb = new StringBuffer();
		try{
			Class<?> clazz = obj.getClass();
			for (; clazz != Object.class ; clazz = clazz.getSuperclass()) {
				sb.append(getNotNullValue(token, obj, clazz));
			}
			
		}catch(Exception e){
			LogUtils.error(log, token, "toString异常", e);
		}
		
		return sb.toString();
	}
	/**
	 * 处理乱码iso8859-1
	 * @param logInfoDto
	 * @param obj
	 */
	public static void dealRandomCode(LogInfoDto logInfoDto, Object obj){
		if(obj == null) return;
		try{
			Class<?> clazz = obj.getClass();
			for (; clazz != Object.class ; clazz = clazz.getSuperclass()) {
				Field[] fs = clazz.getDeclaredFields();
				for(Field f : fs){
					f.setAccessible(true);
					try{
						
						String value = dealRandomCodeValue(logInfoDto, f.get(obj));
						
						if(value != null) f.set(obj, value);
						
					}catch(Exception e){
						LoggerUtils.error(log, logInfoDto, e, "处理乱码值异常");
					}finally{
						f.setAccessible(false);
					}
				}
			}
		}catch(Exception e){
			LoggerUtils.error(log, logInfoDto, e, "处理乱码异常");
		}
	}
	
	/**
	 * 处理乱码的值
	 * @param logInfoDto
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	private static String dealRandomCodeValue(LogInfoDto logInfoDto, Object obj) throws Exception{
		
		if(obj == null) return null;
		
		String type = obj.getClass().getName();
		
		if("java.lang.String".equals(type)){
			//处理字符串类型的乱码
			String value = String.valueOf(obj).trim();
			
			String regex = "^[a-z0-9A-Z]+$";
			
			if(!value.matches(regex) && value.equals(new String(value.getBytes("ISO-8859-1"), "ISO-8859-1"))){
				return new String(value.getBytes("ISO-8859-1"), "UTF-8");
			}
		}else if("java.util.ArrayList".equals(type)){
			//处理ArrayList类型的乱码
			ArrayList list = (ArrayList)obj;
			
			for(int i = 0; i < list.size(); i++){
				String value = dealRandomCodeValue(logInfoDto, list.get(i));
				if(value != null) list.set(i, value);
			}
		}else if(type.startsWith("cn.com.njcb")){
			//处理以cn.com.njcb开头的类
			dealRandomCode(logInfoDto, obj);
		}
		
		return null;
	}
	
	/**
	 * 获取实体类的非空属性的值
	 * @param token
	 * @param obj
	 * @param clazz
	 * @return
	 */
	private static String getNotNullValue(long token, Object obj, Class<?> clazz){
		Field[] fs = clazz.getDeclaredFields();
		StringBuffer sb = new StringBuffer(clazz.getSimpleName() + "[");
		int index = 0;
        FieldAnnotation fieldAnnotation;
		for(Field f : fs){
			f.setAccessible(true);
            String chineseName = null;
		    if(f.isAnnotationPresent(FieldAnnotation.class)) {
                fieldAnnotation = f.getAnnotation(FieldAnnotation.class);
                if (fieldAnnotation != null)
                    chineseName = fieldAnnotation.chineseName();
            }
			String name = f.getName();
			try{
				Object o = f.get(obj);
				if(null != f.getAnnotation(LogFilter.class)) continue;
				if("serialVersionUID".equals(name) || o == null) continue;
				
				String value = getValue(token, o, f.getType().isPrimitive());
				
				if(StringUtils.isEmpty(value)) continue;

				//REQ001609-6 打印日志时不再打印或部分隐藏银行卡号
				Pattern cardNoPattern = Pattern.compile("^(62)(\\d{14}|\\d{15}|\\d{16}|\\d{17})$");
				Matcher cardNoMatcher = cardNoPattern.matcher(value);

				//身份证号正则表达式
				Pattern idNumPattern = Pattern.compile("^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");
				Matcher idNumMatcher = idNumPattern.matcher(value);

				if(cardNoMatcher.find() && !idNumMatcher.find()){
					value = value.replaceAll("(\\d{4})\\d{4}(\\d{4})", "$1****$2");
				}

				if(index > 0) sb.append(",");
				if(StringUtils.isEmpty(chineseName))
				    sb.append(name + "=" + value);
				else
				    sb.append(name + "/" + chineseName + "=" + value);
				index++;
			}catch(Exception e){
				LogUtils.info(log, token, name + "处理异常");
			}finally{
				f.setAccessible(false);
			}
		}
		sb.append("]");
		return sb.toString();
	}
	
	public static String getValue(long token, Object o, boolean isPrimitive){
		String type = o.getClass().getName();
		String value = "";
		if("java.lang.String".equals(type)
				|| isPrimitive//基本类型返回true（byte、short、int、long、float、double、boolean、char）
				|| "java.math.BigDecimal".equals(type)
				|| "java.lang.Boolean".equals(type)
				|| "java.lang.Byte".equals(type)
				|| "java.lang.Character".equals(type)
				|| "java.lang.Short".equals(type)
				|| "java.lang.Integer".equals(type)
				|| "java.lang.Long".equals(type)
				|| "java.lang.Float".equals(type)
				|| "java.lang.Double".equals(type)){
			value = String.valueOf(o).trim();
		}else if("java.util.Date".equals(type) || "java.sql.Timestamp".equals(type)){
			//处理Date、处理Timestamp
			value = DateUtils.dateToStr(token, (Date)o, "yyyy-MM-dd HH:mm:ss");
		}else if("org.hibernate.collection.PersistentList".equals(type) 
				|| "org.hibernate.collection.PersistentSet".equals(type) 
				|| "java.util.HashSet".equals(type) 
				|| "java.util.ArrayList".equals(type)){
			//处理HashSet、处理ArrayList
			value = dealCollection(token, o);
		}else if("org.hibernate.collection.PersistentMap".equals(type) 
				|| "java.util.HashMap".equals(type)){
			//处理HashMap
			value = o.toString();
		}else if(o.getClass().isArray()){
			//处理Array
			value = dealArray(token, o);
		}else if("com.sun.org.apache.xerces.internal.dom.ElementNSImpl".equals(type)){
			//请求对象中部分属性石Object，默认会转换成ElementNSImpl
			ElementNSImpl elementNSImpl = (ElementNSImpl)o;
			NodeList nodeList = elementNSImpl.getChildNodes();
			if(nodeList != null && nodeList.getLength() > 0){
				Node node = nodeList.item(0);
				value = String.valueOf(node.getNodeValue());
			}
			
		}else if(type.startsWith("cn.com.njcb.enums")){
			value = o.toString();
		}else if(type.startsWith("cn.com.njcb")){
			value = getNotNullValue(token, o);
			//处理对象
			/*if("com.sun.org.apache.xerces.internal.dom.ElementNSImpl".equals(type)){
				//请求对象中部分属性石Object，默认会转换成ElementNSImpl
				ElementNSImpl elementNSImpl = (ElementNSImpl)o;
				NodeList nodeList = elementNSImpl.getChildNodes();
				if(nodeList != null && nodeList.getLength() > 0){
					Node node = nodeList.item(0);
					value = String.valueOf(node.getNodeValue());
				}
				
			}else if(!"org.apache.log4j.Logger".equals(type)){
				value = getNotNullValue(token, o);
			}*/
		}
		return value;
	}
	
	public static String dealValue(long token, String value){
		try{
			if(value.equals(new String(value.getBytes("ISO-8859-1"), "ISO-8859-1"))){
				return new String(value.getBytes("ISO-8859-1"), "UTF-8");
			}
		}catch(Exception e){
			LogUtils.error(log, token, "getEncoding异常", e);
		}
		
		return value;
	}
	
	private static String dealCollection(long token, Object os){
		StringBuffer listStr = new StringBuffer();
		listStr.append("[");
		Collection c = (Collection)os;
		int i = 0;
		for(Object o : c){
			if(o == null) continue;
			if(i == 0){
				listStr.append(getValue(token, o, o.getClass().isPrimitive()));
				i++;
				continue;
			}
			listStr.append("," + getValue(token, o, o.getClass().isPrimitive()));
		}
		listStr.append("]");
		return listStr.toString();
	}

	private static String dealArray(long token, Object o){
		StringBuffer arrayStr = new StringBuffer();
		arrayStr.append("[");
		for(int i = 0; i < Array.getLength(o); i++){
			Object item = Array.get(o, i);
			if(item == null) continue;
			if(i == 0){
				arrayStr.append(getValue(token, item, item.getClass().isPrimitive()));
				continue;
			}
			arrayStr.append("," + getValue(token, item, item.getClass().isPrimitive()));
		}
		arrayStr.append("]");
		return arrayStr.toString();
	}
	
	@MethodAnnotation("赋予内存值")
	public static void setCache(long token, Object obj, LinkedHashMap<String, String> configInfoMap) {
		if(CollectionUtils.isEmpty(configInfoMap) || null == obj) {
			return;
		}
		Field[] fields = obj.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			f.setAccessible(true);
			String name = f.getName();
			try {
				String value = configInfoMap.get(name);
				if (StringUtils.isEmpty(value)) {
					continue;
				}
				f.set(obj, value);
				LogUtils.info(log, token, "${" + name + "} = " + value);
			} catch (Exception e) {
				LogUtils.error(log, token, "${" + name + "} = 处理异常", e);
			} finally {
				if (f != null) {
					f.setAccessible(false);
				}
			}
		}
	}
	
	@MethodAnnotation("刷新内存值")
	public static void resetCache(long token, Object obj, String configKey, String configValue) {
		if(null == obj) {
			return;
		}
		Field[] fs = obj.getClass().getDeclaredFields();
		for (int i = 0; i < fs.length; i++) {
			Field f = fs[i];
			try {
				String name = f.getName();
				if (name.equals(configKey)) {
					f = ChannelConfigInfos.class.getField(configKey);
					f.setAccessible(true);
					f.set(obj, configValue);
					return;
				}
			} catch (Exception e) {
				LogUtils.error(log, token, "刷新内存配置信息异常", e);
			} finally {
				if (f != null) {
					f.setAccessible(false);
				}
			}
		}
	}

    /**
     * 获取渠道标识
     * @param basicRequest
     * @return
     */
    public static String getChannel(BasicRequest basicRequest){
        if (StringUtils.isNotEmpty(basicRequest.getChannel())) {
            return basicRequest.getChannel();
        }
        if (StringUtils.isNotEmpty(basicRequest.getChannelId())) {
            return basicRequest.getChannelId();
        }
        if (StringUtils.isNotEmpty(basicRequest.getChannelNum())) {
            return basicRequest.getChannelNum();
        }
        return null;
    }

    /**
     * 获取产品标识
     * @param basicRequest
     * @return
     */
    public static String getProduct(BasicRequest basicRequest){
        if (StringUtils.isNotEmpty(basicRequest.getProduct())) {
            return basicRequest.getProduct();
        }
        if (StringUtils.isNotEmpty(basicRequest.getProductId())) {
            return basicRequest.getProductId();
        }
        if (StringUtils.isNotEmpty(basicRequest.getProductNum())) {
            return basicRequest.getProductNum();
        }
        return null;
    }

    /**
     * 判断响应是否成功
     * @param basicRes
     * @return
     */
    public static boolean resIsNotSuccess(BasicRes basicRes) {
        if (null == basicRes || !StringUtils.isEqual(ResultEnum.SUCCESS.getValue(), basicRes.getResult())) {
            return true;
        }
        return false;
    }
    public static boolean resIsNotSuccess(BasicResponse basicResponse) {
        if (null == basicResponse || !StringUtils.isEqual(ResultEnum.SUCCESS.getValue(), basicResponse.getResult())) {
            return true;
        }
        return false;
    }

    @Override
    public String calcPercent(int num1, int num2, int maximumFractionDigits) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(maximumFractionDigits);
        return numberFormat.format((float) num1 / (float) num2 * 100);
    }

    @MethodAnnotation("map转对象")
    public static Object mapToObject(LogInfoDto logInfoDto, Map<String, Object> map, Class<?> beanClass) {
        if (map == null)
            return null;

        Object obj = null;
        try {
            obj = beanClass.newInstance();

            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                try {
                    int mod = field.getModifiers();
                    if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                        continue;
                    }

                    field.setAccessible(true);
                    field.set(obj, map.get(field.getName()));
                } catch (Exception e) {
                    LoggerUtils.error(log, logInfoDto, e, "map转对象异常");
                } finally {
                    if (field != null) {
                        field.setAccessible(false);
                    }
                }
            }
        } catch (Exception e) {
            LoggerUtils.error(log, logInfoDto, e, "beanClass创建实例异常");
        }
        return obj;
    }

}