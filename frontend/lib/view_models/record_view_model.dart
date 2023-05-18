import 'dart:async';
import 'dart:io';
import 'dart:typed_data';

import 'package:audioplayers/audioplayers.dart';
import 'package:camera/camera.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:http_parser/http_parser.dart';
import 'package:intl/intl.dart';
import 'package:logger/logger.dart';
import 'package:moyeo/view_models/camera_view_model.dart';
import 'package:multi_image_picker_view/multi_image_picker_view.dart';
import 'package:provider/provider.dart';
import 'package:record/record.dart';

import '../models/LocationInformation.dart';
import '../models/UserInfo.dart';
import '../module/audio_player_view_model.dart';
import '../module/gradient_text.dart';
import '../module/my_alert_dialog.dart';
import '../services/upload_repository.dart';

var logger = Logger();

class RecordViewModel extends ChangeNotifier {

  late LocationInformation _locationInfo;

  AudioPlayer audioPlayer = AudioPlayer();

  LocationInformation get locationInfo => _locationInfo;




  late String _recordedFileName;
  String _recordedFilePath = "";
  late AudioPlayerViewModel audioPlayerViewModel;

  Duration _duration = const Duration(seconds: 0);
  final Duration _audioPosition = Duration.zero;



  String get recordedFileName => _recordedFileName;

  String get recordedFilePath => _recordedFilePath;

  Duration get duration => _duration;

  Duration get audioPosition => _audioPosition;

  bool _isUploading = false;

  bool get isUploading => _isUploading;

  bool _havingLocation = false;

  bool get havingLocation => _havingLocation;

  bool _isRecording = false;

  bool get isRecording => _isRecording;

  Timer? _recordingTimer;

  final record = Record();

  String fileName = DateFormat('yyyyMMdd.Hmm.ss').format(DateTime.now());

  // 녹음 메서드
  Future<void> startRecording() async {
    _isRecording = true;
    notifyListeners();
    final directory = Directory('/storage/emulated/0/Documents/records');
    if (!await directory.exists()) {
      await directory.create(recursive: true);
    }
    // 파일 저장 경로 지정
    if (recordedFilePath != "") {
      _recordedFilePath = "";
    }
    final filePath = '${directory.path}/$fileName.wav';
    // 레코딩 시작
    await record.start(path: filePath, encoder: AudioEncoder.wav);
    _recordedFileName = fileName;

    // 30초 뒤 자동으로 녹음 중단
    const maxDuration = Duration(milliseconds: 30000);

    _recordingTimer = Timer(
      maxDuration,
          () {
        stopRecording();
        _isRecording = false;
      },
    );
    changeRecordButtonColor();
    notifyListeners();
  }

  BuildContext context;

  RecordViewModel({required this.context}) {
    changeModalState();
    audioPlayerViewModel = AudioPlayerViewModel(_recordedFilePath);
  }

  // 녹음 끝 파일 저장
  Future<void> stopRecording() async {
    _isRecording = false;
    notifyListeners();
    await record.stop();
    if (_recordingTimer?.isActive == true) {
      _recordingTimer?.cancel();
    }
    final directory = Directory('/storage/emulated/0/Documents/records');

    _recordedFilePath = '${directory.path}/$recordedFileName.wav';
    audioPlayerViewModel.changeFile(_recordedFilePath);
    notifyListeners();
    changeRecordButtonColor();
    notifyListeners();
  }

  // 갤러리에서 파일 가져오기
  Future<void> uploadFileFromGallery(List allFileList) async {
    // 길이가 9 이상이면 작동하지 않음
    if (allFileList.length >= 9) {
      return;
    }

    // multi_image_picker_viewr 라이브러리 사용
    final pickerController = MultiImagePickerController(
        maxImages: 9 - allFileList.length, images: []);
    Directory externalDirectory =
    Directory('/storage/emulated/0/Documents/photos');
    await pickerController.pickImages();
    // 파일들을 저장해서 경로를 만들고 xFile로 불러옴
    for (final image in pickerController.images) {
      if (image.hasPath) {
        var imageFile = File(image.path!);
        Uint8List imageUint8 = await imageFile.readAsBytes();
        List<int> imageData = imageUint8.toList();
        String? fileName = image.name;
        String tempPath = '${externalDirectory.path}/$fileName';
        await File(tempPath).writeAsBytes(imageData);
        XFile xFile = XFile(tempPath);
        allFileList.add(xFile);
      }
    }
    // 변했다고 알려줌
    notifyListeners();
  }

