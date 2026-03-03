package com.lzh.aspect;

import com.lzh.pojo.Result;
import com.lzh.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Flux;

@Slf4j
@Component // 修复1：取消注释，让拦截器生效
@Aspect
public class AuthAspect {

    @Around("execution(* com.lzh.controller.*.*(..))")
    public Object checkToken(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String url = request.getRequestURL().toString();
        log.info("【AOP拦截】收到请求：{}", url);

        if (url.contains("/login") || url.contains("/register")) {
            log.info("【AOP放行】{} 是登录接口，不需要 Token", url);
            return joinPoint.proceed();
        }

        String token = request.getHeader("token");

        if (!StringUtils.hasLength(token)) {
            log.warn("【AOP拦截】没有出示 Token");
            return handleAuthFailure(joinPoint);
        }

        try {
            JwtUtils.parseJwt(token);

            log.info("【AOP放行】Token 验证成功");

            // 大手一挥：放行！执行 Controller 里的方法，并把结果拿回来
            Object result = joinPoint.proceed();

            // 再把结果原封不动地返回给前端
            return result;

        } catch (Exception e) {
            log.warn("【AOP拦截】Token 伪造或已过期");
            return handleAuthFailure(joinPoint);
        }
    }

    /**
     * 修复2：根据目标接口的方法返回值类型，动态决定返回 Result 还是 Flux
     */
    private Object handleAuthFailure(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> returnType = signature.getReturnType();

        if (returnType == Flux.class) {
            // 如果是流式接口，返回携带错误提示的 Flux 对象
            return Flux.just("NOT_LOGIN");
        }

        // 普通接口返回统一的 Result 对象
        return Result.error("NOT_LOGIN");
    }
}