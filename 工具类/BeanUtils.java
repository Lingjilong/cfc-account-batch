package cn.com.njcb.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.springframework.cglib.beans.BeanMap;
import cn.com.njcb.dto.LogInfoDto;

/**
 * @author：Uncle chang
 * @description：Bean工具
 * @time：2021-12-23 15:25
 * @copyright：CFC
 */
public class BeanUtils extends org.springframework.beans.BeanUtils {

    /**
     * 官方直接生抛异常，太粗暴，这里加了非空校验
     * 将source属性拷贝至target，target若带有属性，属性值都会被清空
     * 若target是作为某个父类的子类的对象传入，父类的其余属性不受影响
     *
     * @param source
     * @param target
     */
    public static void copyPropertie(Object source, Object target) {
        if (null != source && null != target) {
            copyProperties(source, target);
        }
    }

    /**
     * bean转map
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T t) {
        Map<String, Object> map = new HashMap<>();
        if (t != null) {
            BeanMap beanMap = BeanMap.create(t);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }

    /**
     * map转bean
     *
     * @param map
     * @param <T>
     * @return
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> t) throws Exception {
        T tt = t.newInstance();
        BeanMap beanMap = BeanMap.create(tt);
        beanMap.putAll(map);
        return tt;
    }

    /**
     * 对象和表对象进行比较，返回新对象并赋值，值为比对后有差异的值
     * 1、需传相同对象进行比较
     * 2、涉及字段需用传入字符数组，代表仅维护部分字段
     * 3、主要适用与数据库对象进行比较，空值不覆盖数据库有值字段
     * 4、该方法主要解决hibernate实现部分字段更新，非全表字段更新，发送给数据库的SQL语句为update set XXX=XXX(,...) where id = XX;
     *
     * @param object
     * @param dbObject
     * @param keys(需维护的字段集合)
     * @param <T>
     * @return
     */
    public static <T> T compareFields(LogInfoDto logInfoDto, T object, T dbObject, String[] keys, Class<T> t) {
        if (null == object || null == dbObject) {
            LoggerUtils.info(logInfoDto, "空对象不进行比较");
            return null;
        }
        T target = null;
        try {
            if (object.getClass() != dbObject.getClass()) {
                LoggerUtils.info(logInfoDto, "仅允许同类进行比较");
                return null;
            }
            nullStringAttrToNull(object);
            nullStringAttrToNull(dbObject);
            int n = 0;
            target = t.newInstance();
            for (String key : keys) {
                Object value = getValueByField(object, key);
                Object dbValue = getValueByField(dbObject, key);
                if (null != value) {
                    if (!value.equals(dbValue)) {
                        // 值不相等的判断且是维护字段
                        cn.cetelem.core.utils.BeanUtils.setProperty(target, key, value);
                        LoggerUtils.info(logInfoDto, "字段[" + key + "]值有差异，由[" + value + "]覆盖[" + dbValue + "]");
                        n++;
                    }
                }
            }
            if (0 == n) {
                // 未进行赋值则删除该对象，否则会生成空对象串
                target = null;
            }
        } catch (Exception e) {
            LoggerUtils.error(logInfoDto, e, "反射比对异常");
        }
        return target;
    }

