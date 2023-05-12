class PushAlarm {
  final content;
  final createTime;
  final isChecked;
  final messageId;

  PushAlarm({required this.content, required this.createTime, required this.isChecked, required this.messageId});

  factory PushAlarm.fromJson(Map<String, dynamic> json) {
    return PushAlarm(
        content: json['content'],
        createTime: json['createTime'],
        isChecked: json['isChecked'],
        messageId: json['messageId'],
    );
  }
}