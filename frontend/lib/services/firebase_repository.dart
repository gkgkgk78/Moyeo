import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';

import '../utils/auth_dio.dart';

class FirebaseRepository {
  FirebaseRepository._internal();

  static final FirebaseRepository _instance = FirebaseRepository._internal();

  factory FirebaseRepository() => _instance;

  Future<dynamic> RefreshToken(BuildContext context, String? fcmToken) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.post("api/auth/fcmtoken", data: fcmToken);
      return response;
    } on DioError catch (error) {
      throw Exception('Fail to upload to Server: ${error.message}');
    }
  }

}
