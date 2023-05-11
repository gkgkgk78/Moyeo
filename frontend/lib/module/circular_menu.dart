import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:logger/logger.dart';
import 'package:moyeo/view_models/app_view_model.dart';
import 'package:provider/provider.dart';

import '../views/camera_screen.dart';
import '../views/login_page.dart';

var logger = Logger();

class CustomCircularMenu extends StatefulWidget {
  const CustomCircularMenu({super.key});

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
                  Navigator.pop(context);
                  const storage = FlutterSecureStorage();
                  storage.deleteAll();
                  FirebaseMessaging.instance.deleteToken();
                  Future.delayed(const Duration(seconds: 5));
                  if (context.mounted) {
                    Navigator.pushAndRemoveUntil(
                      context,
                      PageRouteBuilder(
                        pageBuilder: (_, __, ___) => const LoginPage(),
                      ),
                          (routes) => false,
                    );
                  }
                  logger.d("로그아웃 완료");
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
                  Icons.edit,
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
                    onTap: () {
                      if (animationController.isCompleted) {
                        Navigator.pop(context);
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) => CameraView(),
                          ),
                        );
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
