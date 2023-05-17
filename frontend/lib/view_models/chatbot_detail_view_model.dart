import 'dart:async';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:flutter_keyboard_visibility/flutter_keyboard_visibility.dart';
import 'package:logger/logger.dart';
import 'package:moyeo/models/ChatMessage.dart';
import 'package:moyeo/models/ChatbotRequest.dart';
import 'package:moyeo/models/TimelineDetail.dart';
import 'package:moyeo/services/timeline_repository.dart';

import '../models/TimelineInfo.dart';
import '../services/chatbot_repository.dart';

var logger = Logger();

class ChatbotViewModel extends ChangeNotifier {
  int isTravel;

  late TimelineInfo _latestTimeline;
  late List<TimelineDetail> _latestTimelineDetail;
  List<TimelineDetail> get latestTimelineDetail => _latestTimelineDetail;

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

  List<ChatMessage> _newMessages = [];

  bool _showQuestion = false;
  bool get showQuestion => _showQuestion;

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

  ChatbotViewModel(BuildContext context, {required this.isTravel}) {
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
    startChat(context, isTravel);
  }

  Future<void> startChat(context, isTravel) async {
    _messages = await ChatbotRepository().ChatDetailFromServer(context);
    notifyListeners();
      if (isTravel == -1) {
        await submitMessage(
          ChatMessage(
            message: "안녕하세요! 여봇입니다. \n가시고 싶은 곳은 정하셨나요?",
            sender: "gpt",
          ),
        );
        _showQuestion = true;
      } else {
        _latestTimelineDetail = (await TimelineRepository().getTimelineDetailsByTimelineId(context, isTravel)).timelineDetails!;
        notifyListeners();
        if (_latestTimelineDetail.isEmpty) {
          await submitMessage(
            ChatMessage(
              message: "안녕하세요! 여봇입니다.\n포스트를 등록하시면\n제가 도움을 드릴 수 있을 것 같아요!",
              sender: "gpt",
            ),
          );
        } else {
          await submitMessage(
            ChatMessage(
              message: "안녕하세요! 여봇입니다.\n무엇을 도와드릴까요?",
              sender: "gpt",
            ),
          );
          _showQuestion = true;
        }
      }
    notifyListeners();
  }

  Future<void> submitMessage(ChatMessage message) async {
    _messages.add(message);
    _newMessages.add(message);
    notifyListeners();
    goBottom();
  }

  Future<void> submitMessageListToServer(BuildContext context) async {
    for (ChatMessage element in _newMessages) {
      await ChatbotRepository().ChatToServer(context, element);
    }
    if (context.mounted) {
      Future.delayed(
        const Duration(seconds: 5),
        () {
          Navigator.pop(context);
        },
      );
    }
  }

  void notTravelSubmit(BuildContext context) {
    if (_haveDestination) {
      notTravelHaveDestSubmit(_inputText, context);
    } else {
      notTravelNoDestSubmit(_inputText, context);
    }
  }

  // 여행중이 아니고 목적지가 있을 때
  Future<void> notTravelHaveDestSubmit(String text, BuildContext context) async {
    if (_newDestination == '') {
      _newDestination = text;
      submitMessage(
        ChatMessage(
          message: text,
          sender: 'phone',
        ),
      );
      _textEditingController.clear();
      unFocus();
      notifyListeners();
      await Future.delayed(
        const Duration(seconds: 2),
        () {
          submitMessage(
            ChatMessage(
              message: '$text! 참 좋은 나라죠.\n어느 계절에 떠나시고 싶으신가요?\n봄, 여름, 가을, 겨울 중에 말씀해주세요!',
              sender: 'gpt',
            ),
          );
          notifyListeners();
        },
      );
      return;
    }

    if (_newSeason == '') {
      if (text.contains('여름') ||
          text.contains('겨울') ||
          text.contains('봄') ||
          text.contains('가을')) {
        _newSeason = text;
        submitMessage(ChatMessage(
          message: text,
          sender: 'phone',
        ));
        _textEditingController.clear();
        unFocus();
        notifyListeners();
        await Future.delayed(
          const Duration(seconds: 2),
          () {
            if (text.contains('봄')) {
              submitMessage(
                ChatMessage(
                  message: '따스한 봄햇살을 맞으며 여행하는 것은\n누구에게나 기쁜 일이죠.',
                  sender: 'gpt',
                ),
              );
              notifyListeners();
            } else if (text.contains('여름')) {
              submitMessage(
                ChatMessage(
                  message: '여름의 무더위를 날려버리기 위해\n여행은 좋은 선택 중 하나이죠.',
                  sender: 'gpt',
                ),
              );
              notifyListeners();
            } else if (text.contains('가을')) {
              submitMessage(
                ChatMessage(
                  message: '쓸쓸한 가을을 여행으로 채워보세요.',
                  sender: 'gpt',
                ),
              );
              notifyListeners();
            } else if (text.contains('겨울')) {
              submitMessage(
                ChatMessage(
                  message: '겨울은 매우 춥지만\n동시에 여행할 거리가 매우 많은 계절이죠.',
                  sender: 'gpt',
                ),
              );
              notifyListeners();
            }
          },
        );
        ChatbotRequest newRequest = ChatbotRequest(
          destination: _newDestination,
          season: _newSeason,
          purpose: _newPurpose,
        );
        _chatbotRequest = newRequest;
        if (context.mounted) {
          ChatbotRepository()
              .RecommendActivityNotTraveling(context, newRequest);
        }
        await Future.delayed(const Duration(seconds: 1), () {
          submitMessage(
            ChatMessage(
              message: '여봇이 대답을 생성하고 있어요!\n 생성이 완료되면 알림으로\n 알려드릴게요~!',
              sender: 'gpt',
            ),
          );
          submitMessageListToServer(context);
          notifyListeners();
        });
        return;
      } else {
        return;
      }
    }
  }

