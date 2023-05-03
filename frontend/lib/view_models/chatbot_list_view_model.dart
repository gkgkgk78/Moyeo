
import 'package:flutter/material.dart';
import 'package:logger/logger.dart';
import 'package:mongo_dart/mongo_dart.dart';
import 'package:moyeo/views/chatbot_list_page.dart';

import '../services/chatbot_repository.dart';

var logger = Logger();

class ChatbotListViewModel extends ChangeNotifier {
  BuildContext _context;
  final List _testchatlist = [
    {"title": "테스트 제목1", "sub": "1", "chatId": 1},
    {"title": "테스트 제목2", "sub": "2", "chatId": 2},
    {"title": "테스트 제목3", "sub": "3", "chatId": 3},
    {"title": "테스트 푸시1", "sub": "1", "pushId": 1},
    {"title": "테스트 제목4", "sub": "4", "chatId": 4},
    {"title": "테스트 제목5", "sub": "5", "chatId": 5},
    {"title": "테스트 푸시2", "sub": "2", "pushId": 2},
    {"title": "테스트 푸시3", "sub": "3", "pushId": 3},
    {"title": "테스트 제목6", "sub": "6", "chatId": 6},
    {"title": "테스트 제목7", "sub": "7", "chatId": 7},
    {"title": "테스트 제목8", "sub": "8", "chatId": 8},
    {"title": "테스트 푸시4", "sub": "4", "pushId": 4},
    {"title": "테스트 제목9", "sub": "9", "chatId": 9},
    {"title": "테스트 제목10", "sub": "10", "chatId": 10},
    {"title": "테스트 제목11", "sub": "11", "chatId": 11},
    {"title": "테스트 푸시5", "sub": "5", "pushId": 5},
    {"title": "테스트 제목12", "sub": "12", "chatId": 12},
    {"title": "테스트 제목13", "sub": "13", "chatId": 13},
  ];

  List get chatlist => _testchatlist;

  late List _yeobotList;
  List get yeobotList => _yeobotList;

  late List _pushList;
  List get pushList => _pushList;

  List _chatList = [];

  ChatbotListViewModel(this._context) {
    // getChatList(_context);
    _yeobotList = _testchatlist.where((el) => el.containsKey("chatId")).toList();
    _pushList = _testchatlist.where((el) => el.containsKey("pushId")).toList();
    // testAPI();
  }

  Future<void> getMessageList(BuildContext context) async {
    _chatList = await ChatbotRepository().MessageListFromServer(context);
  }



}
