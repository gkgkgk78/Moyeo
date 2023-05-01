import 'dart:async';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:flutter_keyboard_visibility/flutter_keyboard_visibility.dart';
import 'package:logger/logger.dart';

import '../services/chatbot_repository.dart';

var logger = Logger();

class ChatbotViewModel extends ChangeNotifier {
  int _chatId;
  BuildContext _context;

  String _inputText = "";

  String get inputText => _inputText;

  // 메세지들의 리스트
  List _messages = [
    ["안녕하세요! 여봇입니다.\n 무엇을 도와드릴까요?", "canton"],
  ];

  get messages => _messages;

  // 포커스
  final FocusNode _chatbotFocus = FocusNode();

  FocusNode get chatbotFocus => _chatbotFocus;

  // 키보드 보이는지
  final KeyboardVisibilityController _keyboardVisibilityController =
      KeyboardVisibilityController();

  KeyboardVisibilityController get keyboardVisibilityController =>
      _keyboardVisibilityController;

  // 텍스트 컨트롤러
  final TextEditingController _textEditingController = TextEditingController();

  TextEditingController get textEditingController => _textEditingController;

  // 스크롤 컨트롤러
  final ScrollController _scrollController = ScrollController();

  ScrollController get scrollController => _scrollController;


  ChatbotViewModel(this._context, this._chatId) {
    // 키보드가 안 보이면 언포커스
    keyboardVisibilityController.onChange.listen(
      (bool visible) {
        if (visible == false) {
          unFocus();
        }
      },
    );
    // 채팅 상세내용 가져오기
    // getChatDetail();
  }

  // 개발용 테스트 suibmit
  Future<void> testsubmit(String text) async {
    _messages.add([text, "user's"]);
    _inputText = "";
    textEditingController.clear();
    notifyListeners();
    _messages.add(["잘 모르겠습니다.", "canton"]);
    unFocus();
    goBottom();
    notifyListeners();
  }

  double boxHeight(BoxConstraints constraints) {
    if (_chatbotFocus.hasFocus) {
      return constraints.maxHeight-48;
    } else {
      return constraints.maxHeight-88;
    }
  }
  // 채팅 내용을 가져옴
  Future<void> getChatDetail() async {
    _messages = await ChatbotRepository().ChatDetailFromServer(_context, _chatId);
  }

  // 텍스트 autogpt한테 submit
  Future<void> submit(String text, BuildContext context) async {
    _messages.add([text, "user's"]);
    textEditingController.clear();
    notifyListeners();
    FormData formData = FormData.fromMap({
      'userInput': _inputText
    });
    _inputText = "";

    _messages.add(["검색 중입니다. \n 잠시만 기다려주세요!","canton"]);

    Response response = await ChatbotRepository().ChatToServer(context, formData);

    _messages.removeLast();
    response.data["text"];

    unFocus();
    goBottom();
    notifyListeners();
  }

  // 언포커스
  void unFocus() {
    _chatbotFocus.unfocus();
    goBottom();
    notifyListeners();
  }

  // 제공
  void changeInputText(String text) {
    _inputText = text;
    notifyListeners();
  }

  // 좌로 모여 우로 모여
  CrossAxisAlignment getAlignment(int index) {
    if (_messages[index][1] == "canton") {
      return CrossAxisAlignment.start;
    } else {
      return CrossAxisAlignment.end;
    }
  }

  // 프로필 이미지
  Image profileImage(index) {
    if (_messages[index][1] == "canton") {
      return Image.asset('assets/images/canton.png');
    } else {
      return Image.network("유저이미지url");
    }
  }

  // 유저인지 아닌지
  bool sender(index) {
    if (_messages[index][1] == "canton") {
      return false;
    } else {
      return true;
    }
  }

  // 말풍선 색상
  Color bubbleColor(index) {
    if (_messages[index][1] == "canton") {
      return const Color(0xfffdeedc);
    } else {
      return const Color(0xffddf3fd);
    }
  }

  // 아래로 스크롤
  void goBottom() {
    SchedulerBinding.instance?.addPostFrameCallback(
          (_) {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: const Duration(seconds: 1),
          curve: Curves.fastOutSlowIn,
        );
      },
    );
  }
}
