class SearchedPost {
  final String thumbnailUrl;
  final int favorite;
  final List<moyeoTimelineList> timelineId;
  final int postId;
  final String? timelineTitle;

  SearchedPost(
      {required this.thumbnailUrl,
      required this.favorite,
      required this.timelineId,
      required this.postId,
      required this.timelineTitle});

  factory SearchedPost.fromJson(Map<String, dynamic> json) {
    return SearchedPost(
      thumbnailUrl: json["thumbNail"],
      favorite: json["totalFavorite"],
      timelineId: json["timelineId"],
      postId: json["postId"],
      timelineTitle: json["timelineTitle"],
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
      userProfileUrl: json['userProfileUrl'],
      userNickname: json['userNickname'],
    );
  }
}
