package com.agree.tlqAgent.utils;

import com.agree.tlqAgent.annotations.RequestMessageExecutor;
import org.junit.Test;
import org.reflections.Reflections;

import java.util.Set;

public class PackageScanner {

    public  static Set scanPackageByAnnotation(String packageName, Class annotationClass){
        Reflections reflections = new Reflections(packageName);
        return reflections.getTypesAnnotatedWith(annotationClass);
    }

    @Test
    public void test(){
        PackageScanner packageScanner = new PackageScanner();
        packageScanner.scanPackageByAnnotation("com.agree.tlqAgent.netty.server.request.messageExecutor", RequestMessageExecutor.class);
    }

}
