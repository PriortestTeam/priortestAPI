package com.hu.oneclick.common.advice;
import com.hu.oneclick.model.base.Resp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
/**
 * 全局的http状态码，从Resp里面读取http状态码，设置为response状态码；
 */
@ControllerAdvice
@Slf4j

public class GlobalControllerAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest request, ServerHttpResponse response) {
        try {
            int httpStatus = 0;
            if (body instanceof Resp) {
                Resp<?> resp = (Resp<?>) body;
                httpStatus = resp.getHttpCode();
            }
            if (httpStatus > 0) {
                response.setStatusCode(HttpStatus.valueOf(httpStatus);
            }
        } catch (Exception e) {
            log.warn("global add http status failed, message: {}", e.getMessage();
        }
        return body;
    }
}
}
}
