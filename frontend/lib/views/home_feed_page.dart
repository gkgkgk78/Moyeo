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
                //     Positioned(
                //       left: MediaQuery.of(context).size.width*(0.365),
                //         bottom: 40,
                //         child: Consumer<HomeFeedViewModel> (
                //           builder: (_, viewModel, __){
                //             return Row(
                //             children: [
                //             IconButton(
                //                 onPressed: (){
                //                   if (viewModel.pageKey > 0) {
                //                     viewModel.pageKey -= 1;
                //                     viewModel.getMainTimelineList(context, viewModel.pageKey);
                //                     viewModel.pagingController.refresh();
                //                   }},
                //                 icon: Icon(Icons.arrow_back, color: Colors.red,),
                //                 ),
                //                 Text(
                //                   "${viewModel.pageKey+1}"
                //                 ),
                //                 IconButton(
                //                     onPressed: (){
                //                       viewModel.pageKey += 1;
                //                       viewModel.getMainTimelineList(context, viewModel.pageKey);
                //                       viewModel.pagingController.refresh();
                //                       // ? viewModel.pagingController.refresh()
                //                       // : showDialog(
                //                       //     barrierDismissible: false,
                //                       //     context: context,
                //                       //     builder: (ctx) =>
                //                       //         AlertDialog(
                //                       //           title: const Text('마지막 페이지 입니다'),
                //                       //           actions: [
                //                       //             TextButton(
                //                       //               onPressed: () {
                //                       //                 Navigator.pop(ctx);
                //                       //               },
                //                       //               child: const Text(
                //                       //                 '닫기',
                //                       //                 style: TextStyle(color: Colors.red),
                //                       //               ),
                //                       //             ),
                //                       //           ],
                //                       //        )
                //                       //     );
                //                       },
                //                     icon: Icon(Icons.arrow_forward, color: Colors.red,)
                //                 )
                //           ],
                //         );
                //           }
                // ))
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
