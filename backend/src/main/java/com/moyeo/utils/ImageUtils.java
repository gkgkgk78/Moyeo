//package com.danim.utils;
//
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.imageio.ImageIO;
//import javax.imageio.ImageReader;
//import javax.imageio.metadata.IIOMetadata;
//import javax.imageio.metadata.IIOMetadataNode;
//import javax.imageio.stream.ImageInputStream;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.HashMap;
//import java.util.Iterator;
//
//public class ImageUtils {
//    public static HashMap<String, Double> extractLocationFromImage(MultipartFile imageFile) throws IOException {
//        // imageFile에서 inputStream 가져오기
//        InputStream inputStream = new ByteArrayInputStream(imageFile.getBytes());
//
//        // inputStream에서 metadata 가져오기
//        ImageInputStream iis = ImageIO.createImageInputStream(inputStream);
//        Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
//        if (!readers.hasNext()) {
//            throw new IOException("Unsupported image format");
//        }
//        ImageReader reader = readers.next();
//        reader.setInput(iis, true);
//        IIOMetadata metadata = reader.getImageMetadata(0);
//
//        // exif data 가져오기
//        IIOMetadataNode exif = (IIOMetadataNode) metadata.getAsTree("javax_imageio_1.0");
//
//        // gps 가져오기
//        IIOMetadataNode gps = (IIOMetadataNode) exif.getElementsByTagName("GPS").item(0);
//
//        // latitude, longitude 값 추출하기
//        double latitude = getCoordinate(gps.getElementsByTagName("latitude").item(0));
//        double longitude = getCoordinate(gps.getElementsByTagName("longitude").item(0));
//
//        // HashMap 자료구조에 값 저장하고 location 변수로 반환
//        HashMap<String, Double> location = new HashMap<>();
//        location.put("latitude", latitude);
//        location.put("longitude", longitude);
//        return location;
//    }
//
//    // GPS metadata node에서 coordinate 값 추출하기
//    private static double getCoordinate(Object coordinate) {
//        String[] values = coordinate.toString().replaceAll("[^\\d.]", " ").trim().split("\\s+");
//        double degrees = Double.parseDouble(values[0]);
//        double minutes = Double.parseDouble(values[1]);
//        double seconds = Double.parseDouble(values[2]);
//        return degrees + (minutes / 60) + (seconds / 3600);
//    }
//}
