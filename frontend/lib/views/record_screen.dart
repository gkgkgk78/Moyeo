import 'package:danim/module/my_alert_dialog.dart';
import 'package:danim/module/custom_app_bar.dart';
import 'package:danim/view_models/app_view_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../module/audio_player_view.dart';
import '../module/audio_player_view_model.dart';
import '../module/images_page_view.dart';
import '../view_models/record_view_model.dart';

class RecordView extends StatelessWidget {
  const RecordView({super.key});

  @override
  Widget build(BuildContext context) {
    return Consumer<AppViewModel>(
      builder: (_, appViewModel, __) {
        return Consumer<RecordViewModel>(
          builder: (_, recordViewModel, __) {
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
                              listXFile: recordViewModel.imageList),
                        ),
                        Column(
                          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                          children: [
                            Container(
                              margin: const EdgeInsets.only(top: 25),
                              padding: const EdgeInsets.only(left: 30, right: 30),
                              height: 30,
                              child:
                              !recordViewModel.havingLocation ?
                              Row(
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  Expanded(
                                    flex: 1,
                                    child: recordViewModel.locationInfo.flag !=
                                        null
                                        ? ClipRRect(
                                      borderRadius:
                                      BorderRadius.circular(20),
                                      child: Image.memory(
                                        recordViewModel.locationInfo.flag!,
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
                                          '${recordViewModel.locationInfo.country} ${recordViewModel.locationInfo.address2}'))
                                ],
                              )
                                  : const SizedBox(
                                  width: 30,
                                  child: CircularProgressIndicator(strokeWidth: 3.0,))
                              ,
                            ),
                            Container(
                              margin: const EdgeInsets.only(top: 10),
                              child: ChangeNotifierProvider<AudioPlayerViewModel>(
                                create: (_) => recordViewModel.audioPlayerViewModel,
                                child: AudioPlayerView(),
                              ),
                            ),
                            // 버튼 컨테이너
                            Container(
                              padding: const EdgeInsets.only(top: 26),
                              child: Row(
                                mainAxisAlignment: MainAxisAlignment.spaceBetween,
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
                                        if (recordViewModel.imageList.length >= 9) {
                                          OneButtonMaterialDialog().showFeedBack(
                                              context,
                                              "이미지는 \n최대 9장까지 \n등록 가능합니다.");
                                        } else {
                                          recordViewModel.uploadFileFromGallery();
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
                                    child: ColoredContainer(
                                      defaultColor: Colors.redAccent,
                                      highlightColor: Colors.greenAccent,
                                      onTapDown: () {
                                        recordViewModel.startRecording();
                                      },
                                      onTapUp: () {
                                        recordViewModel.stopRecording();
                                      },
                                      child: const Icon(
                                        Icons.multitrack_audio,
                                        color: Colors.white,
                                        size: 50,
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
                                        recordViewModel.uploadConfirm(
                                          context,
                                          appViewModel.userInfo,
                                          appViewModel.goToTravelingTimelinePage,
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
                    !recordViewModel.isUploading
                        ? const SizedBox.shrink()
                        : Center(
                            child: Container(
                              decoration: BoxDecoration(
                                color: Colors.black54,
                                borderRadius: BorderRadius.circular(10)
                              ),
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

// 버튼을 클릭하고 있을 때에 색상이 바뀌는 컨테이너
class ColoredContainer extends StatefulWidget {
  final Widget child;
  final Color defaultColor;
  final Color highlightColor;
  final VoidCallback? onTapDown;
  final VoidCallback? onTapUp;

  const ColoredContainer({
    Key? key,
    required this.child,
    required this.defaultColor,
    required this.highlightColor,
    this.onTapDown,
    this.onTapUp,
  }) : super(key: key);

  @override
  _ColoredContainerState createState() => _ColoredContainerState();
}

class _ColoredContainerState extends State<ColoredContainer> {
  Color _color = Colors.transparent;

  @override
  Widget build(BuildContext context) {
    // GestureDetector로 감지
    return GestureDetector(
      onTapDown: (_) {
        if (widget.onTapDown != null) {
          widget.onTapDown!();
        }
        setState(() {
          _color = widget.highlightColor;
        });
      },
      onTapUp: (_) {
        if (widget.onTapUp != null) {
          widget.onTapUp!();
        }
        setState(() {
          _color = widget.defaultColor;
        });
      },
      onTapCancel: () {
        setState(() {
          _color = widget.defaultColor;
        });
      },
      child: Container(
        decoration: BoxDecoration(
          shape: BoxShape.circle,
          color: _color != Colors.transparent ? _color : widget.defaultColor,
        ),
        child: widget.child,
      ),
    );
  }
}
