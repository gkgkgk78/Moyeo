import 'package:danim/main.dart';
import 'package:danim/models/Token.dart';
import 'package:danim/models/UserInfo.dart';
import 'package:danim/services/user_repository.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:kakao_flutter_sdk/kakao_flutter_sdk.dart';

class LoginViewModel extends ChangeNotifier {
  final storage = const FlutterSecureStorage();

  LoginViewModel(BuildContext context, Function update) {
    tryLogin(context, update);
  }

  tryLogin(context, Function update) async {
    if ((await storage.read(key: 'accessToken') == null)) return;
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
    bool isInstalled = await isKakaoTalkInstalled();
    OAuthToken token = isInstalled
        ? await UserApi.instance.loginWithKakaoTalk()
        : await UserApi.instance.loginWithKakaoAccount();
    final accessToken = token.accessToken;
    final refreshToken = token.refreshToken;
    Token ourToken = await UserRepository().kakaoLogin(
        Token(accessToken: accessToken, refreshToken: refreshToken));

    storage.write(key: 'accessToken', value: ourToken.accessToken);
    storage.write(key: 'refreshToken', value: ourToken.refreshToken);

    tryLogin(context, update);
  }
}
