package com.hu.oneclick.common.aspect;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author qingyang
 */
@Component
@Aspect


public class AspectPage {


    @Around("@annotation(com.hu.oneclick.model.annotation.Page)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes().getRequest();
        String pageNum = request.getParameter("pageNum");
        String pageSize = request.getParameter("pageSize");

        PageHelper.startPage(StringUtils.isNotEmpty(pageNum) ? Integer.parseInt(pageNum) : 1,
                StringUtils.isNotEmpty(pageSize) ? Integer.parseInt(pageSize) : 10);
        return joinPoint.proceed();
    }




}
}
}
