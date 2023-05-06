
import 'package:flutter/cupertino.dart';
import '../models/UserInfo.dart';

class SelectedUsersProvider with ChangeNotifier{
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
}