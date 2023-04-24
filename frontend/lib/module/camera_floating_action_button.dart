import 'package:danim/view_models/app_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../view_models/camera_view_model.dart';
import '../views/camera_screen.dart';

class CameraFloatingActionButton extends StatelessWidget {
  const CameraFloatingActionButton({super.key});

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 70.0,
      width: 70.0,
      child: FittedBox(
        child: Consumer<AppViewModel>(
          builder: (_, appViewModel, __) {
            return FloatingActionButton(
              child: appViewModel.userInfo.timeLineId == -1
                  ? Image.asset(
                      'assets/images/btn.png',
                      // width: 20,
                      // height: 50,
                      fit: BoxFit.fitWidth,
                    )
                  : const Icon(
                      Icons.camera,
                      color: Colors.white,
                    ),
              onPressed: () {
                if (appViewModel.userInfo.timeLineId == -1) {
                  // 여행 중이 아닐 때 여행 시작
                  appViewModel.startTravel(context);
                } else {
                  // 여행 중일 때 사진 촬영 화면으로 이동
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => ChangeNotifierProvider(
                        create: (_) => CameraViewModel(),
                        child: const CameraView(),
                      ),
                    ),
                  );
                }
              },
            );
          },
        ),
      ),
    );
  }
}