  // 파일을 서버로 업로드하기
  Future<void> postFiles(BuildContext context, UserInfo userInfo, List allFileList , LocationInformation locationInfo,
      Function move) async {
    final flag = MultipartFile.fromBytes(locationInfo.flag!,
        filename: locationInfo.country, contentType: MediaType('image', 'jpg'));
    final List<MultipartFile> imageFiles = allFileList
        .map((el) =>
        MultipartFile.fromFileSync(el.path,
            filename: el.name, contentType: MediaType('image', 'jpg')))
        .toList();
    final audioFile = await MultipartFile.fromFile(recordedFilePath,
        filename: "$recordedFileName.wav",
        contentType: MediaType('audio', 'wav'));

    FormData formData = FormData.fromMap({
      'flagFile': flag,
      'imageFiles': imageFiles,
      'voiceFile': audioFile,
      'timelineId': userInfo.moyeoTimelineId == -1
          ? userInfo.timeLineId
          : userInfo.moyeoTimelineId,
      'address1': locationInfo.country,
      'address2': locationInfo.address2,
      'address3': locationInfo.address3,
      'address4': locationInfo.address4
    });
    _isUploading = true;
    notifyListeners();
    if (context.mounted) {
      userInfo.moyeoTimelineId == -1
          ? await UploadRepository().uploadToServer(context, formData)
          : await UploadRepository().uploadToMoyeo(context, formData);
    }
    _isUploading = false;
    notifyListeners();
    allFileList.clear();
    locationInfo = LocationInformation(
      country: "",
      address2: "",
      address3: "",
      address4: "",
      flag: null,
    );
    if (context.mounted) {
      Navigator.pop(context);
      Navigator.pop(context);
      move(userInfo.timeLineId);
    }
  }

// 위치를 받아 오는 함수


  void uploadConfirm(BuildContext context, UserInfo myInfo, Function move, LocationInformation locationInfo, List allFileList) {
    if (allFileList.isEmpty) {
      OneButtonMaterialDialog().showFeedBack(context, "사진을 등록해주세요");
      return;
    }

    if (_recordedFilePath == "") {
      OneButtonMaterialDialog().showFeedBack(context, "음성을 녹음해주세요");
      return;
    }

    if (locationInfo.flag == null) {
      OneButtonMaterialDialog().showFeedBack(context, "위치를 불러오지 못했습니다.");
      return;
    }
    final alert = AlertDialog(
      content: const Text(
        "포스트를 \n등록할까요?",
        style: TextStyle(fontSize: 25),
      ),
      actions: [
        GestureDetector(
            child: const GradientText(
              "등록할게요",
              gradient: [Colors.blueAccent, Colors.purple],
              style: TextStyle(
                  fontSize: 12
              ),
            ),
            onTap: () {
              Navigator.of(context).pop();
              postFiles(context, myInfo, allFileList, locationInfo, move);
            }),
        GestureDetector(
            child: const GradientText(
              "잠깐만요",
              gradient: [Colors.orangeAccent, Colors.purpleAccent],
              style: TextStyle(
                  fontSize: 12
              ),
            ),
            onTap: () {
              Navigator.of(context).pop();
            })
      ],
    );

    showDialog(
      context: context,
      builder: (BuildContext context) {
        return alert;
      },
    );
  }

  List<Color> _recordButtonGradient = [
    Colors.orangeAccent,
    Colors.redAccent,
    Colors.purpleAccent
  ];

  List<Color> get recordButtonGradient => _recordButtonGradient;

  void changeRecordButtonColor() {
    if (_isRecording == true) {
      _recordButtonGradient = [
        const Color.fromRGBO(146, 240, 162, 1.0),
        const Color.fromRGBO(98, 218, 140, 1.0),
        const Color.fromRGBO(117, 234, 195, 1.0),
      ];

    } else {
      _recordButtonGradient = [
        Colors.orangeAccent,
        Colors.redAccent,
        Colors.purpleAccent
      ];
    }
    notifyListeners();
  }

  void changeUploading() {
    if (_isUploading == true) {
      _isUploading = false;
    }
  }

  bool _isNewbie = true;
  bool get isNewbie => _isNewbie;
  void changeModalState() {
    Timer(const Duration(seconds: 3), () {
      _isNewbie = false;
      notifyListeners();
    },
    );
  }
}