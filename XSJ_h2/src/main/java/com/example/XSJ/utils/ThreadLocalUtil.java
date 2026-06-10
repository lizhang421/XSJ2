package com.example.XSJ.utils;

/**
 * ThreadLocal 工具类
 */
@SuppressWarnings("all")
public class ThreadLocalUtil {
    //用来存取get（）set（）数据
    //适用threadlocalUtil存储数据线程安全
    //开辟线程维护线程安全
    //提供ThreadLocal对象,
    private static final ThreadLocal THREAD_LOCAL = new  ThreadLocal();

    //根据键获取值
    public static <T> T get(){
        return (T) THREAD_LOCAL.get();
    }
	
    //存储键值对
    public static void set(Object value){
        THREAD_LOCAL.set(value);
    }
    //清除ThreadLocal 防止内存泄漏
    //解决内存泄漏的问题
    public static void remove(){
        THREAD_LOCAL.remove();
    }
}
