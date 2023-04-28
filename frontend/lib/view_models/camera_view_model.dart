import 'dart:async';
import 'dart:io';

import 'package:camera/camera.dart';
import 'package:danim/view_models/record_view_model.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:device_info_plus/device_info_plus.dart';


final deviceInfoPlugin = DeviceInfoPlugin();

class CameraViewModel extends ChangeNotifier {
  List<CameraDescription> _cameras = [];
  late CameraController _controller;
  late String _imagePath;
  late RecordViewModel recordViewModel;

  bool _isTaking = false;
  bool get isTaking => _isTaking;

  List<XFile> _allFileList = [];

  List<XFile> get allFileList => _allFileList;

  CameraController get controller => _controller;

  List<CameraDescription> get cameras => _cameras;

  String get imagePath => _imagePath;



  CameraViewModel() {
    recordViewModel = RecordViewModel(allFileList);
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
      _controller = CameraController(_cameras.first, ResolutionPreset.ultraHigh,);
      await _controller.initialize();
      await _controller.setFlashMode(FlashMode.off);
      notifyListeners();
    }
  }

  Future<void> takePhoto() async {

    if (allFileList.length < 9 ) {
      if (_isTaking == false) {
        _isTaking = true;
        notifyListeners();
        XFile file = await _controller.takePicture();
        _isTaking = false;
        allFileList.add(file);
        notifyListeners();

        // 파일 저장할 위치 지정
        Directory externalDirectory =
        Directory('/storage/emulated/0/Documents/photos');
        if (!await externalDirectory.exists()) {
          await externalDirectory.create(recursive: true);
        }

        final List<int> imageBytes = await file.readAsBytes();

        String dir = externalDirectory.path;
        final savePath = "$dir/${file.name}";

        // 파일 생성
        final File imageFile = File(savePath);

        // 파일에 이미지 저장
        await imageFile.writeAsBytes(imageBytes);
      }
    }
    notifyListeners(); // Add this line
  }

  Future<void> changeCamera() async{
    if (_controller.description == _cameras.first) {
      CameraController newController = CameraController(_cameras.last, ResolutionPreset.ultraHigh);
      await _controller.dispose();
      _controller = newController;
      await _controller.initialize();
      notifyListeners();
    } else {
      _controller = CameraController(_cameras.first, ResolutionPreset.veryHigh);
      CameraController newController = CameraController(_cameras.first, ResolutionPreset.ultraHigh);
      await _controller.dispose();
      _controller = newController;
      await _controller.initialize();
      notifyListeners();
    }

  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }
}
