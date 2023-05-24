import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:moyeo/models/ChatMessage.dart';
import 'package:moyeo/models/ChatbotRequest.dart';

import '../utils/auth_dio.dart';


class ChatbotRepository {
  ChatbotRepository._internal();

  static final ChatbotRepository _instance = ChatbotRepository._internal();

  factory ChatbotRepository() => _instance;


  // 채팅 로그를 가져온다.
  Future<List<ChatMessage>> ChatDetailFromServer(BuildContext context) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.get("api/auth/chat");
      return List.from(response.data.map((json) => ChatMessage.fromJson(json)));
    } catch (error) {
      throw Exception('Fail to upload to Server: ${error}');
    }
  }

  Future<void> ChatToServer(BuildContext context, ChatMessage message) async {
    try {
      final dio = await authDio(context);
      await dio.post("api/auth/chat", data: message.toJson());
      return;
    } catch (error) {
      throw Exception('Fail to upload to Server: ${error}');
    }
  }

  // 요청을 서버에 업로드
  Future<dynamic> RecommendActivity(BuildContext context) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.post("api/auth/yeobot/ing/activity");
      return response;
    } on DioError catch (error) {
      throw Exception('Fail to upload to Server: ${error.message}');
    }
  }

  Future<dynamic> RecommendRestaurant(BuildContext context) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.post("api/auth/yeobot/ing/dining");
      return response;
    } on DioError catch (error) {
      throw Exception('Fail to upload to Server: ${error.message}');
    }
  }

  Future<dynamic> RecommendPlace(BuildContext context, ChatbotRequest chatbotRequest) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.post("api/auth/yeobot/yet/place", data: chatbotRequest.toPlaceJson());
      return response;
    } on DioError catch (error) {
      throw Exception('Fail to upload to Server: ${error.message}');
    }
  }

  Future<dynamic> RecommendActivityNotTraveling(BuildContext context, ChatbotRequest chatbotRequest) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.post("api/auth/yeobot/yet/activity", data: chatbotRequest.toActivityJson());
      return response;
    } on DioError catch (error) {
      throw Exception('Fail to upload to Server: ${error.message}');
    }
  }
}

