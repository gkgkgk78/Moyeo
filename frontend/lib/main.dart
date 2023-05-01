import 'dart:io';

import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:kakao_flutter_sdk/kakao_flutter_sdk.dart';
import 'package:logger/logger.dart';
import 'package:moyeo/firebase_options.dart';
import 'package:moyeo/services/firebase_repository.dart';
import 'package:moyeo/utils/white.dart';
import 'package:moyeo/view_models/app_view_model.dart';
import 'package:moyeo/view_models/camera_view_model.dart';
import 'package:moyeo/views/login_page.dart';
import 'package:provider/provider.dart';


import 'models/UserInfo.dart';
import 'module/bottom_navigation.dart';
import 'module/camera_floating_action_button.dart';
import 'module/custom_app_bar.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await dotenv.load(fileName: ".env"); // 추가
  KakaoSdk.init(nativeAppKey: dotenv.env['nativeAppKey']);
  await Firebase.initializeApp(options: DefaultFirebaseOptions.currentPlatform,);


  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(
          create: (_) => AppViewModel(
            UserInfo(
              userUid: -1,
              profileImageUrl: '',
              nickname: '',
            ),
            '홈'
          ),

        ),
        ChangeNotifierProvider(
          create: (_) => CameraViewModel(),
        ),
      ],
      child: const MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Danim',
      theme: ThemeData(
        fontFamily: "GangwonAll",
        primaryTextTheme: const TextTheme(
            titleLarge: TextStyle(
              color: Colors.black
            ),
          titleMedium: TextStyle(
              color: Colors.black
          ),
          titleSmall: TextStyle(
              color: Colors.black
          ),
        ),
        primarySwatch: CustomColors.white,
        // primaryColor:Colors.grey[50] ,
      ),
      home: const LoginPage(),
    );
  }
}

var logger = Logger();

class MyHomePage extends StatelessWidget {
  const MyHomePage({super.key});

  @override
  Widget build(BuildContext context) {
    bool keyboardIsOpen = MediaQuery.of(context).viewInsets.bottom != 0;
    return Consumer<AppViewModel>(builder: (context, viewModel, __) {
      return WillPopScope(
        onWillPop: () async {
          viewModel.changeTitleToFormer();
          if (viewModel.homeFeedNavigatorKey.currentState != null) {
            if (viewModel.homeFeedNavigatorKey.currentState!.canPop()) {
              Navigator.of(viewModel.homeFeedNavigatorKey.currentContext!)
                  .pop();
              return false;
            }
          }
          if (viewModel.myFeedNavigatorKey.currentState != null) {
            if (viewModel.myFeedNavigatorKey.currentState!.canPop()) {
              Navigator.of(viewModel.myFeedNavigatorKey.currentContext!).pop();
              return false;
              // Navigator.pushNamedAndRemoveUntil(
              //   viewModel.myFeedNavigatorKey.currentContext!,
              //   '/',
              //   (routes) => false,
              // );
              // return false;
            }
          }
          if (!Navigator.canPop(context)) {
            showDialog(
              barrierDismissible: false,
              context: context,
              builder: (ctx) => WillPopScope(
                onWillPop: () async => false,
                child: AlertDialog(
                  title: const Text('다님 종료'),
                  content: const Text('다님을  종료 하시겠습니까?'),
                  actions: [
                    TextButton(
                      onPressed: () {
                        Navigator.pop(ctx);
                        exit(0);
                      },
                      child: const Text(
                        '종료',
                        style: TextStyle(color: Colors.red),
                      ),
                    ),
                    TextButton(
                      onPressed: () {
                        Navigator.pop(ctx);
                      },
                      child: const Text('취소'),
                    ),
                  ],
                ),
              ),
            );
            return false;
          }
          return true;
        },
        child: Scaffold(
          appBar: CustomAppBar(
            moveToModifyProfile: () {
              viewModel.goModifyProfilePage();
            },
            moveToYeobot: () {
              viewModel.goYeobotPage();
            }
          ),
          body: PageView(
            controller: viewModel.pageController,
            physics: const NeverScrollableScrollPhysics(),
            children: [
              Navigator(
                key: viewModel.homeFeedNavigatorKey,
                onGenerateRoute: (settings) {
                  return viewModel.onHomeFeedRoute(context, settings);
                },
              ),
              Navigator(
                key: viewModel.myFeedNavigatorKey,
                onGenerateRoute: (settings) {
                  return viewModel.onMyFeedRoute(context, settings);
                },
              ),
            ],
          ),
          resizeToAvoidBottomInset: true,
          floatingActionButton: Visibility(
            visible: !keyboardIsOpen,
            child: const CameraFloatingActionButton(),
          ),
          floatingActionButtonLocation:
              FloatingActionButtonLocation.centerDocked,
          bottomNavigationBar: CustomBottomNavigationBar(),
        ),
      );
    });
  }
}
