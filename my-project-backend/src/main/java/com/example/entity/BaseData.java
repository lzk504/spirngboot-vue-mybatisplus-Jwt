package com.example.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;

public interface BaseData {
    Logger logger = LoggerFactory.getLogger(BaseData.class);

    /**
     *
     * @param clazz 指定VO类型
     * @param consumer 指定参数
     * @return 指定VO对象
     * @param <V> 指定VO类型
     */
    default <V> V asViewObject(Class<V> clazz, Consumer<V> consumer){
        V v = this.asViewObject(clazz);
        consumer.accept(v);
        return v;
    }


    /**
     * 创建指定的VO类并将当前DTO对象中的所有成员变量值直接复制到VO对象中
     *
     * @param clazz 指定VO类型
     * @param <V>   指定VO类型
     * @return 指定VO对象
     */
    default <V> V asViewObject(Class<V> clazz) {
        try {
            //获取需要转换的对象的全部字段
            Field[] declaredFields = clazz.getDeclaredFields();
            //获取构造器
            Constructor<V> constructor = clazz.getConstructor();
            V v = constructor.newInstance();
            Arrays.asList(declaredFields).forEach(field -> copy(field, v));
            return v;
        } catch (ReflectiveOperationException e) {
            logger.error("在VO与DTO转换时出现了一些错误", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 内部使用，快速将当前类中目标对象字段同名字段的值复制到目标对象字段上
     * @param field 目标对象字段
     * @param vo 目标对象
     */
    private void copy(Field field, Object vo) {
        try {
            Field source = this.getClass().getDeclaredField(field.getName());
            field.setAccessible(true);
            source.setAccessible(true);
            field.set(vo,source.get(this));
        } catch (IllegalAccessException | NoSuchFieldException e) {
        }
    }
}
