package com.moyeo.main.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


//사용할 ErrorMessage를 정의하는곳, 개발자가 정의하여 추가해면 됨
@Getter
public enum ErrorMessage {
    VALIDATION_FAIL_EXCEPTION("입력 값의 조건이 잘못 되었습니다.", HttpStatus.BAD_REQUEST),
    UNDEFINED_EXCEPTION("정의되지 않은 에러입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    BINDING_FAIL_EXCEPTION("내부 서버에서 오류가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_PERMISSION_EXCEPTION("권한이 없거나 부족합니다.", HttpStatus.FORBIDDEN),
    NOT_POST_EXCEPTION("멤버 수가 1명이면 포스트 등록을 못 합니다.", HttpStatus.FORBIDDEN),

    NOT_EXIST_ROUTE("존재하지 않는 경로입니다.", HttpStatus.BAD_REQUEST),

    NOT_EXIST_USER("존재하지 않는 유저 입니다", HttpStatus.BAD_REQUEST),
    NOT_EXIST_POST("존재하지 않는 포스트입니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_MOYEO_POST("존재하지 않는 모여 포스트입니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_TIMELINE("존재하지 않는 타임라인입니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_MOYEO_TIMELINE("존재하지 않는 모여 타임라인입니다.", HttpStatus.BAD_REQUEST),
    NOT_TRAVELING("여행 중이 아닙니다.", HttpStatus.BAD_REQUEST),
    ALREADY_MOYEO("이미 동행 중입니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_MEMBERS("동행 멤버가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_MOYEO_PUBLIC("존재하지 않는 모여 퍼블릭입니다.", HttpStatus.BAD_REQUEST),

    NOT_EXIST_FAVORITE("유저가 해당 포스트에 좋아요를 누른 적이 없습니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_USER_FAV_POST("유저가 좋아요를 누른 포스트가 없습니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_KEYWORD("해당 지역명 키워드를 포함한 포스트가 없습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_DONE_TIMELINE("이미 완료된 타임라인입니다.", HttpStatus.BAD_REQUEST),

    NOT_EXIST_TIMELINE_PAGING("존재하지 않는 타임라인 페이징의 페이지 입니다", HttpStatus.BAD_REQUEST),
    OVER_VOICE_TIME("제한시간(30초)를 넘긴 오디오 파일 입니다", HttpStatus.BAD_REQUEST),
    NOT_PERMIT_VOICE_SAVE( "오디오 파일 저장시 에러 발생, 파일 형식을 확인을 해주세요", HttpStatus.BAD_REQUEST),
    NOT_PERMIT_USER( "로그인한 유저가 해당 타임라인에 접근할 권한이 없습니다", HttpStatus.BAD_REQUEST),
    NOT_STT_SAVE( "내부 서버 에러, SPEECH TO TEXT 과정에서 에러가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    UNAUTHORIZED_ACCESSTOKEN("accessToken이 만료되었습니다.", HttpStatus.UNAUTHORIZED),

    NOT_EXIST_PHOTO("존재하지 않는 사진 입니다", HttpStatus.BAD_REQUEST),

    TOO_LONG_COMMAND("input 명령어가 너무 깁니다.", HttpStatus.BAD_REQUEST),

    FIREBASE_INIT_ERROR("firebase 초기화시 에러 발생", HttpStatus.INTERNAL_SERVER_ERROR),

    ASYNC_RUN_ERROR("notification 서버 전송중 에러 발생", HttpStatus.INTERNAL_SERVER_ERROR),

    FIREBASE_SEND_ERROR("firebase 알림 메시지 전송중 에러", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_EXIST_LATEST_ADDRESS("최근 주소 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND);
    NOT_EXIST_LATEST_ADDRESS("최근 주소 정보가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    FLASK_SERVER_ERROR("플라스크 서버 연결에 문제가 있습니다.", HttpStatus.BAD_REQUEST);

    private final String errMsg;
    private final HttpStatus httpStatus;

    ErrorMessage(String errMsg, HttpStatus httpStatus) {
        //this.code = code;
        this.errMsg = errMsg;
        this.httpStatus = httpStatus;
    }


}
