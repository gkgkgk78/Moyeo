package com.moyeo.main;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("{name}")
    public ResponseEntity<?> hello(@PathVariable String name){
        return ResponseEntity.ok("안녕 도커 나는 "+name);
    }
}
