class SearchedPost {
  final String thumbnailUrl;
  final int favorite;
  final List<moyeoTimelineList> timelineIds;
  final int postId;
  final String? timelineTitle;
  final bool isMoyeo;

  SearchedPost(
      {required this.thumbnailUrl,
      required this.favorite,
      required this.timelineIds,
      required this.postId,
      required this.timelineTitle,
      required this.isMoyeo
      });

  factory SearchedPost.fromJson(Map<String, dynamic> json) {
    return SearchedPost(
      thumbnailUrl: json["thumbNail"],
      favorite: json["totalFavorite"],
      timelineIds: List.from((json["timelineInfoList"].map(
        (el) => moyeoTimelineList.fromJson(el),
      ))),
      postId: json["postId"],
      timelineTitle: json["timelineTitle"],
      isMoyeo: json["isMoyeo"],
    );
  }
}

class moyeoTimelineList {
  final int timelineId;
  final String userProfileUrl;
  final String userNickname;

  moyeoTimelineList(
      {required this.timelineId,
      required this.userProfileUrl,
      required this.userNickname});

  factory moyeoTimelineList.fromJson(Map<String, dynamic> json) {
    return moyeoTimelineList(
      timelineId: json['timelineId'],
      userProfileUrl: json['userProfileImageUrl'],
      userNickname: json['userNickname'],
    );
  }
}
