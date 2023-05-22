
import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:kakao_flutter_sdk/kakao_flutter_sdk.dart';
import 'package:logger/logger.dart';
import 'package:moyeo/models/MoyeoTimeline.dart';
import 'package:moyeo/models/UserInfo.dart';

import '../models/TimelineInfo.dart';
import '../utils/auth_dio.dart';
var logger = Logger();
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

  // 유저 추가하고 초대 알림 보내기
  Future<Map<String, dynamic>> addMoyeoUser(
      BuildContext context, int moyeoTimelineId, List<Map<String,dynamic>> userList) async {
    try {
      final dio = await authDio(context);
      logger.d(userList);
      Response response = await dio
        .post('api/auth/moyeo/members/${moyeoTimelineId}', data:userList);
      return response.data;
    } catch(e) {
      throw Exception('AddMoyeoUser Error $e');
    }
  }

  Future<void> acceptInvite(context, moyeoTimelindId) async {
    try {
      final dio = await authDio(context);
      Map<String, dynamic> moyeoData = {
        "moyeoTimelineId" : moyeoTimelindId
      };
      Response response = await dio.post('api/auth/moyeo/members', data: moyeoData);
      return response.data;
    } catch(e) {
      throw Exception('StartMoyeo Error $e');
    }
  }

  Future<List<Map<String, dynamic>>> getMoyeoMembers(BuildContext context, int timeLineId) async {
    try{
      final dio = await authDio(context);
      Response response = await dio.get("api/auth/timeline/${timeLineId}");
      Map<String, dynamic> json = response.data;
      TimelineInfo timelineInfoMembers = TimelineInfo.fromJson(json);
      return timelineInfoMembers.members ?? [];
    } catch(e){
      throw Exception('Error: $e');
    }
  }

  // Future<void> outMoyeo(context, moyeoTimelineId) async {
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
  //       .post('api/auth/moyeo/members/invite', data:userList);
  //     return response.data;
  //   } catch(e) {
  //     throw Exception('AddMoyeoUser Error $e');
  //   }
  // }

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