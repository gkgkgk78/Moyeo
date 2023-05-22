import 'dart:io';

import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:kakao_flutter_sdk/kakao_flutter_sdk.dart';
import 'package:logger/logger.dart';
import 'package:provider/provider.dart';

import 'package:moyeo/firebase_options.dart';
import 'package:moyeo/utils/white.dart';
import 'package:moyeo/view_models/app_view_model.dart';
import 'package:moyeo/view_models/camera_view_model.dart';
import 'package:moyeo/views/login_page.dart';


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
          create: (context) => AppViewModel(
            UserInfo(
              userUid: -1,
              profileImageUrl: '',
              nickname: '',
            ),
            '나의 기록',
            context,
          ),

        ),
        ChangeNotifierProvider(
          create: (_) => CameraViewModel(),
        ),
      ],
      child: Builder(
        builder: (context) {
          AppViewModel appViewModel = Provider.of<AppViewModel>(context);
          return const MyApp();
        },
      )
    ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Moyeo',
      theme: ThemeData(
        textSelectionTheme: const TextSelectionThemeData(
          selectionHandleColor: Colors.orange
        ) ,
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
                  title: const Text('한 번만 더 묻겠습니다.'),
                  content: const Text('정말로 가실 건가요...?'),
                  actions: [
                    TextButton(
                      onPressed: () {
                        Navigator.pop(ctx);
                        exit(0);
                      },
                      child: const Text(
                        '응!',
                        style: TextStyle(color: Colors.red),
                      ),
                    ),
                    TextButton(
                      onPressed: () {
                        Navigator.pop(ctx);
                      },
                      child: const Text('아니;',
                      style: TextStyle(color: Colors.blueAccent),),
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
                key: viewModel.myFeedNavigatorKey,
                onGenerateRoute: (settings) {
                  return viewModel.onMyFeedRoute(context, settings);
                },
              ),
              Navigator(
                key: viewModel.homeFeedNavigatorKey,
                onGenerateRoute: (settings) {
                  return viewModel.onHomeFeedRoute(context, settings);
                },
              ),
            ],
          ),
          resizeToAvoidBottomInset: true,
          floatingActionButton: Visibility(
            visible: !keyboardIsOpen && !viewModel.modalVisible ,
            child: Container(
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(40)

              ),
              child: const CameraFloatingActionButton() ,
            ),
          ),
          floatingActionButtonLocation:
              FloatingActionButtonLocation.centerDocked,
          bottomNavigationBar: CustomBottomNavigationBar(),
        ),
      );
    });
  }
}

