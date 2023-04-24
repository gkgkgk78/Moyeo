import 'package:danim/models/Timeline.dart';
import 'package:danim/models/UserInfo.dart';
import 'package:danim/utils/auth_dio.dart';
import 'package:dio/dio.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

import '../models/Token.dart';

class UserRepository {
  // 싱글턴 패턴
  UserRepository._internal();

  static final UserRepository _instance = UserRepository._internal();

  final storage = const FlutterSecureStorage();

  factory UserRepository() => _instance;

  final _dio = Dio(
    BaseOptions(
      baseUrl: dotenv.env['baseUrl']!,
    ),
  );

  // 타임라인 한개 가져오기
  Future<Timeline> getTimelineById(int id) async {
    try {
      Response response = await _dio.get('api/auth/timeline/$id');
      return Timeline.fromJson(response.data);
    } catch (error) {
      throw Exception('Fail to get timeline id $id : $error');
    }
  }

  // 카카오 로그인
  Future<Token> kakaoLogin(Token token) async {
    try {
      Response response =
          await _dio.post('/api/login/kakao', data: token.toJson());
      return Token.fromJson(response.data);
    } catch (error) {
      throw Exception('Fail to login: $error');
    }
  }

  // 내 정보 가져오기
  Future<UserInfo> getUserInfo(context) async {
    final dio = await authDio(context);
    Response response = await dio.get(
      'api/auth/user/info',
    );
    return UserInfo.fromJson(response.data);
  }

  // 회원정보 변경 요청
  Future<UserInfo> updateUserProfile(context, FormData data) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.post('/api/auth/user/info', data: data);
      return UserInfo.fromJson(response.data);
    } catch (e) {
      throw Exception('Fail to modify profile $e');
    }
  }
}
