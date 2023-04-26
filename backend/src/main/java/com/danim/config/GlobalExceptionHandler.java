package com.danim.config;

import com.danim.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice

/*
 *@ControllerAdvice와 @ResponseBody를 합쳐놓은 어노테이션이다. @ControllerAdvice와 동일한 역할을 수행하고, 추가적으로 @ResponseBody를 통해 객체를 리턴할 수도 있다.
 *따라서 단순히 예외만 처리하고 싶다면 @ControllerAdvice를 적용하면 되고, 응답으로 객체를 리턴해야 한다면 @RestControllerAdvice를 적용하면 된다.
 * @RestControllerAdvice(basePackageClasses="~~.class")
 * @RestControllerAdvice(basePackages="~~.class")
 *와 같이 적용범위를 클래스나 패키지 단위로 제한을 할수가 있다
 * */
public class GlobalExceptionHandler {

    // private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> baseHandler(BaseException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("msg", e.getErrorMessage());
        return new ResponseEntity<Object>(result, e.getHttpStatus());
    }


    @NoArgsConstructor
    @AllArgsConstructor
    static class Error {

        private HttpStatus status;
        private String message;


        static Error create(BaseException exception) {
            return new Error(exception.getHttpStatus(), exception.getErrorMessage());
        }


        public HttpStatus getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }


    }


}
