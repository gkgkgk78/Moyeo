
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';

import '../models/Timeline.dart';
import '../models/TimelineInfo.dart';
import '../utils/auth_dio.dart';

class TimelineRepository {
  TimelineRepository._internal();

  static final TimelineRepository _instance = TimelineRepository._internal();

  factory TimelineRepository() => _instance;

  // 메인피드 타임라인 리스트 가져오기
  Future<List<Timeline>> getMainTimelineByPageNum(context, int pageNum) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.get('api/auth/timeline/main/$pageNum');
      return List.from(response.data.map((json) => Timeline.fromJson(json)));
    } on DioError catch (error) {
      throw Exception('Fail to get timeline: $error');
    }
  }

  Future<List<Timeline>> getMyTimelineByPageNum(context, int pageNum) async {
    final dio = await authDio(context);
    try {
      Response response = await dio.get('api/auth/timeline/mine/$pageNum');
      return List.from(response.data.map((json) => Timeline.fromJson(json)));
    } on DioError catch (error) {
      throw Exception('Fail to get timeline: $error');
    }
  }

  Future<List<Timeline>> getOtherTimelineByPageNum(
      context, int pageNum, userUid) async {
    // 앱뷰모델에서 현재 기기에서 로그인한 유저 uid를 가져오기 위해서 선언
    final dio = await authDio(context);
    try {
      Response response =
          await dio.get('api/auth/timeline/other/$userUid/$pageNum');
      return List.from(response.data.map((json) => Timeline.fromJson(json)));
    } on DioError catch (error) {
      throw Exception('Fail to get timeline: $error');
    }
  }

  // 타임라인 아이디로 타임라인 한개 정보 가져오기
  Future<TimelineInfo> getTimelineDetailsByTimelineId(
      context, int timelineId) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.get('api/auth/timeline/$timelineId');
      return TimelineInfo.fromJson(response.data);
    } catch (error) {
      throw Exception('Fail to get timeline: $error');
    }
  }

  // 타임라인 공개 비공개 전환
  Future<bool> changeTimelinePublic(
      BuildContext context, int timelineId) async {
    try {
      final dio = await authDio(context);
      Response response =
          await dio.put('/api/auth/timeline/switch/$timelineId');
      return response.data;
    } catch (e) {
      throw Exception('Fail to change timeline public: $e');
    }
  }

  // 여행 종료
  Future<void> endTravel(context, int timelineId, String title) async {
    try {
      final dio = await authDio(context);
      await dio.put('/api/auth/timeline/$timelineId/$title');
    } catch (e) {
      throw Exception('Fail to end travel $e');
    }
  }

  //여행 시작
  Future<int> startTravel(context) async {
    try {
      final dio = await authDio(context);
      Response response = await dio.post('api/auth/timeline');
      return response.data;
    } catch (e) {
      throw Exception('Fail to start travel $e');
    }
  }

  // 타임라인 삭제
  Future<void> deleteTimeline(context, timelineId) async {
    try {
      final dio = await authDio(context);
      await dio.delete('/api/auth/timeline/$timelineId');
    } catch (e) {
      showDialog(
        barrierDismissible: false,
        context: context,
        builder: (ctx) => AlertDialog(
          title: const Text('종료 실패'),
          content: const Text('포스트가 없는 여행은 종료할 수 없습니다.'),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.pop(ctx);
              },
              child: const Text('확인'),
            ),
          ],
        ),
      );
      throw Exception('Fail to delete timeline $e');
    }
  }
}
