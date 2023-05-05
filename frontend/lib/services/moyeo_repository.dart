
import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';

import '../utils/auth_dio.dart';

class MoyeoRepository{
  MoyeoRepository._internal();

  static final MoyeoRepository _instance = MoyeoRepository._internal();

  factory MoyeoRepository() => _instance;

  Future<Map<String, dynamic>> addMoyeoUser(
      BuildContext context, int userUid) async {
    try {
      final dio = await authDio(context);
      Response response = await dio
        .post('api/auth/moyeo/timeline', data: {'userUid':userUid});
      return response.data;
    } catch(e) {
      throw Exception('AddMoyeoUser Error $e');
    }
  }
}