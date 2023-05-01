import 'package:flutter/cupertino.dart';
import 'package:infinite_scroll_pagination/infinite_scroll_pagination.dart';

import '../models/Timeline.dart';
import '../services/timeline_repository.dart';



List<Timeline> test = [
  Timeline(timelineId: 1,
      nickname: "테스트닉",
      title: "테스트 제목",
      createTime: "2023-04-24",
      finishTime: "2023-04-24",
      imageUrl: "https://blog.kakaocdn.net/dn/bezjux/btqCX8fuOPX/6uq138en4osoKRq9rtbEG0/img.jpg",
      startPlace: "서울",
      finishPlace: "서울"),
  Timeline(timelineId: 2,
      nickname: "테스트닉2",
      title: "테스트 제목2",
      createTime: "2023-04-24",
      finishTime: "2023-04-24",
      imageUrl: "https://t1.daumcdn.net/cfile/tistory/99128B3E5AD978AF20",
      startPlace: "Seoul",
      finishPlace: "Seoul"),
  Timeline(timelineId: 3,
      nickname: "테스트닉3",
      title: "테스트 제목3",
      createTime: "2023-04-24",
      finishTime: "2023-04-24",
      imageUrl: "https://www.kagoshima-kankou.com/storage/tourism_themes/12/responsive_images/ElwnvZ2u5uZda7Pjcwlk4mMtr08kLNydT8zXA6Ie__1673_1115.jpeg",
      startPlace: "Seoul",
      finishPlace: "Seoul"),
];



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
        // final newItems =
        //     await TimelineRepository().getMainTimelineByPageNum(context, pageKey);
        final newItems = test;
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
