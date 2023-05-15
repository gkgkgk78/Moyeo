
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
        child: Row(
          children: [
            // 프로필 이미지
            Container(
              width: 100,
            ),
            // 사용자 정보
            Container(
              width: 200,
              child: Column(
                children: [
                  // 나의 상태 표시 ( 여행중)
                  Container(
                    height: 50,
                    child: Text(
                      appViewModel.userInfo.timeLineId != -1
                      ? appViewModel.userInfo.moyeoTimelineId != -1
                        ? "모여 여행중"
                        : "여행중"
                      : "휴식중"
                    ),
                  ),
                  // 모여와 함께한지 몇일 이라던가 데이터 표시할 곳
                  Container(
                    height: 50,
                    child: Text(
                      "모여와 함께 한지 "
                    ),
                  ),
                ],
              ),
            ),
          ],
        )
      );
    }
    );
  }
}