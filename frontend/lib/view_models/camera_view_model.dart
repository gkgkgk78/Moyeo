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

  int _countryIndex = 0;
  int _postCodeIndex = 0;

  // double _zoomLevel = 1.0;
  // double get zoomLevel => _zoomLevel;
  LocationInformation _locationInfo = LocationInformation(
    country: "",
    address2: "",
    address3: "",
    address4: "",
    flag: null,
  );

  LocationInformation get locationInfo => _locationInfo;

  String fileName = DateFormat('yyyyMMdd.Hmm.ss').format(DateTime.now());


  CameraViewModel() {
    initializeCamera();

  }

  Future<void> initializeCamera() async {
    AndroidDeviceInfo deviceInfo = await deviceInfoPlugin.androidInfo;
    final sdkInfo = deviceInfo.version.sdkInt;
    if (sdkInfo >= 33) {
      if (await Permission.camera.isDenied) {
        await Permission.camera.request();
      }
      if (await Permission.photos.isDenied) {
        await Permission.photos.request();
      }
      if (await Permission.videos.isDenied) {
        await Permission.videos.request();
      }
      if (await Permission.audio.isDenied) {
        await Permission.audio.request();
      }
      if (await Permission.manageExternalStorage.isDenied) {
        await Permission.manageExternalStorage.request();
      }
      if (await Permission.location.isDenied) {
        await Permission.location.request();
      }
    } else {
      if (await Permission.camera.isDenied) {
        await Permission.camera.request();
      }
      if (await Permission.storage.isDenied) {
        await Permission.storage.request();
      }
      if (await Permission.manageExternalStorage.isDenied) {
        await Permission.manageExternalStorage.request();
      }
      if (await Permission.location.isDenied) {
        await Permission.location.request();
      }
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
    notifyListeners();
  }

  Future<void> takePhoto() async {
    if (_allFileList.length < 9) {
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

  Future<void> changeCamera() async {
    if (_controller.description == _cameras[0]) {
      CameraController newController = CameraController(
          _cameras[1], ResolutionPreset.max);
      await _controller.dispose();
      _controller = newController;
      await _controller.initialize();
      notifyListeners();
    } else {
      CameraController newController = CameraController(
          _cameras[0], ResolutionPreset.ultraHigh);
      await _controller.dispose();
      _controller = newController;
      await _controller.initialize();
      notifyListeners();
    }
  }

  bool _haveLocation = false;

  bool _havingLocation = false;

  bool get havingLocation => _havingLocation;

  void getLocation() async {
    if (!_haveLocation) {
      if (_allFileList.isEmpty) {
        _haveLocation = true;
        _havingLocation = true;
        await dotenv.load(fileName: ".env");
        final currentPosition = await Geolocator.getCurrentPosition();
        final curLong = currentPosition.longitude;
        final curLat = currentPosition.latitude;
        final plainDio = Dio();
        final url =
            'https://api.geoapify.com/v1/geocode/reverse?lat=$curLat&lon=$curLong&apiKey=${dotenv
            .env["geolocatorApiKey"]}&lang=ko&type=street&format=json';
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

  void clear() {
    _allFileList = [];
    _locationInfo = LocationInformation(
      country: "",
      address2: "",
      address3: "",
      address4: "",
      flag: null,
    );
    _haveLocation = false;
    _havingLocation = false;
    notifyListeners();
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



  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }
}
