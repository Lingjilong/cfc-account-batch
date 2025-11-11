package cn.com.njcb.utils;

import java.security.SecureRandom;
import java.util.Map;
import cn.com.njcb.annotation.TypeAnnotation;

/**
 * 算法随机数
 *
 * @author Uncle chang
 * @time 2020年6月2日下午8:07:33
 * @copyright NJCB
 */
@TypeAnnotation("@description:算法随机数 @author:ji.ye @time:2020/6/2 19:17:10")
public class RandomUtils {

    public static String chanceSelect(Map<String, Integer> keyChanceMap) {
        if (CollectionUtils.isEmpty(keyChanceMap)) return null;

        Integer sum = 0;
        for (Integer value : keyChanceMap.values()) {
            sum += value;
        }
        Integer rand = new SecureRandom().nextInt(sum) + 1;

        for (Map.Entry<String, Integer> entry : keyChanceMap.entrySet()) {
            rand -= entry.getValue();
            // 选中
            if (rand <= 0) {
                String item = entry.getKey();
                return item;
            }
        }
        return null;
    }

    //REQ1487 携程PT授信随机获取家庭月收入  随机取值范围为[1600，4000]（只取100的整数倍，如2100、3700等）
    public static String monthIncomeSelect() {
        int max = 40;
        int min = 16;
        SecureRandom rand = new SecureRandom();
        int randNumber = (rand.nextInt(max - min + 1) + min) * 100;
        return String.valueOf(randNumber);
    }
}
