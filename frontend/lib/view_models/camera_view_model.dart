import 'dart:async';
import 'dart:io';
import 'dart:core';
import 'dart:typed_data';

import 'package:audioplayers/audioplayers.dart';
import 'package:camera/camera.dart';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:geolocator/geolocator.dart';
import 'package:http_parser/http_parser.dart';
import 'package:intl/intl.dart';
import 'package:logger/logger.dart';
import 'package:moyeo/module/gradient_text.dart';
import 'package:multi_image_picker_view/multi_image_picker_view.dart';
import 'package:record/record.dart';

import 'package:flutter/foundation.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:device_info_plus/device_info_plus.dart';

import '../models/LocationInformation.dart';
import '../models/UserInfo.dart';
import '../module/audio_player_view_model.dart';
import '../module/my_alert_dialog.dart';
import '../services/upload_repository.dart';

var logger = Logger();

final deviceInfoPlugin = DeviceInfoPlugin();

class CameraViewModel extends ChangeNotifier {
  List<CameraDescription> _cameras = [];
  late CameraController _controller;
  late String _imagePath;

  bool _isTaking = false;
  bool get isTaking => _isTaking;

  List<XFile> _allFileList = [];

  List<XFile> get allFileList => _allFileList;

  CameraController get controller => _controller;

  List<CameraDescription> get cameras => _cameras;

  String get imagePath => _imagePath;

  bool _cameraLoading = true;
  bool get cameraLoading => _cameraLoading;

  // double _zoomLevel = 1.0;
  // double get zoomLevel => _zoomLevel;


  final record = Record();
  String fileName = DateFormat('yyyyMMdd.Hmm.ss').format(DateTime.now());


  CameraViewModel() {
    initializeCamera();
    audioPlayerViewModel = AudioPlayerViewModel(_recordedFilePath);
  }

  Future<void> initializeCamera() async {
    AndroidDeviceInfo deviceInfo = await deviceInfoPlugin.androidInfo;
    final sdkInfo = deviceInfo.version.sdkInt;
    if (sdkInfo >= 33) {
      await Permission.camera.request();
      await Permission.photos.request();
      await Permission.videos.request();
      await Permission.audio.request();
      await Permission.manageExternalStorage.request();
      await Permission.location.request();
    } else {
      await Permission.camera.request();
      await Permission.storage.request();
      await Permission.manageExternalStorage.request();
      await Permission.location.request();
    }

    final cameras = await availableCameras();
    _cameras = cameras;
    if (_cameras.isNotEmpty) {
      _controller = CameraController(_cameras[0], ResolutionPreset.max);
      await _controller.initialize();
      await _controller.setFlashMode(FlashMode.off);
      notifyListeners();
    }
    _cameraLoading = false;
  }

  Future<void> takePhoto() async {
    if (_allFileList.length < 9 ) {
      if (_isTaking == false) {
        _isTaking = true;
        notifyListeners();
        if (_allFileList.isEmpty) {
          getLocation();
        }
        XFile file = await _controller.takePicture();
        _isTaking = false;

        _allFileList.add(file);
        notifyListeners();

        // 파일 저장할 위치 지정
        Directory externalDirectory =
        Directory('/storage/emulated/0/Documents/photos');
        if (!await externalDirectory.exists()) {
          await externalDirectory.create(recursive: true);
        }

        final List<int> imageBytes = await file.readAsBytes();

        String dir = externalDirectory.path;
        final savePath = "$dir/$fileName.jpg";

        // 파일 생성
        final File imageFile = File(savePath);

        // 파일에 이미지 저장
        await imageFile.writeAsBytes(imageBytes);
      }
    }
    notifyListeners(); // Add this line
  }

  Future<void> changeCamera() async{
    if (_controller.description == _cameras[0]) {
      CameraController newController = CameraController(_cameras[1], ResolutionPreset.max);
      await _controller.dispose();
      _controller = newController;
      await _controller.initialize();
      notifyListeners();
    } else {
      CameraController newController = CameraController(_cameras[0], ResolutionPreset.ultraHigh);
      await _controller.dispose();
      _controller = newController;
      await _controller.initialize();
      notifyListeners();
    }
  }

  // 줌 기능
  // Future<void> changeZoomLevel(double scale) async {
  //   logger.d(scale);
  //   _zoomLevel = scale;
  //   _controller.setZoomLevel(_zoomLevel);
  //   notifyListeners();
  // }

  // 촬영 페이지 관련 기능
  //////////////////////////////////////////////////////////////////////////////////////
  // 녹음 페이지 관련 기능

  LocationInformation _locationInfo = LocationInformation(
    country: "",
    address2: "",
    address3: "",
    address4: "",
    flag: null,
  );

  AudioPlayer audioPlayer = AudioPlayer();

  LocationInformation get locationInfo => _locationInfo;

  int _countryIndex = 0;
  int _postCodeIndex = 0;

  bool _haveLocation = false;

