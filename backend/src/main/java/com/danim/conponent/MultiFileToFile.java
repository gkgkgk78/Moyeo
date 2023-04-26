package com.danim.conponent;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class MultiFileToFile {

    public File transTo(MultipartFile multipartFile) throws IOException {
        File f = new File(multipartFile.getOriginalFilename());
        f.createNewFile();
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(multipartFile.getBytes());
        fos.close();
        return f;
    }
}
