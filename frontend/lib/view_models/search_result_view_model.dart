import 'package:danim/models/SearchedPost.dart';
import 'package:danim/services/search_repository.dart';

import 'package:flutter/material.dart';



class SearchResultViewModel extends ChangeNotifier {
  final BuildContext _context;

  final String _keyword;
  String get keyword => _keyword;
  bool isMyFeed;

  List<SearchedPost> _searchedPosts = [];
  List<SearchedPost> get searchedPosts => _searchedPosts;

  SearchResultViewModel(this._context,this._keyword, {required this.isMyFeed}) {
    getPost(_context,_keyword);
    notifyListeners();
  }

  void getPost(BuildContext context, String searchKeyword) async {
    if (isMyFeed) {
      _searchedPosts = await SearchRepository().searchMyPosts(context, searchKeyword);
    } else {
      _searchedPosts = await SearchRepository().searchPosts(context, searchKeyword);
    }

    notifyListeners();
  }

}