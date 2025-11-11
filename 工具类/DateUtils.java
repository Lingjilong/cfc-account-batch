package cn.com.njcb.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import lombok.extern.log4j.Log4j;
import cn.com.njcb.annotation.MethodAnnotation;
import cn.com.njcb.dto.DateDto;
import cn.com.njcb.dto.LogInfoDto;

/**
 * 日期工具类
 *
 * @author 王彬
 * @date 2018年8月21日
 */
@Log4j
public class DateUtils {
    public static final String YMDHMS = "yyyyMMddHHmmss";
    public static final String YMDHMSS = "yyyyMMddHHmmssSSS";
    public static final String YMD_SPACE_HMS = "yyyyMMdd HHmmss";
    public static final String YMD_LINE_SPACE_HMS_COLON = "yyyy-MM-dd HH:mm:ss";
    public static final String YMD_SPACE_HMS_COLON = "yyyyMMdd HH:mm:ss";
    public static final String YMD = "yyyyMMdd";
    public static final String YMD_SLASH = "yyyy/MM/dd";
    public static final String YMD_LINE = "yyyy-MM-dd";
    public static final String YMD_DOT = "yyyy.MM.dd";
    public static final String DAY_YMD_SPACER = "yyyy MM dd";
    public static final String YM = "yyyyMM";
    public static final String HMS = "HHmmss";
    public static final String HM = "HHmm";
    public static final String HMS_COLON = "HH:mm:ss";
    public static final String HMSS = "HHmmssSSS";
    public static final String HMSS_COLON = "HH:mm:ss:SSS";
    public static final String YYY = "yyy";
    public static final String YEAR = "yyyy";
    public static final String MONTH = "MM";
    public static final String DAY = "dd";
    public static final String HOUR = "HH";
    public static final String MINUTE = "mm";
    public static final String SECOND = "ss";
    public static final String CONTROL_LOAN_TIME_LEFT_ONE = "23:30";
    public static final String CONTROL_LOAN_TIME_RIGHT_ONE = "24:30";
    public static final String CONTROL_LOAN_TIME_LEFT_TWO = "23:54";
    public static final String CONTROL_LOAN_TIME_RIGHT_TWO = "24:00";
    public static final String CONTROL_LOAN_TIME_RIGHT_THREE = "01:00";
    public static final String CONTROL_LOAN_TIME_RIGHT_FOUR = "00:10";
    public static final String CONTROL_LOAN_TIME_LEFT_THREE = "23:00";
    public static final String CONTROL_LOAN_TIME_AM_TWO = "02:00";
    public static final String CONTROL_REPAY_TIME_LEFT_ONE = "23:20";
    public static final String CONTROL_REPAY_TIME_RIGHT_ONE = "24:00";
    public static final String YMD_CHARACTERS = "yyyy年MM月dd日";
    public static final String HHMM = "HHmm";
    public static final String HHMM_CHARACTERS = "HH点mm分";
    public static final String YMD_LINE_SPACE_HMS_COLON_CHARACTERS = "yyyy年MM月dd日 HH时mm分ss秒";

    /**
     * 根据指定格式将日期转字符串
     *
     * @param date    日期
     * @param pattern 格式
     * @return 指定格式的字符串类型日期
     */
    public static String dateToStr(Date date, String pattern) {
        return dateToStr(0, date, pattern);
    }

    /**
     * 根据指定格式将日期转字符串
     *
     * @param token   唯一标识
     * @param date    日期
     * @param pattern 格式
     * @return 指定格式的字符串类型日期
     */
    public static String dateToStr(long token, Date date, String pattern) {
        String dateStr = null;
        try {
            if (date != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                dateStr = dateFormat.format(date);

                if (DateUtils.YYY.equals(pattern)) {
                    dateStr = dateStr.substring(1);
                }
            }
        } catch (Exception e) {
            LogUtils.error(log, token, "日期转字符串异常", e);
        }

        return dateStr;
    }

