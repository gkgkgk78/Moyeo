import 'package:danim/module/my_alert_dialog.dart';
import 'package:danim/module/custom_app_bar.dart';
import 'package:danim/view_models/app_view_model.dart';
import 'package:flutter/material.dart';
import 'package:lottie/lottie.dart';
import 'package:provider/provider.dart';

import '../module/audio_player_view.dart';
import '../module/audio_player_view_model.dart';
import '../module/images_page_view.dart';
import '../view_models/camera_view_model.dart';

class RecordView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Consumer<AppViewModel>(
      builder: (_, appViewModel, __) {
        return Consumer<CameraViewModel>(
          builder: (_, cameraViewModel, __) {
            return WillPopScope(
              onWillPop: () async {
                appViewModel.changeTitleToFormer();
                return true;
              },
              child: Scaffold(
                appBar: CustomAppBar(
                  moveToModifyProfile: () {
                    Navigator.pop(context);
                    Navigator.pop(context);
                    appViewModel.goModifyProfilePage();
                  },
                  moveToYeobot: () {
                    Navigator.pop(context);
                    Navigator.pop(context);
                    appViewModel.goYeobotPage();
                },
                ),
                body: Stack(
                  children: [
                    Column(
                      children: [
                        // 캐러셀
                        SizedBox(
                          height: MediaQuery.of(context).size.height * 0.55,
                          // 컨슈머로 변화 감지
                          child: ImagesPageView(
                              listXFile: cameraViewModel.allFileList),
                        ),
                        Column(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            Container(
                              margin: const EdgeInsets.only(top: 25),
                              padding:
                              const EdgeInsets.only(left: 30, right: 30),
                              height: 30,
                              child: !cameraViewModel.havingLocation
                                  ? Row(
                                mainAxisAlignment:
                                MainAxisAlignment.center,
                                children: [
                                  Expanded(
                                    flex: 1,
                                    child: cameraViewModel
                                        .locationInfo.flag !=
                                        null
                                        ? ClipRRect(
                                      borderRadius:
                                      BorderRadius.circular(20),
                                      child: Image.memory(
                                        cameraViewModel
                                            .locationInfo.flag!,
                                        fit: BoxFit.fitHeight,
                                      ),
                                    )
                                        : Container(),
                                  ),
                                  const SizedBox(
                                    width: 10,
                                  ),
                                  Expanded(
                                      flex: 5,
                                      child: Text(
                                          '${cameraViewModel.locationInfo.country} ${cameraViewModel.locationInfo.address2}'))
                                ],
                              )
                                  : const SizedBox(
                                  width: 30,
                                  child: CircularProgressIndicator(
                                    strokeWidth: 3.0,
                                  )),
                            ),

                            Container(
                              margin: const EdgeInsets.only(top: 10),
                              child:
                              ChangeNotifierProvider<AudioPlayerViewModel>(
                                create: (_) =>
                                cameraViewModel.audioPlayerViewModel,
                                child: AudioPlayerView(),
                              ),
                            ),
                            // 버튼 컨테이너
                            Container(
                              padding: const EdgeInsets.only(top: 26),
                              child: Row(
                                mainAxisAlignment:
                                MainAxisAlignment.spaceBetween,
                                children: [
                                  Container(
                                    margin: const EdgeInsets.only(left: 50),
                                    width: 50,
                                    height: 50,
                                    decoration: const BoxDecoration(
                                      shape: BoxShape.circle,
                                      color: Colors.green,
                                    ),
                                    child: IconButton(
                                      onPressed: () {
                                        if (cameraViewModel
                                            .allFileList.length >=
                                            9) {
                                          OneButtonMaterialDialog()
                                              .showFeedBack(context,
                                              "이미지는 \n최대 9장까지 \n등록 가능합니다.");
                                        } else {
                                          cameraViewModel
                                              .uploadFileFromGallery();
                                        }
                                      },
                                      icon: const Icon(Icons.photo_outlined),
                                      color: Colors.white,
                                    ),
                                  ),

                                  // 녹음 버튼
                                  SizedBox(
                                    width: 70,
                                    height: 70,
                                    child: GestureDetector(
                                      onTapDown: (_) {
                                        cameraViewModel.startRecording();
                                      },
                                      onTapUp: (_) {
                                        cameraViewModel.stopRecording();
                                      },
                                      child: Container(
                                        decoration: BoxDecoration(
                                            borderRadius:
                                            BorderRadius.circular(40),
                                            color:
                                            cameraViewModel.buttonColor()),
                                        child: const Icon(
                                          Icons.multitrack_audio,
                                          color: Colors.white,
                                          size: 50,
                                        ),
                                      ),
                                    ),
                                  ),

                                  Container(
                                    margin: const EdgeInsets.only(right: 50),
                                    width: 50,
                                    height: 50,
                                    decoration: const BoxDecoration(
                                      shape: BoxShape.circle,
                                      color: Colors.blueAccent,
                                    ),
                                    child: IconButton(
                                      onPressed: () {
                                        cameraViewModel.uploadConfirm(
                                          context,
                                          appViewModel.userInfo,
                                          appViewModel
                                              .goToTravelingTimelinePage,
                                        );
                                      },
                                      icon: const Icon(Icons.upload, size: 28),
                                      color: Colors.white,
                                    ),
                                  )
                                ],
                              ),
                            ),
                          ],
                        )
                      ],
                    ),
                    cameraViewModel.isRecording
                        ? Center(
                      child: Opacity(
                        opacity: 0.5,
                        child: Container(
                          height: 78,
                          width: MediaQuery.of(context).size.width,
                          child: Lottie.asset(
                              'assets/lottie/wave_lottie_animation.json',
                              fit: BoxFit.cover),
                        ),
                      ),
                    )
                        : const SizedBox.shrink(),
                    !cameraViewModel.isUploading
                        ? const SizedBox.shrink()
                        : Center(
                      child: Container(
                        decoration: BoxDecoration(
                            color: Colors.black54,
                            borderRadius: BorderRadius.circular(10)),
                        width: 100,
                        height: 100,
                        child: Column(
                          mainAxisAlignment:
                          MainAxisAlignment.spaceEvenly,
                          children: const [
                            Text(
                              "업로드 중...",
                              style: TextStyle(color: Colors.white),
                            ),
                            Center(
                              child: CircularProgressIndicator(),
                            ),
                          ],
                        ),
                      ),
                    )
                  ],
                ),
              ),
            );
          },
        );
      },
    );
  }
}
