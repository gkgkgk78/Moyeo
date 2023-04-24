import 'package:danim/view_models/home_feed_view_model.dart';
import 'package:danim/view_models/search_bar_view_model.dart';
import 'package:danim/views/search_bar_view.dart';
import 'package:danim/views/timeline_list_page.dart';
import 'package:flutter/cupertino.dart';
import 'package:provider/provider.dart';
import 'package:flutter/material.dart';

import '../view_models/app_view_model.dart';

class HomeFeedPage extends StatelessWidget {
  const HomeFeedPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Consumer<AppViewModel>(
      builder: (_, appViewModel, __) {
        return ChangeNotifierProvider(
          create: (_) => HomeFeedViewModel(
            context: context,
            myUserUid: appViewModel.userInfo.userUid,
          ),
          builder: (context, snapshot) {
            return Consumer<HomeFeedViewModel>(
              builder: (_, viewModel, __) {
                return Stack(
                  children: [
                    GestureDetector(
                      behavior: HitTestBehavior.translucent,
                      onTap: () {
                        FocusScope.of(context).unfocus();
                      },
                      child: Padding(
                        padding: const EdgeInsets.only(top: 65.0),
                        child: TimelineListPage(
                          pagingController: viewModel.pagingController,
                        ),
                      ),
                    ),
                    Positioned(
                      top: 0,
                      left: 0,
                      right: 0,
                      bottom: 0,
                      child: Container(
                        decoration: BoxDecoration(
                          border: Border.all(color: Colors.black)
                        ),
                        margin:
                            const EdgeInsets.only(left: 10, right: 10, top: 10),
                        child: ChangeNotifierProvider<SearchBarViewModel>(
                          create: (_) => SearchBarViewModel(isMyFeed: false),
                          child: const SearchBar(),
                        ),
                      ),
                    ),
                  ],
                );
              },
            );
          },
        );
      },
    );
  }
}
