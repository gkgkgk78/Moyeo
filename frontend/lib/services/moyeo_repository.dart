
import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:kakao_flutter_sdk/kakao_flutter_sdk.dart';
import 'package:moyeo/models/MoyeoTimeline.dart';
import 'package:moyeo/models/UserInfo.dart';

import '../utils/auth_dio.dart';

class MoyeoRepository{
  MoyeoRepository._internal();

  static final MoyeoRepository _instance = MoyeoRepository._internal();

  factory MoyeoRepository() => _instance;

  Future<MoyeoTimeline> startMoyeo(BuildContext context) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.post('api/auth/moyeo/timeline');
      return response.data;
    } catch(e) {
      throw Exception('StartMoyeo Error $e');
    }
  }

  Future<MoyeoTimeline> addMoyeoUser(
      BuildContext context, List<Map<String,int>> userList) async {
    try {
      // 이전 로직
      // List<Map<String,int>> userIdList = [];
      //
      // for (var user in userList){
      //   userIdList.add({"userId":user.userUid});
      // }

      final dio = await authDio(context);
      Response response = await dio
        .post('api/auth/moyeo/members', data:userList);
      return response.data;
    } catch(e) {
      throw Exception('AddMoyeoUser Error $e');
    }
  }
}