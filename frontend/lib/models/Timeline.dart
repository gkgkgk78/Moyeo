// Timeline DTO
class Timeline {
  final int timelineId;
  final String nickname;
  final String title;
  final String createTime;
  final String finishTime;
  final String imageUrl;
  final String startPlace;
  final String finishPlace;

  Timeline({
    required this.timelineId,
    required this.nickname,
    required this.title,
    required this.createTime,
    required this.finishTime,
    required this.imageUrl,
    required this.startPlace,
    required this.finishPlace,
  });

  // Serialize(직렬화) Timeline객체 -> JSON객체
  Map<String, dynamic> toJson() => {
        'timelineId': timelineId,
        'userId': nickname,
        'title': title,
        'createTime': createTime,
        'modifyTime': finishTime,
        'imageUrl': imageUrl,
        'startPlace': startPlace,
        'finishPlace': finishPlace,
      };

  // Deserialize(파싱?) JSON객체 -> Timeline객체
  factory Timeline.fromJson(Map<String, dynamic> json) {
    return Timeline(
      timelineId: json['timelineId'],
      nickname: json['nickname'],
      title: json['title'],
      createTime: json['createTime'].length > 2
          ? json['createTime'].substring(2)
          : json['createTime'],
      finishTime: json['finishTime'].length > 2
          ? json['finishTime'].substring(2)
          : json['finishTime'],
      imageUrl: json['imageUrl'],
      startPlace: json['startPlace'],
      finishPlace: json['finishPlace'],
    );
  }
}
