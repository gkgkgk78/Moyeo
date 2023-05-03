class ChatbotRequest {
  late final destination;
  late final season;
  late final purpose;

  ChatbotRequest({required this.destination, required this.season, required this.purpose});

  Map<String, dynamic> toPlaceJson() => {
    'destination': destination,
    'season': season,
    'purpose': purpose
  };

  Map<String, dynamic> toActivityJson() => {
    'destination': destination,
    'season': season,
  };
}
