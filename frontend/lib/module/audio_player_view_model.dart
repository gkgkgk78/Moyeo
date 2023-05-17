import 'package:audioplayers/audioplayers.dart';
import 'package:flutter/material.dart';
import 'package:logger/logger.dart';

var logger = Logger();

class AudioPlayerViewModel extends ChangeNotifier {
  // 파일 경로
  String? _audioFilePath;
  bool _isPlaying = false;
  Duration _duration = const Duration(microseconds: 0);

  AudioPlayer? _audioPlayer = AudioPlayer();
  AudioPlayer? get audioPlayer => _audioPlayer;

  Duration _audioPosition = Duration.zero;

  String? get getAudioFilePath => _audioFilePath;

  bool get isPlaying => _isPlaying;

  Duration get duration => _duration;

  Duration get audioPosition => _audioPosition;

  // optional parameter
  AudioPlayerViewModel(this._audioFilePath) {
    _audioPlayer?.onDurationChanged.listen((newDuration) {
      _duration = newDuration;
      notifyListeners();
    });
    if (_audioFilePath != '') initDuration();
  }

  Future<void> initDuration() async {
    await _audioPlayer?.setSourceUrl(_audioFilePath!);
    _duration = (await _audioPlayer?.getDuration())!;
    notifyListeners();
  }

  void changeFile(String filePath) async {
    _audioPosition = Duration.zero;
    _audioFilePath=filePath;
    await _audioPlayer?.setSourceUrl(filePath);
    notifyListeners();
  }

  // 재생 시작
  Future<void> playRecordedFile() async {
    if (_isPlaying == true ||
        getAudioFilePath == null ||
        getAudioFilePath!.isEmpty) {
      return;
    } else {
      await _audioPlayer?.play(DeviceFileSource(_audioFilePath!));
      _isPlaying = true;

      notifyListeners();
      // 재생이 완료 되었을 때 변수 바꿔주기
      _audioPlayer?.onPlayerComplete.listen((event) {
        _isPlaying = false;
        notifyListeners();
      });

      // 음성 파일에서의 현재 위치 갱신
      _audioPlayer?.onPositionChanged.listen((curPos) {
        _audioPosition = curPos;
        notifyListeners();
      });
    }
  }

  // 일시정지
  Future<void> pauseRecordedFile() async {
    if (_isPlaying == true) {
      _audioPlayer?.pause();
      _isPlaying = false;
      notifyListeners();
    } else {
      return;
    }
  }

  // 계속 재생
  Future<void> resumeRecordedFile() async {
    if (_isPlaying == false) {
      _audioPlayer?.resume();
      _isPlaying = true;
      notifyListeners();
    } else {
      return;
    }
  }

  // 완전 멈춤
  Future<void> stopRecordedFile() async {
    if (_isPlaying == true) {
      _audioPlayer?.stop();
      _isPlaying = false;
      notifyListeners();
    } else {
      return;
    }
  }

  saveAudio(String audioFilePath) {
    _audioFilePath = audioFilePath;
    _audioPosition = Duration.zero;
    initDuration();
    notifyListeners();
  }

  // 위치 갱신
  void updateAudioPosition(Duration position) {
    _audioPosition = position;
    notifyListeners();
  }

  // 현재 위치 참조
  Future<void> seekTo(Duration position) async {
    await _audioPlayer?.seek(position);
    updateAudioPosition(position);
  }

  getDurationTimeToString() {
    return timeToString(duration.inSeconds);
  }

  getAudioPosTimeToString() {
    return timeToString(audioPosition.inSeconds);
  }

  timeToString(int time) {
    if (time <= 9) return '00:0$time';
    return '00:$time';
  }

  @override
  void dispose() {
    _audioFilePath = null;
    stopRecordedFile();
    _duration = Duration.zero;
    _audioPosition = Duration.zero;
  }
}
