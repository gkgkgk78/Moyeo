import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import './audio_player_view_model.dart';
import 'custom_track_shape.dart';

class AudioPlayerView extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(top: 10, bottom: 10),
      child: Consumer<AudioPlayerViewModel>(
        builder: (context, viewModel, child) {
          return Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              // 재생 버튼
              IconButton(
                  onPressed: () {
                    // playSound
                    if (viewModel.isPlaying) {
                      viewModel.pauseRecordedFile();
                    } else {
                      viewModel.playRecordedFile();
                    }
                  },
                  icon: Icon(
                    viewModel.isPlaying ? Icons.pause : Icons.play_arrow,
                    color: Colors.black,
                  )),
              // 프로그레스 바
              Expanded(
                  child: viewModel.duration != const Duration(microseconds: 0)
                      ? Container(
                          margin: const EdgeInsets.only(right: 15.0),
                          height: 11,
                          decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(5),
                              gradient: const LinearGradient(
                                  begin: Alignment.topLeft,
                                  end: Alignment.bottomRight,
                                  colors: [
                                    Colors.redAccent,
                                    Colors.deepPurpleAccent,
                                    Colors.orangeAccent,
                                  ])),
                          child: SliderTheme(
                            data: SliderThemeData(
                              trackShape: CustomTrackShape(),
                              thumbColor: Colors.transparent,
                              thumbShape: const RoundSliderThumbShape(
                                  enabledThumbRadius: 20,
                                  disabledThumbRadius: 0,
                                  elevation: 0.0,
                                  pressedElevation: 10.0),
                              overlayColor: Colors.transparent,
                              trackHeight: 14,
                              activeTrackColor: Colors.transparent,
                              inactiveTrackColor: Colors.white,
                            ),
                            child: Slider(
                              // 현재 위치
                              value: viewModel.audioPosition.inMicroseconds
                                  .toDouble(),
                              // 최대 길이 = 음성 파일 길이
                              max: viewModel.duration.inMicroseconds.toDouble(),
                              onChanged: (value) {
                                // 위치 지속적으로 갱신
                                final position =
                                    Duration(microseconds: value.toInt());
                                viewModel.seekTo(position);
                              },
                            ),
                          ),
                        )
                      : Container(
                          margin: const EdgeInsets.only(right: 15.0),
                          height: 11,
                          decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(5),
                              gradient: const LinearGradient(colors: [
                                Colors.redAccent,
                                Colors.deepPurpleAccent,
                                Colors.orangeAccent,
                              ])),
                          child: SliderTheme(
                            data: SliderThemeData(
                                trackShape: CustomTrackShape(),
                                overlayShape: SliderComponentShape.noOverlay,
                                thumbColor: Colors.transparent,
                                thumbShape: const RoundSliderThumbShape(
                                    enabledThumbRadius: 20),
                                overlayColor: Colors.blueAccent,
                                trackHeight: 14,
                                activeTrackColor: Colors.transparent,
                                inactiveTrackColor: Colors.white),
                            child: Slider(
                              // 현재 위치
                              value: viewModel.audioPosition.inMicroseconds
                                  .toDouble(),
                              // 최대 길이 = 음성 파일 길이
                              max: viewModel.duration.inMicroseconds.toDouble(),
                              onChanged: (value) {
                                // 위치 지속적으로 갱신
                                final position =
                                    Duration(microseconds: value.toInt());
                                viewModel.seekTo(position);
                              },
                            ),
                          ),
                        )),
              Text(
                '${viewModel.getAudioPosTimeToString()} /',
              ),
              Text(
                '${viewModel.getDurationTimeToString()}',
              ),
              const SizedBox(
                width: 10,
              )
            ],
          );
        },
      ),
    );
  }
}
