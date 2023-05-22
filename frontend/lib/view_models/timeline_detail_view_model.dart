
import 'package:flutter/material.dart';
import 'package:moyeo/view_models/app_view_model.dart';
import 'package:provider/provider.dart';

import '../models/TimelineDetail.dart';
import '../models/UserInfo.dart';
import '../services/timeline_repository.dart';
import '../services/moyeo_repository.dart';
import '../services/user_repository.dart';

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
  List<Map<String, dynamic>>? _members = [];
  // 모여 타임라인
  String? get title => _title;

  int? postId;

  get nowMoyeo => _nowMoyeo;

  get isMine => _isMine;

  get isPublic => _isPublic;

  get isComplete => _isComplete;

  get timelineDetails => _timelineDetails;

  get members => _members;

  final ScrollController _scrollController = ScrollController();

  ScrollController get scrollController => _scrollController;

  changeTitle(String newTitle) {
    _title = newTitle;
    notifyListeners();
  }

  TimelineDetailViewModel(BuildContext context, this.timelineId, this.postId) {
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
    _members = timelineInfo.members;
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
  }

  // 모여 시작하기
  startMoyeo(context) async {
    final res = await MoyeoRepository().startMoyeo(context);

    UserInfo userInfo = await UserRepository().getUserInfo(context);
    userInfo.moyeoTimelineId = res.moyeoTimelineId;

    AppViewModel appVM = Provider.of<AppViewModel>(context, listen: false);
    appVM.updateUserInfo(userInfo);

    notifyListeners();
  }

  // 모여 멤버 추가
  addMoyeoUser(context, int moyeoTimelineId, List<Map<String,dynamic>> userList) async {
    await MoyeoRepository().addMoyeoUser(context, moyeoTimelineId, userList);
    notifyListeners();
  }

  //모여 나가기
 outMoyeo(context, int userId, int moyeoTimelineId) async {
    await TimelineRepository().outMoyeo(context, userId, moyeoTimelineId);
    AppViewModel appVM = Provider.of<AppViewModel>(context, listen: false);
    UserInfo newUserInfo = await UserRepository().getUserInfo(context);
    appVM.updateUserInfo(newUserInfo);

    notifyListeners();
 }

  void goBottom() {
    WidgetsBinding.instance.addPostFrameCallback(
          (_) {
        _scrollController.jumpTo(
          _scrollController.position.maxScrollExtent,
        );
      },
    );
  }
}