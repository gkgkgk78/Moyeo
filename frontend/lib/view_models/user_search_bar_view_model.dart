
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

  addMoyeoUser(context, List<Map<String,dynamic>> userList) async {
    await MoyeoRepository().addMoyeoUser(context, userList);
    notifyListeners();
  }

  @override
  void dispose() {
    super.dispose();
  }
}