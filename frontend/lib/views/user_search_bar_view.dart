
import 'package:extended_image/extended_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_keyboard_visibility/flutter_keyboard_visibility.dart';
import 'package:moyeo/view_models/user_search_bar_view_model.dart';
import 'package:provider/provider.dart';

import '../view_models/app_view_model.dart';
import '../view_models/search_bar_view_model.dart';

class UserSearchBar extends StatelessWidget {
  final List<Map<String, dynamic>> members;

  const UserSearchBar({required this.members, Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final tapUser = Provider.of<SelectedUsersProvider>(context, listen: true);
    Set<Map<String, dynamic>> memberSet = members.toSet();
    return Consumer<AppViewModel>(
      builder: (context, appViewModel, _) {
        return Consumer<SearchBarViewModel>(
          builder: (context, viewModel, _) {
            return KeyboardVisibilityBuilder(
              builder: (context, isKeyboardVisible) {
                return Stack(
                  children: [
                    SizedBox(
                      height: viewModel.getSearchBarHeight(),
                      child: Column(
                        children: [
                              // 키워드가 없을 때엔 검색 결과창이 뜨지 않는다.
                              viewModel.myFocus.hasFocus &&
                                      viewModel.searchKeyWord != "" &&
                                  isKeyboardVisible
                                  // && memberSet.intersection(viewModel.searchedResults.toSet()) == {}
                                  ? Expanded(
                                      child: Container(
                                        margin: const EdgeInsets.only(top: 30),
                                        decoration: const BoxDecoration(
                                          color: Colors.white,
                                          border: Border(
                                            top: BorderSide(
                                              color: Colors.transparent,
                                              width: 2,
                                            ),
                                            left: BorderSide(
                                                color: Colors.black54, width: 2),
                                            right: BorderSide(
                                                color: Colors.black54, width: 2),
                                            bottom: BorderSide(
                                                color: Colors.black54, width: 2),
                                          ),
                                        ),
                                        child: Container(
                                          margin: const EdgeInsets.only(top: 14),
                                          child: SizedBox(
                                            height:
                                                (viewModel.searchedResults.length +
                                                        1) *
                                                    75,
                                            child: ListView.builder(
                                              itemCount:
                                                  viewModel.searchedResults.length +
                                                      1,
                                              itemBuilder: (BuildContext context,
                                                  int index) {
                                                if (index == 0) {
                                                  return GestureDetector(
                                                    child: ListTile(
                                                      leading:
                                                          const Icon(Icons.search),
                                                      title: Text(
                                                          "${viewModel.searchKeyWord}(으)로 검색..."),
                                                    ),
                                                  );
                                                } else {
                                                  return GestureDetector(
                                                    onTap: () {
                                                      if (viewModel.searchedResults[index-1].moyeoTimelineId == -1
                                                      && viewModel.searchedResults[index-1].timeLineId != -1) {
                                                        tapUser.addUser(
                                                            viewModel
                                                                .searchedResults[index -
                                                                1], members
                                                        );
                                                        viewModel.unFocus();
                                                        FocusScope.of(context).unfocus();
                                                      } else if (viewModel.searchedResults[index-1].moyeoTimelineId != -1) {
                                                        showDialog(
                                                            barrierDismissible: false,
                                                            context: context,
                                                            builder: (ctx) => AlertDialog(
                                                              title: const Text('동행중인 사용자'),
                                                              content: const Text('동행 중인 유저는 추가할 수 없습니다.'),
                                                              actions: [
                                                                TextButton(
                                                                  onPressed: () {
                                                                    Navigator.pop(ctx);
                                                                  },
                                                                  child: const Text(
                                                                    '닫기',
                                                                    style: TextStyle(color: Colors.red),
                                                                  ),
                                                                ),
                                                              ],
                                                            )
                                                          );
                                                      } else if (viewModel.searchedResults[index-1].timeLineId == -1){
                                                        showDialog(
                                                            barrierDismissible: false,
                                                            context: context,
                                                            builder: (ctx) => AlertDialog(
                                                              title: const Text('휴식 중인 사용자'),
                                                              content: const Text('휴식 중인 유저는 추가할 수 없습니다.'),
                                                              actions: [
                                                                TextButton(
                                                                  onPressed: () {
                                                                    Navigator.pop(ctx);
                                                                  },
                                                                  child: const Text(
                                                                    '닫기',
                                                                    style: TextStyle(color: Colors.red),
                                                                  ),
                                                                ),
                                                              ],
                                                            )
                                                        );
                                                      }
                                                    },
                                                    child: Column(
                                                      children: [
                                                        const Divider(
                                                          height: 1,
                                                          thickness: 1,
                                                        ),
                                                        ListTile(
                                                          leading: SizedBox(
                                                            width: 45,
                                                            height: 45,
                                                            child: ClipRRect(
                                                              borderRadius:
                                                                  BorderRadius
                                                                      .circular(
                                                                          30.0),
                                                              child: ExtendedImage
                                                                  .network(
                                                                viewModel
                                                                    .searchedResults[
                                                                        index - 1]
                                                                    .profileImageUrl,
                                                                fit: BoxFit.cover,
                                                                cache: true,
                                                              ),
                                                            ),
                                                          ),
                                                          title: Text(viewModel
                                                              .searchedResults[
                                                                  index - 1]
                                                              .nickname),
                                                          trailing:
                                                          viewModel.searchedResults[index - 1].timeLineId == -1
                                                            ? const Text("휴식중")
                                                            : Text(viewModel.searchedResults[index - 1].moyeoTimelineId == -1
                                                              ? ""
                                                              : "동행중"),
                                                        ),
                                                      ],
                                                    ),
                                                  );
                                                }
                                              },
                                            ),
                                          ),
                                        ),
                                      ),
                                    )
                                  : const SizedBox.shrink()
                        ],
                      ),
                    ),
                    // 검색창을 위에 띄우기 위해 children 내에서 뒤에 위치시킨다.
                    Container(
                      color: Colors.white,
                      height: 45,
                      child: TextField(
                        autofocus: false,
                        focusNode: viewModel.myFocus,
                        keyboardType: TextInputType.text,
                        textInputAction: TextInputAction.done,
                        onChanged: (String? keyword) async {
                          viewModel.searchUser(context, keyword);
                        },
                        // 포커스 일 때 스타일 바꾸기
                        decoration: const InputDecoration(
                          border: OutlineInputBorder(
                              borderRadius: BorderRadius.all(Radius.circular(20)),
                              borderSide:
                                  BorderSide(color: Colors.black54, width: 2)),
                          enabledBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.all(Radius.circular(20)),
                            borderSide: BorderSide(color: Colors.grey, width: 1.5),
                          ),
                          focusedBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.all(Radius.circular(20)),
                            borderSide:
                                BorderSide(color: Colors.orangeAccent, width: 3),
                          ),
                          suffixIcon: Icon(Icons.search, color: Colors.blueAccent),
                          hintText: "검색...",
                          hintStyle: TextStyle(fontSize: 12),
                        ),
                      ),
                    ),
                  ],
                );
              }
            );
          },
        );
      },
    );
  }
}
