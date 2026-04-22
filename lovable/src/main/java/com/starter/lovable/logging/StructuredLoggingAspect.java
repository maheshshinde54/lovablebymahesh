package com.starter.lovable.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class StructuredLoggingAspect {
    @Pointcut("within(com.starter.lovable.controller..*) || within(com.starter.lovable.service..*)")
    public void applicationLayer() {
    }

    @Around("execution(public * *(..)) && applicationLayer()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        MDC.put("event", "method.invocation");
        MDC.put("step", "entry");

        log.info("Entering {}", method);
        if (log.isDebugEnabled()) {
            log.debug("Entering {} with args={}", method, Arrays.toString(joinPoint.getArgs()));
        }

        try {
            Object result = joinPoint.proceed();
            MDC.put("step", "exit");
            log.info("Exiting {}", method);
            if (log.isDebugEnabled()) {
                log.debug("Exiting {} result={}", method, result);
            }
            return result;
        } catch (Throwable throwable) {
            MDC.put("step", "exception");
            log.warn("Exception in {}: {}", method, throwable.toString());
            throw throwable;
        } finally {
            MDC.remove("step");
            MDC.remove("event");
        }
    }
}
