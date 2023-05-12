
import 'package:flutter/cupertino.dart';
import '../services/moyeo_repository.dart';
import '../models/UserInfo.dart';

class SelectedUsersProvider extends ChangeNotifier{
  List<UserInfo> _selectedUsers = [];

  List<UserInfo> get selectedUsers => _selectedUsers;

  void addUser(UserInfo user){
    _selectedUsers.add(user);
    notifyListeners();
  }

  void removeUser(UserInfo user){
    _selectedUsers.remove(user);
    notifyListeners();
  }

  addMoyeoUser(context, int moyeoTimelineId) async {
    final List<Map<String,dynamic>> userList = [];

    for (var person in selectedUsers) {
      userList.add(
          {"userId":person.userUid,}
      );
    }

    await MoyeoRepository().addMoyeoUser(context, moyeoTimelineId, userList);
    notifyListeners();
  }

  @override
  void dispose() {
    super.dispose();
  }
}