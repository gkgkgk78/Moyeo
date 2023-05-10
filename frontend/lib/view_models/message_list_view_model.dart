import 'package:flutter/material.dart';

class MessageListViewModel extends ChangeNotifier {
  List _yeobotList = [];
  List get yeobotList => _yeobotList;

  List _pushList = [];
  List get pushList => _pushList;

  MessageListViewModel(BuildContext context) {}

}