  late String _recordedFileName;
  String _recordedFilePath = "";
  late AudioPlayerViewModel audioPlayerViewModel;

  Duration _duration = const Duration(seconds: 0);
  final Duration _audioPosition = Duration.zero;

  bool _isFirstPhotoFromGallery = false;

  bool get isFirstPhotoFromGallery => _isFirstPhotoFromGallery;

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
  }

  // 갤러리에서 파일 가져오기
  Future<void> uploadFileFromGallery() async {
    // 길이가 9 이상이면 작동하지 않음
    if (_allFileList.length >= 9) {
      return;
    }

    if (_allFileList.isEmpty) {
      _isFirstPhotoFromGallery = true;
    }

    // multi_image_picker_viewr 라이브러리 사용
    final pickerController = MultiImagePickerController(
        maxImages: 9 - _allFileList.length, images: []);
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
        _allFileList.add(xFile);
      }
    }
    // 변했다고 알려줌
    notifyListeners();
  }

  // 파일을 서버로 업로드하기
  Future<void> postFiles(
      BuildContext context, UserInfo userInfo, Function move) async {
    final flag = MultipartFile.fromBytes(locationInfo.flag!,
        filename: locationInfo.country, contentType: MediaType('image', 'jpg'));
    final List<MultipartFile> imageFiles = _allFileList
        .map((el) => MultipartFile.fromFileSync(el.path,
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
    if (context.mounted) {
      Navigator.pop(context);
      Navigator.pop(context);
      move(userInfo.timeLineId);
    }
  }

// 위치를 받아 오는 함수
  void getLocation() async {
    if (!_haveLocation) {
      if (isFirstPhotoFromGallery == false) {
        _haveLocation = true;
        _havingLocation = true;
        await dotenv.load(fileName: ".env");
        final currentPosition = await Geolocator.getCurrentPosition();
        final curLong = currentPosition.longitude;
        final curLat = currentPosition.latitude;
        final plainDio = Dio();
        final url =
            'https://api.geoapify.com/v1/geocode/reverse?lat=$curLat&lon=$curLong&apiKey=${dotenv.env["geolocatorApiKey"]}&lang=ko&type=street&format=json';
        Response response = await plainDio.get(url);
        if (response.statusCode == 200) {
          if (response.data["results"] != null) {
            List keyList = await response.data["results"][0].keys.toList();
            // 날
            _countryIndex = keyList.indexOf("country");
            _postCodeIndex = keyList.indexOf("postcode");
            // country 포함 6개 불러옴
            // 위치에 따라 응답이 조금씩 다르게 옴
            List valueList = await response.data["results"][0].values
                .toList()
                .sublist(_countryIndex, _countryIndex + 6);
            // 우편 번호 삭제
            valueList.removeAt(_postCodeIndex - _countryIndex);
            String countryName = valueList[0];
            String address2Name = valueList[2];
            String address3Name = valueList[3];
            String address4Name;
            if (keyList[4] == "postcode") {
              address4Name = valueList[5];
            } else {
              address4Name = valueList[4];
            }
            if (address3Name == address4Name) {
              address4Name = "";
            }
            String countryCode = valueList[1];
            final flagUrl = 'https://flagcdn.com/h240/$countryCode.png';
            Response<Uint8List> flagResponse = await plainDio.get(flagUrl,
                options: Options(responseType: ResponseType.bytes));
            Uint8List? flagData = flagResponse.data;
            LocationInformation newLocation = LocationInformation(
                country: countryName,
                address2: address2Name,
                address3: address3Name,
                address4: address4Name,
                flag: flagData);
            _locationInfo = newLocation;
            _havingLocation = false;
            notifyListeners();
          }
        }
      }
    }
  }

  void uploadConfirm(BuildContext context, UserInfo myInfo, Function move) {
    if (_allFileList.isEmpty) {
      OneButtonMaterialDialog().showFeedBack(context, "사진을 등록해주세요");
      return;
    }

    if (_recordedFilePath == "") {
      OneButtonMaterialDialog().showFeedBack(context, "음성을 녹음해주세요");
      return;
    }

    if (_locationInfo ==
        LocationInformation(
          country: "",
          address2: "",
          address3: "",
          address4: "",
          flag: null,
        )) {
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
              gradient: [Colors.blueAccent,Colors.purple],
              style: TextStyle(
                fontSize: 12
              ),
            ),
            onTap: () {
              Navigator.of(context).pop();
              postFiles(context, myInfo, move);
            }),
        GestureDetector(
            child: const GradientText(
              "잠깐만요",
              gradient: [Colors.orangeAccent,Colors.purpleAccent],
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

  Color buttonColor() {
    if (_isRecording == true) {
      return Colors.greenAccent;
    } else {
      return Colors.redAccent;
    }
  }

  void changeUploading() {
    if (_isUploading == true) {
      _isUploading = false;
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    audioPlayerViewModel.dispose();
    super.dispose();
  }
}
