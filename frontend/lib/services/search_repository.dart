
import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';

import '../models/SearchedPost.dart';
import '../models/UserInfo.dart';
import '../utils/auth_dio.dart';


class SearchRepository {
  SearchRepository._internal();

  static final SearchRepository _instance = SearchRepository._internal();

  factory SearchRepository() => _instance;

  // 검색창에 검색했을 때 유저를 가져옴
  Future<List<UserInfo>> searchToSearchBar(BuildContext context, String keyword) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.get("api/auth/user?search=$keyword");
      return List.from(response.data.map((json) =>
          UserInfo.fromJson(json)));
    } on DioError catch (error) {
      throw Exception('Fail to get search Results: ${error.message}');
    }
  }

  // 검색 화면에 진입했을 때 포스트들을 요청함
  Future<List<SearchedPost>> searchPosts(BuildContext context, String keyword) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.get('/api/auth/post/main/$keyword');
      return List.from(response.data.map((json) => SearchedPost.fromJson(json)));
    } on DioError catch (error) {
      throw Exception('Fail to get search Posts: ${error.message}');
    }
  }

  Future<List<SearchedPost>> searchMyPosts(BuildContext context, String keyword) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.get('/api/auth/post/mine/$keyword');
      return List.from(response.data.map((json) => SearchedPost.fromJson(json)));
    } on DioError catch (error) {
      throw Exception('Fail to get search Posts: ${error.message}');
    }
  }

}
