import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/cupertino.dart';
import 'package:infinite_scroll_pagination/infinite_scroll_pagination.dart';
import 'package:logger/logger.dart';
import 'package:moyeo/services/firebase_repository.dart';

import '../models/Timeline.dart';
import '../services/timeline_repository.dart';

var logger = Logger();

class HomeFeedViewModel extends ChangeNotifier {
  int searchedUserUid;
  final int myUserUid;

  final PagingController<int, Timeline> pagingController =
      PagingController(firstPageKey: 0);

  HomeFeedViewModel({
    required BuildContext context,
    this.searchedUserUid = -1,
    required this.myUserUid,
  }) {

    if (searchedUserUid == -1) {
      pagingController.addPageRequestListener((pageKey) {
        getMainTimelineList(context, pageKey);
      });
    } else if (searchedUserUid != -1 && myUserUid != -1) {
      if (searchedUserUid == myUserUid) {
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

  Future<void> getMainTimelineList(BuildContext context, int pageKey) async {
    try {
      final newItems =
          await TimelineRepository().getMainTimelineByPageNum(context, pageKey);
      // final newItems = test;
      final isLastPage = newItems.length < 15;
      if (isLastPage) {
        pagingController.appendLastPage(newItems);
      } else {
        final nextPageKey = pageKey + 1;
        pagingController.appendPage(newItems, nextPageKey);
      }
    } catch (e) {
      throw Exception('get timeline list fail: $e');
    }
  }

  Future<void> getUserTimelineList(BuildContext context, int pageKey) async {
    final newItems = await TimelineRepository()
        .getOtherTimelineByPageNum(context, pageKey, searchedUserUid);
    // final newItems = test;
    final isLastPage = newItems.length < 15;
    if (isLastPage) {
      pagingController.appendLastPage(newItems);
    } else {
      final nextPageKey = pageKey + 1;
      pagingController.appendPage(newItems, nextPageKey);
    }
  }

  Future<void> getMyTimelineList(BuildContext context, int pageKey) async {
    final newItems =
        await TimelineRepository().getMyTimelineByPageNum(context, pageKey);
    // final newItems = test;
    final isLastPage = newItems.length < 15;
    if (isLastPage) {
      pagingController.appendLastPage(newItems);
    } else {
      final nextPageKey = pageKey + 1;
      pagingController.appendPage(newItems, nextPageKey);
    }
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
