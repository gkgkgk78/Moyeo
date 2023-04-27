//package com.danim.utils;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.sound.sampled.AudioFormat;
//import javax.sound.sampled.AudioInputStream;
//import javax.sound.sampled.AudioSystem;
//import java.awt.*;
//import java.io.*;
//
//@Slf4j
//public class VoiceUtils {
//    public static double getVoiceFileLength(MultipartFile voiceFile) throws Exception {
//
////        File file = new File(voiceFile.getOriginalFilename());
////        voiceFile.transferTo(file);
//
//        File file = new File("C:\Users\SSAFY\Desktop");
//
//        AudioInputStream audioInputStream = null;
//        double durationInSeconds = 0.0;
//
//        try {
//            audioInputStream = AudioSystem.getAudioInputStream(file);
//            AudioFormat format = audioInputStream.getFormat();
//            long frames = audioInputStream.getFrameLength();
//            log.info("frames",frames);
//            durationInSeconds = (frames + 0.0) / format.getFrameRate();
//        } catch (Exception e) {
//            throw e;
//        } finally {
//            if (audioInputStream != null) {
//                audioInputStream.close();
//            }
//        }
//
//        return durationInSeconds;
//    }
//}
