import 'package:flutter/material.dart';
import 'package:moyeo/module/circular_menu.dart';
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
      child: Consumer<AppViewModel>(
        builder: (_, appViewModel, __) {
          return FloatingActionButton(
            child:
                GestureDetector(
              child: Container(
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
                        ],),),

                child:
                    appViewModel.userInfo.timeLineId == -1
                ? const Icon(
                      Icons.airplane_ticket_outlined,
                      color: Colors.white,
                      size: 40,
                    )
                : const Icon(
                  Icons.camera,
                  color: Colors.white,
                  size: 40,
                ),
              ),
                  onTap: () async {
                    // 여행 중이 아니면 여행 시작
                    if (appViewModel.userInfo.timeLineId == -1) {
                      appViewModel.startTravel(context);
                      // 여행중이고, 모여 중이고 멤버가 나 혼자면
                    } else if (appViewModel.userInfo.moyeoTimelineId != -1 && (appViewModel.timelineInfo.members?.length)! <= 1){
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
                                    },
                                    child: const Text(
                                      "뒤로가기",
                                      style: TextStyle(color: Colors.red),
                                    ),
                                  )
                                ],
                              )
                      );
                      // 여행중이고, 모여가 아니거나 모여인데 일행이 두 명 이상이면
                    } else {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => CameraView(),
                        ),
                      );
                    }
                  },
                  onLongPress: () {
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
                                      child: CustomCircularMenu(
                                          userInfo: appViewModel.userInfo)),
                                ),
                              ),
                            ],
                          ),
                        );
                      },
                    ).then((_) => appViewModel.chageModalVisible());
                  },
            ),
            onPressed: () {},
          );
        },
      ),
    );
  }
}
