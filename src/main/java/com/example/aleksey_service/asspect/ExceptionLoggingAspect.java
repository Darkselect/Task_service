package com.example.aleksey_service.asspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExceptionLoggingAspect {
    @AfterThrowing(pointcut = "@annotation(com.example.aleksey_service.asspect.annotation.ExceptionLogging)", throwing = "ex")
    public void logException(JoinPoint joinPoint, Exception ex) {
        log.error("Exception in method {} with message: {}", joinPoint.getSignature().getName(), ex.getMessage(), ex);
    }
}
