import 'package:flutter/material.dart';
import 'package:logger/logger.dart';
import 'package:lottie/lottie.dart';
import 'package:provider/provider.dart';

import '../module/audio_player_view.dart';
import '../module/audio_player_view_model.dart';
import '../module/custom_app_bar.dart';
import '../module/images_page_view.dart';
import '../module/my_alert_dialog.dart';
import '../view_models/app_view_model.dart';
import '../view_models/camera_view_model.dart';
import '../view_models/record_view_model.dart';

var logger = Logger();

class RecordView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Consumer<AppViewModel>(
      builder: (_, appViewModel, __) {
        return Consumer<CameraViewModel>(
          builder: (_, cameraViewModel, __) {
            return Consumer<RecordViewModel>(
              builder: (_, recordViewModel, __) {
                return WillPopScope(
                  onWillPop: () async {
                    appViewModel.changeTitleToFormer();
                    recordViewModel.changeUploading();
                    return true;
                  },
                  child: Scaffold(
                    appBar: const CustomAppBar(),
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
                                    recordViewModel.audioPlayerViewModel,
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
                                          gradient: LinearGradient(
                                              begin: Alignment.topRight,
                                              end: Alignment.bottomLeft,
                                              colors: [
                                                Colors.lightGreen,
                                                Colors.green,
                                                Color.fromRGBO(189, 241, 176, 1.0),
                                              ]),
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
                                              recordViewModel
                                                  .uploadFileFromGallery(cameraViewModel.allFileList);
                                            }
                                          },
                                          icon: const Icon(Icons.folder_copy),
                                          color: Colors.white,
                                        ),
                                      ),

                                      // 녹음 버튼
                                      SizedBox(
                                        width: 70,
                                        height: 70,
                                        child: GestureDetector(
                                          onTapDown: (_) {
                                            recordViewModel.startRecording();
                                          },
                                          onTapUp: (_) {
                                            recordViewModel.stopRecording();
                                          },
                                          child: Container(
                                            decoration: BoxDecoration(
                                                borderRadius:
                                                BorderRadius.circular(40),
                                                gradient: LinearGradient(
                                                    colors: recordViewModel
                                                        .recordButtonGradient)),
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
                                          gradient: LinearGradient(
                                              begin: Alignment.topLeft,
                                              end: Alignment.bottomRight,
                                              colors: [
                                                Colors.lightBlue,
                                                Colors.blue,
                                                Colors.lightBlueAccent,
                                              ]),
                                        ),
                                        child: IconButton(
                                          onPressed: () {
                                            recordViewModel.uploadConfirm(
                                              context,
                                              appViewModel.userInfo,
                                              appViewModel
                                                  .goToTravelingTimelinePage,
                                              cameraViewModel.locationInfo,
                                              cameraViewModel.allFileList
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
                        recordViewModel.isRecording
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
                        !recordViewModel.isUploading
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
                        ),
                        appViewModel.userInfo.timelineNum == 1 && recordViewModel.isNewbie == true
                            ? Center(
                          child: Container(
                            width: 200,
                            height: 200,
                            decoration: BoxDecoration(
                                color: Colors.black54,
                                borderRadius: BorderRadius.circular(20)
                            ),
                            child: Column(
                              children: [
                                SizedBox(
                                  width: 120,
                                  height: 120,
                                  child: Lottie.asset(
                                    'assets/lottie/presshold.json',
                                    fit: BoxFit.contain,
                                  ),
                                ),
                                const Text(
                                  "녹음하실 때엔\n버튼을 꾸욱~ 누르고\n말씀해주세요!",
                                  style: TextStyle(color: Colors.white),
                                  textAlign: TextAlign.center,
                                )
                              ],
                            ),
                          ),
                        )
                            : const SizedBox.shrink(),
                      ],
                    ),
                  ),
                );
              }
            );
          },
        );
      },
    );
  }
}