    /**
     * 根据指定格式将字符串转日期
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Date strToDate(String dateStr, String pattern) {
        return strToDate(0, dateStr, pattern);
    }

    /**
     * 根据指定格式将字符串转日期
     *
     * @param token
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Date strToDate(long token, String dateStr, String pattern) {
        Date date = null;

        try {
            if (!StringUtils.isEmpty(dateStr)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                date = dateFormat.parse(dateStr);
            }
        } catch (Exception e) {
            LogUtils.error(log, token, "字符串转日期异常,当前格式:" + pattern, e);
        }

        return date;
    }

    public static Date stringToDate(LogInfoDto logInfoDto, String dateStr, String pattern) {
        Date date = null;

        try {
            if (!StringUtils.isEmpty(dateStr)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                date = dateFormat.parse(dateStr);
            }
        } catch (Exception e) {
            LoggerUtils.error(log, logInfoDto, e, "字符串转日期异常,当前日期:" + dateStr + "，当前格式:" + pattern);
            // 特殊情况即使转换异常无影响大不了取其他时间，故此处需不打印error日志
        }

        return date;
    }

    /**
     * 格式化日期字符串
     *
     * @param token
     * @param dateStr
     * @param src_pattern
     * @param tag_pattern
     * @return
     */
    public static String dateStrFormat(long token, String dateStr, String src_pattern, String tag_pattern) {
        return dateToStr(strToDate(dateStr, src_pattern), tag_pattern);
    }

    /**
     * 获取指定分钟之后的的日期
     *
     * @param date
     * @param minutes
     * @return
     */
    public static Date getDateAfterMinutes(Date date, int minutes) {
        return getDateInterval(date, Calendar.MINUTE, minutes);
    }

    /**
     * 在指定日期上增加指定年份
     * @param date
     * @param year
     * @param pattern
     * @return
     */
    public static String addYear(Date date, int year, String pattern) {
    	 Calendar c = Calendar.getInstance();
         c.setTime(date);
         c.add(Calendar.YEAR, year);
         return dateToStr(c.getTime(), pattern);
    }
    
    /**
     * 获取指定分钟之前的的日期
     *
     * @param date
     * @param minutes
     * @return
     */
    public static Date getDateBeforeMinutes(Date date, int minutes) {
        return getDateInterval(date, Calendar.MINUTE, -minutes);
    }

    /**
     * 获取间隔时间，根据指定类型
     *
     * @param date
     * @param fieldType
     * @param minutes
     * @return
     */
    public static Date getDateInterval(Date date, int fieldType, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(fieldType, minutes);
        return calendar.getTime();
    }

    /**
     * 判断待检查日期是否在检查日期之前
     *
     * @param beCheckDate
     * @param checkDate
     * @return
     */
    public static boolean isBeforeTime(Date beCheckDate, Date checkDate) {
        return beCheckDate.before(checkDate);
    }

    /**
     * 判断待检查日期是否在检查日期之后
     *
     * @param beCheckDate
     * @param checkDate
     * @return
     */
    public static boolean isAfterTime(Date beCheckDate, Date checkDate) {
        return beCheckDate.after(checkDate);
    }

    /**
     * 校验时间戳超过指定分钟
     *
     * @param token
     * @param timeStamp
     * @param minute
     * @return
     */
    public static boolean checkTimeStamp(long token, String timeStamp, int minute) {
        long currentTime = new Date().getTime();

        long calcTime = (currentTime - Long.parseLong(timeStamp)) / (1000 * 60);
        return !(calcTime > minute);
    }

    /**
     * 获取指定时间与当前时间的时间间隔(秒)
     *
     * @param logInfoDto
     * @param date
     * @return
     */
    public static long getSeconds(LogInfoDto logInfoDto, Date date) {
        return (new Date().getTime() - date.getTime()) / 1000;
    }

    /**
     * 获取指定时间与当前时间的时间间隔(秒)
     *
     * @param logInfoDto
     * @param date
     * @return
     */
    public static long getMinutes(LogInfoDto logInfoDto, Date date) {
        return Math.abs((new Date().getTime() - date.getTime()) / 1000 / 60);
    }

    /**
     * 在指定日期上增加指定月份
     *
     * @param dateStr
     * @param month
     * @param pattern
     * @return
     */
    public static String addMonth(String dateStr, int month, String pattern) {
        Date date = strToDate(dateStr, pattern);
        return dateToStr(addMonth(date, month), pattern);
    }

