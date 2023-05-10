
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

  changeMoyeoFavorite(context, userUid) async {
    final res = await PostRepository()
        .changeFavoriteMoyeoPost(context, post.postId, userUid);
    post.isFavorite = res['favorite'];
    post.favoriteCount = res['totalFavorite'];
    notifyListeners();
  }

  //공유 변경
  //changePostPublic(BuildContext context) async {
  //     _isPublic =
  //     await PostRepository().changePostPublic(context, timelineId);
  //     notifyListeners();
  //   }

  // post 삭제
  deletePost(context) async {
    await PostRepository().deletePost(context, post.postId);
    notifyListeners();
  }

  // 모여 포스트 삭제
  deleteMoyeoPost(context) async {
    // 모여 포스트 아이디로 변경필요
    await PostRepository().deleteMoyeoPost(context, post.postId);
    notifyListeners();
  }

  // 모여 나가기
  // outMoyeo(context) async {
  //   await PostRepository().outMoyeo(context, moyeoTimelineId);
  //   notifyListeners();
  // }
}
