
import 'package:flutter/cupertino.dart';
import 'package:kakao_flutter_sdk/kakao_flutter_sdk_friend.dart';
import '../services/moyeo_repository.dart';
import '../models/UserInfo.dart';
import 'app_view_model.dart';

class SelectedUsersProvider extends ChangeNotifier{
  late AppViewModel _appViewModel;

  List<UserInfo> _selectedUsers = [];

  List<UserInfo> get selectedUsers => _selectedUsers;

  void addUser(UserInfo user){
    _selectedUsers.add(user);
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
      for (var member in members){
        if (member['userUid'] != person.userUid){
          userList.add(
            {"userId":person.userUid,}
          );
        }
      }
    }

    await MoyeoRepository().addMoyeoUser(context, moyeoTimelineId, userList);
    notifyListeners();
  }

  @override
  void dispose() {
    super.dispose();
  }
}