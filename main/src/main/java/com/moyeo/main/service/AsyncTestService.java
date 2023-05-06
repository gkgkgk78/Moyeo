package com.moyeo.main.service;

import com.moyeo.main.dto.PostInsertReq;
import com.moyeo.main.exception.BaseException;
import com.moyeo.main.exception.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;


public interface AsyncTestService {


    void check();

    void test(PostInsertReq post);


}
