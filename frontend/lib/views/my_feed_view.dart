
import 'package:flutter/material.dart';
import 'package:moyeo/views/search_bar_view.dart';
import 'package:moyeo/views/timeline_list_page.dart';
import 'package:moyeo/views/user_information_view.dart';
import 'package:provider/provider.dart';

import '../view_models/app_view_model.dart';
import '../view_models/my_feed_view_model.dart';
import '../view_models/search_bar_view_model.dart';

class MyFeedView extends StatelessWidget {
  const MyFeedView({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(top: 15),
      child: Consumer<AppViewModel>(
        builder: (_, appViewModel, child) {
          return Consumer<MyFeedViewModel>(
            builder: (context, userTimelineListViewModel, child) {
              return Stack(
                children: [
                  // 검색창과 그 결과 부분을 제외한 부분을 터치하면 포커스가 해제되고 키워드를 없애 검색창을 사라지게 한다.
                  GestureDetector(
                    behavior: HitTestBehavior.translucent,
                    onTap: () {
                      FocusScope.of(context).unfocus();
                    },
                    child: Column(
                      children: [
                        // 개인 정보가 들어가는 칸
                        ChangeNotifierProvider(
                          create: (_) => MyFeedViewModel(
                            context: context,
                            myInfo: userTimelineListViewModel.myInfo,
                            userInfo: userTimelineListViewModel.userInfo,
                          ),
                          child: UserInformationView(
                              userInfo: userTimelineListViewModel.userInfo),
                        ),
                        const SizedBox(
                          height: 60,
                        ),
                        // 타임라인 리스트가 들어가는 칸
                        Expanded(
                          child: TimelineListPage(
                            pagingController:
                                userTimelineListViewModel.pagingController,
                          ),
                        ),
                      ],
                    ),
                  ),
                  Positioned(
                    top: 87,
                    left: 0,
                    right: 0,
                    bottom: 0,
                    child: Container(
                      margin: const EdgeInsets.symmetric(horizontal: 10),
                      child: ChangeNotifierProvider<SearchBarViewModel>(
                        create: (_) => SearchBarViewModel(isMyFeed: true),
                        child: const SearchBar(),
                      ),
                    ),
                  ),
                ],
              );
            },
          );
        },
      ),
    );
  }
}
