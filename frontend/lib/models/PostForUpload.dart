import 'package:dio/dio.dart';

class PostForUpload extends FormData {
  final MultipartFile flag;
  final List<MultipartFile> imageSources;
  final MultipartFile audioSource;
  final String address1;
  final String address2;
  final String address3;
  final String? address4;


  PostForUpload({
    required this.audioSource,
    required this.imageSources,
    required this.flag,
    required this.address1,
    required this.address2,
    required this.address3,
    this.address4,
  });
}