package com.moyeo.main.conponent;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class AwsS3 {
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final String AWS_URL = "https://mydanimbucket.s3.ap-northeast-2.amazonaws.com";
    public String upload(MultipartFile file,String directoryName)throws Exception{

        String originalName = file.getOriginalFilename();
        String uploadPath = directoryName;
        String uploadFileName = UUID.randomUUID().toString()+"."+originalName.substring(originalName.indexOf(".")+1);
        String uploadUrl = "";
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        String keyname =uploadPath+"/"+uploadFileName;
        // amazonS3Client.putObject(new PutObjectRequest(bucket,keyname,file.getInputStream(),objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead)); // TODO
        amazonS3Client.putObject(new PutObjectRequest(bucket,keyname,file.getInputStream(),objectMetadata)); // TODO
        uploadUrl = amazonS3Client.getUrl(bucket,keyname).toString();
        return uploadUrl;
    }
    public String uploadFile(File file,String directoryName)throws Exception{

        String originalName = file.getName();
        String uploadPath = directoryName;
        String uploadFileName = UUID.randomUUID().toString()+"."+originalName.substring(originalName.indexOf(".")+1);
        String uploadUrl = "";
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(Files.size(file.toPath()));
        objectMetadata.setContentType(Files.probeContentType(new File(file.getName()).toPath()));
        String keyname =uploadPath+"/"+uploadFileName;
//        amazonS3Client.putObject(new PutObjectRequest(bucket,keyname,new FileInputStream(file),objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
        amazonS3Client.putObject(bucket,keyname,file);
       uploadUrl = amazonS3Client.getUrl(bucket,keyname).toString();
        return uploadUrl;
    }
    public void delete(String url){
        // url = url.substring(AWS_URL.length());
        int slashIndex = url.indexOf("/", 8);
        url = url.substring(slashIndex + 1);
        boolean isObjectExist = amazonS3Client.doesObjectExist(bucket, url);
        if(isObjectExist)amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, url));
    }
}
