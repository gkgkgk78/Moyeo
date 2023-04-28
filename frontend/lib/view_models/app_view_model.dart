import 'dart:async';

import 'package:danim/services/timeline_repository.dart';
import 'package:danim/view_models/chatbot_list_view_model.dart';
import 'package:danim/view_models/my_feed_view_model.dart';
import 'package:danim/view_models/search_bar_view_model.dart';
import 'package:danim/view_models/timeline_detail_view_model.dart';
import 'package:danim/views/chatbot_list_page.dart';
import 'package:danim/views/home_feed_page.dart';
import 'package:danim/views/my_feed_view.dart';
import 'package:danim/views/timeline_detail_page.dart';
import 'package:flutter/material.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import '../models/UserInfo.dart';
import '../utils/stack.dart';
import '../views/modify_profile.dart';

class AppViewModel with ChangeNotifier {
  int currentIndex;
  final pageController = PageController(initialPage: 0);
  final GlobalKey<NavigatorState> homeFeedNavigatorKey = GlobalKey();
  final GlobalKey<NavigatorState> myFeedNavigatorKey = GlobalKey();
  UserInfo _userInfo;
  String _title = '';
  final MyStack<String> _formerTitle = MyStack<String>();

  AppViewModel(this._userInfo, this._title, {this.currentIndex = 0});

  String get title => _title;

  UserInfo get userInfo => _userInfo;

  final logger = Logger();

  void changePage(index) {
    pageController.jumpToPage(index);
    if (currentIndex == index) {
      if (index == 0) {
        Navigator.popAndPushNamed(homeFeedNavigatorKey.currentContext!, '/');
      } else {
        Navigator.popAndPushNamed(myFeedNavigatorKey.currentContext!, '/');
      }
    }
    currentIndex = index;
    changeTitle(index == 0 ? '홈' : _userInfo.nickname);
    _formerTitle.clear();
    notifyListeners();
  }

  updateUserInfo(UserInfo userInfo) {
    _userInfo = userInfo;
    notifyListeners();
  }

  goYeobotPage() {
    changePage(1);
    changeTitle("채팅 리스트");
    Timer(
      const Duration(milliseconds: 100),
          () {
        Navigator.pushNamed(
          myFeedNavigatorKey.currentContext!,
          '/chatbot/list/',
        );
      },
    );
  }

  goModifyProfilePage() {
    changePage(1);
    changeTitle('프로필 변경');
    Timer(
      const Duration(milliseconds: 100),
      () {
        Navigator.pushNamed(
          myFeedNavigatorKey.currentContext!,
          '/modify/profile',
        );
      },
    );
  }

  startTravel(context) async {
    int timelineId = await TimelineRepository().startTravel(context);
    userInfo.timeLineId = timelineId;
    notifyListeners();
    goToTravelingTimelinePage(timelineId);
  }

  goToTravelingTimelinePage(int timelineId) {
    changePage(1);
    changeTitle(userInfo.nickname);
    notifyListeners();
    Timer(
      const Duration(milliseconds: 100),
      () {
        changeTitle('여행중');
        Navigator.pushNamed(
            myFeedNavigatorKey.currentContext!, '/timeline/detail/$timelineId');
      },
    );
  }

  onHomeFeedRoute(context, settings) {
    if (settings.name!.startsWith('/timeline/detail')) {
      final timelineId = int.parse(settings.name.split('/')[3]);
      return PageRouteBuilder(
        pageBuilder: (context, __, ___) {
          return ChangeNotifierProvider<TimelineDetailViewModel>(
            create: (_) => TimelineDetailViewModel(context, timelineId),
            child: const TimelineDetailPage(),
          );
        },
        transitionDuration: Duration.zero,
      );
    } else {
      return PageRouteBuilder(
        pageBuilder: (_, __, ___) => const HomeFeedPage(),
        transitionDuration: Duration.zero,
      );
    }
  }

  onMyFeedRoute(context, settings) {
    Widget page;
    if (settings.name!.startsWith('/timeline/detail')) {
      final timelineId = int.parse(settings.name.split('/')[3]);
      page = ChangeNotifierProvider(
        create: (_) => TimelineDetailViewModel(context, timelineId),
        child: const TimelineDetailPage(),
      );
    } else if (settings.name == '/modify/profile') {
      page = const ModifyProfile();
    } else if (settings.name == '/chatbot/list/') {
      page = ChangeNotifierProvider(
          create: (_) => ChatbotListViewModel(context),
          child: ChatbotListPage(),
      );
    } else {
      page = MultiProvider(
        providers: [
          ChangeNotifierProvider<MyFeedViewModel>(
            create: (_) => MyFeedViewModel(
                context: context, myInfo: userInfo, userInfo: userInfo),
          ),
          ChangeNotifierProvider<SearchBarViewModel>(
            create: (_) => SearchBarViewModel(isMyFeed: true),
          ),
        ],
        child: const MyFeedView(),
      );
    }
    return PageRouteBuilder(
      pageBuilder: (_, __, ___) => page,
      transitionDuration: Duration.zero,
    );
  }

  changeTitle(String newTitle) {
    _formerTitle.push(_title);
    _title = newTitle;
    notifyListeners();
  }

  changeTitleToFormer() {
    String? tmp = _formerTitle.pop();
    if (tmp != null) _title = tmp;
    notifyListeners();
  }
}
