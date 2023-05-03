import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:mongo_dart/mongo_dart.dart';
import 'package:moyeo/models/ChatbotRequest.dart';

import '../utils/auth_dio.dart';

class ChatbotRepository {
  ChatbotRepository._internal();

  static final ChatbotRepository _instance = ChatbotRepository._internal();

  factory ChatbotRepository() => _instance;

  // 전체 채팅 리스트를 가져온다.
  Future<dynamic> MessageListFromServer(BuildContext context) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.post("api/auth/chatlist");
      return response;
    } on DioError catch (error) {
      throw Exception('Fail to upload to Server: ${error.message}');
    }
  }

  Future<dynamic> CreateChat(BuildContext context) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.post("api/auth/yeobot");
      return response;
    } on DioError catch (error) {
      throw Exception('Fail to upload to Server: ${error.message}');
    }
  }

  // 채팅 로그를 가져온다.
  Future<dynamic> ChatDetailFromServer(BuildContext context, int chatId) async {
    String? mongoUrl = dotenv.env['mongoUrl'];
    try {
      Db db = Db('$mongoUrl');
      await db.open();
      DbCollection chatDetail = db.collection("$chatId");
      final chatLog = await chatDetail.find().toList();
      return chatLog;
    } on DioError catch (error) {
      throw Exception('Fail to upload to Server: ${error.message}');
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

  Future<dynamic> RecommendPlace(BuildContext context) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.post("api/auth/yeobot/yet/place");
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
