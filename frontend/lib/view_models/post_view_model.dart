import 'package:danim/services/post_repository.dart';
import 'package:flutter/cupertino.dart';

import '../models/Post.dart';

class PostViewModel extends ChangeNotifier {
  final Post post;
  final bool isMine;

  PostViewModel(this.post, this.isMine);

  changeIsFavorite(context, userUid) async {
    final res = await PostRepository()
        .changeFavoritePost(context, post.postId, userUid);
    post.isFavorite = res['favorite'];
    post.favoriteCount = res['totalFavorite'];
    notifyListeners();
  }
}
