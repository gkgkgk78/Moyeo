
import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';

import '../utils/auth_dio.dart';

class UploadRepository {
  UploadRepository._internal();

  static final UploadRepository _instance = UploadRepository._internal();

  factory UploadRepository() => _instance;


  // 서버에 업로드
  Future<dynamic> uploadToServer(BuildContext context, FormData formData) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.post("api/auth/post", data: formData);
      return response;
    } on DioError catch (error) {
      throw Exception('Fail to upload to Server: ${error.message}');
    }
  }

  // 모여 포스트 요청
  Future<dynamic> uploadToMoyeo(BuildContext context, FormData formData) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.post("api/auth/post",
          data: formData,
          queryParameters:{'isMoyeo':true},);
      return response;
    } on DioError catch (error) {
      throw Exception('Fail to upload to Server: ${error.message}');
    }
  }

}
