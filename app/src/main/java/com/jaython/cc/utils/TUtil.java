package com.jaython.cc.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * time:2016/6/21
 * description:泛型工具类
 *
 * @author sunjianfei
 */
public class TUtil {

    public static <T> T getT(Object o, int i) {
        try {
            Type genType = o.getClass().getGenericSuperclass();
            if (!(genType instanceof ParameterizedType)) {
                return null;
            }
            //返回表示此类型实际类型参数的 Type 对象的数组。
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            if (i >= params.length || i < 0) {
                return null;
            }
            if (!(params[i] instanceof Class)) {
                return null;
            }
            Class<T> clzz = (Class<T>) params[i];
            return clzz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
