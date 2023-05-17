
import 'package:extended_image/extended_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_keyboard_visibility/flutter_keyboard_visibility.dart';
import 'package:moyeo/views/search_result_page.dart';
import 'package:provider/provider.dart';

import '../view_models/app_view_model.dart';
import '../view_models/my_feed_view_model.dart';
import '../view_models/search_bar_view_model.dart';
import '../view_models/search_result_view_model.dart';
import 'my_feed_view.dart';

class SearchBar extends StatelessWidget {
  const SearchBar({super.key});

  @override
  Widget build(BuildContext context) {
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
                          !viewModel.isMyFeed
                              ?
                              // 키워드가 없을 때엔 검색 결과창이 뜨지 않는다.
                              viewModel.myFocus.hasFocus &&
                                      viewModel.searchKeyWord != "" && isKeyboardVisible
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
                                                    onTap: () {
                                                      appViewModel.changeTitle('"${viewModel.searchKeyWord!}" 검색 결과');
                                                      FocusScope.of(context).unfocus();
                                                      Navigator.push(
                                                        context,
                                                        PageRouteBuilder(
                                                          pageBuilder: (context, _,
                                                                  __) =>
                                                              ChangeNotifierProvider<
                                                                  SearchResultViewModel>(
                                                            create: (_) =>
                                                                SearchResultViewModel(
                                                              context,
                                                              viewModel
                                                                  .searchKeyWord!,
                                                              isMyFeed: viewModel
                                                                  .isMyFeed,
                                                            ),
                                                            child:
                                                                SearchResultView(),
                                                          ),
                                                          transitionDuration:
                                                              Duration.zero,
                                                        ),
                                                      );
                                                    },
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
                                                      appViewModel.changeTitle(
                                                          '${viewModel
                                                              .searchedResults[
                                                          index - 1]
                                                              .nickname}의 모여');
                                                      FocusScope.of(context).unfocus();
                                                      Navigator.push(
                                                        context,
                                                        PageRouteBuilder(
                                                          pageBuilder:
                                                              (context, _, __) =>
                                                                  MultiProvider(
                                                            providers: [
                                                              ChangeNotifierProvider<
                                                                  MyFeedViewModel>(
                                                                create: (_) =>
                                                                    MyFeedViewModel(
                                                                  context: context,
                                                                  myInfo:
                                                                      appViewModel
                                                                          .userInfo,
                                                                  userInfo: viewModel
                                                                          .searchedResults[
                                                                      index - 1],
                                                                ),
                                                              ),
                                                              ChangeNotifierProvider<
                                                                  SearchBarViewModel>(
                                                                create: (_) =>
                                                                    SearchBarViewModel(
                                                                  isMyFeed: false,
                                                                ),
                                                              ),
                                                            ],
                                                            child: MyFeedView(),
                                                          ),
                                                          transitionDuration:
                                                              Duration.zero,
                                                        ),
                                                      );
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
                              : viewModel.myFocus.hasFocus &&
                                      viewModel.searchKeyWord != "" && isKeyboardVisible
                                  ? Expanded(
                                      child: Container(
                                        margin: const EdgeInsets.only(top: 30),
                                        decoration: const BoxDecoration(
                                            color: Colors.white,
                                            border: Border(
                                              top: BorderSide(
                                                  color: Colors.transparent,
                                                  width: 2),
                                              left: BorderSide(
                                                  color: Colors.black54, width: 2),
                                              right: BorderSide(
                                                  color: Colors.black54, width: 2),
                                              bottom: BorderSide(
                                                  color: Colors.black54, width: 2),
                                            )),
                                        child: Container(
                                          margin: const EdgeInsets.only(top: 14),
                                          child: SizedBox(
                                            height: 50,
                                            child: GestureDetector(
                                              onTap: () {
                                                appViewModel.changeTitle('"${viewModel.searchKeyWord!}" 검색 결과');
                                                FocusScope.of(context).unfocus();
                                                Navigator.push(
                                                  context,
                                                  PageRouteBuilder(
                                                    pageBuilder: (context, _, __) =>
                                                        ChangeNotifierProvider<
                                                            SearchResultViewModel>(
                                                      create: (_) =>
                                                          SearchResultViewModel(
                                                        context,
                                                        viewModel.searchKeyWord!,
                                                        isMyFeed:
                                                            viewModel.isMyFeed,
                                                      ),
                                                      child: SearchResultView(),
                                                    ),
                                                    transitionDuration:
                                                        Duration.zero,
                                                  ),
                                                );
                                              },
                                              child: ListTile(
                                                leading: const Icon(Icons.search),
                                                title: Text(
                                                    "${viewModel.searchKeyWord}(으)로 검색..."),
                                              ),
                                            ),
                                          ),
                                        ),
                                      ),
                                    )
                                  : const SizedBox.shrink(),
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
                        onSubmitted: (String keyWord) {
                          appViewModel.changeTitle('"${viewModel.searchKeyWord!}" 검색 결과');
                          viewModel.unFocus();
                          Navigator.push(
                            context,
                            PageRouteBuilder(
                              pageBuilder: (context, _, __) {
                                return ChangeNotifierProvider<
                                    SearchResultViewModel>(
                                  create: (_) => SearchResultViewModel(
                                    context,
                                    keyWord,
                                    isMyFeed: viewModel.isMyFeed,
                                  ),
                                  child: const SearchResultView(),
                                );
                              },
                              transitionDuration: Duration.zero,
                            ),
                          );
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
                          suffixIcon: Icon(Icons.search, color: Colors.orangeAccent),
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
