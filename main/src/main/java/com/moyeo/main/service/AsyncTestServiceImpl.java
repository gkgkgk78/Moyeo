package com.moyeo.main.service;

import com.moyeo.main.dto.PostInsertReq;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AsyncTestServiceImpl implements AsyncTestService {

    @Autowired
    private AsyncService asyncService;


    public void check() {
        CompletableFuture<String> stringCompletableFuture = asyncService.testAsync("10");


        // Exception발생 시 처리
        stringCompletableFuture.exceptionally(
                throwable -> {
                    log.error("AsyncError: ", throwable);
                    throw new BaseException(ErrorMessage.ASYNC_RUN_ERROR);
                }
        );

        // 성공, 실패 값 둘다 처리 (반대 값들은 null형태로 들어옴) -> 처리후 반환값 지정 필요 x 이전 Completable 반환됨.
        // peek처럼 그냥 불러와서 별도 처리 가능.
        stringCompletableFuture.whenComplete(
                (s, throwable) -> {
                    if (Objects.isNull(throwable)) {
                        log.info(s);
                    } else {
                        log.error("AsyncError: " + throwable);
                    }
                }
        );

//        // 성공, 실패 값 둘다 처리 (반대 값들은 null형태로 들어옴) -> 처리후 반환값 지정 필요
//        stringCompletableFuture.handle(
//                (s, throwable) -> {
//                    if (Objects.isNull(throwable)) {
//                        log.info(s);
//                    } else {
//                        log.error("AsyncError: " + throwable);
//                    }
//                    return null;
//                }
//        );

//        // 성공했을 시 작업 수행(return 값이 필요 없음)
//        stringCompletableFuture.thenAccept(s -> {
//
//        });
//
//        // 성공했을 시 작업 수행(return 값이 필요함)
//        CompletableFuture<Integer> integerCompletableFuture = stringCompletableFuture.thenApply(s -> {
//            return 2;
//        });

    }

    public void test(PostInsertReq post) {
        asyncService.toNotification(post);



    }


}
