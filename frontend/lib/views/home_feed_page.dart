import 'package:danim/view_models/home_feed_view_model.dart';
import 'package:danim/view_models/search_bar_view_model.dart';
import 'package:danim/views/search_bar_view.dart';
import 'package:danim/views/timeline_list_page_main.dart';
import 'package:extended_image/extended_image.dart';
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
                        padding: const EdgeInsets.only(top: 70.0),
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
                    Positioned(
                        top:MediaQuery.of(context).size.height*(0.8),
                        child: GridView.builder(
                          itemCount: viewModel,//count,,
                          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                            crossAxisCount: 3,
                          ),
                          scrollDirection: Axis.vertical,
                          itemBuilder: (BuildContext context, int index){
                            return GridTile(
                            child: Row(
                              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                              children: [
                                Stack(
                                  children: [
                                    SizedBox(
                                      width: MediaQuery.of(context).size.width*(0.2),
                                      height:MediaQuery.of(context).size.height*(0.2),
                                      child: GestureDetector(
                                        child: ExtendedImage.network(
                                          borderRadius: BorderRadius.circular(15),
                                          fit: BoxFit.cover,
                                          //url입력 필수
                                        ),
                                        onTap: () {
                                          appViewModel.changeTitle(
                                              viewModel
                                                  .searchedPosts[index]
                                                  .timelineTitle);
                                          Navigator.pushNamed(context,
                                              '/timeline/detail/${}');
                                        },
                                      ),
                                    ),
                                    Positioned(
                                      top:MediaQuery.of(context).size.height*(0.2),
                                      left: MediaQuery.of(context).size.width*(0.1),
                                      child: Container(
                                        decoration: BoxDecoration(
                                          color: Colors.black54,
                                          borderRadius:
                                          BorderRadius.circular(10),
                                        ),
                                        width: 50,
                                        height: 25,
                                        child: Row(
                                          mainAxisAlignment:
                                          MainAxisAlignment
                                              .spaceEvenly,
                                          children: [
                                            const Icon(
                                              Icons.favorite,
                                              color: Colors.redAccent,
                                              size: 12,
                                            ),
                                            Text(
                                              '${viewModel.searchedPosts[index].favorite}',
                                              style: const TextStyle(
                                              color: Colors.white,
                                              fontSize: 12,
                                              ),
                                            )
                                          ],
                                        ),
                                      ),
                                    )
                                  ],
                                )
                              ],
                            ),
                        )
                )
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
