
import 'package:flutter/cupertino.dart';

import '../models/Post.dart';
import '../services/post_repository.dart';

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

  // post 삭제
  deletePost(context) async {
    await PostRepository().deletePost(context, post.postId);
    notifyListeners();
  }

}
