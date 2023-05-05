import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:logger/logger.dart';
import 'package:mongo_dart/mongo_dart.dart';
import 'package:moyeo/models/ChatMessage.dart';
import 'package:moyeo/models/ChatbotRequest.dart';

import '../utils/auth_dio.dart';

var logger = Logger();

class ChatbotRepository {
  ChatbotRepository._internal();

  static final ChatbotRepository _instance = ChatbotRepository._internal();

  factory ChatbotRepository() => _instance;

  final String? _mongoUrl = dotenv.env['mongoUrl'];


  // 전체 채팅 리스트를 가져온다.
  Future<dynamic> ChatListFromServer(BuildContext context) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.get("api/auth/yeobot/");
      return response;
    } catch (error) {
      throw Exception('Fail to upload to Server: ${error}');
    }
  }


  // 채팅 로그를 가져온다.
  Future<dynamic> ChatDetailFromServer(BuildContext context, String chatId) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.get("api/auth/yeobot/$chatId");
      return List.from(response.data.map((json) => ChatMessage.fromJson(json)));
    } catch (error) {
      throw Exception('Fail to upload to Server: ${error}');
    }
  }

  Future<dynamic> CreateNewChat(BuildContext context) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.post("api/auth/yeobot/");
      return response;
    } catch (error) {
      throw Exception('Fail to upload to Server: ${error}');
    }
  }

  Future<dynamic> ChatToServer(BuildContext context, chatId, List<ChatMessage> messages) async {
    try {
      final dio = await authDio(context);
      final jsonList = messages.map((message) => message.toJson()).toList();
      Response response = await dio.post("api/auth/yeobot/$chatId", data: jsonList);
      return response;
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