    /**
     ** 该方法是用于相同对象不同属性值的合并<br>
     ** 如果两个相同对象中同一属性都有值，那么sourceBean中的值会覆盖tagetBean重点的值<br>
     ** 如果sourceBean有值，targetBean没有，则采用sourceBean的值<br>
     ** 如果sourceBean没有值，targetBean有，则保留targetBean的值
     *
     * @param sourceBean    被提取的对象bean
     * @param targetBean    用于合并的对象bean
     * @return targetBean,合并后的对象
     */
    public static <T> void combineSydwCore(T sourceBean, T targetBean) {
        if (sourceBean == null || targetBean == null) {
            return;
        }
        nullStringAttrToNull(sourceBean);
        nullStringAttrToNull(targetBean);
        Class<?> sourceBeanClass = sourceBean.getClass();
        Class<?> targetBeanClass = targetBean.getClass();

        Field[] sourceFields = sourceBeanClass.getDeclaredFields();
        Field[] targetFields = targetBeanClass.getDeclaredFields();
        Field sourceField = null;
        Field targetField = null;
        try {
            for (int i = 0; i < sourceFields.length; i++) {
                sourceField = sourceFields[i];
                targetField = targetFields[i];
                if (null == sourceField || null == targetField) {
                    continue;
                }
                if (Modifier.isStatic(sourceField.getModifiers())) {
                    continue;
                }
                if (Modifier.isStatic(targetField.getModifiers())) {
                    continue;
                }
                sourceField.setAccessible(true);
                targetField.setAccessible(true);
                try {
                    if (!(sourceField.get(sourceBean) == null) && !"serialVersionUID".equals(sourceField.getName())) {
                        targetField.set(targetBean, sourceField.get(sourceBean));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } finally {
            if (null != sourceField) {
                sourceField.setAccessible(false);
            }
            if (null != targetField) {
                targetField.setAccessible(false);
            }
        }
    }

    /**
     * 把对象中的 String 类型为空字符串的字段，转换为null
     *
     * @param cls 待转化对象
     * @return 转化好的对象
     */
    public static void nullStringAttrToNull(Object cls) {
        if (cls == null) {
            return;
        }
        Field[] fields = cls.getClass().getDeclaredFields();
        if (fields.length == 0) {
            return;
        }
        for (Field field : fields) {
            if ("String".equals(field.getType().getSimpleName())) {
                field.setAccessible(true);
                try {
                    if (StringUtils.isEmpty(String.valueOf(field.get(cls)))) {
                        field.set(cls, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    field.setAccessible(false);
                }
            }
        }
    }

    /**
     * 把对象中的 String 类型为null的字段，转换为null
     *
     * @param cls 待转化对象
     * @return 转化好的对象
     */
    public static void nullToNullStringAttr(Object cls) {
        if (cls == null) {
            return;
        }
        Field[] fields = cls.getClass().getDeclaredFields();
        if (fields.length == 0) {
            return;
        }
        for (Field field : fields) {
            if ("String".equals(field.getType().getSimpleName())) {
                field.setAccessible(true);
                try {
                    if (null == field.get(cls)) {
                        field.set(cls, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    field.setAccessible(false);
                }
            }
        }
    }

    /**
     * 根据属性集反射获取值
     *
     * @param t
     * @param params(指定属性值)
     * @param <T>
     * @return
     */
    public static <T> boolean valueIsAllEmpty(T t, String[] params) {
        for (String param : params) {
            Object valueByField = getValueByField(t, param);
            if (null != valueByField) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据属性反射获取值
     *
     * @param t
     * @param name
     * @param <T>
     * @return
     */
    public static <T> Object getValueByField(T t, String name) {
        Object value = null;
        Field field = null;
        try {
            field = t.getClass().getDeclaredField(name);
            field.setAccessible(true);
            value = field.get(t);
        } catch (Exception e) {
        } finally {
            if (null != field) {
                field.setAccessible(false);
            }
        }
        return value;
    }

    /**
     * JAXB实现bean转xml
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public static String beanToXml(Object obj) throws Exception {
        JAXBContext context;
        StringWriter writer = new StringWriter();
        context = JAXBContext.newInstance(obj.getClass());
        Marshaller marshaller = context.createMarshaller();
        // 在这里指定JAXB_FORMATTED_OUTPUT=true，表明格式化输出XML
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        // 在这里指定JAXB_ENCODING=UTF-8，表明使用UTF-8字符编码
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        // 在这里指定JAXB_FRAGMENT=true，表明结果不再声明XML头信息
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        marshaller.marshal(obj, writer);
        return org.apache.commons.lang.StringUtils.replace(writer.toString(), "&quot;", "'");
    }

    /**
     * JAXB实现xml转bean
     *
     * @param xml
     * @param t
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T xmlToBean(String xml, Class<T> t) throws Exception {
        JAXBContext context;
        context = JAXBContext.newInstance(t);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (T) unmarshaller.unmarshal(new StringReader(xml));
    }
}