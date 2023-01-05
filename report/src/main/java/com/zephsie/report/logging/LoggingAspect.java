package com.zephsie.report.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

@Component
@Aspect
@Slf4j
public class LoggingAspect {

    @Pointcut("@annotation(Logging)")
    public void loggingMethods() {}

    @Async
    @Before("loggingMethods()")
    public void logMethodExecution(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Logging loggingAnnotation = method.getAnnotation(Logging.class);

        int userIdPosition = loggingAnnotation.userIdPosition();
        String type = loggingAnnotation.type();
        String description = loggingAnnotation.description();

        UUID userId = (UUID) joinPoint.getArgs()[userIdPosition];

        log.info("Executing method {} with userId {}, type {}, and description {}", method.getName(), userId, type, description);
    }
}