    /**
     * 在指定日期上增加指定时间
     *
     * @param date
     * @param dateDto
     * @return
     */
    public static Date calcDate(Date date, DateDto dateDto) {
        if (null == date || null == dateDto) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        if (0 != dateDto.getYear()) {
            c.add(Calendar.YEAR, dateDto.getYear());
        }
        if (0 != dateDto.getMonth()) {
            c.add(Calendar.MONTH, dateDto.getMonth());
        }
        if (0 != dateDto.getDay()) {
            c.add(Calendar.DATE, dateDto.getDay());
        }
        if (0 != dateDto.getHour()) {
            c.add(Calendar.HOUR_OF_DAY, dateDto.getHour());
        }
        if (0 != dateDto.getMinute()) {
            c.add(Calendar.MINUTE, dateDto.getMinute());
        }
        if (0 != dateDto.getSecond()) {
            c.add(Calendar.SECOND, dateDto.getSecond());
        }
        return c.getTime();
    }

    /**
     * 在指定日期上增加指定时间
     *
     * @param date
     * @param dateDto
     * @return
     */
    public static String calcDate(String date, DateDto dateDto) {
        Calendar c = Calendar.getInstance();
        c.setTime(strToDate(date, YMD_LINE_SPACE_HMS_COLON));
        if (0 != dateDto.getYear()) {
            c.add(Calendar.YEAR, dateDto.getYear());
        }
        if (0 != dateDto.getMonth()) {
            c.add(Calendar.MONTH, dateDto.getMonth());
        }
        if (0 != dateDto.getDay()) {
            c.add(Calendar.DATE, dateDto.getDay());
        }
        if (0 != dateDto.getHour()) {
            c.add(Calendar.HOUR_OF_DAY, dateDto.getHour());
        }
        if (0 != dateDto.getMinute()) {
            c.add(Calendar.MINUTE, dateDto.getMinute());
        }
        if (0 != dateDto.getSecond()) {
            c.add(Calendar.SECOND, dateDto.getSecond());
        }
        return dateToStr(c.getTime(), YMD_LINE_SPACE_HMS_COLON);
    }

