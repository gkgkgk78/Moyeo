
import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';

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
}
