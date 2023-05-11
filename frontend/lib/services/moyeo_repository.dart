
import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
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

      return MoyeoTimeline.fromJson(response.data);
    } catch(e) {
      throw Exception('StartMoyeo Error $e');
    }
  }

  // Future<MoyeoTimeline> addMoyeoUser(
  //     BuildContext context, List<Map<String,int>> userList) async {
  //   try {
  //     // 이전 로직
  //     // List<Map<String,int>> userIdList = [];
  //     //
  //     // for (var user in userList){
  //     //   userIdList.add({"userId":user.userUid});
  //     // }
  //
  //     final dio = await authDio(context);
  //     Response response = await dio
  //       .post('api/auth/moyeo/members', data:userList);
  //     return response.data;
  //   } catch(e) {
  //     throw Exception('AddMoyeoUser Error $e');
  //   }
  // }

  Future<void> acceptInvite(context, moyeoTimelindId) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.post('api/auth/moyeo/members', data: moyeoTimelindId);
      return response.data;
    } catch(e) {
      throw Exception('StartMoyeo Error $e');
    }
  }

  Future<void> outMoyeo(context, moyeoTimelineId) async {
    try {
      final dio = await authDio(context);
      await dio.put('/api/auth/moyeo/members', data: moyeoTimelineId);
    } catch (e) {
      showDialog(
        barrierDismissible: false,
        context: context,
        builder: (ctx) => AlertDialog(
          title: const Text('종료 실패'),
          content: const Text('포스트가 없는 여행은 종료할 수 없습니다.'),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.pop(ctx);
              },
              child: const Text('확인'),
            ),
          ],
        ),
      );
      throw Exception('Fail to delete moyeotimeline $e');
    }
  }

  Future<Map<String, dynamic>> changeFavoritePost(
      BuildContext context, int postId, userUid) async {
    try {
      final dio = await authDio(context);
      Response response = await dio
          .post('api/auth/like', data: {'userUid': userUid, 'postId': postId});
      return response.data;
    } catch (e) {
      throw Exception('ChangeFavoritePost Error $e');
    }
  }

}