  Future<void> notTravelNoDestSubmit(String text, BuildContext context) async {
    if (_newDestination == '') {
      _newDestination = text;
      submitMessage(
        ChatMessage(
          message: text,
          sender: 'phone',
        ),
      );
      _textEditingController.clear();
      unFocus();
      notifyListeners();
      await Future.delayed(const Duration(seconds: 2), () {
        submitMessage(
          ChatMessage(
            message: '$text! 참 좋은 지역이죠.\n어느 계절에 떠나시고 싶으신가요?\n봄, 여름, 가을, 겨울 중에 말씀해주세요!',
            sender: 'gpt',
          ),
        );
        notifyListeners();
      },
      );
      return;
    }
    if (_newSeason == '') {
      if (text.contains('여름') ||
          text.contains('겨울') ||
          text.contains('봄') ||
          text.contains('가을')) {
        _newSeason = text;
        submitMessage(
          ChatMessage(
            message: text,
            sender: 'phone',
          ),
        );
        _textEditingController.clear();
        unFocus();
        notifyListeners();
        await Future.delayed(const Duration(seconds: 2), () {
          if (text.contains('봄')) {
            submitMessage(
              ChatMessage(
                message: '따스한 봄햇살을 맞으며 여행하는 것은\n누구에게나 기쁜 일이죠.\n무엇을 즐기러 가실건가요?',
                sender: 'gpt',
              ),
            );
            notifyListeners();
          } else if (text.contains('여름')) {
            submitMessage(
              ChatMessage(
                message: '여름의 무더위를 날려버리기 위해\n여행은 좋은 선택 중 하나이죠.\n무엇을 즐기러 가실건가요?',
                sender: 'gpt',
              ),
            );
            notifyListeners();
          } else if (text.contains('가을')) {
            submitMessage(
              ChatMessage(
                message: '쓸쓸한 가을을 여행으로 채워보세요.\n무엇을 즐기러 가실건가요?',
                sender: 'gpt',
              ),
            );
            notifyListeners();
          } else if (text.contains('겨울')) {
            submitMessage(
              ChatMessage(
                message: '겨울은 매우 춥지만\n동시에 여행할 거리가 매우 많은 계절이죠.\n무엇을 즐기러 가실건가요?',
                sender: 'gpt',
              ),
            );
            notifyListeners();
          }
        },);
        return;
      } else {
        return;
      }
    }
    if (_newPurpose == '') {
      _newPurpose = text;
      submitMessage(
        ChatMessage(
          message: text,
          sender: 'phone',
        ),
      );
      _textEditingController.clear();
      unFocus();
      notifyListeners();
      await Future.delayed(
        const Duration(seconds: 2),
        () {
          submitMessage(
            ChatMessage(
              message: '$_newSeason에\n$_newDestination을 가시는 군요',
              sender: 'gpt',
            ),
          );
          notifyListeners();
        },
      );
      ChatbotRequest newRequest = ChatbotRequest(
        destination: _newDestination,
        season: _newSeason,
        purpose: _newPurpose,
      );
      _chatbotRequest = newRequest;
      if (context.mounted) {
        ChatbotRepository().RecommendPlace(context, newRequest);
      }
      await Future.delayed(
        const Duration(seconds: 1),
        () {
          submitMessage(
            ChatMessage(
              message: '여봇이 대답을 생성하고 있어요!\n 생성이 완료되면 알림으로\n 알려드릴게요~!',
              sender: 'gpt',
            ),
          );
          submitMessageListToServer(context);
          notifyListeners();
        },
      );
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


  // 언포커스
  void unFocus() {
    _chatbotFocus.unfocus();
    notifyListeners();
  }

  // 제공
  void changeInputText(String text) {
    _inputText = text;
    notifyListeners();
  }

  // 좌로 모여 우로 모여
  CrossAxisAlignment getAlignment(int index) {
    if (_messages[index].sender == "gpt") {
      return CrossAxisAlignment.start;
    } else {
      return CrossAxisAlignment.end;
    }
  }

  // 프로필 이미지
  Image profileImage(index) {
    if (_messages[index].sender == "gpt") {
      return Image.asset('assets/images/canton.png');
    } else {
      return Image.network(
          "http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg");
    }
  }

  // 유저인지 아닌지
  bool sender(index) {
    if (_messages[index].sender == "gpt") {
      return false;
    } else {
      return true;
    }
  }

  // 말풍선 색상
  Color bubbleColor(index) {
    if (_messages[index].sender == "gpt") {
      return const Color(0xfffdeedc);
    } else {
      return const Color(0xffddf3fd);
    }
  }

  // 아래로 스크롤
  void goBottom() {
    WidgetsBinding.instance.addPostFrameCallback(
      (_) {
        _scrollController.jumpTo(
          _scrollController.position.maxScrollExtent,
        );
      },
    );
  }

  // 액티비티 추천
  Future<void> selectActivity(context) async {
    _isAnswered = true;
    submitMessage(ChatMessage(message: '놀러갈 곳 추천 "해"', sender: 'phone'));
    notifyListeners();
    ChatbotRepository().RecommendActivity(context);
    await Future.delayed(
      const Duration(seconds: 2),
      () {
        submitMessage(
          ChatMessage(
              message: '알겠습니다!'
                  '\n최신 포스트 위치를 기준으로\n가볼 만한 곳을 추천해드릴게요.'
                  '\n답변이 완료되면 알림으로 알려드리겠습니다~',
              sender: 'gpt'),
        );
        submitMessageListToServer(context);
        notifyListeners();
      },
    );
  }

  // 식당 추천
  Future<void> selectRestaurant(context) async {
    _isAnswered = true;
    submitMessage(ChatMessage(message: '식당 추천 "해"', sender: 'phone'));
    notifyListeners();
    ChatbotRepository().RecommendRestaurant(context);
    await Future.delayed(
      const Duration(seconds: 2),
      () {
        submitMessage(
          ChatMessage(
              message: '알겠습니다!'
                  '\n최신 포스트 위치를 기준으로\n맛있는 식당을 추천해드릴게요.'
                  '\n답변이 완료되면 알림으로 알려드리겠습니다~',
              sender: 'gpt',),
        );
        submitMessageListToServer(context);
        notifyListeners();
      },
    );
  }

  void userHaveDestination() async {
    submitMessage(ChatMessage(message: '오냐', sender: 'phone'));
    _isAnswered = true;
    _ableTextField = true;
    _haveDestination = true;

    notifyListeners();
    await Future.delayed(
      const Duration(seconds: 2),
      () {
        submitMessage(ChatMessage(
            message: '그러시군요!'
                '\n어떤 나라에 가실 예정이신가요?'
                '\n하나의 나라이름을 입력해주세요^^',
            sender: 'gpt'));
        notifyListeners();
      },
    );
  }

  void userHaveNoDestination() async {
    submitMessage(ChatMessage(message: '아니', sender: 'phone'));
    _isAnswered = true;
    _ableTextField = true;

    notifyListeners();
    await Future.delayed(
      const Duration(seconds: 2),
      () {
        submitMessage(ChatMessage(
            message: '그러시군요!'
                '\n어떤 지역에 가시고 싶으신가요?'
                '\n하나의 지역명을 입력해주세요^^',
            sender: 'gpt'));
        notifyListeners();
      },
    );
  }

}
