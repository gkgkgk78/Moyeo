import 'package:danim/services/chatbot_repository.dart';
import 'package:flutter/material.dart';

class ChatbotListViewModel extends ChangeNotifier {
  BuildContext _context;

  final List _testchatlist = [
    {"title": "테스트 제목1", "sub": "1", "chatId": 1},
    {"title": "테스트 제목2", "sub": "2", "chatId": 2},
    {"title": "테스트 제목3", "sub": "3", "chatId": 3},
    {"title": "테스트 제목4", "sub": "4", "chatId": 4},
    {"title": "테스트 제목5", "sub": "5", "chatId": 5},
    {"title": "테스트 제목6", "sub": "6", "chatId": 6},
    {"title": "테스트 제목7", "sub": "7", "chatId": 7},
    {"title": "테스트 제목8", "sub": "8", "chatId": 8},
    {"title": "테스트 제목9", "sub": "9", "chatId": 9},
    {"title": "테스트 제목10", "sub": "10", "chatId": 10},
    {"title": "테스트 제목11", "sub": "11", "chatId": 11},
    {"title": "테스트 제목12", "sub": "12", "chatId": 12},
    {"title": "테스트 제목13", "sub": "13", "chatId": 13},
  ];

  List get chatlist => _testchatlist;

  List _chatList = [];

  ChatbotListViewModel(this._context) {
    // getChatList(_context);
  }

  Future<void> getChatList(BuildContext context) async {
    _chatList = await ChatbotRepository().ChatListFromServer(context);
  }

}
