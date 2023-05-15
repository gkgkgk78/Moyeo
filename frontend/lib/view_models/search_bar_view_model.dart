
import 'package:flutter/material.dart';
import 'package:flutter_keyboard_visibility/flutter_keyboard_visibility.dart';

import '../models/UserInfo.dart';
import '../services/search_repository.dart';

class SearchBarViewModel extends ChangeNotifier {
  final bool isMyFeed;

  String? _searchKeyWord = "";

  String? get searchKeyWord => _searchKeyWord;

  double getSearchBarHeight() {
    if (!isMyFeed) {
      if (_searchedResults.isEmpty) {
        return (_searchedResults.length + 1) * 105;
      } else if (_searchedResults.length == 1) {
        return (_searchedResults.length + 1) * 82;
      } else if (_searchedResults.length >= 2 && _searchedResults.length <= 4) {
        return (_searchedResults.length + 1) * 75;
      } else {
        return 450;
      }
    }
    return 105;
  }

  List<UserInfo> _searchedResults = [];

  List<UserInfo> get searchedResults => _searchedResults;

  List? _posts;

  List? get posts => _posts;

  final bool _isSearching = false;

  bool get isSearching => _isSearching;

  SearchBarViewModel({required this.isMyFeed}) {
    keyboardVisibilityController.onChange.listen((bool visible) {
      if ( visible == false ) {
        unFocus();
      }
    });
  }

  final FocusNode _myFocus = FocusNode();

  FocusNode get myFocus => _myFocus;

  UnfocusDisposition disposition = UnfocusDisposition.scope;

  KeyboardVisibilityController keyboardVisibilityController = KeyboardVisibilityController();

  Future<void> searchUser(BuildContext context, String? keyword) async {
    _searchKeyWord = keyword;
    if (keyword != "") {
      if (!isMyFeed) {
        _searchedResults =
            await SearchRepository().searchToSearchBar(context, keyword!);
      }
    }
    notifyListeners();
  }

  void unFocus() {
    _myFocus.unfocus();
    notifyListeners();
  }

  // @override
  // void dispose() {
  //   // TODO: implement dispose
  //   // isSearching = false;
  //   // searchKeyWord = "";
  //   super.dispose();
  // }
}
