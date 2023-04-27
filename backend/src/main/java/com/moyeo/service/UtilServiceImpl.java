package com.moyeo.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class UtilServiceImpl implements UtilService {
    @Override
    public String invertLocalDate(LocalDateTime time) {
        LocalDate s2 = time.toLocalDate();
        String s3 = s2.toString();
        s3 = s3.replace("-", ".");
        return s3;
    }
}
