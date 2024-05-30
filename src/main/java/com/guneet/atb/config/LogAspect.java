package com.guneet.atb.config;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.function.Consumer;

@Aspect
@Slf4j
@Component
@ConditionalOnProperty(prefix = "aspect", name = "execution-time", havingValue = "true")
public class LogAspect {
    @Pointcut("within(com.guneet.atb.service..*)")
    public void serviceCalls() {
        //to track service layer method calls
    }

    @Pointcut("within(com.guneet.atb.controller..*)")
    public void controllerCalls() {
        //to track controller layer method calls
    }

    @SneakyThrows
    @Around(value = "controllerCalls()")
    public Object logAround(ProceedingJoinPoint joinPoint) {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        if (result instanceof Mono)
            return ((Mono) result).doOnSuccess(getConsumer(joinPoint, start));
        return result;
    }

    public Consumer getConsumer(ProceedingJoinPoint joinPoint, long start) {
        Signature pjpSign = joinPoint.getSignature();
        return o -> {
            long end = System.currentTimeMillis();
            log.info("Execution of {} in {} took {} ms", pjpSign.getName(), pjpSign.getDeclaringType().getSimpleName(), end - start);
            log.debug("Args: {}", Arrays.toString(joinPoint.getArgs()));
        };
    }
}

