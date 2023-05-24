import 'package:flutter/material.dart';
import 'package:infinite_scroll_pagination/infinite_scroll_pagination.dart';

import '../models/Timeline.dart';
import '../models/UserInfo.dart';
import '../services/timeline_repository.dart';



class MyFeedViewModel extends ChangeNotifier {
  BuildContext context;
  UserInfo userInfo;
  UserInfo myInfo;

  MyFeedViewModel(
      {required this.context, required this.myInfo, required this.userInfo}) {
    if (userInfo.userUid == -1) {
      pagingController.addPageRequestListener((pageKey) {
        getMainTimelineList(context, pageKey);
      });
    } else if (userInfo.userUid != -1) {
      if (userInfo.userUid == myInfo.userUid) {
        pagingController.addPageRequestListener((pageKey) {
          getMyTimelineList(context, pageKey);
        });
      } else {
        pagingController.addPageRequestListener((pageKey) {
          getUserTimelineList(context, pageKey);
        });
      }
    }
  }

  final PagingController<int, Timeline> pagingController =
      PagingController(firstPageKey: 0);

  Future<void> getMainTimelineList(BuildContext context, int pageKey) async {
    try {
      final newItems =
          await TimelineRepository().getMainTimelineByPageNum(context, pageKey);
      final isLastPage = newItems.length < 15;
      if (isLastPage) {
        pagingController.appendLastPage(newItems);
      } else {
        final nextPageKey = pageKey + 1;
        pagingController.appendPage(newItems, nextPageKey);
      }
      notifyListeners();
    } catch (e) {
      throw Exception('get timeline list fail: $e');
    }
  }

  Future<void> getUserTimelineList(BuildContext context, int pageKey) async {
    final newItems = await TimelineRepository()
        .getOtherTimelineByPageNum(context, pageKey, userInfo.userUid);
    final isLastPage = newItems.length < 15;
    if (isLastPage) {
      pagingController.appendLastPage(newItems);
    } else {
      final nextPageKey = pageKey + 1;
      pagingController.appendPage(newItems, nextPageKey);
    }
    notifyListeners();
  }

  Future<void> getMyTimelineList(BuildContext context, int pageKey) async {
    final newItems =
        await TimelineRepository().getMyTimelineByPageNum(context, pageKey);
    final isLastPage = newItems.length < 15;
    if (isLastPage) {
      pagingController.appendLastPage(newItems);
    } else {
      final nextPageKey = pageKey + 1;
      pagingController.appendPage(newItems, nextPageKey);
    }
    notifyListeners();
  }

  refresh(context) async {
    pagingController.refresh();
  }

  @override
  void dispose() {
    pagingController.dispose();
    super.dispose();
  }
}
