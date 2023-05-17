import 'package:extended_image/extended_image.dart';
import 'package:flutter/material.dart';
import 'package:moyeo/utils/black.dart';
import 'package:provider/provider.dart';

import '../models/UserInfo.dart';
import '../view_models/app_view_model.dart';
import 'modify_profile.dart';

class UserInformationView extends StatelessWidget {
  final UserInfo userInfo;

  const UserInformationView({super.key, required this.userInfo});

  @override
  Widget build(BuildContext context) {
    return Consumer<AppViewModel>(builder: (_, appViewModel, __) {
      return Container(
        height: 80,
        margin: const EdgeInsets.symmetric(horizontal: 30),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: [
            Column(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Text(
                  userInfo.timelineNum.toString(),
                  style: const TextStyle(fontWeight: FontWeight.bold),
                ),
                const Text('게시물'),
              ],
            ),
            InkWell(
              splashColor: Colors.transparent,
              highlightColor: Colors.transparent,
              onTap: () {
                if (userInfo.userUid == appViewModel.userInfo.userUid) {
                  appViewModel.changeTitle('프로필 변경');
                  Navigator.push(
                    context,
                    PageRouteBuilder(
                      pageBuilder: (_, __, ___) => ModifyProfile(),
                      transitionDuration: Duration.zero,
                    ),
                  );
                }
              },
              child: ExtendedImage.network(
                userInfo.profileImageUrl,
                width: 80,
                height: 80,
                fit: BoxFit.cover,
                border: Border.all(
                  color: Colors.black,
                ),
                cache: true,
                shape: BoxShape.circle,
              ),
            ),
            Column(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Text(
                  userInfo.timeLineId == -1 ? '휴식중' : '여행중',
                  style: const TextStyle(
                      color: CustomColors.black,
                      fontWeight: FontWeight.bold
                  ),
                ),
                const Text('상태'),
              ],
            )
          ],
        ),
      );
    });
  }
}
