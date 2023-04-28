import 'package:danim/view_models/app_view_model.dart';
import 'package:danim/views/login_page.dart';
import 'package:extended_image/extended_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:provider/provider.dart';

class CustomAppBar extends StatelessWidget implements PreferredSizeWidget {
  final Function moveToModifyProfile;
  final Function moveToYeobot;

  const CustomAppBar({super.key, required this.moveToModifyProfile, required this.moveToYeobot});

  @override
  Widget build(BuildContext context) {
    final appViewModel = Provider.of<AppViewModel>(context, listen: true);
    return AppBar(
      automaticallyImplyLeading: false,
      leading: const Padding(
        padding: EdgeInsets.only(left: 10),
        child: 
        // 이전 UI
        // CircleAvatar(
        //   foregroundImage: AssetImage('assets/images/transparent_logo.png'),
        //   backgroundColor: Colors.transparent,
        // ),
        Icon(Icons.arrow_back_ios)
      ),
      actions: [
        Theme(
          data: Theme.of(context).copyWith(
            highlightColor: Colors.white,
            splashColor: Colors.white,
          ),
          child: PopupMenuButton(
            tooltip: '',
            offset: const Offset(0, 55),
            icon: const Icon(Icons.menu),
            // 이전 UI
            // ExtendedImage.network(
            //   appViewModel.userInfo.profileImageUrl,
            //   width: 50,
            //   height: 50,
            //   border: Border.all(
            //     color: Colors.white,
            //     width: 0.5,
            //   ),
            //   shape: BoxShape.circle,
            //   fit: BoxFit.cover,
            //   cache: true,
            //   borderRadius: BorderRadius.circular(30.0),
            // ),
            iconSize: 30,
            onSelected: (value) {
              switch (value) {
                case 0:
                  moveToYeobot();
                  break;
                case 1:
                  moveToModifyProfile();
                  break;
                case 2:
                  const storage = FlutterSecureStorage();
                  storage.deleteAll();
                  Navigator.pushAndRemoveUntil(
                    context,
                    PageRouteBuilder(
                      pageBuilder: (_, __, ___) => const LoginPage(),
                    ),
                    (routes) => false,
                  );
                  break;
              }
            },
            itemBuilder: (context) => <PopupMenuEntry>[
              const PopupMenuItem(
                  value: 0,
                  child: SizedBox(
                    width: 80,
                    child: Text('여봇'),
                  )
              ),
              const PopupMenuItem(
                value: 1,
                child: SizedBox(
                  width: 80,
                  child: Text('프로필변경'),
                ),
              ),
              const PopupMenuItem(
                value: 2,
                child: SizedBox(
                  width: 80,
                  child: Text(
                    '로그아웃',
                    style: TextStyle(color: Colors.red),
                  ),
                ),
              ),

            ],
          ),
        ),
      ],
      title: Text(
        appViewModel.title,
        maxLines: 1,
        overflow: TextOverflow.ellipsis,
        style: const TextStyle(color: Colors.black),
      ),
      centerTitle: true,
      // 그림자 없애기
      bottomOpacity: 0.0,
      elevation: 0.0,
    );
  }

  @override
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);
}
