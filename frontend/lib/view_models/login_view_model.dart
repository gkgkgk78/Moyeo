
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:kakao_flutter_sdk/kakao_flutter_sdk.dart';
import 'package:logger/logger.dart';
import 'package:moyeo/view_models/app_view_model.dart';
import 'package:provider/provider.dart';

import '../main.dart';
import '../models/Token.dart';
import '../models/UserInfo.dart';
import '../services/user_repository.dart';

class LoginViewModel extends ChangeNotifier {
  final storage = const FlutterSecureStorage();
  late AppViewModel _appViewModel;

  LoginViewModel(BuildContext context, Function update) {
    tryLogin(context, update);
    _appViewModel = Provider.of<AppViewModel>(context,listen: false);
  }

  Future<void> tryLogin(context, Function update) async {
    if ((await storage.read(key: 'accessToken') == null)) return;
    if ((_appViewModel.fcmToken == '')) {
      Future.delayed(Duration(seconds: 2),
          tryLogin(context, update)
      );
    }

    try {
      UserInfo userInfo = await UserRepository().getUserInfo(context);
      update(userInfo);
      Navigator.pushAndRemoveUntil(
        context,
        MaterialPageRoute(
          builder: (_) => const MyHomePage(),
        ),
        (route) => false,
      );
    } catch (error) {
      throw Exception('Our login Error: $error');
    }
  }

  Future<void> loginButtonPressed(context, Function update) async {
    AppViewModel appViewModel = Provider.of<AppViewModel>(context,listen: false);
    if (appViewModel.fcmToken != "") {
      String fcmToken = appViewModel.fcmToken;
      bool isInstalled = await isKakaoTalkInstalled();
      OAuthToken token = isInstalled
          ? await UserApi.instance.loginWithKakaoTalk()
          : await UserApi.instance.loginWithKakaoAccount();
      final accessToken = token.accessToken;
      final refreshToken = token.refreshToken;
      Token ourToken = await UserRepository().kakaoLogin(
          Token(accessToken: accessToken, refreshToken: refreshToken, fcmToken: fcmToken));

      storage.write(key: 'accessToken', value: ourToken.accessToken);
      storage.write(key: 'refreshToken', value: ourToken.refreshToken);

      tryLogin(context, update);
    }
  }
}
