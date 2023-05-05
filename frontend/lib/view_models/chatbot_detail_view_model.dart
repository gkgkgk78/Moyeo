import 'dart:async';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:flutter_keyboard_visibility/flutter_keyboard_visibility.dart';
import 'package:logger/logger.dart';
import 'package:moyeo/models/ChatMessage.dart';
import 'package:moyeo/models/ChatbotRequest.dart';

import '../services/chatbot_repository.dart';

var logger = Logger();

class ChatbotViewModel extends ChangeNotifier {
  String chatId;
  int isTravel;
  bool isFinished;
  BuildContext _context;

  String _inputText = "";

  String get inputText => _inputText;

  bool _isAnswered = false;

  bool get isAnswered => _isAnswered;

  bool _ableTextField = false;

  bool get ableTextField => _ableTextField;

  bool _haveDestination = false;

  // 메세지들의 리스트
  List<ChatMessage> _messages = [];

  get messages => _messages;

  ChatbotRequest _chatbotRequest =
      ChatbotRequest(destination: '', season: '', purpose: '');

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
        } else {
          goBottom();
        }
      },
    );

    // 새로 생성된 채팅이면
    if (isFinished == false) {
      // 채팅 새로 하나 생성
      CreateNewChat();
      if (isTravel == -1) {
        _messages.add(
          ChatMessage(
            message: "안녕하세요! 여봇입니다. \n 가시고 싶은 곳은 정하셨나요?",
            sender: "canton",
          ),
        );
      } else {
        _messages.add(
          ChatMessage(
            message: "안녕하세요! 여봇입니다.\n 무엇을 도와드릴까요?",
            sender: "canton",
          ),
        );
      }
      // 이미 끝난 채팅이면
    } else {
      // 채팅 상세내용 가져오기
      getChatDetail();
    }
  }

  Future<dynamic> CreateNewChat() async {
    chatId = await ChatbotRepository().CreateNewChat(_context);
  }

  void notTravelSubmit() {
    if (_haveDestination) {
      notTravelHaveDestSubmit(_inputText);
    } else {
      notTravelNoDestSubmit(_inputText);
    }
  }

  // 여행중이 아니고 목적지가 있을 때
  Future<void> notTravelHaveDestSubmit(String text) async {
    if (_newDestination == '') {
      _newDestination = text;
      _messages.add(ChatMessage(
        message: text,
        sender: 'user',
      ),);
      _textEditingController.clear();
      unFocus();
      notifyListeners();
      await Future.delayed(const Duration(seconds: 2), () {
        _messages.add(
          ChatMessage(
              message: '${text}! 참 좋은 나라죠.\n어느 계절에 떠나시고 싶으신가요?',
              sender: 'canton',
          )
        );
        notifyListeners();
      });
      goBottom();
      return;
    }

    if (_newSeason == '') {
      if (text.contains('여름') ||
          text.contains('겨울') ||
          text.contains('봄') ||
          text.contains('가을')) {
        _newSeason = text;
        _messages.add(ChatMessage(
          message: text,
          sender: 'user',
        ));
        _textEditingController.clear();
        unFocus();
        notifyListeners();
        await Future.delayed(const Duration(seconds: 2), () {
          if (text.contains('봄')) {
            _messages.add(
                ChatMessage(
                  message: '따스한 봄햇살을 맞으며 여행하는 것은\n누구에게나 기쁜 일이죠.\n무엇을 즐기러 가실건가요?',
                  sender: 'canton',
                ),
            );
            notifyListeners();
          } else if (text.contains('여름')) {
            _messages.add(
                ChatMessage(
                  message: '여름의 무더위를 날려버리기 위해\n여행은 좋은 선택 중 하나이죠.\n무엇을 즐기러 가실건가요?',
                  sender: 'canton',
                ),
                );
            notifyListeners();
          } else if (text.contains('가을')) {
            _messages.add(
                ChatMessage(
                  message: '쓸쓸한 가을을 여행으로 채워보세요.\n무엇을 즐기러 가실건가요?',
                  sender: 'canton',
                ),
            );
            notifyListeners();
          } else if (text.contains('겨울')) {
            _messages.add(
                ChatMessage(
                  message: '겨울은 매우 춥지만\n동시에 여행할 거리가 매우 많은 계절이죠.\n무엇을 즐기러 가실건가요?',
                  sender: 'canton',
                ),
                );
            notifyListeners();
          }
          goBottom();
        });
        ChatbotRequest newRequest = ChatbotRequest(
          destination: _newDestination,
          season: _newSeason,
          purpose: _newPurpose,
        );
        _chatbotRequest = newRequest;
        if (_context.mounted) {
          ChatbotRepository().RecommendActivityNotTraveling(_context, newRequest);
        }
        await Future.delayed(const Duration(seconds: 1), () {
          _messages.add(
              ChatMessage(
                message: '여봇이 대답을 생성하고 있어요!\n 생성이 완료되면 알림으로\n 알려드릴게요~!',
                sender: 'canton',
              ),
            );
          notifyListeners();
          goBottom();
        });
        if (_context.mounted) {
          ChatbotRepository().ChatToServer(_context, chatId, _messages);
        }
        return;
      } else {
        return;
      }
    }
  }

  Future<void> notTravelNoDestSubmit(String text) async {
    if (_newDestination == '') {
      _newDestination = text;
      _messages.add(ChatMessage(
        message: text,
        sender: 'user',
      ),);
      _textEditingController.clear();
      unFocus();
      notifyListeners();
      await Future.delayed(const Duration(seconds: 2), () {
        _messages.add(
            ChatMessage(
              message: '${text}! 참 좋은 지역이죠.\n어느 계절에 떠나시고 싶으신가요?',
              sender: 'canton',
            ),
           );
        notifyListeners();
      });
      goBottom();
      return;
    }
    if (_newSeason == '') {
      if (text.contains('여름') ||
          text.contains('겨울') ||
          text.contains('봄') ||
          text.contains('가을')) {
        _newSeason = text;
        _messages.add(
            ChatMessage(
                message: text,
                sender: 'user',
            ),);
        _textEditingController.clear();
        unFocus();
        notifyListeners();
        await Future.delayed(const Duration(seconds: 2), () {
          if (text.contains('봄')) {
            _messages.add(
                ChatMessage(
                  message: '따스한 봄햇살을 맞으며 여행하는 것은\n누구에게나 기쁜 일이죠.\n무엇을 즐기러 가실건가요?',
                  sender: 'canton',
                ),
                );
            notifyListeners();
          } else if (text.contains('여름')) {
            _messages.add(
                ChatMessage(
                  message: '여름의 무더위를 날려버리기 위해\n여행은 좋은 선택 중 하나이죠.\n무엇을 즐기러 가실건가요?',
                  sender: 'canton',
                ),
                );
            notifyListeners();
          } else if (text.contains('가을')) {
            _messages.add(
                ChatMessage(
                  message: '쓸쓸한 가을을 여행으로 채워보세요.\n무엇을 즐기러 가실건가요?',
                  sender: 'canton',
                ),
            );
            notifyListeners();
          } else if (text.contains('겨울')) {
            _messages.add(
                ChatMessage(
                  message: '겨울은 매우 춥지만\n동시에 여행할 거리가 매우 많은 계절이죠.\n무엇을 즐기러 가실건가요?',
                  sender: 'canton',
                ),
                );
            notifyListeners();
          }
          goBottom();
        });
        return;
      } else {
        return;
      }
    }
    if (_newPurpose == '') {
      _newPurpose = text;
      _messages.add(ChatMessage(
        message: text,
        sender: 'user',
      ),);
      _textEditingController.clear();
      unFocus();
      notifyListeners();
      await Future.delayed(Duration(seconds: 2), () {
        _messages.add(
            ChatMessage(
              message: '${_newSeason}에\n${_newDestination}을 가시는 군요',
              sender: 'canton',
            ),
            );
        notifyListeners();
        goBottom();
      });
      ChatbotRequest newRequest = ChatbotRequest(
        destination: _newDestination,
        season: _newSeason,
        purpose: _newPurpose,
      );
      _chatbotRequest = newRequest;
      if (_context.mounted) {
        ChatbotRepository().RecommendPlace(_context, newRequest);
      }
      await Future.delayed(const Duration(seconds: 1), () {
        _messages.add(
            ChatMessage(
              message: '여봇이 대답을 생성하고 있어요!\n 생성이 완료되면 알림으로\n 알려드릴게요~!',
              sender: 'canton',
            ),
            );
        notifyListeners();
        goBottom();
      }
      );
      if (_context.mounted) {
        ChatbotRepository().ChatToServer(_context, chatId, _messages);
      }
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

  }

  // 채팅 내용을 가져옴
  Future<void> getChatDetail() async {
    _messages =
        await ChatbotRepository().ChatDetailFromServer(_context, chatId);
    notifyListeners();
  }

  Future<void> chatToServer(message) async {}

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
    if (_messages[index].sender == "canton") {
      return CrossAxisAlignment.start;
    } else {
      return CrossAxisAlignment.end;
    }
  }

  // 프로필 이미지
  Image profileImage(index) {
    if (_messages[index].sender == "canton") {
      return Image.asset('assets/images/canton.png');
    } else {
      return Image.network(
          "http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg");
    }
  }

  // 유저인지 아닌지
  bool sender(index) {
    if (_messages[index].sender == "canton") {
      return false;
    } else {
      return true;
    }
  }

  // 말풍선 색상
  Color bubbleColor(index) {
    if (_messages[index].sender == "canton") {
      return const Color(0xfffdeedc);
    } else {
      return const Color(0xffddf3fd);
    }
  }

  // 아래로 스크롤
  void goBottom() {
    SchedulerBinding.instance.addPostFrameCallback(
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
    _messages.add(ChatMessage(message: '놀러갈 곳 추천 "해"', sender: 'user'));
    notifyListeners();
    ChatbotRepository().RecommendActivity(context);
    await Future.delayed(const Duration(seconds: 2), () {
      _messages.add(
          ChatMessage(
              message: '알겠습니다!'
                  '\n최신 포스트 위치를 기준으로\n가볼 만한 곳을 추천해드릴게요.'
                  '\n답변이 완료되면 알림으로 알려드리겠습니다~', sender: 'canton')
          );
      notifyListeners();
    });
    if (_context.mounted) {
      await ChatbotRepository().ChatToServer(_context, chatId, _messages);
    }
  }

  // 식당 추천
  Future<void> selectRestaurant(context) async {
    _isAnswered = true;
    _messages.add(ChatMessage(message: '식당 추천 "해"', sender: 'users'));
    notifyListeners();
    ChatbotRepository().RecommendRestaurant(context);
    await Future.delayed(const Duration(seconds: 2), () {
      _messages.add(
          ChatMessage(message: '알겠습니다!'
              '\n최신 포스트 위치를 기준으로\n맛있는 식당을 추천해드릴게요.'
              '\n답변이 완료되면 알림으로 알려드리겠습니다~', sender: 'canton')
          );
      notifyListeners();
    });
    if (_context.mounted) {
      ChatbotRepository().ChatToServer(_context, chatId, _messages);
    }
  }

  void userHaveDestination() async {
    _messages.add(ChatMessage(message: '오냐', sender: 'users'));
    _isAnswered = true;
    _ableTextField = true;
    _haveDestination = true;
    goBottom();
    notifyListeners();
    await Future.delayed(const Duration(seconds: 2), () {
      _messages.add(
          ChatMessage(message: '그러시군요!'
              '\n어떤 나라에 가실 예정이신가요?'
              '\n하나의 나라이름을 입력해주세요^^', sender: 'canton')
      );
      notifyListeners();
    }
    );
  }

  void userHaveNoDestination() async {
    _messages.add(ChatMessage(message: '아니', sender: 'users'));
    _isAnswered = true;
    _ableTextField = true;
    goBottom();
    notifyListeners();
    await Future.delayed(const Duration(seconds: 2), () {
      _messages.add(
          ChatMessage(message: '그러시군요!'
              '\n어떤 지역에 가시고 싶으신가요?'
              '\n하나의 지역명을 입력해주세요^^', sender: 'canton')
      );
      notifyListeners();
    }
    );
  }
}
