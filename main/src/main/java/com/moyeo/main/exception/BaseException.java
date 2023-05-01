package com.moyeo.main.exception;

import org.springframework.http.HttpStatus;


//에러 코드와 메시지를 같이 넘겨 주기 위해 선언이 된 class이다
public class BaseException extends RuntimeException {
    //private int errorCode;
    private String errorMessage;
    private HttpStatus httpStatus;


    public BaseException(ErrorMessage errorMessage) {
        System.out.println(errorMessage);
        this.errorMessage = errorMessage.getErrMsg();
        this.httpStatus = errorMessage.getHttpStatus();

        System.out.println(errorMessage);
        System.out.println(httpStatus);
    }



    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }


}