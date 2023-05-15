import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:logger/logger.dart';
import 'package:moyeo/models/PushAlarm.dart';
import 'package:moyeo/services/message_repository.dart';
import 'package:moyeo/view_models/app_view_model.dart';
import 'package:provider/provider.dart';

import '../models/UserInfo.dart';

var logger = Logger();

class MessageListViewModel extends ChangeNotifier {
  List<PushAlarm> _pushList = [];

  List get pushList => _pushList;

  List<PushAlarm> _gptList = [];

  List get gptList => _gptList;

  List<PushAlarm> _inviteList = [];

  List get inviteList => _inviteList;

  late UserInfo userInfo;

  int _initialIndex = 0;

  int get initialIndex => _initialIndex;

  MessageListViewModel(BuildContext context, bool fromPush,
      {required this.userInfo}) {
    if (fromPush == true) {
      _initialIndex = 1;
    }

    getPushList(context);
  }

  Future<void> getPushList(BuildContext context) async {
    _pushList =
        await MessageRepository().getPushList(context, userInfo.userUid);
    _gptList = _pushList.where(
      (el) {
        return el.inviteKey == null;
      },
    ).toList();
    _inviteList = _pushList.where(
      (el) {
        return el.inviteKey != null;
      },
    ).toList();
    notifyListeners();
  }

  Future<void> readOneMessage(BuildContext context, int messageId) async {
    await MessageRepository().readOnePush(context, messageId);
    if (context.mounted) {
      await MessageRepository().getPushList(context, userInfo.userUid);
    }
  }

  @override
  void dispose() {
    super.dispose();
  }
}
