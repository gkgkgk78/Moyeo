import 'dart:async';
import 'dart:convert';

import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:logger/logger.dart';
import 'package:moyeo/services/moyeo_repository.dart';
import 'package:moyeo/view_models/message_list_view_model.dart';
import 'package:moyeo/view_models/search_bar_view_model.dart';
import 'package:moyeo/view_models/timeline_detail_view_model.dart';
import 'package:moyeo/views/message_list.dart';
import 'package:moyeo/views/push_alarm_page.dart';
import 'package:provider/provider.dart';

import '../models/UserInfo.dart';
import '../services/timeline_repository.dart';
import '../services/user_repository.dart';
import '../utils/stack.dart';
import '../views/chatbot_detail_page.dart';
import '../views/home_feed_page.dart';
import '../views/modify_profile.dart';
import '../views/my_feed_view.dart';
import '../views/timeline_detail_page.dart';
import 'chatbot_detail_view_model.dart';
import 'my_feed_view_model.dart';

class AppViewModel with ChangeNotifier {
  int currentIndex;
  final pageController = PageController(initialPage: 0);
  final GlobalKey<NavigatorState> homeFeedNavigatorKey = GlobalKey();
  final GlobalKey<NavigatorState> myFeedNavigatorKey = GlobalKey();
  UserInfo _userInfo;
  String _title = '';
  BuildContext _context;
  final MyStack<String> _formerTitle = MyStack<String>();

  AppViewModel(this._userInfo, this._title, this._context,
      {this.currentIndex = 0}) {
    FirebaseMessaging.instance.onTokenRefresh.listen(
      (newToken) {
        _fcmToken = newToken;
        notifyListeners();
      },
    );
    FirebaseMessaging.onMessageOpenedApp.listen(
      (RemoteMessage message) async {
        if (message.notification?.body?.contains('동행') == true) {
          logger.d('동행수락');
          if (_messageLimitSecond <= 61) {
            await MoyeoRepository()
                .acceptInvite(_context, message.data["moyeoTimelineId"]);
            if (_context.mounted) {
              _userInfo = await UserRepository().getUserInfo(_context);
            }
            _messageLimitTimer.cancel();
            _messageLimitSecond = 0;
          }
        } else {
          goMessageListPage();
        }
        _messageLimitTimer.cancel();
        _messageLimitSecond = 0;
        notifyListeners();
      },
    );

    initializeFirebase();
    _initLocalNotification(_context);
  }

  String _fcmToken = '';

  String get fcmToken => _fcmToken;

  String get title => _title;

  UserInfo get userInfo => _userInfo;

  int _localMessageId = 0;

  final logger = Logger();

  void changePage(index) {
    pageController.jumpToPage(index);
    if (currentIndex == index) {
      if (index == 0) {
        Navigator.popAndPushNamed(myFeedNavigatorKey.currentContext!, '/');
      } else {
        Navigator.popAndPushNamed(homeFeedNavigatorKey.currentContext!, '/');
      }
    }
    currentIndex = index;
    changeTitle(index == 0 ? '나의 모여' : '모두의 여행');
    _formerTitle.clear();
    notifyListeners();
  }

  updateUserInfo(UserInfo userInfo) {
    _userInfo = userInfo;
    notifyListeners();
  }

  goYeobotPage() {
    changePage(0);
    changeTitle("채팅 리스트");
    Future.delayed(
      const Duration(milliseconds: 100),
      () {
        Navigator.pushNamed(
          myFeedNavigatorKey.currentContext!,
          '/chatbot',
        );
      },
    );
  }

  goModifyProfilePage() {
    changePage(0);
    changeTitle('프로필 변경');
    Future.delayed(
      const Duration(milliseconds: 100),
      () {
        Navigator.pushNamed(
          myFeedNavigatorKey.currentContext!,
          '/modify/profile',
        );
      },
    );
  }

