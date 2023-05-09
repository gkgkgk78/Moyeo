
import 'package:extended_image/extended_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:provider/provider.dart';

import '../view_models/app_view_model.dart';
import '../views/login_page.dart';

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
      ),
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
