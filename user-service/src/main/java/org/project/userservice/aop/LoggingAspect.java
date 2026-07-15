package org.project.userservice.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @Pointcut("execution(public * org.project.userservice.service..*(..))")
    public void serviceLayerMethods() {
    }

    @Before("serviceLayerMethods()")
    public void logBefore(JoinPoint joinPoint) {
        var methodName = getMethodName(joinPoint);
        String arguments = Arrays.deepToString(joinPoint.getArgs());

        log.info("[SERVICE] Calling {} with args: {}", methodName, arguments);
    }

    @AfterReturning(
            pointcut = "serviceLayerMethods()",
            returning = "result"
    )
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        var methodName = getMethodName(joinPoint);

        log.info("[SERVICE] {} returned: {}", methodName, result);
    }

    @AfterThrowing(
            pointcut = "serviceLayerMethods()",
            throwing = "exception"
    )
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        var methodName = getMethodName(joinPoint);

        log.error(
                "[SERVICE] {} threw exception: {} - {}",
                methodName,
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                exception
        );
    }

    private String getMethodName(JoinPoint joinPoint) {
        var signature = (MethodSignature) joinPoint.getSignature();

        return signature.getDeclaringType().getSimpleName() + "." + signature.getName();
    }
}
