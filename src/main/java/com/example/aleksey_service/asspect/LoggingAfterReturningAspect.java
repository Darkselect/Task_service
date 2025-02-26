package com.example.aleksey_service.asspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAfterReturningAspect {
    @AfterReturning(pointcut = "@annotation(com.example.aleksey_service.asspect.annotation.AfterReturningLogging)", returning = "result")
    public void loggingAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Method {} successfully result: {} ", joinPoint.getSignature().getName(), result.toString());
    }
}
