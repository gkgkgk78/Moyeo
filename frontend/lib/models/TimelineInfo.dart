import 'package:danim/models/TimelineDetail.dart';

class TimelineInfo {
  final List<TimelineDetail> timelineDetails;
  final bool isComplete;
  final bool isMine;
  bool isPublic;

  TimelineInfo({
    required this.timelineDetails,
    required this.isPublic,
    required this.isComplete,
    required this.isMine,
  });

  factory TimelineInfo.fromJson(Map<String, dynamic> json) {
    return TimelineInfo(
      timelineDetails: json['timeline'] == null
          ? []
          : List.from(json['timeline'].map(
              (timelineDetail) => TimelineDetail.fromJson(timelineDetail))),
      isPublic: json['isPublic'],
      isComplete: json['isComplete'],
      isMine: json['isMine'],
    );
  }
}
