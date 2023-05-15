class PushAlarm {
  final content;
  final createTime;
  final isChecked;
  final messageId;
  final inviteKey;

  PushAlarm({required this.content, required this.createTime, required this.isChecked, required this.messageId, required this.inviteKey});

  factory PushAlarm.fromJson(Map<String, dynamic> json) {
    return PushAlarm(
        content: json['content'],
        createTime: json['createTime'],
        isChecked: json['isChecked'],
        messageId: json['messageId'],
        inviteKey: json['inviteKey'],
    );
  }
}