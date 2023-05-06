
import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:kakao_flutter_sdk/kakao_flutter_sdk.dart';
import 'package:moyeo/models/UserInfo.dart';

import '../utils/auth_dio.dart';

class MoyeoRepository{
  MoyeoRepository._internal();

  static final MoyeoRepository _instance = MoyeoRepository._internal();

  factory MoyeoRepository() => _instance;

  Future<List<UserInfo>> addMoyeoUser(
      BuildContext context, List<UserInfo> userList) async {
    try {
      final dio = await authDio(context);
      Response response = await dio
        .post('api/auth/moyeo/timeline', data:userList);
      return response.data;
    } catch(e) {
      throw Exception('AddMoyeoUser Error $e');
    }
  }
}