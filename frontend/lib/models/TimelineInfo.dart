import 'package:moyeo/models/TimelineDetail.dart';

class TimelineInfo {
  final List<TimelineDetail>? timelineDetails;
  final bool isComplete;
  final bool isMine;
  final bool nowMoyeo;
  List<Map<String,dynamic>>? members;
  bool isPublic;

  TimelineInfo({
    required this.timelineDetails,
    required this.isPublic,
    required this.isComplete,
    required this.isMine,
    required this.nowMoyeo,
    required this.members
  });

  factory TimelineInfo.fromJson(Map<String, dynamic> json) {
    return TimelineInfo(
      timelineDetails: List.from(json['timeline'].map(
              (timelineDetail) => TimelineDetail.fromJson(timelineDetail))),
      isPublic: json['isPublic'],
      isComplete: json['isComplete'],
      isMine: json['isMine'],
      nowMoyeo: json['nowMoyeo'],
      members: List<Map<String,dynamic>>.from(json['nowMembers']),
    );
  }
}
