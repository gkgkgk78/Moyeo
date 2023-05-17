
import 'package:extended_image/extended_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:provider/provider.dart';

import '../view_models/app_view_model.dart';
import '../view_models/modify_profile_view_model.dart';

class ModifyProfile extends StatelessWidget {
  const ModifyProfile({super.key});

  @override
  Widget build(BuildContext context) {
    final appViewModel = Provider.of<AppViewModel>(context, listen: false);
    return ChangeNotifierProvider<ModifyProfileViewModel>(
      create: (_) => ModifyProfileViewModel(
        appViewModel.userInfo.profileImageUrl,
        appViewModel.userInfo.nickname,
      ),
      child: Consumer<ModifyProfileViewModel>(
        builder: (context, viewModel, child) {
          return Scaffold(
            body: Column(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                Stack(
                  children: [
                    viewModel.selectedImageFile == null
                        ? ExtendedImage.network(
                            viewModel.imagePath,
                            width: 250,
                            height: 250,
                            fit: BoxFit.cover,
                            shape: BoxShape.circle,
                            borderRadius: BorderRadius.circular(30.0),
                          )
                        : ExtendedImage.file(
                            viewModel.selectedImageFile,
                            width: 250,
                            height: 250,
                            fit: BoxFit.cover,
                            shape: BoxShape.circle,
                          ),
                    Positioned(
                      bottom: 0,
                      right: 0,
                      child: InkWell(
                        onTap: () {
                          viewModel.changeProfileImage();
                        },
                        child: const Icon(
                          Icons.settings,
                          color: Colors.grey,
                          size: 30,
                        ),
                      ),
                    )
                  ],
                ),
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 30),
                  child: SizedBox(
                    height: 80,
                    child: TextField(
                      focusNode: viewModel.inputFocus,
                      controller: viewModel.textEditController,
                      onChanged: (value) {
                        viewModel.changeNicknameInput(context, value);
                      },
                      cursorColor: Colors.redAccent,
                      decoration: InputDecoration(
                        labelText: '원하시는 닉네임을 입력해주세요!',
                        labelStyle: TextStyle(
                          color: viewModel.inputFocus.hasFocus
                              ? Colors.purple
                            : Colors.grey
                        ),
                        focusedBorder: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(10.0),
                          borderSide: const BorderSide(
                            color: Colors.limeAccent,
                            width: 2.0,
                          ),
                        ),
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(10.0),
                          borderSide: const BorderSide(
                            color: Colors.grey,
                            width: 2.0,
                          ),
                        ),
                      ),
                    ),
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 30),
                  child: SizedBox(
                    height: 40,
                    width: double.infinity,
                    child: ElevatedButton(
                      style: ElevatedButton.styleFrom(
                        foregroundColor: Colors.white, // text color
                        backgroundColor: Colors.blue, // button color
                      ),
                      onPressed: () {
                        viewModel.sendModifyProfile(context, appViewModel);
                      },
                      child: const Text('수정'),
                    ),
                  ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }
}
