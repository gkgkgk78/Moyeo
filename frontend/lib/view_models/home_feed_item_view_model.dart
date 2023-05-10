import 'package:flutter/cupertino.dart';
import 'package:infinite_scroll_pagination/infinite_scroll_pagination.dart';

import '../models/Timeline.dart';
import '../services/timeline_repository.dart';

class HomeFeedItemViewModel extends ChangeNotifier {
  int searchedUserUid;

  final PagingController<int, Timeline> pagingController =
  PagingController(firstPageKey: 0);

  HomeFeedItemViewModel({
    required BuildContext context,
    this.searchedUserUid = -1,
  }) {
    pagingController.addPageRequestListener((pageKey) {
        getMainTimelineList(context, pageKey);
      });
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
        notifyListeners();
      } catch (e) {
        throw Exception('get timeline list fail: $e');
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
