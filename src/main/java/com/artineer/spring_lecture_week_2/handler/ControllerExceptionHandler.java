package com.artineer.spring_lecture_week_2.handler;

import com.artineer.spring_lecture_week_2.dto.Response;
import com.artineer.spring_lecture_week_2.exception.ApiException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//이 객체는 controller 다음에도 이 객체의 내용이 호출된다
@RestControllerAdvice
public class ControllerExceptionHandler {
    //예외를 처리하는 것을 핸들링
    @ExceptionHandler(ApiException.class)
    public Response<String> apiException(ApiException e) {
        return Response.<String>builder().code(e.getCode()).data(e.getMessage()).build();
    }
}
