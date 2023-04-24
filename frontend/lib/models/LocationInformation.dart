import 'dart:typed_data';

class LocationInformation {
  final String country;
  final String? address2;
  final String? address3;
  final String? address4;
  late final Uint8List? flag;

  LocationInformation({
      required this.country,
      this.address2,
      this.address3,
      this.address4,
      this.flag
  });
}