
import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';

import '../utils/auth_dio.dart';

class MoyeoRepository{
  MoyeoRepository._internal();

  static final MoyeoRepository _instance = MoyeoRepository._internal();
}