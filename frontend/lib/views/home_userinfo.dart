
import 'package:provider/provider.dart';
import 'package:flutter/cupertino.dart';

import '../models/UserInfo.dart';
import '../view_models/app_view_model.dart';

class HomeUserInfo extends StatelessWidget{
  final UserInfo userInfo;

  const HomeUserInfo({super.key, required this.userInfo});

  @override
  Widget build(BuildContext context){
    return Consumer<AppViewModel>(builder: (_, appViewModel, __){
      return Container(
        height: 100,
        width: 300,
        // 유저의 상태를 표시해주는 (여행중/ 휴식중 등등) 혹은 모여를 시작한지 몇일째 이런거 넣어보기
        child: Container(),
      );
    }
    );
  }
}