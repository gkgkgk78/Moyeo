package com.danim.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Controller
@Slf4j
public class RestDocController {
    private final String FILE_PATH = "/BOOT-INF/classes/static/index.html";
    private final String FILE_PATH1 = "/static/docs/index.html";


    @GetMapping("/1")
    public ClassPathResource restDoc(){
        ClassPathResource classPathResource = new ClassPathResource(FILE_PATH);
        if(classPathResource.exists()==false){
            log.info("invaild filepath :{}",FILE_PATH);
            throw new IllegalArgumentException();
        }
        log.info("file path exists = {}", classPathResource.exists());
        return classPathResource;
    }


    @GetMapping("/2")
    public ClassPathResource restDoc1() {
        ClassPathResource classPathResource = new ClassPathResource(FILE_PATH1);
        if (classPathResource.exists() == false) {
            log.info("invaild filepath :{}", FILE_PATH1);
            throw new IllegalArgumentException();
        }
        log.info("file path exists = {}", classPathResource.exists());
        return classPathResource;
    }

    @GetMapping("/3")
    public String restDocd3() throws IOException {
        Resource[] resources = ResourcePatternUtils
                .getResourcePatternResolver(new DefaultResourceLoader())
                .getResources("classpath/**");
        return "**/index.html";
    }

    @GetMapping("/4")
    public String restDocd4(){
        
        return htmlFileReader(FILE_PATH);
    }

    @GetMapping("/5")
    public String restDocd5() {

        return htmlFileReader(FILE_PATH1);
    }

    static String htmlFileReader(String pathStr) {

        ClassPathResource resource = new ClassPathResource(pathStr);
        String resultcontent = "";
        try {
            InputStream inputStream = resource.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

            StringBuilder builder = new StringBuilder();
            while (true) {
                String line = br.readLine();
                if (line == null) break;
                builder.append(line);
            }

            resultcontent = builder.toString();

        } catch (IOException e) {

        }
        return resultcontent;
    }
}
