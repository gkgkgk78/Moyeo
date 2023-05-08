
import 'package:flutter/material.dart';
import 'package:logger/logger.dart';
import 'package:mongo_dart/mongo_dart.dart';
import 'package:moyeo/views/chatbot_list_page.dart';

import '../services/chatbot_repository.dart';

var logger = Logger();

class ChatbotListViewModel extends ChangeNotifier {
  BuildContext _context;
  List _chatList = [];

  List get chatList => _chatList;

  late List _yeobotList = [];
  List get yeobotList => _yeobotList;

  List _pushList =[];
  List get pushList => _pushList;

  ChatbotListViewModel(this._context) {
  }

}