    /**
     * 在指定日期上增加指定月份
     *
     * @param date
     * @param month
     * @return
     */
    public static Date addMonth(Date date, int month) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, month);

        return c.getTime();
    }

    /**
     * 设置天
     *
     * @param date
     * @param day
     * @return
     */
    public static Date setDay(Date date, int day) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, day);

        return c.getTime();
    }

    /**
     * 获取指定天数间隔的日期
     *
     * @param date
     * @param i
     * @return
     */
    public static Date getNextDay(Date date, int i) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(Calendar.DATE, i);

        return gc.getTime();
    }

    /**
     * 获取下一小时
     *
     * @param date
     * @param i
     * @return
     */
    public static Date getNextHour(Date date, int i) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(Calendar.HOUR, i);
        return gc.getTime();
    }

    /**
     * 获取当前天
     *
     * @param date
     * @return
     */
    public static int getDaysOfMonth(Date date) {
        return getSomeValueOfDay(date, Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前小时
     *
     * @param date
     * @return
     */
    public static int getHoursOfDay(Date date) {
        return getSomeValueOfDay(date, Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前分钟
     *
     * @param date
     * @return
     */
    public static int getMinutes(Date date) {
        return getSomeValueOfDay(date, Calendar.MINUTE);
    }

    /**
     * 获取指定类型的值
     *
     * @param date
     * @param valueType
     * @return
     */
    public static int getSomeValueOfDay(Date date, int valueType) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        return c.get(valueType);
    }

    /**
     * 按指定格式输出
     *
     * @param token
     * @param date
     * @param pattern
     * @return
     */
    public static Date formatDate(long token, Date date, String pattern) {
        String dateStr = dateToStr(token, date, pattern);
        return strToDate(token, dateStr, pattern);
    }

    /**
     * 下个月
     *
     * @param date
     * @param i
     * @return
     * @auth zsp
     * @date 2019年12月9日 下午2:18:06
     */
    public static Date getNextMonth(Date date, int i) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(Calendar.MONTH, i);

        return gc.getTime();
    }

    /**
     * 控制放款时间段，true为需要控制，false为不需要
     *
     * @param token
     * @param date
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean controlTime(Logger logger, long token, Date date, String beginTime, String endTime) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        Date now = null;
        Date beginTimes = null;
        Date endTimes = null;
        try {
            now = df.parse(df.format(date));
            beginTimes = df.parse(beginTime);
            endTimes = df.parse(endTime);
        } catch (Exception e) {
            LogUtils.error(logger, token, e);
        }
        boolean isControl = now.getTime() >= beginTimes.getTime() && now.getTime() <= endTimes.getTime();
        LogUtils.info(logger, token, "controlTime result is " + isControl);
        return isControl;
    }

    /**
     * 是否处于限制时间内
     * 异常均返回true，控制住
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean betweenLimitTime(String beginTime, String endTime) {
        if (StringUtils.isEmpty(beginTime) || StringUtils.isEmpty(endTime)) {
            return true;
        }
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Date now;
        Date beginTimes;
        Date endTimes;
        try {
            now = df.parse(df.format(new Date()));
            beginTimes = df.parse(beginTime);
            endTimes = df.parse(endTime);
        } catch (Exception e) {
            return true;
        }
        return now.getTime() >= beginTimes.getTime() && now.getTime() <= endTimes.getTime();
    }

    public static String addMonth(String yyyyMM, int month) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Date date = sdf.parse(yyyyMM);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, month);
        return sdf.format(c.getTime());
    }

    public static String addDay(String yyyyMMdd, int day) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = sdf.parse(yyyyMMdd);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, day);
        return sdf.format(c.getTime());
    }

    public static String addLineDay(String lineDayStr, int day) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(lineDayStr);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, day);
        return sdf.format(c.getTime());
    }

    //两个日期相差多少天
    public static int getBetweenDays(String date1, String date2) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sdf.parse(date1));
        long time1 = calendar.getTimeInMillis();
        calendar.setTime(sdf.parse(date2));
        long time2 = calendar.getTimeInMillis();
        long betweenTime = (time2 - time1) / (3600 * 24 * 1000);
        return Math.abs(Integer.parseInt(String.valueOf(betweenTime)));
    }

    /**
     * @param time 起息日期time和贷款期限term
     * @return 到期日期 String(yyyyMMDD)
     */
    public static String addTermTime(Date time, String term) {
        SimpleDateFormat sd = new SimpleDateFormat(DateUtils.YMD);
        String strTime = sd.format(time);
        if (Integer.valueOf(term) == 0)
            return strTime;
        Calendar cal = Calendar.getInstance();
        try {
            // 设置初始时间time格式必须是yyyyMMdd
            cal.setTime(sd.parse(strTime));
        } catch (ParseException e) {
            log.error("转换日期异常" + e.getMessage());
        }
        cal.add(Calendar.MONTH, Integer.valueOf(term)); // 日期加term个月
        Date newDate = cal.getTime();
        return sd.format(newDate);
    }

    /**
     * 判断格式是否为YYYY-MM-DD
     * @Author: Uncle chang
     * @Date： 2020-07-06 16:00:48
     */
    public static boolean isYMDLINE(Date date) {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.YMD_LINE);
            sdf.format(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * 判断格式是否为自定义
     * @Author: Uncle chang
     * @Date： 2020-07-06 16:00:48
     */
    public static boolean isDefinePattern(String date, String datePattern) {
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
            sdf.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据某个日期和生日精准计算年龄
     * 若未指定某个日期则取当前时间
     * @Author: Uncle chang
     * @Date： 2020-07-27 18:51:02
     */
    public static int calcAgeByDate(Date birthDay, Date someDay) {
        Calendar calendar = Calendar.getInstance();
        if(birthDay == null || calendar.before(birthDay)) {
            return 0;
        }
        if(someDay != null) calendar.setTime(someDay);
        int yearNow = calendar.get(Calendar.YEAR);
        int monthNow = calendar.get(Calendar.MONTH);
        int dayOfMonthNow = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTime(birthDay);
        int yearBirth = calendar.get(Calendar.YEAR);
        int monthBirth = calendar.get(Calendar.MONTH);
        int dayOfMonthBirth = calendar.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;
        if(monthNow <= monthBirth) {
            if(monthNow == monthBirth) {
                if(dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }
        return age <= 0 ? 0 : age;
    }

    /**
     * 日期相加(单位：年)
     * @Author: Uncle chang
     * @Date： 2020-07-27 18:51:02
     */
    public static Date calcDateByYear(Date date, int num) {
        if(date == null) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, num);
        return calendar.getTime();
    }

    /**
     * 控制放款时间段，true为需要控制，false为不需要, 跨天时间的判断
     * @param token
     * @param date
     * @return
     */
    public static boolean controlTimeAcross(Logger logger, long token, Date date, String beginTime, String endTime ){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        Date now =null;
        Date beginTimes = null;
        Date endTimes = null;
        try {
            now = df.parse(df.format(date));
            beginTimes = df.parse(beginTime);
            endTimes = df.parse(endTime);
        } catch (Exception e) {
            LogUtils.error(logger, token, e);
        }
        boolean isControl = false;
        if(beginTimes.after(endTimes) || beginTimes.equals(endTimes)){
            isControl = now.getTime() >= beginTimes.getTime() || now.getTime() <= endTimes.getTime();
        }else{
            isControl = now.getTime() >= beginTimes.getTime() && now.getTime() <= endTimes.getTime();
        }
        LogUtils.info(logger, token, "controlTime result is " +isControl);
        return isControl;
    }

    /**
     * 得到固定时间
     * @param date
     * @param hours
     * @param minutes
     * @param seconds
     * @return
     */
    public static Date getSpecfiedDate(Date date, int hours, int minutes, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, seconds);
        return calendar.getTime();
    }

    /**
     * 在指定日期上增加指定分钟
     *
     * @param date
     * @param minute
     * @return
     */
    public static Date addMinute(Date date, int minute) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, minute);

        return c.getTime();
    }

    /**
     * 日期相加
     * @param date
     * @param day
     * @return
     */
    public static Date addDay(Date date, int day) {
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, day);
            return c.getTime();
        } catch (Exception e) {
        }
        return null;
    }

    @MethodAnnotation("判断日期天数是否正确")
    public static boolean isDay(String day) {
        boolean returnValue = false;
        try {
            if (!StringUtils.isEmpty(day)) {
                returnValue = Pattern.compile("([0-2][1-9])|10|20|30|31").matcher(day).matches();
            }
        } catch (Exception e) {
        }
        return returnValue;
    }

    /**
     * 设置到期日期
     * @param time
     * @param term
     * @param dd
     * @return
     */
    public static String addTermTimeDay(String time, int term, String dd) {
        if(term == 0) {
            return time;
        }
        time = time.substring(0, 6).concat(dd);
        Calendar cal = Calendar.getInstance();
        // 设置初始时间time格式必须是yyyyMMdd
        cal.setTime(strToDate(time, YMD));
        // 日期加term个月
        cal.add(Calendar.MONTH, term);
        return dateToStr(cal.getTime(), YMD);
    }

    /**
     * 计算天数之差
     * 为精确计算时间之差，则要求左边时间一定要大于右边时间这里不取绝对值
     * @param leftDay
     * @param rightDay
     * @return
     */
    public static long calcDifferenceOfDay(String leftDay, String rightDay, String pattern) {
        Date leftDate = strToDate(leftDay, pattern);
        Date rightDate = strToDate(rightDay, pattern);
        return (leftDate.getTime() - rightDate.getTime()) / (1000 * 3600 * 24);
    }
    public static long calcDifferenceOfDay(Date leftDate, Date rightDate) {
        return (leftDate.getTime() - rightDate.getTime()) / (1000 * 3600 * 24);
    }

    /**
     * 获取身份证有效区间
     *
     * @param logInfoDto
     * @param left
     * @param right
     * @param pattern
     * @param interval
     * @return
     */
    public static String getValidRangeOfIDCard(LogInfoDto logInfoDto, Date left, Date right, String pattern, String interval) {
        String result = null;
        if (StringUtils.isAllNotEmpty(pattern, interval)) {
            String leftDate = dateToStr(0, left, pattern);
            String rightDate = dateToStr(0, right, pattern);
            LoggerUtils.info(logInfoDto, "当前传入发证日[" + leftDate + "],到期日[" + rightDate + "],日期格式[" + pattern + "],分隔符[" + interval + "]");
            if (dateToStr(strToDate("9999-12-31", YMD_LINE), pattern).equals(rightDate)) {
                rightDate = "长期";
            }
            result = leftDate + interval + rightDate;
            LoggerUtils.info(logInfoDto, "经转换后的值[" + result + "]");
            return result;
        }
        return result;
    }

    /**
     * 获取指定时间的格式
     * @param date
     * @return
     */
    public static Date getDateStart(Date date) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 自定义事件
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Date getDate(int year, int month, int day) {
        GregorianCalendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);
        return c.getTime();
    }
    
	/**
	 * 
	 * @return
	 */
	public static Date getCurrentDate() {
		GregorianCalendar c = new GregorianCalendar();
		return c.getTime();
	}
}