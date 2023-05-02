import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../view_models/app_view_model.dart';
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
            return
                //   FloatingActionButton(
                //   onPressed: () {
                //     Navigator.push(
                //       context,
                //       MaterialPageRoute(
                //         builder: (context) => CameraView(),
                //       ),
                //     );
                //   },
                //   child: Container(
                //     height: 70,
                //     width: 70,
                //     decoration: const BoxDecoration(
                //         shape: BoxShape.circle,
                //         gradient: LinearGradient(
                //             begin: Alignment.topLeft,
                //             end: Alignment.bottomRight,
                //             colors: [
                //               Colors.redAccent,
                //               Colors.orangeAccent,
                //             ]
                //         )
                //     ),
                //     child: const Icon(
                //       Icons.camera,
                //       color: Colors.white,
                //       size: 40,
                //     ),
                //   ),
                // );

                FloatingActionButton(
              child: appViewModel.userInfo.timeLineId == -1
                  ? Image.asset(
                      'assets/images/transparent_logo.png',
                      width: 50,
                      height: 50,
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
                      builder: (context) => CameraView(),
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
