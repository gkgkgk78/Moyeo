import 'package:dio/dio.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:logger/logger.dart';
import 'package:lottie/lottie.dart';
import 'package:moyeo/view_models/app_view_model.dart';
import 'package:provider/provider.dart';

import '../models/TimelineInfo.dart';
import '../models/UserInfo.dart';
import '../utils/auth_dio.dart';
import '../views/camera_screen.dart';
import '../views/login_page.dart';
import '../views/moyeo_add_user.dart';

var logger = Logger();

class CustomCircularMenu extends StatefulWidget {
  final UserInfo userInfo;

  const CustomCircularMenu({Key? key, required this.userInfo});

  @override
  _CustomCircularMenuState createState() => _CustomCircularMenuState();
}

class _CustomCircularMenuState extends State<CustomCircularMenu>
    with SingleTickerProviderStateMixin {
  late AnimationController animationController;
  late Animation degOneTranslationAnimation,
      degTwoTranslationAnimation,
      degThreeTranslationAnimation;
  late Animation rotationAnimation;

  bool isMenuVisible = true;

  double getRadiansFromDegree(double degree) {
    double unitRadian = 57.295779513;
    return degree / unitRadian;
  }

  @override
  void initState() {
    animationController = AnimationController(
        vsync: this, duration: const Duration(milliseconds: 250));
    degOneTranslationAnimation = TweenSequence(<TweenSequenceItem>[
      TweenSequenceItem<double>(
          tween: Tween<double>(begin: 0.0, end: 1.2), weight: 75.0),
      TweenSequenceItem<double>(
          tween: Tween<double>(begin: 1.2, end: 1.0), weight: 25.0),
    ]).animate(animationController);
    degTwoTranslationAnimation = TweenSequence(<TweenSequenceItem>[
      TweenSequenceItem<double>(
          tween: Tween<double>(begin: 0.0, end: 1.4), weight: 55.0),
      TweenSequenceItem<double>(
          tween: Tween<double>(begin: 1.4, end: 1.0), weight: 45.0)
    ]).animate(animationController);
    degThreeTranslationAnimation = TweenSequence(<TweenSequenceItem>[
      TweenSequenceItem<double>(
          tween: Tween<double>(begin: 0.0, end: 1.75), weight: 35.0),
      TweenSequenceItem<double>(
          tween: Tween<double>(begin: 1.75, end: 1.0), weight: 65.0)
    ]).animate(animationController);
    rotationAnimation = Tween<double>(begin: 180.0, end: 0.0).animate(
        CurvedAnimation(parent: animationController, curve: Curves.easeOut));
    super.initState();
    animationController.forward();
    animationController.addListener(() {
      setState(() {});
    });
  }

  Future<List<Map<String, dynamic>>> _fetchData(int timeLineId) async {
    try{
      final dio = await authDio(context);
      Response response = await dio.get("api/auth/timeline/${timeLineId}");
      Map<String, dynamic> json = response.data;
      TimelineInfo timelineInfoMembers = TimelineInfo.fromJson(json);
      return timelineInfoMembers.members ?? [];
    } catch(e){
      throw Exception('Error: $e');
    }
  }

  @override
  void dispose() {
    animationController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<AppViewModel>(builder: (context, appViewModel, _) {
      return Stack(
        alignment: Alignment.bottomCenter,
        children: <Widget>[
          Transform.translate(
            offset: Offset.fromDirection(getRadiansFromDegree(0),
                degOneTranslationAnimation.value * 100),
            child: Transform(
              transform: Matrix4.rotationZ(
                  getRadiansFromDegree(rotationAnimation.value))
                ..scale(degOneTranslationAnimation.value),
              alignment: Alignment.center,
              child: CircularButton(
                color: Colors.white,
                width: 50,
                height: 50,
                icon: const Icon(
                  Icons.logout,
                  color: Colors.red,
                ),
                onClick: () async {
                  appViewModel.changeLogouting();
                  const storage = FlutterSecureStorage();
                  await storage.deleteAll();
                  await appViewModel.deleteFCMToken();
                  Future.delayed(const Duration(milliseconds: 500));
                  appViewModel.changeLogouting();
                  if (context.mounted) {
                    Navigator.pop(context);
                    await Navigator.pushAndRemoveUntil(
                      context,
                      PageRouteBuilder(
                        pageBuilder: (_, __, ___) => const LoginPage(),
                      ),
                      (routes) => false,
                    );
                  }
                },
              ),
            ),
          ),
          Transform.translate(
            offset: Offset.fromDirection(getRadiansFromDegree(300),
                degOneTranslationAnimation.value * 100),
            child: Transform(
              transform: Matrix4.rotationZ(
                  getRadiansFromDegree(rotationAnimation.value))
                ..scale(degOneTranslationAnimation.value),
              alignment: Alignment.center,
              child: CircularButton(
                color: const Color.fromRGBO(244, 181, 46, 1.0),
                width: 50,
                height: 50,
                icon: const Icon(
                  Icons.manage_accounts,
                  color: Colors.white,
                ),
                onClick: () {
                  appViewModel.goModifyProfilePage();
                  Navigator.pop(context);
                },
              ),
            ),
          ),
          Transform.translate(
            offset: Offset.fromDirection(getRadiansFromDegree(240),
                degOneTranslationAnimation.value * 100),
            child: Transform(
              transform: Matrix4.rotationZ(
                  getRadiansFromDegree(rotationAnimation.value))
                ..scale(degOneTranslationAnimation.value),
              alignment: Alignment.center,
              child: CircularButton(
                color: const Color.fromRGBO(244, 142, 46, 1.0),
                width: 50,
                height: 50,
                icon: const Icon(
                  Icons.notifications,
                  color: Colors.white,
                ),
                onClick: () {
                  Navigator.pop(context);
                  appViewModel.goMessageListPage();
                },
              ),
            ),
          ),
          Transform.translate(
            offset: Offset.fromDirection(getRadiansFromDegree(180),
                degOneTranslationAnimation.value * 100),
            child: Transform(
              transform: Matrix4.rotationZ(
                  getRadiansFromDegree(rotationAnimation.value))
                ..scale(degOneTranslationAnimation.value),
              alignment: Alignment.center,
              child: CircularButton(
                color: const Color.fromRGBO(244, 79, 46, 1.0),
                width: 50,
                height: 50,
                icon: const Icon(
                  Icons.smart_toy,
                  color: Colors.white,
                ),
                onClick: () {
                  appViewModel.goYeobotPage();
                  Navigator.pop(context);
                },
              ),
            ),
          ),
          appViewModel.userInfo.timeLineId == -1
              ? Transform(
                  transform: Matrix4.rotationZ(
                      getRadiansFromDegree(rotationAnimation.value)),
                  alignment: Alignment.center,
                  child: GestureDetector(
                    onTap: () {
                      if (animationController.isCompleted) {
                        Navigator.pop(context);
                        appViewModel.startTravel(context);
                      } else {
                        animationController.forward();
                      }
                    },
                    child: Container(
                      margin: const EdgeInsets.only(bottom: 10),
                      height: 70,
                      width: 70,
                      decoration: const BoxDecoration(
                          shape: BoxShape.circle,
                          gradient: LinearGradient(
                              begin: Alignment.topLeft,
                              end: Alignment.bottomRight,
                              colors: [
                                Colors.redAccent,
                                Colors.orangeAccent,
                              ])),
                      child: const Icon(
                        Icons.airplane_ticket_outlined,
                        color: Colors.white,
                        size: 40,
                      ),
                    ),
                  ),
                )
              : Transform(
                  transform: Matrix4.rotationZ(
                      getRadiansFromDegree(rotationAnimation.value)),
                  alignment: Alignment.center,
                  child: GestureDetector(
                    onTap: () async {
                      List<Map<String, dynamic>> members = await _fetchData(appViewModel.userInfo.timeLineId);
                      // 동행 1명일 때는 포스트 동작 막기
                      if (animationController.isCompleted) {;
                        // 모여 중이 아니거나
                        if (appViewModel.userInfo.moyeoTimelineId == -1){
                            Navigator.pop(context);
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (context) => CameraView(),
                              ),
                            );
                        // 모여 중이라면 nowMember가 빈배열이 아니여야함.
                        } else if (members.length<=1) {
                          showDialog(
                            barrierDismissible: false,
                              context: context,
                              builder: (ctx) =>
                                  AlertDialog(
                                    title: const Text("혼자야?"),
                                    content: const Text("혼자서는 포스트를 등록 할 수 없습니다."),
                                    actions: [
                                      TextButton(
                                          onPressed:(){
                                            Navigator.pop(context);
                                            Navigator.push(
                                                context,
                                                MaterialPageRoute(
                                                  builder: (context) => MoyeoAddUser(members: members),
                                                )
                                            );
                                          } ,
                                          child: const Text(
                                            "동행 추가하러가기",
                                            style: TextStyle(color: Colors.orangeAccent),
                                          ),
                                      ),
                                      TextButton(
                                          onPressed:(){
                                            Navigator.pop(context);
                                          },
                                          child: const Text(
                                            "뒤로가기",
                                            style: TextStyle(color: Colors.red),
                                          ),
                                      )
                                    ],
                                  )
                          );
                        } else if (members.length>1) {
                          Navigator.pop(context);
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (context) => CameraView(),
                            ),
                          );
                        }
                      } else {
                        animationController.forward();
                      }
                    },
                    child: Container(
                      margin: const EdgeInsets.only(bottom: 10),
                      height: 70,
                      width: 70,
                      decoration: const BoxDecoration(
                          shape: BoxShape.circle,
                          gradient: LinearGradient(
                              begin: Alignment.topLeft,
                              end: Alignment.bottomRight,
                              colors: [
                                Colors.redAccent,
                                Colors.orangeAccent,
                              ])),
                      child: const Icon(
                        Icons.camera,
                        color: Colors.white,
                        size: 40,
                      ),
                    ),
                  ),
                ),
          Positioned(
              left: 0,
              right: 0,
              top: 0,
              bottom: 0,
              child: appViewModel.isLogouting == false
                  ? SizedBox.shrink()
                  : Lottie.asset(
                      'assets/lottie/logout.json',
                    ))
        ],
      );
    });
  }
}

class CircularButton extends StatelessWidget {
  final double width;
  final double height;
  final Color color;
  final Icon icon;
  final VoidCallback onClick;

  CircularButton(
      {super.key,
      required this.color,
      required this.width,
      required this.height,
      required this.icon,
      required this.onClick});

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(color: color, shape: BoxShape.circle),
      width: width,
      height: height,
      child: IconButton(
        icon: icon,
        enableFeedback: true,
        onPressed: () {
          onClick();
        },
      ),
    );
  }
}