  goMessageListPage() {
    changePage(0);
    changeTitle('날 귀찮게 했던 것들');
    Future.delayed(
      const Duration(milliseconds: 100),
      () {
        Navigator.pushNamed(
          myFeedNavigatorKey.currentContext!,
          '/messages',
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
    changePage(0);
    changeTitle('나의 모여');
    notifyListeners();
    Future.delayed(
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
    } else if (settings.name == '/chatbot') {
      page = ChangeNotifierProvider(
        create: (_) =>
            ChatbotViewModel(context, isTravel: _userInfo.timeLineId),
        child: const ChatbotPage(),
      );
    } else if (settings.name == '/messages') {
      page = ChangeNotifierProvider(
        create: (_) =>
            MessageListViewModel(context, _fromPush, userInfo: _userInfo),
        child: MessageListPage(),
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

  int _moyeoTimelineId = -1;

  String _pushTitle = "";
  String _pushBody = "";

  late Timer _messageLimitTimer;
  int _messageLimitSecond = 0;

  Future<void> initializeFirebase() async {
    String firebaseValidKey = dotenv.env["firebaseValidKey"]!;
    _fcmToken = (await FirebaseMessaging.instance
        .getToken(vapidKey: firebaseValidKey))!;
    logger.d(_fcmToken);
    notifyListeners();
    // 알림 권한 요청
    await FirebaseMessaging.instance.requestPermission(
      alert: true,
      announcement: true,
      badge: true,
      carPlay: true,
      criticalAlert: true,
      provisional: true,
      sound: true,
    );

    FirebaseMessaging.onMessage.listen(
      (RemoteMessage rm) {
        _messageLimitTimer = Timer.periodic(Duration(seconds: 1), (timer) {
          _messageLimitSecond += 1;
        });

        NotificationDetails details;
        if (rm.notification?.body?.contains("동행") == true) {
          NotificationDetails buttonDetails = const NotificationDetails(
            android: AndroidNotificationDetails(
              '초대1',
              '모여1',
              importance: Importance.max,
              priority: Priority.high,
              styleInformation: BigTextStyleInformation(''),
              category: AndroidNotificationCategory.social,
            ),
            iOS: DarwinNotificationDetails(
              presentAlert: true,
              presentBadge: true,
              presentSound: true,
            ),
          );
          logger.d(rm.notification?.title);
          logger.d(rm.notification?.body);
          _pushTitle = (rm.notification?.title)!;
          _pushBody = (rm.notification?.body)!;
          details = buttonDetails;
          String? moyeoTimeLineId = rm.data["moyeoTimelineId"];
          _moyeoTimelineId = int.parse(moyeoTimeLineId!);
        } else {
          NotificationDetails plainDetails = const NotificationDetails(
            android: AndroidNotificationDetails('moyeo1', '모여1'),
            iOS: DarwinNotificationDetails(
              presentAlert: true,
              presentBadge: true,
              presentSound: true,
            ),
          );
          details = plainDetails;
        }

        FlutterLocalNotificationsPlugin localNotification =
            FlutterLocalNotificationsPlugin();

        localNotification.show(
          _localMessageId,
          rm.notification?.title,
          rm.notification?.body,
          details,
        );
        _localMessageId += 1;
      },
    );
  }

  Future<void> _initLocalNotification(BuildContext context) async {
    FlutterLocalNotificationsPlugin localNotification =
        FlutterLocalNotificationsPlugin();
    AndroidInitializationSettings initSettingsAndroid =
        const AndroidInitializationSettings('@mipmap/launcher_icon');
    DarwinInitializationSettings initSettingsIOS =
        const DarwinInitializationSettings(
      requestSoundPermission: false,
      requestBadgePermission: false,
      requestAlertPermission: false,
    );
    InitializationSettings initSettings = InitializationSettings(
      android: initSettingsAndroid,
      iOS: initSettingsIOS,
    );
    await localNotification.initialize(
      initSettings,
      onDidReceiveNotificationResponse: (NotificationResponse payload) async {
        // _moyeoTimelineId
        if (_pushBody.contains("동행")) {
          _fromPush = true;
          if (_messageLimitSecond <= 61) {
            await MoyeoRepository().acceptInvite(context, _moyeoTimelineId);
            _messageLimitTimer.cancel();
            _messageLimitSecond = 0;
            if (context.mounted) {
              _userInfo = await UserRepository().getUserInfo(context);
            }
          }
        } else {
          _messageLimitTimer.cancel();
          _messageLimitSecond = 0;
          await goMessageListPage();
        }
        _fromPush = false;
        notifyListeners();
      },
    );
  }

  bool _fromPush = false;

  bool get fromPush => _fromPush;

  Future<void> deleteFCMToken() async {
    String firebaseValidKey = dotenv.env["firebaseValidKey"]!;
    await FirebaseMessaging.instance.deleteToken();
    _fcmToken = (await FirebaseMessaging.instance
        .getToken(vapidKey: firebaseValidKey))!;
    notifyListeners();
  }

  void changeFromPush() {
    if (_fromPush == true) {
      _fromPush = false;
    }
    notifyListeners();
  }

  bool _modalVisible = false;

  bool get modalVisible => _modalVisible;

  void chageModalVisible() {
    if (_modalVisible == false) {
      _modalVisible = true;
    } else {
      _modalVisible = false;
    }
    notifyListeners();
  }

  bool _isLogouting = false;

  bool get isLogouting => _isLogouting;

  void changeLogouting() {
    if (_isLogouting == false) {
      _isLogouting = true;
    } else {
      _isLogouting = false;
    }
    notifyListeners();
  }
}
