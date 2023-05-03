import 'dart:async';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:flutter_keyboard_visibility/flutter_keyboard_visibility.dart';
import 'package:logger/logger.dart';
import 'package:moyeo/models/ChatbotRequest.dart';

import '../services/chatbot_repository.dart';

var logger = Logger();

class ChatbotViewModel extends ChangeNotifier {
  int chatId;
  int isTravel;
  bool isFinished;
  BuildContext _context;

  String _inputText = "";

  String get inputText => _inputText;

  bool _isAnswered = false;

  bool get isAnswered => _isAnswered;

  bool _ableTextField = false;

  bool get ableTextField => _ableTextField;

  // 메세지들의 리스트
  List _messages = [];

  get messages => _messages;

  ChatbotRequest _chatbotRequest = ChatbotRequest(destination: '', season: '', purpose: '');

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

  String _newDestination = "";
  String _newSeason = "";
  String _newPurpose = "";

  ChatbotViewModel(this._context,
      {required this.chatId,
      required this.isTravel,
      required this.isFinished}) {
    // 키보드가 안 보이면 언포커스
    keyboardVisibilityController.onChange.listen(
      (bool visible) {
        if (visible == false) {
          unFocus();
        }
      },
    );
    if (isFinished == false) {
      // 채팅 새로 하나 생성
      // CreateNewChat();
    } else {
      // 채팅 상세내용 가져오기
      // getChatDetail();
    }

    if (isTravel == -1) {
      _messages.add(["안녕하세요! 여봇입니다. \n 가시고 싶은 곳은 정하셨나요?", "canton"]);
    } else {
      _messages.add(["안녕하세요! 여봇입니다.\n 무엇을 도와드릴까요?", "canton"]);
    }
  }

  // 개발용 테스트 suibmit
  Future<void> testsubmit(String text) async {
    if (_newDestination == '') {
      _newDestination = text;
      _messages.add([text, 'user']);
      textEditingController.clear();
      notifyListeners();
      await Timer(const Duration(seconds: 2), () {
        _messages.add([
          '${text}! 참 좋은 나라죠.'
              '\n어느 계절에 떠나시고 싶으신가요?',
          'canton'
        ]);
        notifyListeners();
      });
      return;
    }
    if (_newSeason == '') {
      if (text.contains('여름') || text.contains('겨울') || text.contains('봄') || text.contains('가을')) {
        _newSeason = text;
        _messages.add([text, 'user']);
        notifyListeners();
        Timer(const Duration(seconds: 2), () {
          if (text.contains('봄')) {
            _messages.add([
              '따스한 봄햇살을 맞으며 여행하는 것은'
                  '\n누구에게나 기쁜 일이죠.'
                  '\n무엇을 즐기러 가실건가요?',
              'canton'
            ]);
            notifyListeners();
          } else if (text.contains('여름')) {
            _messages.add([
              '여름의 무더위를 날려버리기 위해'
                  '\n여행은 좋은 선택 중 하나이죠.'
                  '\n무엇을 즐기러 가실건가요?',
              'canton'
            ]);
            notifyListeners();
          } else if (text.contains('가을')) {
            _messages.add([
              '쓸쓸한 가을을 여행으로 채워보세요.'
                  '\n무엇을 즐기러 가실건가요?',
              'canton'
            ]);
            notifyListeners();
          } else if (text.contains('겨울')) {
            _messages.add([
              '겨울은 매우 춥지만'
                  '\n동시에 여행할 거리가 매우 많은 계절이죠.'
                  '\n무엇을 즐기러 가실건가요?',
              'canton'
            ]);
            notifyListeners();
          }
        });
        return;
      } else {
        return;
      }

    }
    if (_newPurpose == '') {
      _newPurpose = text;
      _messages.add([text, 'user']);
      notifyListeners();
      await Future.delayed(Duration(seconds: 2), () {
        _messages.add([
          '${_newSeason}에'
              '\n${_newDestination}을 가시는 군요'
          ,
          'canton'
        ]);
        notifyListeners();
      });
      ChatbotRequest newRequest = ChatbotRequest(
        destination: _newDestination, season: _newSeason, purpose: _newPurpose,);
      _chatbotRequest = newRequest;
      await Future.delayed(Duration(seconds: 1), () {
        _messages.add([
          '여봇이 대답을 생성하고 있어요!'
              '\n 생성이 완료되면 알림으로'
              '\n 알려드릴게요~!'
          ,
          'canton'
        ]);
        notifyListeners();
      });
    }
    notifyListeners();

  }

