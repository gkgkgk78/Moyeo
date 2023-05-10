class Post {
  final int postId;
  final String voiceUrl;
  final double voiceLength;
  final String address;
  final String text;
  final List<String> photoList;
  // 동행 추가
  final List<Map<String,dynamic>> members;
  bool isMoyeo;
  bool isFavorite;
  int favoriteCount;
  bool isExpand;

  Post({
    required this.postId,
    required this.voiceUrl,
    required this.voiceLength,
    required this.address,
    required this.text,
    required this.photoList,
    // 동행 추가
    required this.members,
    required this.isFavorite,
    required this.favoriteCount,
    required this.isMoyeo,
    this.isExpand = false,
  });

  factory Post.fromJson(Map<String, dynamic> json) {
    return Post(
      postId: json['postId'],
      voiceUrl: json['voiceUrl'],
      voiceLength: json['voiceLength'],
      address: (json['address2'] ?? '') +
          ' ' +
          (json['address3'] ?? '') +
          ' ' +
          (json['address4'] ?? ''),
      text: json['text'],
      photoList: List.from(json['photoList']),
      members: List<Map<String,dynamic>>.from(json['members']),
      isMoyeo: json['isMoyeo'],
      isFavorite: json['isFavorite'],
      favoriteCount: json['favoriteCount'],
    );
  }
}
