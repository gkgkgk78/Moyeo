class SearchedPost {
  final String thumbnailUrl;
  final int favorite;
  final int timelineId;
  final int postId;
  final String timelineTitle;

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
        timelineTitle: json["timelineTitle"]);
  }
}
