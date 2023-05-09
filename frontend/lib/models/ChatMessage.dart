class ChatMessage {
  final message;
  final sender;

  ChatMessage({required this.message, required this.sender});

  Map<String, dynamic> toJson() => {
    'message': [message],
    'sender': sender,
  };

  factory ChatMessage.fromJson(Map<String, dynamic> json) {
    return ChatMessage(
        message: json['message'],
        sender: json['sender'],
    );
  }
}