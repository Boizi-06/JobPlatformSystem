package com.example.jobplatformsystem.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around(
            "execution(* com.example.jobplatformsystem.controller..*(..)) || " +
                    "execution(* com.example.jobplatformsystem.service..*(..))"
    )
    public Object logExecutionTime(
            ProceedingJoinPoint joinPoint)
            throws Throwable {

        long startTime = System.currentTimeMillis();

        String className =
                joinPoint.getTarget()
                        .getClass()
                        .getSimpleName();

        String methodName =
                joinPoint.getSignature()
                        .getName();

        try {

            Object result =
                    joinPoint.proceed();

            long executionTime =
                    System.currentTimeMillis()
                            - startTime;

            log.info(
                    "{}.{} executed in {} ms",
                    className,
                    methodName,
                    executionTime
            );

            return result;

        } catch (Exception e) {

            long executionTime =
                    System.currentTimeMillis()
                            - startTime;

            log.error(
                    "{}.{} failed after {} ms",
                    className,
                    methodName,
                    executionTime
            );

            throw e;
        }
    }
}