import 'dart:io';

import 'package:danim/models/UserInfo.dart';
import 'package:danim/services/user_repository.dart';
import 'package:danim/view_models/app_view_model.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:http_parser/http_parser.dart';
import 'package:image_picker/image_picker.dart';

class ModifyProfileViewModel extends ChangeNotifier {
  final textEditController = TextEditingController();
  bool _isLive = true;
  String _imagePath = '';
  XFile? _selectedImageFile;
  String _nickname = '';

  bool get isLive => _isLive;

  ModifyProfileViewModel(profileImageUrl, nickname) {
    _nickname = nickname;
    _imagePath = profileImageUrl;
    textEditController.text = _nickname;
    notifyListeners();
  }

  sendModifyProfile(BuildContext context, AppViewModel appViewModel) async {
    final multipartFile = _selectedImageFile == null
        ? null
        : MultipartFile.fromFileSync(
            _selectedImageFile!.path,
            filename: _selectedImageFile!.name,
            contentType: MediaType('image', 'png'),
          );
    final FormData formData = FormData.fromMap({
      'profileImage': multipartFile,
      'nickname': _nickname,
    });
    UserInfo newUserInfo =
        await UserRepository().updateUserProfile(context, formData);
    appViewModel.updateUserInfo(newUserInfo);
    if (context.mounted) {
      showDialog(
        context: context,
        builder: (context) => AlertDialog(
          title: const Text('변경 완료'),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.pop(context);
              },
              child: const Text(
                '확인',
              ),
            ),
          ],
        ),
      );
    }

    notifyListeners();
  }

  changeProfileImage() async {
    _selectedImageFile =
        await ImagePicker().pickImage(source: ImageSource.gallery);
    notifyListeners();
  }

  get imagePath => _imagePath;

  get selectedImageFile =>
      _selectedImageFile == null ? null : File(_selectedImageFile!.path);

  set imagePath(value) {
    _imagePath = value;
    notifyListeners();
  }

  get nickname => _nickname;

  set nickname(value) {
    _nickname = value;
    notifyListeners();
  }

  @override
  void dispose() {
    _isLive = false;
    _selectedImageFile = null;
    super.dispose();
  }
}