  double boxHeight(BoxConstraints constraints) {
    if (_chatbotFocus.hasFocus) {
      return constraints.maxHeight - 48;
    } else {
      return constraints.maxHeight - 88;
    }
  }

  // 채팅을 하나 만듦
  Future<void> createNewChat() async {
    await ChatbotRepository().CreateChat(_context);
  }

  // 채팅 내용을 가져옴
  Future<void> getChatDetail() async {
    _messages =
        await ChatbotRepository().ChatDetailFromServer(_context, chatId);
    notifyListeners();
  }

  // 텍스트 autogpt한테 submit
  Future<void> submit(String text, BuildContext context) async {
    // _messages.add([text, "user's"]);
    // textEditingController.clear();
    // notifyListeners();
    // FormData formData = FormData.fromMap({'userInput': _inputText});
    // _inputText = "";
    //
    // _messages.add(["검색 중입니다. \n 잠시만 기다려주세요!", "canton"]);
    //
    // Response response =
    //     await ChatbotRepository().ChatToServer(context, formData);
    //
    // _messages.removeLast();
    // response.data["text"];
    //
    // unFocus();
    // goBottom();
    // notifyListeners();
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
      return Image.network(
          "http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg");
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

  // 액티비티 추천
  Future<void> selectActivity(context) async {
    _isAnswered = true;
    _messages.add(['놀러갈 곳 추천 "해"', 'user']);
    notifyListeners();
    ChatbotRepository().RecommendActivity(context);
    Timer(const Duration(seconds: 2), () {
      _messages.add([
        '알겠습니다!'
            '\n최신 포스트 위치를 기준으로\n가볼 만한 곳을 추천해드릴게요.'
            '\n답변이 완료되면 알림으로 알려드리겠습니다~',
        'canton'
      ]);
      notifyListeners();
    });
  }

  // 식당 추천
  Future<void> selectRestaurant(context) async {
    _isAnswered = true;
    _messages.add(['식당 추천 "해"', 'user']);
    notifyListeners();
    ChatbotRepository().RecommendRestaurant(context);
    Timer(const Duration(seconds: 2), () {
      _messages.add([
        '알겠습니다!'
            '\n최신 포스트 위치를 기준으로\n맛있는 식당을 추천해드릴게요.'
            '\n답변이 완료되면 알림으로 알려드리겠습니다~',
        'canton'
      ]);
      notifyListeners();
    });
  }

  Future<void> selectPlace(context) async {
    _isAnswered = true;
    _messages.add(['식당 추천 "해"', 'user']);
    notifyListeners();
    ChatbotRepository().RecommendRestaurant(context);
    Timer(const Duration(seconds: 2), () {
      _messages.add([
        '알겠습니다!'
            '\n요즘 여행하기 좋은 곳을 추천해드릴게요.'
            '\n답변이 완료되면 알림으로 알려드리겠습니다~',
        'canton'
      ]);
      notifyListeners();
    });
  }

  Future<void> selectActivityNotTraveling(context, String place) async {
    _isAnswered = true;
    _messages.add(['놀러갈 곳 추천 "해"', 'user']);
    notifyListeners();
    // ChatbotRepository().RecommendActivityNotTraveling(context, place);
    Timer(const Duration(seconds: 2), () {
      _messages.add([
        '알겠습니다!'
            '\n최신 포스트 위치를 기준으로\n가볼 만한 곳을 추천해드릴게요.'
            '\n답변이 완료되면 알림으로 알려드리겠습니다~',
        'canton'
      ]);
      notifyListeners();
    });
  }

  void userHaveDestination() async {
    _messages.add(["오냐", 'user']);
    _isAnswered = true;
    notifyListeners();
    Timer(const Duration(seconds: 2), () {
      _messages.add([
        '그러시군요!'
            '\n어떤 나라에 가실 예정이신가요?'
            '\n하나의 나라이름을 입력해주세요^^',
        'canton'
      ]);
      _ableTextField = true;
      notifyListeners();
    });
  }

  void userHaveNoDestination() async {
    await ChatbotRepository().RecommendActivityNotTraveling(_context, _chatbotRequest);
  }
}
