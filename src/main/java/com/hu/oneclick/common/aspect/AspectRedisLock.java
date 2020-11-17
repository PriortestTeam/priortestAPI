//package com.hu.oneclick.common.aspect;
//
//import com.hu.oneclick.model.annotation.Resubmit;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.redisson.api.RBucket;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import java.lang.reflect.Method;
//import java.util.Objects;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author qingyang
// */
//@Component
//@Aspect
//public class AspectRedisLock {
//
//    private static final Logger logger = LoggerFactory.getLogger("AspectRedisLock");
//
//    /**
//     * 锁超时时间
//     */
//
//    @Resource
//    RedissonClient redis;
//    /**
//     * 配置环绕通知,使用在方法aspect()上注册的切入点
//     *
//     * @param joinPoint
//     */
//
//    @Around("@annotation(com.hu.oneclick.model.annotation.Resubmit)")
//    public Object around(ProceedingJoinPoint joinPoint) {
//        String appTokenKey = getAppTokenKey();
//        RLock lock = redis.getLock("Lock-" + appTokenKey);
//        RBucket<String> bucket = redis.getBucket("Resubmit-" + appTokenKey + "-"+ joinPoint);
//        try {
//            //加锁
//            int LOCK_EXPIRE_SECONDS = 60;
//            lock.lock(LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);
//            //防重复提交
//            if (bucket.get() == null) {
//                bucket.set("resubmit");
//                bucket.expire(getSeconds(joinPoint), TimeUnit.SECONDS);
//                //放行
//                return joinPoint.proceed();
//            }
//            //响应重复提交异常
//            return ResponseDtoBuilder.buildErrorMsg(SysRetCodeConstants.REDIS_NON_REPEATABLE_SUBMISSION.getCode(),
//                    SysRetCodeConstants.REDIS_NON_REPEATABLE_SUBMISSION.getMessage());
//        } catch (Throwable throwable) {
//            logger.error(throwable.getMessage());
//            return ResponseDtoBuilder.buildErrorMsg(SysRetCodeConstants.SYSTEM_ERROR.getCode(),
//                    SysRetCodeConstants.SYSTEM_ERROR.getMessage());
//        } finally {
//            //释放锁
//            lock.unlock();
//        }
//    }
//
//    /**
//     * 获取 accessToken 的值
//     * @return
//     */
//    private String getAppTokenKey(){
//        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
//        return request.getHeader("accessToken");
//    }
//
//    /**
//     * 获取注解上的超时时间
//     * @param joinPoint
//     * @return
//     */
//    private int getSeconds(ProceedingJoinPoint joinPoint){
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        Method method = signature.getMethod();
//        Resubmit annotation = method.getAnnotation(Resubmit.class);
//        return annotation.delaySeconds();
//    }
//
//
//}
