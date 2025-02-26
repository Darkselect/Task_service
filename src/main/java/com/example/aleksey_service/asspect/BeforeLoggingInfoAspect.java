package com.example.aleksey_service.asspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class BeforeLoggingInfoAspect {
    @Before("@annotation(com.example.aleksey_service.asspect.annotation.BeforeLoggingAspect)")
    public void before(JoinPoint joinPoint) {
        log.info("Before starting method {}, with params {} ", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }
}
