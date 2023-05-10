import 'package:flutter/material.dart';
import 'package:logger/logger.dart';
import 'package:moyeo/services/message_repository.dart';
import 'package:moyeo/view_models/app_view_model.dart';
import 'package:provider/provider.dart';

import '../models/UserInfo.dart';

var logger = Logger();

class MessageListViewModel extends ChangeNotifier {
  List _pushList = [];
  List get pushList => _pushList;

  List _inviteList = [];
  List get inviteList => _inviteList;

  late UserInfo userInfo;

  late AppViewModel _appViewModel;

  int _initialIndex = 0;
  int get initialIndex => _initialIndex;

  MessageListViewModel(BuildContext context, {required this.userInfo}) {
    _appViewModel = Provider.of<AppViewModel>(context);
    if(_appViewModel.fromPush == true) {
      _initialIndex = 1;
    }
    getPushList(context);
  }

  Future<void> getPushList(BuildContext context) async {
    _pushList = await MessageRepository().getPushList(context, userInfo.userUid);
    logger.d(_pushList);
    notifyListeners();
  }

  Future<void> getInviteList(BuildContext context) async {
  }

  @override
  void dispose() {
    _appViewModel.changeFromPush();
    super.dispose();
  }
}