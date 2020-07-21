package com.agree.tlqAgent.utils;

import java.lang.reflect.Constructor;

public class ObjectBuilder {
    private Class clazz;

    public ObjectBuilder(Class clazz){
        this.clazz = clazz;
    }

    public Object build(){
        try {
            Constructor constructor=clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return  constructor.newInstance();
        } catch (Exception e) {
        }
        return null;
    }
}
