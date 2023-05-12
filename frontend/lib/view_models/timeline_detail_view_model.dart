
import 'package:flutter/material.dart';
import 'package:moyeo/models/UserInfo.dart';

import '../models/TimelineDetail.dart';
import '../services/timeline_repository.dart';
import '../services/moyeo_repository.dart';

import '../models/Post.dart';
import '../models/TimelineInfo.dart';
import '../views/moyeo_timeline.dart';
import '../views/moyeo_add_user.dart';

class TimelineDetailViewModel extends ChangeNotifier {
  final int timelineId;
  bool _isMine = false;
  bool _isPublic = false;
  bool _isComplete = false;
  String? _title;
  bool _nowMoyeo = false;
  final int expansionTileAnimationTile = 200;
  final textController = TextEditingController();
  List<TimelineDetail> _timelineDetails = [];
  // 모여 타임라인
  String? get title => _title;

  get nowMoyeo => _nowMoyeo;

  get isMine => _isMine;

  get isPublic => _isPublic;

  get isComplete => _isComplete;

  get timelineDetails => _timelineDetails;

  changeTitle(String newTitle) {
    _title = newTitle;
    notifyListeners();
  }

  TimelineDetailViewModel(BuildContext context, this.timelineId) {
    loadTimelineDetails(context);
    notifyListeners();
  }

  loadTimelineDetails(context) async {
    final timelineInfo = await TimelineRepository()
        .getTimelineDetailsByTimelineId(context, timelineId);
    _timelineDetails = timelineInfo.timelineDetails!;
    _isMine = timelineInfo.isMine;
    _isPublic = timelineInfo.isPublic;
    _isComplete = timelineInfo.isComplete;
    _nowMoyeo = timelineInfo.nowMoyeo;
    notifyListeners();
  }

  changeExpansions(int timelineIndex, bool isExpand) async {
    if (!isExpand) {
      await Future.delayed(Duration(milliseconds: expansionTileAnimationTile));
      for (var post in _timelineDetails[timelineIndex].postList) {
        post.isExpand = false;
      }
    }
    _timelineDetails[timelineIndex].isExpand = isExpand;
    notifyListeners();
  }

  changePostExpansion(context, int timelineIndex, int postIndex,
      bool isExpand) async {
    if (!isExpand) {
      await Future.delayed(Duration(milliseconds: expansionTileAnimationTile));
    } else {
      _scrollToSelectedContent(context);
    }
    _timelineDetails[timelineIndex].postList[postIndex].isExpand = isExpand;
    notifyListeners();
  }

  void _scrollToSelectedContent(context) {
    if (context != null) {
      Future.delayed(Duration(milliseconds: expansionTileAnimationTile))
          .then((value) {
        Scrollable.ensureVisible(context,
            duration: Duration(milliseconds: expansionTileAnimationTile));
      });
    }
  }

  changeIsPublic(BuildContext context) async {
    _isPublic =
    await TimelineRepository().changeTimelinePublic(context, timelineId);
    notifyListeners();
  }

  showPublicIcon() {
    if (_isMine) {
      if (!_isPublic) {
        return const Icon(Icons.lock);
      } else {
        return const Icon(Icons.lock_open);
      }
    }
    return Container();
  }

  resetTitle() {
    changeTitle('');
    notifyListeners();
  }

  endTimeline(context) async {
    await TimelineRepository().endTravel(context, timelineId, _title!);
    Navigator.popAndPushNamed(context, '/timeline/detail/$timelineId');
  }

  deleteTimeline(context) async {
    await TimelineRepository().deleteTimeline(context, timelineId);
    Navigator.pop(context);
  }

  //모여 나가기
 outMoyeo(context, int moyeoTimelineId) async {
    await TimelineRepository().outMoyeo(context, moyeoTimelineId);
    notifyListeners();
 }
}