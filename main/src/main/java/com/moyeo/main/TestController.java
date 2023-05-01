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

    @GetMapping("/api/{name}")
    public ResponseEntity<?> hello(@PathVariable String name){
        log.info("insert string : {}",name);
        return ResponseEntity.ok("안녕 도커 나는 SSL 확인중입니다. SSL test! "+name+"야~");
    }
}
