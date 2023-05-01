import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:mongo_dart/mongo_dart.dart';

import '../utils/auth_dio.dart';

class ChatbotRepository {
  ChatbotRepository._internal();

  static final ChatbotRepository _instance = ChatbotRepository._internal();

  factory ChatbotRepository() => _instance;

  // 전체 채팅 리스트를 가져온다.
  Future<dynamic> ChatListFromServer(BuildContext context) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.post("api/auth/chatlist");
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
