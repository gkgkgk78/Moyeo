class Token {
  final accessToken;
  final refreshToken;
  final fcmToken;

  Token({required this.accessToken, required this.refreshToken, this.fcmToken});

  Map<String, dynamic> toJson() => {
        'accessToken': accessToken,
        'refreshToken': refreshToken,
        'deviceToken': fcmToken,
      };

  factory Token.fromJson(Map<String, dynamic> json) {
    return Token(
      accessToken: json['accessToken'],
      refreshToken: json['refreshToken'],
    );
  }
}
