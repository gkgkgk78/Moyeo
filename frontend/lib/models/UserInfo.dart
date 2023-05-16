class UserInfo {
  final int userUid;
  final String profileImageUrl;
  final String nickname;
  int timeLineId;
  int moyeoTimelineId;
  int timelineNum;
  // bool nowMoyeo;

  UserInfo({
    required this.userUid,
    required this.profileImageUrl,
    required this.nickname,
    this.moyeoTimelineId = -1,
    this.timeLineId = -1,
    this.timelineNum = 0,
    // this.nowMoyeo = false,
  });

  factory UserInfo.fromJson(Map<String, dynamic> json) {
    return UserInfo(
      userUid: json['userUid'],
      profileImageUrl: json['profileImageUrl'],
      nickname: json['nickname'],
      moyeoTimelineId: json['moyeoTimelineId'] ?? -1,
      timeLineId: json['timeLineId'] ?? -1,
      timelineNum: json['timelineNum'] ?? 0,
      // nowMoyeo: json['nowMoyeo'] ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userUid': userUid,
      'profileImageUrl': profileImageUrl,
      'nickname': nickname,
      'moyeoTimelineId': moyeoTimelineId,
      'timeLineId': timeLineId,
      'timelineNum': timelineNum,
    };
  }
}
