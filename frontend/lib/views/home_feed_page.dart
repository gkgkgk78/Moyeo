import 'package:flutter/cupertino.dart';
import 'package:moyeo/views/home_userinfo.dart';
import 'package:moyeo/views/search_bar_view.dart';
import 'package:moyeo/views/timeline_list_page_main.dart';
import 'package:provider/provider.dart';
import 'package:flutter/material.dart';

import '../view_models/app_view_model.dart';
import '../view_models/chatbot_detail_view_model.dart';
import '../view_models/home_feed_view_model.dart';
import '../view_models/search_bar_view_model.dart';
import 'chatbot_detail_page.dart';

class HomeFeedPage extends StatelessWidget {

  const HomeFeedPage({super.key});

  @override
  Widget build(BuildContext context) {
    int pageKey = 0;
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
                        child: TimelineListPageMain(
                          pagingController: viewModel.pagingController,
                        ),
                      ),
                    ),
                    // Positioned(
                    //   bottom:30,
                    //     child:HomeUserInfo(userInfo: appViewModel.userInfo)),
                    Positioned(
                      top: 0,
                      left: 0,
                      right: 0,
                      bottom: 0,
                      child: Container(
                        // decoration: BoxDecoration(
                        //   border: Border.all(color: Colors.black)
                        // ),
                        margin:
                            const EdgeInsets.only(left: 10, right: 10, top: 10),
                        child: ChangeNotifierProvider<SearchBarViewModel>(
                          create: (_) => SearchBarViewModel(isMyFeed: false),
                          child: const SearchBar(),
                        ),
                      ),
                    ),
                    Positioned(
                        bottom: 30,
                        child: Container(
                        child:Row(
                          children: [
                            IconButton(
                                onPressed: (){
                                  viewModel.getMainTimelineList(context, pageKey-1);
                                },
                                icon: Icon(Icons.arrow_back, color: Colors.red,),
                            ),
                            Text(
                              "${pageKey+1}"
                            ),
                            IconButton(
                                onPressed: (){
                                  viewModel.getMainTimelineList(context, pageKey+1);
                                },
                                icon: Icon(Icons.arrow_forward, color: Colors.red,))
                          ],
                        )))
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
