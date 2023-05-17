import 'package:flutter/material.dart';
import 'package:logger/logger.dart';
import 'package:moyeo/module/circular_menu.dart';
import 'package:provider/provider.dart';

import '../view_models/app_view_model.dart';
import '../views/camera_screen.dart';

var logger = Logger();

class CameraFloatingActionButton extends StatelessWidget {
  const CameraFloatingActionButton({super.key});

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 70.0,
      width: 70.0,
      child: Consumer<AppViewModel>(
        builder: (_, appViewModel, __) {
          return FloatingActionButton(
            child:
                // appViewModel.userInfo.timeLineId == -1
                //     ? Image.asset(
                //   'assets/images/transparent_logo.png',
                //   width: 50,
                //   height: 50,
                // )
                //     :
                Container(
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
            onPressed: () {
              appViewModel.chageModalVisible();
              showDialog(
                context: context,
                builder: (BuildContext dialogContext) {
                  return GestureDetector(
                    onTap: () {
                      Navigator.pop(dialogContext);
                    },
                    child: Stack(
                      children: [
                        Dialog(
                          shadowColor: Colors.transparent,
                          backgroundColor: Colors.transparent,
                          child: SizedBox(
                            height: MediaQuery.of(context).size.height,
                            width: double.infinity,
                            child: Container(
                                height: 250,
                                decoration: const BoxDecoration(
                                    color: Colors.transparent),
                                child: CustomCircularMenu(userInfo: appViewModel.userInfo)),
                          ),
                        ),
                      ],
                    ),
                  );
                },
              ).then((_) => appViewModel.chageModalVisible());
              // if (appViewModel.userInfo.timeLineId == -1) {
              //   // 여행 중이 아닐 때 여행 시작
              //   appViewModel.startTravel(context);
              // } else {
              //   // 여행 중일 때 사진 촬영 화면으로 이동
              //   Navigator.push(
              //     context,
              //     MaterialPageRoute(
              //       builder: (context) => CameraView(),
              //     ),
              //   );
              // }
            },
          );
        },
      ),
    );
  }
}
