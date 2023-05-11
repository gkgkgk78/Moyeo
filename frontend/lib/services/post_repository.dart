
import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import '../utils/auth_dio.dart';

class PostRepository {
  PostRepository._internal();

  static final PostRepository _instance = PostRepository._internal();

  factory PostRepository() => _instance;

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

  // 동행 포스트 좋아요
  Future<Map<String, dynamic>> changeFavoriteMoyeoPost(
      BuildContext context, int postId, userUid) async {
    try {
      final dio = await authDio(context);
      Response response = await dio
          .post('api/auth/like',
          data: {'userUid': userUid, 'postId': postId},
          queryParameters:{'isMoyeo':true}
      );
      return response.data;
    } catch (e) {
      throw Exception('ChangeFavoritePost Error $e');
    }
  }

  // 동행 포스트 공개여부
  // 기본값 1 요청 들어갈때마다 바뀜
  Future<bool> changePostPublic(
      BuildContext context, int postId) async {
    try {
      final dio = await authDio(context);
      Response response =
      await dio.put('api/auth/moyeo/post/$postId');
      return response.data;
    } catch (e) {
      throw Exception('Fail to change timeline public: $e');
    }
  }

  // 포스트 삭제하기
  Future<void> deletePost(BuildContext context, int postId) async {
    try{
      final dio = await authDio(context);
      Response response = await dio.delete('api/auth/post/$postId');
      return response.data;
    } catch(e) {
      throw Exception('DeletePost Error $e');
    }
  }

  // 모여 포스트 삭제하기
  Future<void> deleteMoyeoPost(BuildContext context, int postId) async {
    try{
      final dio = await authDio(context);
      Response response = await dio.delete('api/auth/moyeo/$postId');
      return response.data;
    } catch(e) {
      throw Exception('DeletePost Error $e');
    }
  }
}
