
import 'package:flutter/cupertino.dart';
import 'package:kakao_flutter_sdk/kakao_flutter_sdk_friend.dart';
import '../services/moyeo_repository.dart';
import '../models/UserInfo.dart';

class SelectedUsersProvider extends ChangeNotifier{
  List<UserInfo> _selectedUsers = [];

  List<UserInfo> get selectedUsers => _selectedUsers;

  void addUser(UserInfo user){
    _selectedUsers.add(user);
    notifyListeners();
  }

  SelectedUsersProvider(context, {required nowMoyeo}) {
    if (nowMoyeo == -1) {
      MoyeoRepository().startMoyeo(context);
    }
  }

  void removeUser(UserInfo user){
    _selectedUsers.remove(user);
    notifyListeners();
  }

  addMoyeoUser(context, List<Map<String,dynamic>> userList, int moyeoTimelineId) async {
    await MoyeoRepository().addMoyeoUser(context, userList, moyeoTimelineId);
    notifyListeners();
  }

  @override
  void dispose() {
    super.dispose();
  }
}