import 'package:danim/utils/auth_dio.dart';
import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';

class ChatbotRepository {
  ChatbotRepository._internal();

  static final ChatbotRepository _instance = ChatbotRepository._internal();

  factory ChatbotRepository() => _instance;

  // 전체 채팅 리스트를 가져온다.
  Future<dynamic> ChatListFromServer(BuildContext context) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.post("api/auth/chatlog");
      return response;
    } on DioError catch (error) {
      throw Exception('Fail to upload to Server: ${error.message}');
    }
  }

  // 채팅 로그를 가져온다.
  Future<dynamic> ChatDetailFromServer(BuildContext context, int chatId) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.post("api/auth/chatlog/$chatId");
      return response;
    } on DioError catch (error) {
      throw Exception('Fail to upload to Server: ${error.message}');
    }
  }

  // 서버에 업로드
  Future<dynamic> ChatToServer(BuildContext context, FormData formData) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.post("api/auth/autogpt", data: formData);
      return response;
    } on DioError catch (error) {
      throw Exception('Fail to upload to Server: ${error.message}');
    }
  }
}