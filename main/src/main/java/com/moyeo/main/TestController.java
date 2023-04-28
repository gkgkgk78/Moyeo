package com.moyeo.main;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

    @GetMapping("{name}")
    public ResponseEntity<?> hello(@PathVariable String name){
        log.info("insert string : {}",name);
        return ResponseEntity.ok("안녕 도커 나는 CICD 되는지 확인중입니다. 10번째 마지막 자동화 태스트야! "+name+"야~");
    }
}
