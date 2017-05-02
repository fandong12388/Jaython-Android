package com.jaython.cc.utils;

import java.text.DecimalFormat;

/**
 * time:2017/2/11
 * description:
 *
 * @author fandong
 */
public class NumberUtil {

    public static String getFormatNumber(int number) {
        if (number <= 999) {
            return "" + number;
        } else {
            DecimalFormat format = new DecimalFormat("####.##");
            return format.format(number / 10000.00) + "万";
        }
    }
}
