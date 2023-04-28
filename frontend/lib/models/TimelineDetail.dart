import 'package:danim/models/Post.dart';

class TimelineDetail {
  final String flag;
  final String nation;
  final List<Post> postList;
  final String startDate;
  final String finishDate;
  bool isExpand;

  TimelineDetail({
    required this.flag,
    required this.nation,
    required this.postList,
    required this.startDate,
    required this.finishDate,
    this.isExpand = false,
  });

  factory TimelineDetail.fromJson(Map<String, dynamic> json) => TimelineDetail(
        flag: json['flag'],
        nation: json['nation'],
        postList: List.from(
          json['postList'].map(
            (jsonPost) => Post.fromJson(jsonPost),
          ),
        ),
        startDate:
            json['startDate'] == null ? '' : json['startDate'].substring(2),
        finishDate:
            json['finishDate'] == null ? '' : json['finishDate'].substring(2),
      );
}
