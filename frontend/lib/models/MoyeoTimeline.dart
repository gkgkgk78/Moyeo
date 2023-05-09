// MoyeoTimeline DTO
class MoyeoTimeline{
  final int timelineId;
  final int moyeoTimelineId;
  final int userId;

  MoyeoTimeline({
    required this.timelineId,
    required this.moyeoTimelineId,
    required this.userId
  });

  Map<String, dynamic> toJson() => {
    'timelineId':timelineId,
    'moyeoTimelineId':timelineId,
    'userId':userId,
  };

  factory MoyeoTimeline.fromJson(Map<String, dynamic> json){
    return MoyeoTimeline(
        timelineId: json['timelineId'],
        moyeoTimelineId: json['moyeoTimelineId'],
        userId: json['userId']
    );
  }

}