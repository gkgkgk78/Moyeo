import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:moyeo/models/PushAlarm.dart';

import '../utils/auth_dio.dart';

class MessageRepository {
  MessageRepository._internal();

  static final MessageRepository _instance = MessageRepository._internal();

  factory MessageRepository() => _instance;

  Future<List<PushAlarm>> getPushList(BuildContext context, int userUid) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.get("api/auth/message/$userUid");
      return List.from(response.data.map((json) => PushAlarm.fromJson(json)));
    } catch (error) {
      throw Exception('Fail to get alarm list from Server: ${error}');
    }
  }

  Future<dynamic> readOnePush(BuildContext context, int messageId) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.get("api/auth/message/$messageId");
      return response.data;
    } catch (error) {
      throw Exception('Fail to get alarm list from Server: ${error}');
    }
  }

  // Future<void> getInviteAlarm(BuildContext context) async {}

}