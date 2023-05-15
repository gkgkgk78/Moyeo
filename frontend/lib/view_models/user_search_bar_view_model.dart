
import 'package:flutter/cupertino.dart';
import 'package:kakao_flutter_sdk/kakao_flutter_sdk_friend.dart';
import '../services/moyeo_repository.dart';
import '../models/UserInfo.dart';
import 'app_view_model.dart';

class SelectedUsersProvider extends ChangeNotifier{
  late AppViewModel _appViewModel;

  List<UserInfo> _selectedUsers = [];

  List<UserInfo> get selectedUsers => _selectedUsers;

  List<Map<String, dynamic>> get selectedUsersMapList
    => _selectedUsers.map((userInfo) => userInfo.toJson()).toList();


  void addUser(UserInfo user, List<Map<String, dynamic>> members){
    if (user.moyeoTimelineId == -1){
      for (var member in members){
        if (member['userUid'] != user.userUid){
          if (_selectedUsers.isEmpty){
            _selectedUsers.add(user);
          } else {
            for (var selected in _selectedUsers) {
              if (selected.userUid != user.userUid){
                _selectedUsers.add(user);
              }
            }
          }
        }
      }
    } else{
      // 여기에 다른 동행 참여중인 사람 알림 띄우기
    }
    notifyListeners();
  }

  SelectedUsersProvider(context, {required nowMoyeo}) {}

  void removeUser(UserInfo user){
    _selectedUsers.remove(user);
    notifyListeners();
  }

  addMoyeoUser(context, int moyeoTimelineId, List<Map<String, dynamic>> members) async {
    final List<Map<String,dynamic>> userList = [];

    for (var person in selectedUsers) {
      userList.add(
          {"userId":person.userUid,}
      );

    }

    await MoyeoRepository().addMoyeoUser(context, moyeoTimelineId, userList);
    notifyListeners();
  }

}