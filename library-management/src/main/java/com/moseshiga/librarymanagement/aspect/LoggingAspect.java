package com.moseshiga.librarymanagement.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    @Around(value = "execution(* com.moseshiga.librarymanagement.service.*.*(..))")
    public Object logPerformanceAndExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        log.debug("Starting execution of method: {}", methodName);

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            log.error("Exception in method {}: {}", methodName, ex.getMessage());
            throw ex;
        }

        long timeTaken = System.currentTimeMillis() - startTime;
        log.info("Finished execution of {}. Time taken: {} ms", methodName, timeTaken);

        return result;
    }
}
