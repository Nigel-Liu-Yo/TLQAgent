package com.agree.tlqAgent.netty.server.request.messageExecutorHelper;

import com.agree.tlqAgent.annotations.RequestMessageExecutor;
import com.agree.tlqAgent.message.MessageConverter;
import com.agree.tlqAgent.netty.server.request.constants.RequestMessageTypes;
import com.agree.tlqAgent.netty.server.request.messageExecutor.IRequestMessageExecutor;
import com.agree.tlqAgent.utils.ObjectBuilder;
import com.agree.tlqAgent.utils.PackageScanner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RequestMessageStrategy {
    static volatile Map<String, Class> executorsMapper;
    static final String EXECUTOR_PACKAGE_NAME = "com.agree.tlqAgent.netty.server.request.messageExecutor";

    public IRequestMessageExecutor getExecutor(String msg) {
        getExecutorsMapper();
        Class clazz = new MessageConverter(msg).supportsXML() ? executorsMapper.get(RequestMessageTypes.DEFAULT) : executorsMapper.get(msg);
        return (IRequestMessageExecutor) (new ObjectBuilder(clazz).build());
    }

    private void getExecutorsMapper() {
        if (executorsMapper == null) {
            synchronized (this) {
                if (executorsMapper == null) {
                    executorsMapper = new HashMap<>();
                    Set<Class> allClasses = PackageScanner.scanPackageByAnnotation(EXECUTOR_PACKAGE_NAME, RequestMessageExecutor.class);
                    allClasses.forEach(clazz -> {
                        RequestMessageExecutor requestMessageExecutorAnno = (RequestMessageExecutor) clazz.getAnnotation(RequestMessageExecutor.class);
                        executorsMapper.put(requestMessageExecutorAnno.value(), clazz);
                    });
                }
            }
        }
    }

}
