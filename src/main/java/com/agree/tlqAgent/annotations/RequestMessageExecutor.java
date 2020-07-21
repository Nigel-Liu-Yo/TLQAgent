package com.agree.tlqAgent.annotations;

import com.agree.tlqAgent.netty.server.request.constants.RequestMessageTypes;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMessageExecutor {
    String value() default RequestMessageTypes.DEFAULT;
}
